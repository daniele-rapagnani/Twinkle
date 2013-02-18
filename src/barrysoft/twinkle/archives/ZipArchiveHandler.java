package barrysoft.twinkle.archives;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * <p>A simple {@link ArchiveHandler} implementation
 * capable of handling archives in the ZIP file format.</p>
 * 
 * <p>To determine if a file can actually be processed,
 * the implementation attempts to create a {@link ZipFile}
 * instance and fails on exception. A consequence of this is
 * that read permission is needed to check if a file can be
 * handled.</p>
 * 
 * @author Daniele Rapagnani
 */

public class ZipArchiveHandler implements ArchiveHandler 
{
	private static final ZipArchiveHandler instance = new ZipArchiveHandler();
	
	private static final int BUFFER_SIZE = 2048;
	
	public static ZipArchiveHandler getInstance()
	{
		return instance;
	}
	
	private ZipArchiveHandler()
	{
		
	}

	public boolean canHandle(File archiveFile)
	{
		try {
			new ZipFile(archiveFile);
		} catch (IOException e) {
			return false;
		}
		
		return true;
	}

	public File[] handle(File f) throws IOException
	{
		ZipFile zipFile = new ZipFile(f);
		
		File destDir = f.getParentFile();
		
		Vector<File> extractedFiles = new Vector<File>();
		
        for (Enumeration<? extends ZipEntry> e = zipFile.entries(); e.hasMoreElements(); ) 
            extractedFiles.add(extract(zipFile, e.nextElement(), destDir));
        
        return extractedFiles.toArray(new File[extractedFiles.size()]);
	}
	
	protected File extract(ZipFile file, ZipEntry entry, File directory) 
		throws IOException
	{
		File destFile = new File(directory + File.separator + entry.getName());
		
		if (entry.isDirectory())
		{
			destFile.mkdir();
		}
		else
		{
			BufferedOutputStream bos = 
				new BufferedOutputStream(new FileOutputStream(destFile));
			
			BufferedInputStream bis = new BufferedInputStream(file.getInputStream(entry));
			
			int size;
			byte[] buffer = new byte[BUFFER_SIZE];
			
			while((size = bis.read(buffer, 0, buffer.length)) != -1)
				bos.write(buffer, 0, size);
			
			bos.flush();
			bos.close();
			bis.close();
		}
		
		return destFile;
	}
}
