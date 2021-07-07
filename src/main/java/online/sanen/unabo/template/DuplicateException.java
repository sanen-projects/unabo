package online.sanen.unabo.template;

import java.sql.SQLException;

public class DuplicateException extends RuntimeException {

	public DuplicateException(String message, SQLException ex) {
		super(message, ex);
	}

	private static final long serialVersionUID = 1L;

}
