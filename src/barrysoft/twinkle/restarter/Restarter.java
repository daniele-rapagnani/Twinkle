package barrysoft.twinkle.restarter;

/**
 * Interface definition that must be implemented
 * by classes providing restart facilities.
 * 
 * @author Daniele Rapagnani
 */

public interface Restarter 
{
	/**
	 * Restarts the application running the provided
	 * class.
	 * 
	 * @param mainClass The class to be re-launched
	 */
	void restart(Class<?> mainClass);
}
