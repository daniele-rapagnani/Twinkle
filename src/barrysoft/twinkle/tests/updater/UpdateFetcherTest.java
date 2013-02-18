package barrysoft.twinkle.tests.updater;

import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;
import barrysoft.twinkle.UpdateException;
import barrysoft.twinkle.UpdateVersion;
import barrysoft.twinkle.fetcher.UpdateFetcher;

public abstract class UpdateFetcherTest extends TestCase {

	private UpdateFetcher 	fetcher;
	private DateFormat		dateFormat;
	
	public UpdateFetcherTest(UpdateFetcher fetcher)
	{
		this.fetcher = fetcher;
		this.dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z");
	}
	
	public void testFetcher()
	{
		try 
		{
			List<UpdateVersion> operations = fetcher.fetchVersions(getTestFile());
			
			assertEquals(operations.size(), 3);
			
			assertUpdateVersion(operations.get(0),
					"Version 2.0 (2 bugs fixed; 3 new features)", 
					null, "http://you.com/app/2.0.html", null,
					parseDate("Wed, 09 Jan 2006 19:20:11 +0000"),
					"http://you.com/app/Your Great App 2.0.zip",
					1623481,"BAFJW4B6B1K1JyW30nbkBwainOzrN6EQuAh", 
					null, "2.0", null);
			
			assertUpdateVersion(operations.get(1),
					"Version 1.5 (8 bugs fixed; 2 new features)", 
					"This is just a description",
					"http://you.com/app/1.5.html", "1.6.0",
					parseDate("Wed, 01 Jan 2006 12:20:11 +0000"),
					"http://you.com/app/Your Great App 1.5.zip",
					1472893, "234818feCa1JyW30nbkBwainOzrN6EQuAh", 
					"5e037a38c74aab7d63a5a7b9aa3dba5d", "1.5", null);
			
			assertUpdateVersion(operations.get(2),
					"Version 1.4 (5 bugs fixed; 2 new features)", 
					"This is a <b>bold</b> word. <span class=\"item\">" +
					"This is a styled element</span>", 
					"http://you.com/app/1.4.html", null,
					parseDate("Wed, 25 Dec 2005 12:20:11 +0000"),
					"http://you.com/app/Your Great App 1.4.zip",
					1472349, "MC0CFBfeCa1JyW30nbkBwainOzrN6EQuAh=", 
					null, "241", "1.4");
		} 
		catch (UpdateException e) 
		{
			fail(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public Date parseDate(String dateStr)
	{
		Date date = null;
		try {
			date = dateFormat.parse(dateStr);
		} catch (ParseException e) {
			fail(e.getMessage());
			e.printStackTrace();
		}
		
		return date;
	}
	
	public void assertName(UpdateVersion version, String name)
	{
		assertEquals("Name", name, version.getName());
	}
	
	public void assertDescription(UpdateVersion version, String desc)
	{
		assertEquals("Description", desc, version.getDescription());
	}
	
	public void assertReleaseNotesLink(UpdateVersion version, String rnl)
	{
		assertEquals("Release Notes Link", rnl, version.getReleaseNotesLink().toString());
	}
	
	public void assertMinimumSystemVersion(UpdateVersion version, String msv)
	{
		assertEquals("Minimum System Version", msv, version.getMinimumSystemVersion());
	}
	
	public void assertDate(UpdateVersion version, Date date)
	{
		assertEquals("Publish Date", date, version.getDate());
	}
	
	public void assertDownloadURL(UpdateVersion version, String url)
	{
		assertEquals("Download URL", url, version.getDownloadUrl().toString());
	}
	
	public void assertDownloadSize(UpdateVersion version, long size)
	{
		assertEquals("Download Size", size, version.getDownloadSize());
	}
	
	public void assertDsaSignature(UpdateVersion version, String dsaSignature)
	{
		assertEquals("DSA Signature", dsaSignature, version.getDsaSignature());
	}
	
	public void assertMd5Checksum(UpdateVersion version, String md5Checksum)
	{
		assertEquals("MD5 Checksum", md5Checksum, version.getMd5Sum());
	}
	
	public void assertVersion(UpdateVersion version, String aVersion)
	{
		assertEquals("Version", aVersion, version.getVersion());
	}
	
	public void assertShortVersion(UpdateVersion version, String shortVersion)
	{
		assertEquals("Short Version", shortVersion, version.getShortVersion());
	}
	
	public void assertUpdateVersion(UpdateVersion version, 
								 	String name, 
								 	String desc,
								 	String rnl,
								 	String msv,
								 	Date date,
								 	String url,
									long size,
									String dsaSignature,
									String md5Checksum,
									String aVersion,
									String shortVersion)
	{
		assertName(version, name);
		assertDescription(version, desc);
		assertReleaseNotesLink(version, rnl);
		assertMinimumSystemVersion(version, msv);
		assertDate(version, date);
		assertDownloadURL(version, url);
		assertDownloadSize(version, size);
		assertDsaSignature(version, dsaSignature);
		assertMd5Checksum(version, md5Checksum);
		assertVersion(version, aVersion);
		assertShortVersion(version, shortVersion);
	}
	
	public URL getTestFile()
	{
		return getClass().getResource("data/"+getClass().getSimpleName()+".xml");
	}
	
}
