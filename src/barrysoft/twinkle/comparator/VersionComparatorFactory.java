package barrysoft.twinkle.comparator;

public class VersionComparatorFactory 
{
	public 	static final int STANDARD_COMPARATOR = 0;
	
	private	static final int DEFAULT_COMPARATOR = STANDARD_COMPARATOR;
	
	public static VersionComparator getDefault()
	{
		return getComparator(DEFAULT_COMPARATOR);
	}
	
	public static VersionComparator getComparator(int type)
	{
		switch (type) {
		case STANDARD_COMPARATOR:
			return StandardVersionComparator.getInstance();
		
		default:
			throw new RuntimeException("Unknown VersionComparator type: "+type);
		}
	}
}
