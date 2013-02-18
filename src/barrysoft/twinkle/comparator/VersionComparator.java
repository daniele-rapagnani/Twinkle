package barrysoft.twinkle.comparator;

/**
 * This interface must be implemented by
 * any class implementing comparison between
 * two version strings.
 * 
 * @author Daniele Rapagnani
 */

public interface VersionComparator 
{
	/**
	 * Holds the result of a comparison between
	 * two version strings.
	 */
	public enum ComparatorResult
	{
		VERSION_EQUAL,
		VERSION_NEW,
		VERSION_OLD;
		
		public static ComparatorResult parseCompareResult(int result)
		{
			if (result > 0)
				return VERSION_OLD;
			else if (result < 0)
				return VERSION_NEW;
			else
				return VERSION_EQUAL;
		}
	};
	
	/**
	 * Compares two version strings and determine
	 * if the first refers to a version newer or
	 * older than the second one.
	 * 
	 * @param version		First version string
	 * @param toVersion		Second version string
	 * 
	 * @return A {@link ComparatorResult}
	 */
	ComparatorResult compareVersions(String version, String toVersion);
}
