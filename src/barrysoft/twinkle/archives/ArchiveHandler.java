package barrysoft.twinkle.archives;

import java.io.File;
import java.io.IOException;

/**
 * This interface should be implemented by any class
 * that can extract files from an archive type.
 * 
 * @author Daniele Rapagnani
 */

public interface ArchiveHandler 
{
	/**
	 * Check whether this ArchiveHandler implementation
	 * can handle the provided type of archive file.
	 * 
	 * @param archiveFile	The archive file to be checked
	 * 
	 * @return True if the file can be handled by this
	 * 			implementation, false otherwise
	 */
	boolean canHandle(File archiveFile);
	
	/**
	 * Do the decompression magic on the provided
	 * file.
	 * 
	 * @param f	The file to be processed by the implementation
	 * 
	 * @return An array of {@link File} containing all the entries
	 * 			extracted from the processed archive file.
	 * 
	 * @throws IOException If something goes wrong during the process.
	 */
	//TODO: Maybe we should pass a destination directory too?
	public File[] handle(File f) 
		throws IOException;
}
