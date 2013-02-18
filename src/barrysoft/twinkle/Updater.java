package barrysoft.twinkle;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;

import barrysoft.common.Observable;
import barrysoft.resources.ResourcesManager;
import barrysoft.twinkle.archives.ArchiveHandler;
import barrysoft.twinkle.archives.ArchiveHandlerManager;
import barrysoft.twinkle.comparator.VersionComparator;
import barrysoft.twinkle.comparator.VersionComparatorFactory;
import barrysoft.twinkle.comparator.VersionComparator.ComparatorResult;
import barrysoft.twinkle.fetcher.UpdateFetcher;
import barrysoft.twinkle.fetcher.UpdateFetchersFactory;
import barrysoft.twinkle.restarter.RestartersFactory;
import barrysoft.twinkle.validator.UpdateValidator;
import barrysoft.twinkle.validator.UpdateValidatorsManager;
import barrysoft.web.ProgressEvent;
import barrysoft.web.ProgressListener;
import barrysoft.web.WebDownloader;

/**
 * <p>This is the class in charge of the update process.</p>
 * 
 * <p>Update informations are obtained by parsing an 
 * <a href="http://connectedflow.com/appcasting/">App-Cast</a> 
 * feed very much like the Sparkle library for Mac OS X.</p>
 * 
 * @author Daniele Rapagnani
 */

