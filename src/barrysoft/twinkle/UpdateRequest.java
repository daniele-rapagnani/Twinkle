package barrysoft.twinkle;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

import org.apache.commons.codec.binary.Base64;

import barrysoft.application.ApplicationInfo;

import sun.security.provider.DSAPublicKey;

/**
 * <p>This class represents an update request and holds
 * all the informations needed to check for an update,
 * including the <i>App-Cast</i> feed URL and the 
 * DSA public key used for validation (if any).</p>
 * 
 * @author Daniele Rapagnani
 */

public class UpdateRequest 
{	
	//TODO: Maybe moving appcast url in ApplicationInfo makes sense?
	
	public enum VersionType 
	{
		VERSION_NUMBER,
		BUILD_NUMBER,
		REVISION_NUMBER
	};
	
	private final VersionType			versionType;
	private final URL 					url;
	private final File					destinationDirectory;
	private final String				mainClass;
	private final ApplicationInfo		applicationInfo;
	private final PublicKey				dsaPublicKey;
	
	/**
	 * Creates a new UpdateRequest with holding
	 * the provided informations.
	 * 
	 * @param versionType 		Which type of version information
	 * 							(in {@link ApplicationInfo}) should
	 * 							be used in comparison with potential
	 * 							new version parsed in the App-Cast
	 * 
	 * @param applicationInfo	A filled instance of {@code ApplicationInfo}
	 * 							holding useful informations on the application
	 * 							such as the current version
	 * 
	 * @param url				A {@link URL} to the App-Cast RSS feed
	 * 
	 * @param destinationDirectory	The destination directory where the update files
	 * 								should be placed
	 * 
	 * @param dsaPublicKeyFile	A file holding the current DSA public key, {@code null}
	 * 							if none
	 * 
	 * @throws FileNotFoundException If the dsaPublicKeyFile was not {@code null} but
	 * 									doesn't exists
	 */
	
	public UpdateRequest
	(
		VersionType 		versionType,
		ApplicationInfo 	applicationInfo,
		String				mainClass,
		URL 				url,
		File 				destinationDirectory,
		File 				dsaPublicKeyFile
	) 
		throws FileNotFoundException
	{
		this(versionType, 
			applicationInfo, 
			mainClass,
			url, 
			destinationDirectory, 
			(dsaPublicKeyFile != null ? new FileInputStream(dsaPublicKeyFile) : null));
	}
	
	/**
	 * Creates a new UpdateRequest with holding
	 * the provided informations.
	 * 
	 * @param versionType 		Which type of version information
	 * 							(in {@link ApplicationInfo}) should
	 * 							be used in comparison with potential
	 * 							new version parsed in the App-Cast
	 * 
	 * @param applicationInfo	A filled instance of {@code ApplicationInfo}
	 * 							holding useful informations on the application
	 * 							such as the current version
	 * 
	 * @param url				A {@link URL} to the App-Cast RSS feed
	 * 
	 * @param destinationDirectory	The destination directory where the update files
	 * 								should be placed
	 * 
	 * @param dsaPublicKey		An input stream holding the current DSA public key, 
	 * 							{@code null} if none
	 * 
	 * @throws FileNotFoundException If there was an error parsing the DSA public key
	 */
	
	public UpdateRequest
	(
		VersionType 	versionType,
		ApplicationInfo applicationInfo, 
		String			mainClass,
		URL 			url, 
		File 			destinationDirectory, 
		InputStream 	dsaPublicKey
	)
	{
		this.url = url;
		this.destinationDirectory = destinationDirectory;
		this.mainClass = mainClass;
		this.applicationInfo = applicationInfo;
		this.versionType = versionType;
		
		if (dsaPublicKey != null)
		{
			try {
				this.dsaPublicKey = readDSAPublicKey(dsaPublicKey);
			} catch (FileNotFoundException e) {
				throw new RuntimeException("The DSA Public Key file doesn't exist.",e);
			} catch (IOException e) {
				throw new RuntimeException("Can't read DSA Public Key.",e);
			} catch (InvalidKeyException e) {
				throw new RuntimeException("Can't load DSA Public Key as it seems invalid.",e);
			} catch (NoSuchAlgorithmException e) {
				throw new RuntimeException("It appears that DSA is not a valid algorithm.",e);
			} catch (InvalidKeySpecException e) {
				throw new RuntimeException("Can't load DSA Public Key as it seems invalid.",e);
			}
		}
		else
		{
			this.dsaPublicKey = null;
		}
	}
	
	/*
	 * TODO: This is a bit hackish.
	 * 		Decodes openssl public DSA key format to be
	 * 		parsed by DSAPublicKey.
	 */	
	protected PublicKey readDSAPublicKey(InputStream is) 
		throws 	FileNotFoundException,
				IOException, 
				InvalidKeyException, 
				NoSuchAlgorithmException, 
				InvalidKeySpecException
	{
		if (is == null)
			return null;
		
		is = new BufferedInputStream(is);
		
		byte[] buffer = new byte[(int)is.available()];
	
		is.read(buffer, 0, buffer.length);
		
		String base64Key = new String(buffer);
		
		base64Key = base64Key.replaceAll(".*?-----.+-----.*?", "");
		base64Key = base64Key.replaceAll("\n", "").trim();
		
		byte[] derData = Base64.decodeBase64(base64Key);
		
		DSAPublicKey publicKey = new DSAPublicKey(derData);
		
		return publicKey;
	}
	
	public File getDestinationDirectory()
	{
		return destinationDirectory;
	}

	public URL getUrl()
	{
		return url;
	}

	public ApplicationInfo getApplicationInfo()
	{
		return applicationInfo;
	}
	
	/**
	 * Returns a version that can be used in a
	 * comparison as specified on construction
	 * with the {@link VersionType} parameter.
	 * 
	 * @return A string representing a version
	 * 			that can be compared to other version
	 * 			strings
	 */
	
	public String getComparableVersion()
	{
		switch(versionType) {
		case VERSION_NUMBER:
			return getApplicationInfo().getVersion();
		
		case BUILD_NUMBER:
			return getApplicationInfo().getBuildNumber();
			
		case REVISION_NUMBER:
			return getApplicationInfo().getRevisionNumber();
		}
		
		throw new RuntimeException("Unknown version type.");
	}

	public PublicKey getDsaPublicKey()
	{
		return dsaPublicKey;
	}

	public String getMainClass()
	{
		return mainClass;
	}
	
}
