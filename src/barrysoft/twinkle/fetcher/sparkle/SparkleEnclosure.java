package barrysoft.twinkle.fetcher.sparkle;

import java.io.Serializable;

public class SparkleEnclosure implements Serializable, Cloneable 
{
	private static final long serialVersionUID = -1254318562711253991L;
	
	private String version;
	private String shortVersionString;
	private String dsaSignature;
	private String md5Sum;
	
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
	
	public String getShortVersionString()
	{
		return shortVersionString;
	}

	public void setShortVersionString(String shortVersionString)
	{
		this.shortVersionString = shortVersionString;
	}
	
	@Override
	public SparkleEnclosure clone() throws CloneNotSupportedException
	{
		return (SparkleEnclosure)super.clone();
	}
}
