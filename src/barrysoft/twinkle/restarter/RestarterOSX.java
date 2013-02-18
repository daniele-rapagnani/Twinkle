package barrysoft.twinkle.restarter;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.log4j.Logger;

public class RestarterOSX implements Restarter {

	private final static RestarterOSX instance = new RestarterOSX();
	
	public static RestarterOSX getInstance() 
	{
		return instance;
	}
	
	private RestarterOSX()
	{
	}
	
	public void restart(Class<?> mainClass)
	{
		String [] args = new String[] {
			"open",
			"-n",
			getAppBundle(mainClass)
		};
		
		Logger.getLogger(getClass()).debug("Restarter args: "+args);
		
		ProcessBuilder builder = new ProcessBuilder(args);
		builder.directory(new File("."));
		
		try {
			builder.start();
		} catch (IOException e) {
			throw new RuntimeException("Can't restart the application.", e);
		}
		
		System.exit(0);
	}
	
	public String getAppBundle(Class<?> mainClass)
	{
		File jarFile;
		
		try 
		{
			jarFile = new File(mainClass.getProtectionDomain().
					getCodeSource().getLocation().toURI());
		} 
		catch (URISyntaxException e)
		{
			e.printStackTrace();
			return "";
		}
		
		return getAppBundle(jarFile);
	}
	
	public String getAppBundle(File jarFile)
	{
		if (!jarFile.toString().matches(".*\\.app.*"))
			throw new RuntimeException("Can't find Application Bundle path from: "+jarFile);
		
		return jarFile.toString().replaceAll("(.*\\.app).*", "$1");
	}
	
}
