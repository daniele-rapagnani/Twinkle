package barrysoft.twinkle.fetcher;

public class UpdateFetchersFactory
{
	public static final int	SPARKLE_FETCHER = 0;
	
	private static final int DEFAULT_FETCHER = SPARKLE_FETCHER;
	
	public static UpdateFetcher getDefault()
	{
		return getFetcher(DEFAULT_FETCHER);
	}
	
	public static UpdateFetcher getFetcher(int type)
	{
		switch(type)
		{
		case SPARKLE_FETCHER:
			return UpdateFetcherSparkle.getInstance();
			
		default:
			throw new RuntimeException("Unknown UpdateFetcher type: "+type);
		}
	}
}
