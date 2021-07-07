package online.sanen.unabo.api.exception;
/**
 * <pre>
 * @author LazyToShow
 * Date: 2017/10/21
 * Time: 23:19
 * </pre>
 */
public class QueryException extends RuntimeException{
	
	private static final long serialVersionUID = 9077577006054129370L;
	
	
	public QueryException(String message) {
		super(message);
	}


	public QueryException(String message,Throwable throwable) {
		super(message, throwable);
	}


	public QueryException(Exception e) {
		super(e);
	}

}
