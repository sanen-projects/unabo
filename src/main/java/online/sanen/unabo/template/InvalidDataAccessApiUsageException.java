package online.sanen.unabo.template;

/**
 * Exception thrown on incorrect usage of the API, such as failing to "compile"
 * a query object that needed compilation before execution.
 *
 * <p>
 * This represents a problem in our Java data access framework, not the
 * underlying data access infrastructure.
 * 
 * @author LazyToShow <br>
 *         Date: 2018年10月15日 <br>
 *         Time: 下午4:27:01
 */
@SuppressWarnings("serial")
public class InvalidDataAccessApiUsageException extends DataAccessException {

	/**
	 * Constructor for InvalidDataAccessApiUsageException.
	 * 
	 * @param msg the detail message
	 */
	public InvalidDataAccessApiUsageException(String msg) {
		super(msg); 
	}

	/**
	 * Constructor for InvalidDataAccessApiUsageException.
	 * 
	 * @param msg   the detail message
	 * @param cause the root cause from the data access API in use
	 */
	public InvalidDataAccessApiUsageException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
