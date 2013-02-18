package barrysoft.twinkle;

/**
 * An exception thrown when something in the update
 * process goes wrong.
 * 
 * @author Daniele Rapagnani
 */

public class UpdateException extends Exception 
{
	private static final long serialVersionUID = 1L;
	
	public UpdateException()
	{
	}
	
	public UpdateException(String message)
	{
		super(message);
	}
	
	public UpdateException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
