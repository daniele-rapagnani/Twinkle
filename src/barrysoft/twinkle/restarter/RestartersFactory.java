package barrysoft.twinkle.restarter;

import barrysoft.utils.OSSpecific;

public class RestartersFactory 
{
	public static Restarter getDefault() 
	{
		if (OSSpecific.isOSX())
			return RestarterOSX.getInstance();
		else
			return RestarterJar.getInstance();
	}
}
