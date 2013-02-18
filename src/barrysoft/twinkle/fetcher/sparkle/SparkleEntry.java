package barrysoft.twinkle.fetcher.sparkle;

import java.util.List;

public interface SparkleEntry 
{
	public String 					getReleaseNotesLink();
	
	public void						setReleaseNotesLink(String link);

	public String					getMinimumSystemVersion();
	
	public void						setMinimumSystemVersion(String msv);
	
	public List<SparkleEnclosure>	getEnclosures();
	
	public void						setEnclosures(List<SparkleEnclosure> enclosures);
}
