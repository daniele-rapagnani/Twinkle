package barrysoft.twinkle.archives;

//TODO: Runtime addition of handlers
public class ArchiveHandlerManager
{	
	private static final ArchiveHandler[] defaultHandlers = new ArchiveHandler[] {
		ZipArchiveHandler.getInstance()
	};
	
	public static ArchiveHandler[] getDefaultHandlers()
	{
		return defaultHandlers;
	}
}
