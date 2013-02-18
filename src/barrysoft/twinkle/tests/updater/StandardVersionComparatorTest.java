package barrysoft.twinkle.tests.updater;

import barrysoft.twinkle.comparator.StandardVersionComparator;

public class StandardVersionComparatorTest extends VersionComparatorTest {

	public StandardVersionComparatorTest()
	{
		super(StandardVersionComparator.getInstance());
	}

}
