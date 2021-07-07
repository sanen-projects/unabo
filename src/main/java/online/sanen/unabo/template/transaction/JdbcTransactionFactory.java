package online.sanen.unabo.template.transaction;

import java.sql.Connection;

import javax.sql.DataSource;

import com.mhdt.degist.Properties;

/**
 * 
 *
 * @author LazyToShow <br>
 *         Date: Nov 3, 2018 <br>
 *         Time: 10:48:36 AM
 */
public class JdbcTransactionFactory implements TransactionFactory {

	@Override
	public void setProperties(Properties props) {

	}

	public Transaction newTransaction(Connection conn) {
		return new JdbcTransaction(conn);
	}

	@Override
	public Transaction newTransaction(DataSource ds, TransactionIsolationLevel level, boolean autoCommit) {

		if (autoCommit)
			return null;

		return new JdbcTransaction(ds, level, autoCommit);
	}
}
