package barrysoft.twinkle.fetcher;

import java.net.URL;
import java.util.List;

import barrysoft.twinkle.UpdateException;
import barrysoft.twinkle.UpdateVersion;

/**
 * This interface should be implemented by
 * any class able to parse an {@link URL}
 * and return an {@link UpdateVersion}.
 * 
 * @author Daniele Rapagnani
 */

public interface UpdateFetcher 
{
	/**
	 * Fetches data from a {@link URL} and returns
	 * an {@link UpdateVersion} as a result of parsing
	 * that data.
	 * 
	 * @param from	The source {@code URL}
	 * 
	 * @return An {@code UpdateVersion} instance holding
	 * 			the parsed informations
	 * 
	 * @throws UpdateException
	 */
	
	List<UpdateVersion> fetchVersions(URL from)
		throws UpdateException;	
}
