package online.sanen.unabo.template;

import java.util.Collection;

import com.mhdt.degist.Validate;

/**
 * 
 *
 * @author online.sanen <br>
 * Date:2018年10月14日 <br>
 * Time:下午6:28:59
 */
public class DataAccessUtils {

	public static <T> T requiredSingleResult(Collection<T> results) throws DataAccessException {
		
		if (Validate.isNullOrEmpty(results))
			return null;
		
		if (results.size() > 1)
			throw new DataAccessException(
					"The number of results should be 1, but the actual number is " + results.size());
		
		return results.iterator().next();
	}

}
