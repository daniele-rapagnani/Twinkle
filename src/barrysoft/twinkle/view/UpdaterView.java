package barrysoft.twinkle.view;

import barrysoft.common.Observable;
import barrysoft.twinkle.UpdateException;
import barrysoft.twinkle.UpdaterObserver;

/**
 * By implementing this interface, a class can be
 * attached to an {@link UpdateController} to
 * present update informations visually to the user
 * (with a GUI for example).
 * 
 * @author Daniele Rapagnani
 */

public interface UpdaterView extends Observable<UpdaterViewObserver>, UpdaterObserver
{
	/**
	 * Called when an error occurred during the update process.
	 * 
	 * @param e The exception that was thrown
	 */
	void updateError(UpdateException e);
}
