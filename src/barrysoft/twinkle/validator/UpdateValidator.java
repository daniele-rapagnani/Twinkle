package barrysoft.twinkle.validator;

import java.io.File;

import barrysoft.twinkle.UpdateRequest;
import barrysoft.twinkle.UpdateVersion;

/**
 * This interface is implemented by classes that provide
 * a way to verify the integrity and authenticity of an
 * an update file.
 * 
 * @author Daniele Rapagnani
 */

public interface UpdateValidator 
{
	/**
	 * Returns the name of this validator.
	 * 
	 * @return The name of the validator
	 */
	String 	getName();
	
	/**
	 * Checks a file's integrity and authenticity.
	 * 
	 * @param updateArchive	The downloaded file to be checked
	 * @param version The version the downloaded file refers to
	 * @param source The request from which the {@link UpdateVersion} was fetched
	 * @return
	 */
	boolean	validate(File updateArchive, UpdateVersion version, UpdateRequest source);
}
