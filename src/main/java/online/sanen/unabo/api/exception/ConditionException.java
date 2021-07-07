package online.sanen.unabo.api.exception;


/**
 * Sql condition exception.
 * @author lazyToShow<br>
 * Date: 2016/07/25<br>
 * Time: 16:27
 */
public class ConditionException extends RuntimeException{
	
	private static final long serialVersionUID = -3812288272968494908L;

	public ConditionException(String message){
		super(message);
	}

	public ConditionException(String message, Throwable e) {
		super(message, e);
	}

}
