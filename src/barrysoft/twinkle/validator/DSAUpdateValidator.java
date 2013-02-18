package barrysoft.twinkle.validator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

import barrysoft.twinkle.UpdateRequest;
import barrysoft.twinkle.UpdateVersion;

/**
 * <p>A basic implementation of {@link UpdateValidator} using
 * DSA signing as validation mechanism.</p>
 * 
 * @author Daniele Rapagnani
 */
public class DSAUpdateValidator implements UpdateValidator 
{
	private static final DSAUpdateValidator instance = new DSAUpdateValidator();
	
	public final static DSAUpdateValidator getInstance()
	{
		return instance;
	}
	
	private DSAUpdateValidator()
	{
	}
	
	public String getName()
	{
		return "DSA Signature";
	}

	public boolean validate(File updateArchive, UpdateVersion version, UpdateRequest source)
	{
		try {
			return validate(updateArchive, version.getDsaSignature(), source.getDsaPublicKey());
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("SHA1 with DSA seems not to be implemented.",e);
		} catch (NoSuchProviderException e) {
			throw new RuntimeException("SUN seems not to be a valid provider.",e);
		} catch (InvalidKeyException e) {
			throw new RuntimeException("Invalid DSA public key.",e);
		} catch (FileNotFoundException e) {
			return false;
		} catch (IOException e) {
			throw new RuntimeException("Can't validate update archive.",e);
		} catch (SignatureException e) {
			throw new RuntimeException("Signature is not initialized properly.",e);
		}
	}
	
	protected boolean validate(File updateArchive, String dsaSignature, PublicKey dsaPublicKey) 
		throws 	NoSuchAlgorithmException, 
				NoSuchProviderException, 
				InvalidKeyException, 
				FileNotFoundException,
				IOException, 
				SignatureException
	{
		if (dsaPublicKey == null || dsaSignature == null || dsaSignature.isEmpty())
			return true;
		
		Signature signature = Signature.getInstance("SHA1withDSA", "SUN");
		signature.initVerify(dsaPublicKey);
		
		InputStream is = new FileInputStream(updateArchive);
		
		byte[] shaHash = DigestUtils.sha(is);
		
		signature.update(shaHash);
		
		return signature.verify(Base64.decodeBase64(dsaSignature));
	}
}
