package online.sanen.unabo.template;

import java.sql.SQLException;

/**
 * 
 *
 * @author LazyToShow	<br>
 * Date:	2018年10月15日	<br>
 * Time:	上午10:50:18
 */
public class InvalidResultSetAccessException extends DataAccessException {
	
	private static final long serialVersionUID = -6520006858941298723L;

	public InvalidResultSetAccessException(String s) {
		super(s);
	}
	
	public InvalidResultSetAccessException(String msg, Throwable cause) {
		super(msg,cause);
	}
	

	public InvalidResultSetAccessException(SQLException se) {
		super(se.getMessage(), se);
	}
	
	

}
