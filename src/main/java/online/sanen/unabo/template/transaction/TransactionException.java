package online.sanen.unabo.template.transaction;

import java.sql.SQLException;

/**
 * 
 *
 * @author LazyToShow <br>
 *         Date: Nov 3, 2018 <br>
 *         Time: 10:53:30 AM
 */
public class TransactionException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public TransactionException(String arg0, SQLException arg1) {
		super(arg0, arg1);
	}

	

}
