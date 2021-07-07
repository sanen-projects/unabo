package online.sanen.unabo.template;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.sql.DataSource;

import online.sanen.unabo.template.transaction.Transaction;
import online.sanen.unabo.template.transaction.TransactionFactory;
import online.sanen.unabo.template.transaction.TransactionIsolationLevel;

/**
 * 
 *
 * @author LazyToShow <br>
 *         Date: Nov 5, 2018 <br>
 *         Time: 6:26:31 PM
 */
public class TransactionManager {

	private static Map<DataSource, ThreadLocal<Transaction>> threadLcoal = new LinkedHashMap<>();

	

	public static synchronized Transaction getTransaction(TransactionFactory factory,DataSource dataSource, TransactionIsolationLevel level,
			boolean flag) {
		

		if (threadLcoal.get(dataSource) == null)
			threadLcoal.put(dataSource, new ThreadLocal<Transaction>());

		Transaction transaction = threadLcoal.get(dataSource).get();

		if (transaction == null && factory != null) {
			transaction = factory.newTransaction(dataSource, level, flag);
			threadLcoal.get(dataSource).set(transaction);
		}

		return transaction;
	}

	public static void closeSqlTransaction(DataSource dataSource) throws SQLException {

		Transaction transaction = threadLcoal.get(dataSource).get();
		if (transaction != null) {
			transaction.close();
			threadLcoal.get(dataSource).remove();
		}

	}

}