public class Updater 
	implements	Observable<UpdaterObserver>, 
				ProgressListener<WebDownloader> 
{
	private final static Updater instance = new Updater();
	
	private final Vector<UpdaterObserver>	observers = 
		new Vector<UpdaterObserver>();
	
	private final ExecutorService	updatesExecutor;
	
	private UpdateFetcher 		fetcher;
	private VersionComparator	comparator;
	private ArchiveHandler[]	archiveHandlers;
	private UpdateValidator[]	updateValidators;
	
	private WebDownloader		downloader;
	
	private UpdateVersion		currentVersion;
	
	private Future<File>		downloadFuture;
	
	public static Updater getInstance()
	{
		return instance;
	}
	
	private Updater()
	{		
		fetcher = UpdateFetchersFactory.getDefault();
		comparator = VersionComparatorFactory.getDefault();
		archiveHandlers = ArchiveHandlerManager.getDefaultHandlers();
		updateValidators = UpdateValidatorsManager.getDefaultValidators();
		
		updatesExecutor = Executors.newSingleThreadExecutor();
		
		downloader = new WebDownloader();
		downloader.addProgressListener(this);
		
		ResourcesManager.setResources(new UpdaterResources());
	}
	
	public void checkUpdates(UpdateRequest ...requests) 
		throws UpdateException
	{		
		for (UpdateRequest request : requests) {
			fireChecking(request, false);
			process(getFetcher().fetchVersions(request.getUrl()), request);
			fireChecking(request, true);
		}
	}
	
	public void update(UpdateVersion version, UpdateRequest source)
		throws UpdateException
	{
		setCurrentDownloadFuture(updatesExecutor.
				submit(createUpdateThread(version, source)));
		
		File updateArchive;
		
		try 
		{
			updateArchive = downloadFuture.get();
		}
		catch (CancellationException e)
		{
			fireUpdateCanceled();
			
			return;
		}
		catch (InterruptedException e) 
		{
			throw new UpdateException("Error while updating.", e);
		} 
		catch (ExecutionException e) 
		{
			if (e.getCause() != null && e.getCause() instanceof UpdateException)
				throw (UpdateException)e.getCause();
			
			throw new UpdateException("Error while updating.", e);
		}
		
		clearCurrentDownloadFuture();
		
		validateUpdate(updateArchive, version, source);
		
		extractArchive(updateArchive);
			
		updateArchive.delete();
		
		fireUpdateCompleted();
		
		fireRestartRequired(source);
		
		setCurrentVersion(null);
	}
	
	public void restart(UpdateRequest source)
	{
		if (source.getMainClass() == null || source.getMainClass().isEmpty())
			return;
		
		Class<?> c;
		try {
			c = Class.forName(source.getMainClass());
		} catch (ClassNotFoundException e) {
			Logger.getLogger(getClass()).
				error("Can't find main class '"+source.getMainClass()+"' to be restarted.");
			return;
		}
		
		RestartersFactory.getDefault().restart(c);
	}
	
	public boolean cancelUpdate()
	{
		if (downloadFuture == null)
			return false;
		
		return downloadFuture.cancel(true);
	}
	
	protected Callable<File> createUpdateThread(final UpdateVersion version, final UpdateRequest source)
	{
		return new Callable<File>() {

			public File call() throws Exception
			{				
				return downloadUpdate(version, source);
			}
			
		};
	}
	
	protected void setCurrentDownloadFuture(Future<File> downloadFuture)
	{
		this.downloadFuture = downloadFuture;
	}
	
	protected void clearCurrentDownloadFuture()
	{
		downloadFuture = null;
	}
	
	protected File downloadUpdate(UpdateVersion version, UpdateRequest source)
		throws UpdateException
	{
		downloader.setUrl(version.getDownloadUrl());
		downloader.setMethod(WebDownloader.METHOD_GET);
		
		File updateArchive;
		try {
			updateArchive = downloader.download(source.getDestinationDirectory());
		} catch (IOException e) {
			throw new UpdateException("Can't download update file", e);
		}
		
		return updateArchive;
	}
	
	protected File[] extractArchive(File archiveFile)
		throws UpdateException
	{
		fireExtraction(archiveFile, false);
		
		for (ArchiveHandler handler : archiveHandlers)
		{
			if (handler.canHandle(archiveFile))
			{
				try 
				{
					File[] files = handler.handle(archiveFile);
					return files;
				} 
				catch (IOException e) 
				{
					throw new UpdateException("Can't extract " +
							"downloaded update file.", e);
				}
				finally
				{
					fireExtraction(archiveFile, true);
				}
			}
		}
		
		throw new UpdateException("Unknown archive format for: "+archiveFile.getName());
	}
	
	protected void validateUpdate(File updateArchive, UpdateVersion version, UpdateRequest source)
		throws UpdateException
	{
		fireValidation(version, updateArchive, false);
		
		for (UpdateValidator validator : updateValidators) 
			if (!validator.validate(updateArchive, version, source))
				throw new UpdateException(validator.getName()+" validation failed.");
		
		fireValidation(version, updateArchive, true);
	}
	
	protected void process(List<UpdateVersion> versions, UpdateRequest source)
	{
		UpdateVersion latestVersion = getLatestVersion(versions);
		
		if (latestVersion == null)
			return;
		
		process(latestVersion, source);
	}
	
	protected void process(UpdateVersion version, UpdateRequest source)
	{
		ComparatorResult result = getComparator().
			compareVersions(source.getComparableVersion(), version.getVersion());
		
		if (result == ComparatorResult.VERSION_NEW)
			fireNewVersionFound(version, source);
		else
			fireNoUpdateRequired();
	}
	
	protected UpdateVersion getLatestVersion(List<UpdateVersion> versions)
	{
		if (versions.isEmpty())
			return null;
		
		UpdateVersion newer = versions.get(0);
		
		for (int i=1; i < versions.size(); i++)
		{			
			ComparatorResult compResult = getComparator().compareVersions(newer.getVersion(),
					versions.get(i).getVersion());
			
			if (compResult == ComparatorResult.VERSION_NEW)
				newer = versions.get(i);
		}
		
		return newer;
	}
	
	protected void fireRestartRequired(UpdateRequest source)
	{
		for (UpdaterObserver observer : observers)
			observer.restartRequired(source);
	}
	
	protected void fireChecking(UpdateRequest source, boolean ended)
	{
		for (UpdaterObserver observer : observers)
		{
			if (ended)
				observer.checkingEnded(source);
			else
				observer.checkingStarted(source);
		}
	}
	
	protected void fireUpdateCompleted()
	{
		for (UpdaterObserver observer : observers)
			observer.updateCompleted();
	}
	
	protected void fireValidation(UpdateVersion version, File archiveFile, boolean ended)
	{
		for (UpdaterObserver observer : observers)
		{
			if (ended)
				observer.validationEnded(version, archiveFile);
			else
				observer.validationStarted(version, archiveFile);
		}
	}
	
	protected void fireExtraction(File archiveFile, boolean ended)
	{
		for (UpdaterObserver observer : observers)
		{
			if (ended)
				observer.extractionEnded(archiveFile);
			else
				observer.extractionStarted(archiveFile);
		}
	}
	
	protected void fireUpdateCanceled()
	{
		for (UpdaterObserver observer : observers)
			observer.updateCanceled();
	}
	
	protected void fireNewVersionFound(UpdateVersion version, UpdateRequest source)
	{
		for (UpdaterObserver observer : observers)
			observer.newVersionFound(version, source);
	}
	
	protected void fireNoUpdateRequired()
	{
		for (UpdaterObserver observer : observers)
			observer.noUpdateRequired();
	}
	
	protected void fireDownloadStarted(UpdateVersion version)
	{
		for (UpdaterObserver observer : observers)
			observer.downloadStarted(version);
	}
	
	protected void fireDownloadCompleted(UpdateVersion version)
	{
		for (UpdaterObserver observer : observers)
			observer.downloadCompleted(version);
	}

	protected void fireDownloadProgress(UpdateVersion version, int bytesLoaded)
	{
		for (UpdaterObserver observer : observers)
			observer.downloadProgress(version, bytesLoaded);
	}

	public void addObserver(UpdaterObserver observer)
	{
		observers.add(observer);
	}

	public void removeObserver(UpdaterObserver observer)
	{
		observers.add(observer);
	}
	
	public void progressFinish(ProgressEvent<WebDownloader> event)
	{
		fireDownloadCompleted(getCurrentVersion());
	}

	public void progressStart(ProgressEvent<WebDownloader> event)
	{
		fireDownloadStarted(getCurrentVersion());
	}

	public void progressUpdate(ProgressEvent<WebDownloader> event)
	{
		fireDownloadProgress(getCurrentVersion(), event.getCurrent());
	}
	
	protected VersionComparator getComparator()
	{
		return comparator;
	}

	protected UpdateFetcher getFetcher()
	{
		return fetcher;
	}

	protected UpdateVersion getCurrentVersion()
	{
		return currentVersion;
	}

	protected void setCurrentVersion(UpdateVersion currentVersion)
	{
		this.currentVersion = currentVersion;
	}

}
