package barrysoft.twinkle.view;

import barrysoft.twinkle.UpdateRequest;
import barrysoft.twinkle.UpdateVersion;

/**
 * <p>This interface must be implemented by any class
 * that needs to be informed of the state of an
 * {@link UpdaterView}.</p>
 * 
 * <p>An instance of the implementation can then be added
 * as an observer by calling {@link UpdaterView#addObserver(UpdaterViewObserver)}</p>
 * 
 * @author Daniele Rapagnani
 */

public interface UpdaterViewObserver 
{
	/**
	 * Called when the user has chosen to proceed with
	 * the updated process.
	 * 
	 * @param version	The version to update to
	 * @param source	The source {@link UpdateRequest} that was used
	 * 					to check for available updates
	 */
	void updateRequested(UpdateVersion version, UpdateRequest source);
	
	/**
	 * Called when the user agreed to restart the application	
	 * 
	 * @param source	The source {@link UpdateRequest} that was used
	 * 					to check for available updates
	 */
	void restartRequested(UpdateRequest source);
	
	/**
	 * Called when the user has chosen <b>not</b> to proceed
	 * with the update process.
	 */
	void userCanceledUpdate();
}
