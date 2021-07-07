package online.sanen.unabo.template;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * Default implementation of the {@link KeyHolder} interface, to be used for
 * holding auto-generated keys (as potentially returned by JDBC insert
 * statements).
 *
 * <p>
 * Create an instance of this class for each insert operation, and pass it to
 * the corresponding {@link SqlTemplate}
 * 
 * @author LazyToShow <br>
 *         Date: 2018年10月15日 <br>
 *         Time: 下午4:36:59
 */
public class GeneratedKeyHolder implements KeyHolder {

	private final List<Map<String, Object>> keyList;

	/**
	 * Create a new GeneratedKeyHolder with a default list.
	 */
	public GeneratedKeyHolder() {
		this.keyList = new LinkedList<Map<String, Object>>();
	}

	/**
	 * Create a new GeneratedKeyHolder with a given list.
	 * 
	 * @param keyList a list to hold maps of keys
	 */
	public GeneratedKeyHolder(List<Map<String, Object>> keyList) {
		this.keyList = keyList;
	}

	String specified;

	public GeneratedKeyHolder(String name) {
		this.keyList = new LinkedList<Map<String, Object>>();
		this.specified = name;
	}

	public Number getKey() throws InvalidDataAccessApiUsageException, DataRetrievalFailureException {
		if (this.keyList.size() == 0) {
			return null;
		}

		boolean flag = false;

		if (this.keyList.size() > 1 || this.keyList.get(0).size() > 1) {
			if (specified != null && keyList.stream().anyMatch(predicate -> predicate.containsKey(specified)))
				flag = true;
			else
				throw new InvalidDataAccessApiUsageException(
						"The getKey method should only be used when a single key is returned.  "
								+ "The current key entry contains multiple keys: " + this.keyList);
		}


		if (keyList.size() == 1 && keyList.get(0).containsKey(specified)
				&& keyList.get(0).get(specified) instanceof Number)
			return (Number) keyList.get(0).get(specified);

		Iterator<Object> keyIter = flag
				? this.keyList.stream().filter(predicate -> predicate.containsKey(specified)).findFirst().get().values()
						.iterator()
				: this.keyList.get(0).values().iterator();
		if (keyIter.hasNext()) {
			Object key = keyIter.next();
			if (!(key instanceof Number)) {
				throw new DataRetrievalFailureException("The generated key is not of a supported numeric type. "
						+ "Unable to cast [" + (key != null ? key.getClass().getName() : null) + "] to ["
						+ Number.class.getName() + " " + key + "]");
			}
			return (Number) key;
		} else {
			throw new DataRetrievalFailureException(
					"Unable to retrieve the generated key. " + "Check that the table has an identity column enabled.");
		}
	}

	public Map<String, Object> getKeys() throws InvalidDataAccessApiUsageException {
		if (this.keyList.size() == 0) {
			return null;
		}
		if (this.keyList.size() > 1)
			throw new InvalidDataAccessApiUsageException(
					"The getKeys method should only be used when keys for a single row are returned.  "
							+ "The current key list contains keys for multiple rows: " + this.keyList);
		return this.keyList.get(0);
	}

	public List<Map<String, Object>> getKeyList() {
		return this.keyList;
	}

}
