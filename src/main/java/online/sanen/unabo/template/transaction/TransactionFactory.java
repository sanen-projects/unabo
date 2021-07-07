package online.sanen.unabo.template.transaction;

import java.sql.Connection;

import javax.sql.DataSource;

import com.mhdt.degist.Properties;

/**
 *
 * @author LazyToShow <br>
 *         Date: Nov 3, 2018 <br>
 *         Time: 10:48:46 AM
 */
public interface TransactionFactory {

	/**
	 * Sets transaction factory custom properties.
	 * 
	 * @param props
	 */
	void setProperties(Properties props);

	/**
	 * Creates a {@link Transaction} out of an existing connection.
	 * 
	 * @param conn Existing database connection
	 * @return Transaction
	 * @since 3.1.0
	 */
	Transaction newTransaction(Connection conn);

	/**
	 * Creates a {@link Transaction} out of a datasource.
	 * 
	 * @param dataSource DataSource to take the connection from
	 * @param level      Desired isolation level
	 * @param autoCommit Desired autocommit
	 * @return Transaction
	 * @since 3.1.0
	 */
	Transaction newTransaction(DataSource dataSource, TransactionIsolationLevel level, boolean autoCommit);
}
