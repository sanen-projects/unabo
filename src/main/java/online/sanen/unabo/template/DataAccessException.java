package online.sanen.unabo.template;

/**
 *
 * @author LazyToShow	<br>
 * Date:	2018年10月14日	<br>
 * Time:	下午2:21:38
 */
public  class DataAccessException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor for DataAccessException.
	 * @param msg the detail message
	 */
	public DataAccessException(String msg) {
		super(msg);
	}

	/**
	 * Constructor for DataAccessException.
	 * @param msg the detail message
	 * @param cause the root cause (usually from using a underlying
	 * data access API such as JDBC)
	 */
	public DataAccessException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
