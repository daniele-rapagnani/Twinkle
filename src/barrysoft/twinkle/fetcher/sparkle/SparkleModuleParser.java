package barrysoft.twinkle.fetcher.sparkle;

import java.util.List;
import java.util.Vector;

import org.jdom.Element;
import org.jdom.Namespace;

import com.sun.syndication.feed.module.Module;
import com.sun.syndication.io.ModuleParser;

public class SparkleModuleParser implements ModuleParser 
{
	private static final Namespace NS = Namespace.getNamespace(SparkleModule.URI);
	
	public String getNamespaceUri()
	{
		return SparkleModule.URI;
	}

	public Module parse(Element e)
	{
		SparkleModuleImpl spk = new SparkleModuleImpl();
		
		spk.setReleaseNotesLink(e.getChildText("releaseNotesLink", SparkleModuleParser.NS));
		spk.setMinimumSystemVersion(e.getChildText("minimumSystemVersion", SparkleModuleParser.NS));
		
		List<?> children = e.getChildren();
		
		Vector<SparkleEnclosure> sparkleEnclosures = new Vector<SparkleEnclosure>();
		
		for (Object child : children)
		{
			Element elem = (Element)child;
			
			if (!elem.getName().equals("enclosure"))
				continue;
			
			SparkleEnclosure sparkleEnclosure = new SparkleEnclosure();
			
			sparkleEnclosure.setVersion(elem.getAttributeValue("version", 
					SparkleModuleParser.NS));
			
			sparkleEnclosure.setDsaSignature(elem.getAttributeValue("dsaSignature", 
					SparkleModuleParser.NS));
			
			sparkleEnclosure.setMd5Sum(elem.getAttributeValue("md5Sum", 
					SparkleModuleParser.NS));
			
			sparkleEnclosure.setShortVersionString(elem.getAttributeValue("shortVersionString", 
					SparkleModuleParser.NS));
			
			sparkleEnclosures.add(sparkleEnclosure);
		}
		
		spk.setEnclosures(sparkleEnclosures);
		
		return spk;
	}
}
