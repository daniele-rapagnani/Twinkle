package barrysoft.twinkle;

import java.net.URL;
import java.util.Date;

import barrysoft.utils.FileUtils;

/**
 * <p>This class represents an entry in an
 * <a href="http://connectedflow.com/appcasting/">App-Cast</a>
 * feed.</p>
 * 
 * <p>It is usually filled by an {@code App-Cast} parser.</a>
 * 
 * @author Daniele Rapagnani
 */

public class UpdateVersion 
{
	private String 		name;
	private String		description;
	private Date		date;
	private URL			releaseNotesLink;
	private String		minimumSystemVersion;
	private URL 		downloadUrl;
	private long		downloadSize;
	private String		version;
	private String		shortVersion;
	//TODO: These should be moved inside an hashmap to allow for custom validator inputs
	private String		md5Sum;
	private String		dsaSignature;
	
	public long getDownloadSize()
	{
		return downloadSize;
	}
	
	public void setDownloadSize(long downloadSize)
	{
		this.downloadSize = downloadSize;
	}
	
	public URL getDownloadUrl()
	{
		return downloadUrl;
	}
	
	public void setDownloadUrl(URL downloadUrl)
	{
		this.downloadUrl = downloadUrl;
	}
	
	public String getDsaSignature()
	{
		return dsaSignature;
	}
	
	public void setDsaSignature(String dsaSignature)
	{
		this.dsaSignature = dsaSignature;
	}
	
	public String getMd5Sum()
	{
		return md5Sum;
	}
	
	public void setMd5Sum(String md5Sum)
	{
		this.md5Sum = md5Sum;
	}
	
	public String getVersion()
	{
		return version;
	}
	
	public void setVersion(String version)
	{
		this.version = version;
	}

	public String getShortVersion()
	{
		return shortVersion;
	}

	public void setShortVersion(String shortVersion)
	{
		this.shortVersion = shortVersion;
	}
	
	public Date getDate()
	{
		return date;
	}
	
	public void setDate(Date date)
	{
		this.date = date;
	}
	
	public String getMinimumSystemVersion()
	{
		return minimumSystemVersion;
	}
	
	public void setMinimumSystemVersion(String minimumSystemVersion)
	{
		this.minimumSystemVersion = minimumSystemVersion;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public URL getReleaseNotesLink()
	{
		return releaseNotesLink;
	}
	
	public void setReleaseNotesLink(URL releaseNotesLink)
	{
		this.releaseNotesLink = releaseNotesLink;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}	
	
	@Override
	public String toString()
	{
		String s = new String();
		
		s += "Name: "+getName()+"\n";
		s += "Description: "+getDescription()+"\n";
		s += "Date: "+getDate()+"\n";
		s += "Minimum System Version: "+getMinimumSystemVersion()+"\n";
		s += "Release Notes Link: "+getReleaseNotesLink()+"\n\n";
		s += "Download URL: "+getDownloadUrl()+"\n";
		s += "Size: "+FileUtils.bytesToSize(getDownloadSize())+"\n";
		s += "Version: "+getVersion()+"\n";
		s += "Short Version: "+getShortVersion()+"\n";
		s += "MD5 Checksum: "+getMd5Sum()+"\n";
		s += "DSA Signature: "+getDsaSignature()+"\n";
		
		return s;
	}
}
