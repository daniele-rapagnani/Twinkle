package barrysoft.twinkle.fetcher;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;

import barrysoft.twinkle.UpdateException;
import barrysoft.twinkle.UpdateVersion;
import barrysoft.twinkle.fetcher.sparkle.SparkleEnclosure;
import barrysoft.twinkle.fetcher.sparkle.SparkleEntry;
import barrysoft.twinkle.fetcher.sparkle.SparkleModule;
import barrysoft.twinkle.fetcher.sparkle.SparkleModuleImpl;

import com.sun.syndication.feed.synd.SyndEnclosure;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

/**
 * This {@link UpdateFetcher} implementation fetches and parses
 * <a href="http://sparkle.andymatuschak.org/">Sparkle</a> 
 * {@code App-Cast} RSS feeds using a custom ROME module 
 * ({@link SparkleModuleImpl})
 * 
 * @author Daniele Rapagnani
 */

public class UpdateFetcherSparkle implements UpdateFetcher 
{
	private static final UpdateFetcherSparkle instance = new UpdateFetcherSparkle();
	
	public static UpdateFetcherSparkle getInstance()
	{
		return instance;
	}
	
	private UpdateFetcherSparkle()
	{
		
	}
	
	public List<UpdateVersion> fetchVersions(URL from) throws UpdateException
	{
		Vector<UpdateVersion> operations = 
			new Vector<UpdateVersion>();
		
		SyndFeed feed = fetchFeedFromURL(from);
		
		for (Object e : feed.getEntries()) 
		{
			SyndEntry entry = (SyndEntry)e;

			operations.add(convertSparkleEntry(entry));
		}
		
		return operations;
	}
	
	protected SyndFeed fetchFeedFromURL(URL feedUrl) throws UpdateException
	{
		SyndFeedInput sfi = new SyndFeedInput();
		SyndFeed feed;
		
		try {
			feed = sfi.build(new XmlReader(feedUrl));
		} catch (IllegalArgumentException e) {
			throw new UpdateException("Unknown type of update feed", e);
		} catch (FeedException e) {
			throw new UpdateException("Error while parsing update feed", e);
		} catch (IOException e) {
			throw new UpdateException("Can't fetch update feed", e);
		}
		
		return feed;
	}
	
	protected UpdateVersion convertSparkleEntry(SyndEntry entry)
		throws UpdateException
	{
		UpdateVersion op = new UpdateVersion();
		
		SparkleEntry spk = (SparkleEntry)entry.getModule(SparkleModule.URI);
		
		op.setName(entry.getTitle());
		op.setDate(entry.getPublishedDate());
		op.setMinimumSystemVersion(spk.getMinimumSystemVersion());
		
		if (entry.getDescription() != null)
			op.setDescription(entry.getDescription().getValue());
		
		try {
			op.setReleaseNotesLink(new URL(spk.getReleaseNotesLink()));
		} catch (MalformedURLException e) {
			throw new UpdateException("Can't parse release note URL", e);
		}
		
		convertSparkleEnclosures(entry, op);
		
		return op;
	}
	
	protected void convertSparkleEnclosures(SyndEntry entry, UpdateVersion targetOperation)
		throws UpdateException
	{
		SparkleEntry spk = (SparkleEntry)entry.getModule(SparkleModule.URI);

		if (entry.getEnclosures().isEmpty())
		{
			Logger.getLogger(getClass()).debug("No enclosure was specified for this " +
					"AppCast, this is probably an error!");
			
			return;
		}
		
		SyndEnclosure enclosure = (SyndEnclosure)entry.getEnclosures().get(0);
		SparkleEnclosure senclosure = spk.getEnclosures().get(0);
		
		try {
			targetOperation.setDownloadUrl(new URL(enclosure.getUrl()));
		} catch (MalformedURLException e1) {
			throw new UpdateException("Can't parse download url", e1);
		}
		
		targetOperation.setDownloadSize(enclosure.getLength());
		targetOperation.setDsaSignature(senclosure.getDsaSignature());
		targetOperation.setMd5Sum(senclosure.getMd5Sum());
		targetOperation.setVersion(senclosure.getVersion());
		targetOperation.setShortVersion(senclosure.getShortVersionString());
	}
}
