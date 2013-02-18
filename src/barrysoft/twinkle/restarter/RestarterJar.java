package barrysoft.twinkle.restarter;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * A {@link Restarter} implementation that is able to restart
 * the JVM.
 * 
 * @author Daniele Rapagnani
 */

public class RestarterJar implements Restarter 
{
	private final static RestarterJar instance = new RestarterJar();
	
	public static RestarterJar getInstance() 
	{
		return instance;
	}
	
	private RestarterJar()
	{
	}
	
	/**
	 * <p>This method should launch a new JVM and run
	 * the provided class in it.<p>
	 * 
	 * <p>An attempt at retrieving the original arguments
	 * with which the JVM was launched the first time
	 * is made.</p>
	 * 
	 * @param mainClass A class containing a static main method
	 * 					that can be executed by the JVM
	 */
	
	public void restart(Class<?> mainClass)
	{
		List<String> args = buildJVMArgs(mainClass);
		
		ProcessBuilder builder = new ProcessBuilder(args);
		builder.directory(new File("."));
		
		try {
			builder.start();
		} catch (IOException e) {
			throw new RuntimeException("Can't restart the application.", e);
		}
		
		System.exit(0);
	}
	
	protected boolean isClassInJar(Class<?> clazz)
	{
		return clazz.getProtectionDomain().getCodeSource().
			getLocation().getFile().endsWith(".jar");
	}
	
	protected List<String> buildJVMArgs(Class<?> mainClass)
	{
		String javaBin = getJavaLauncherBin();
		
		RuntimeMXBean rmxb = ManagementFactory.getRuntimeMXBean();
		
		ArrayList<String> list = new ArrayList<String>();
		list.add(javaBin);
		list.addAll(rmxb.getInputArguments());
		list.add("-cp");
		list.add(rmxb.getClassPath());

		buildJVMArgs(list, mainClass);
		
		return list;
	}
	
	protected void buildJVMArgs(List<String> args, Class<?> mainClass)
	{
		if (!isClassInJar(mainClass))
			args.add(mainClass.getCanonicalName());
		else
			buildJVMArgsForJar(args, mainClass);
	}
	
	protected void buildJVMArgsForJar(List<String> args, Class<?> mainClass)
	{
		args.add("-jar");
		
		try 
		{
			File jarFile = new File(mainClass.getProtectionDomain().
					getCodeSource().getLocation().toURI());
			
			args.add(jarFile.toString());
		} 
		catch (URISyntaxException e) 
		{
			throw new RuntimeException("Can't parse Jar URI.", e);
		}
	}
	
	protected String getJavaLauncherBin()
	{
		return System.getProperty("java.home") + File.separator 
			+ "bin" + File.separator + "java";
	}
}
