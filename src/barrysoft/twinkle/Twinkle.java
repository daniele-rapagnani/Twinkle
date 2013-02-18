package barrysoft.twinkle;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;

import barrysoft.application.ApplicationInfo;
import barrysoft.options.Options;
import barrysoft.resources.ResourcesManager;
import barrysoft.twinkle.UpdateRequest.VersionType;
import barrysoft.twinkle.view.UpdaterView;
import barrysoft.twinkle.view.UpdaterViewSwing;

public class Twinkle 
{
	private static final Twinkle instance = new Twinkle();
	
	public static Twinkle getInstance()
	{
		return instance;
	}
	
	/**
	 * Helper method to quickly start the update process.
	 * 
	 * @param main The class containing the main method.
	 * 				If after the update restarting is needed,
	 * 				this will be the class to be executed.
	 * 
	 * @param appcastUrl The url to the appcast feed
	 * 
	 * @param appinfoUrl Url to the property file holding the
	 * 						application infos.
	 */
	
	//TODO: It's just a quick hack for now
	public void runUpdate(Class<?> main, String appcastUrl, String appinfoUrl)
	{
		//Initialize the updater
		Updater.getInstance();
		
		ApplicationInfo info = new ApplicationInfo(main.getResourceAsStream(appinfoUrl));
		
		final UpdateRequest r;
		try
		{
			r = new UpdateRequest
			(
				VersionType.BUILD_NUMBER,
				info,
				main.getCanonicalName(),
				new URL(appcastUrl),
				ResourcesManager.getResources().getWorkingDirectory(),
				(File)null //TODO: Not using DSA Verification
			);
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
			return;
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
			return;
		}
		
		UpdaterView view = new UpdaterViewSwing(new Options());
		final UpdaterController uc = new UpdaterController(Updater.getInstance(), view);

		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				uc.checkUpdates(r);
			}
		}).start();
	}
}
