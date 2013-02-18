package barrysoft.twinkle;

import java.io.File;

import barrysoft.twinkle.validator.UpdateValidator;

/**
 * <p>This interface must be implemented by any class
 * that needs to be informed of the state of the
 * current update process.</p>
 * 
 * <p>An instance of the implementation can then be added
 * as an observer by calling {@link Updater#addObserver(UpdaterObserver)}</p>
 * 
 * @author Daniele Rapagnani
 */

public interface UpdaterObserver 
{
	/**
	 * Called when the app-cast feed is being fetched
	 * and parsed to check for any new version.
	 * 
	 * @param source The current {@link UpdateRequest}
	 */
	void checkingStarted(UpdateRequest source);
	
	/**
	 * Called when the app-cast feed has been fetched
	 * and processed, whether a new version was found
	 * or not.
	 * 
	 * @param source The current {@link UpdateRequest}
	 */
	void checkingEnded(UpdateRequest source);
	
	/**
	 * Called when a new version of the program has been
	 * found in the paresed app-cast.
	 * 
	 * @param version The new version's {@link UpdateVersion}
	 * @param source The current {@link UpdateRequest}
	 */
	void newVersionFound(UpdateVersion version, UpdateRequest source);
	
	/**
	 * Called when the download of the newly found
	 * update started.
	 * 
	 * @param version The version currently downloading
	 */
	void downloadStarted(UpdateVersion version);
	
	/**
	 * Called when the update data is being downloaded.
	 * 
	 * @param version The version being downloaded
	 * @param bytesLoaded The number of bytes downloaded
	 */
	void downloadProgress(UpdateVersion version, int bytesLoaded);
	
	/**
	 * Called when download of update data 
	 * completed successfully;
	 * 
	 * @param version The version that finished downloading
	 */
	void downloadCompleted(UpdateVersion version);
	
	/**
	 * Called when validation of the downloaded file(s) has started.
	 * 
	 * @param version		The version whose data is being
	 * 						validated
	 * @param archiveFile	The update file archive to validate
	 * 
	 * @see UpdateValidator
	 */
	void validationStarted(UpdateVersion version, File archiveFile);
	
	/**
	 * Called when validation of the downloaded file(s) has ended.
	 * 
	 * @param version		The version whose data passed
	 * 						validation.
	 * @param archiveFile	The update file archive to validate
	 * 
	 * @see UpdateValidator
	 */
	void validationEnded(UpdateVersion version, File archiveFile);
	
	/**
	 * Called when extraction of the update file(s) has started.
	 * 
	 * @param archiveFile Path to the downloaded archive
	 */
	
	void extractionStarted(File archiveFile);
	
	/**
	 * Called when extraction of the update file(s) has ended
	 * (whether successfully or not).
	 * 
	 * @param archiveFile Path to the downloaded archive
	 */
	
	void extractionEnded(File archiveFile);
	
	/**
	 * This is called when extraction of the update file(s)
	 * has ended and the application must be restarted.
	 * 
	 * @param source The originating {@link UpdateRequest}
	 */
	
	void restartRequired(UpdateRequest source);
	
	/**
	 * This is called when no available updates have been found.
	 */
	
	void noUpdateRequired();
	
	/**
	 * This is called when the whole update process has
	 * completed successfully.
	 */
	
	void updateCompleted();
	
	/**
	 * This is called when the update process was cancelled
	 * by the user.
	 */
	
	void updateCanceled();
}
