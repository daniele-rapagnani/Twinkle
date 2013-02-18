package barrysoft.twinkle.fetcher.sparkle;

import java.util.List;
import java.util.Vector;

import com.sun.syndication.feed.module.ModuleImpl;

public class SparkleModuleImpl extends ModuleImpl 
	implements SparkleModule, SparkleEntry 
{
	private static final long serialVersionUID = 5686916272005686676L;
	
	private String 					releaseNotesLink;
	private String					minimumSystemVersion;
	private List<SparkleEnclosure>	enclosures;
	
	public SparkleModuleImpl()
	{
		super(SparkleModuleImpl.class, SparkleModule.URI);
	}
	
	public String getUri()
	{
		return SparkleModule.URI;
	}

	public void copyFrom(Object o)
	{
		if (!(o instanceof SparkleModule))
			return;
		
		SparkleModuleImpl spk = (SparkleModuleImpl)o;
		
		setReleaseNotesLink(spk.getReleaseNotesLink());
		
		setMinimumSystemVersion(spk.getMinimumSystemVersion());
		
		Vector<SparkleEnclosure> enclosures = new Vector<SparkleEnclosure>();
		setEnclosures(enclosures);
		
		if (spk.getEnclosures() != null)
			enclosures.addAll(spk.getEnclosures());
	}

	public Class<?> getInterface()
	{
		return CopyFromInterface.class;
	}

	public String getReleaseNotesLink()
	{
		return releaseNotesLink;
	}
	
	public void setReleaseNotesLink(String link)
	{
		releaseNotesLink = link;
	}

	public String getMinimumSystemVersion()
	{
		return minimumSystemVersion;
	}

	public void setMinimumSystemVersion(String msv)
	{
		minimumSystemVersion = msv;
	}
	

	public List<SparkleEnclosure> getEnclosures()
	{
		return enclosures;
	}

	public void setEnclosures(List<SparkleEnclosure> enclosures)
	{
		this.enclosures = enclosures;
	}
	
    public static interface CopyFromInterface extends SparkleModule, SparkleEntry {}
}
