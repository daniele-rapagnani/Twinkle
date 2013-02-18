package barrysoft.twinkle.validator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.digest.DigestUtils;

import barrysoft.twinkle.UpdateRequest;
import barrysoft.twinkle.UpdateVersion;

/**
 * <p>A basic implementation of {@link UpdateValidator} using
 * MD5 hashes as validation mechanism.<p>
 *  
 * @author Daniele Rapagnani
 */
public class MD5UpdateValidator implements UpdateValidator 
{
	private static final MD5UpdateValidator instance = new MD5UpdateValidator();
	
	private static final int MD5_HASH_LENGTH = 16;
	
	public final static MD5UpdateValidator getInstance()
	{
		return instance;
	}
	
	private MD5UpdateValidator()
	{
	}
	
	public String getName()
	{
		return "MD5 Checksum";
	}
	
	public boolean validate(File updateArchive, UpdateVersion version, UpdateRequest source)
	{		
		try {
			return validate(updateArchive, version.getMd5Sum());
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("MD5 seems not to be supported!", e);
		} catch (FileNotFoundException e) {
			return false;
		} catch (IOException e) {
			throw new RuntimeException("Can't validate update archive.",e);
		}
	}
	
	protected boolean validate(File updateArchive, String md5Checksum) 
		throws 	FileNotFoundException, 
				IOException, 
				NoSuchAlgorithmException
	{
		if (md5Checksum == null || md5Checksum.isEmpty())
			return true;
		
		if (md5Checksum.length() != (MD5_HASH_LENGTH * 2))
			throw new RuntimeException("Invalid MD5 Hash length");
		
		InputStream is = new FileInputStream(updateArchive);
		
		String currentChecksum = DigestUtils.md5Hex(is);
		
		return md5Checksum.equalsIgnoreCase(currentChecksum);
	}
}
