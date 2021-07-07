package online.sanen.unabo.template.transaction;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 
 *
 * @author LazyToShow <br>
 *         Date: Nov 3, 2018 <br>
 *         Time: 10:39:33 AM
 */
public interface Transaction {

	/**
	 * Retrieve inner database connection
	 * 
	 * @return DataBase connection
	 * @throws SQLException
	 */
	Connection getConnection() throws SQLException;

	/**
	 * Commit inner database connection.
	 * 
	 * @throws SQLException
	 */
	void commit() throws SQLException;

	/**
	 * Rollback inner database connection.
	 * 
	 * @throws SQLException
	 */
	void rollback() throws SQLException;

	/**
	 * Close inner database connection.
	 * 
	 * @throws SQLException
	 */
	void close() throws SQLException;

	/**
	 * Get transaction timeout if set
	 * 
	 * @throws SQLException
	 */
	Integer getTimeout() throws SQLException;

}
