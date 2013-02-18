package barrysoft.twinkle.tests.updater;

import barrysoft.twinkle.comparator.VersionComparator;
import barrysoft.twinkle.comparator.VersionComparator.ComparatorResult;
import junit.framework.TestCase;

public abstract class VersionComparatorTest extends TestCase {
	
	private VersionComparator comparator;
	
	public VersionComparatorTest(VersionComparator comparator)
	{
		this.comparator = comparator;
	}
	
	public void testNumbers()
	{
		assertNewer("1.0", "1.1");
		assertSame("1.0", "1.0");
		assertOlder("2.0", "1.1");
		assertOlder("0.1", "0.0.1");
		assertNewer("0.1", "0.1.2");
	}
	
	public void testPrereleases()
	{
		assertNewer("1.0a1", "1.0b1");
        assertNewer("1.0b1", "1.0");
        assertNewer("0.9", "1.0a1");
        assertNewer("1.0b", "1.0b2");
        assertNewer("1.0b10", "1.0b11");
        assertNewer("1.0b9", "1.0b10");
        assertNewer("1.0rc", "1.0");
        assertNewer("1.0b", "1.0");
        assertNewer("1.0pre1", "1.0");
        assertNewer("1.0 beta", "1.0");
        assertNewer("1.0 alpha", "1.0 beta");
	}
	
	public void assertResult(String version, String toVersion, ComparatorResult result)
	{
		assertTrue(version+" : "+toVersion+" = "+result.toString(), 
				(comparator.compareVersions(version, toVersion) == result));
	}
	
	public void assertNewer(String version, String toVersion)
	{
		assertResult(version, toVersion, ComparatorResult.VERSION_NEW);
	}
	
	public void assertOlder(String version, String toVersion)
	{
		assertResult(version, toVersion, ComparatorResult.VERSION_OLD);
	}
	
	public void assertSame(String version, String toVersion)
	{
		assertResult(version, toVersion, ComparatorResult.VERSION_EQUAL);
	}

}
