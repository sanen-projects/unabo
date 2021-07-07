package online.sanen.unabo.template;

/**
 * Exception thrown if certain expected data could not be retrieved, e.g. when
 * looking up specific data via a known identifier. This exception will be
 * thrown either by O/R mapping tools or by DAO implementations.
 *
 * @author LazyToShow <br>
 *         Date: 2018年10月15日 <br>
 *         Time: 下午4:38:18
 */
@SuppressWarnings("serial")
public class DataRetrievalFailureException extends DataAccessException {

	/**
	 * Constructor for DataRetrievalFailureException.
	 * 
	 * @param msg the detail message
	 */
	public DataRetrievalFailureException(String msg) {
		super(msg);
	}

	/**
	 * Constructor for DataRetrievalFailureException.
	 * 
	 * @param msg   the detail message
	 * @param cause the root cause from the data access API in use
	 */
	public DataRetrievalFailureException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
