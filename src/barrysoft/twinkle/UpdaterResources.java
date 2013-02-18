package barrysoft.twinkle;

import barrysoft.resources.Resources;

public class UpdaterResources extends Resources 
{
	public UpdaterResources()
	{
		super(Updater.class, 
			Updater.class.getPackage().getName() + ".resources");
	}
}
