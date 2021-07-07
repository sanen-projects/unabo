package online.sanen.unabo.extend.spring.transaction;
import java.sql.Connection;

import javax.sql.DataSource;

import com.mhdt.degist.Properties;

import online.sanen.unabo.template.transaction.Transaction;
import online.sanen.unabo.template.transaction.TransactionFactory;
import online.sanen.unabo.template.transaction.TransactionIsolationLevel;


/**
 * 
 *
 * @author LazyToShow	<br>
 * Date:	Nov 5, 2018	<br>
 * Time:	12:23:56 PM
 */
/**
 * Creates a {@code SpringManagedTransaction}.
 *
 */
public class SpringManagedTransactionFactory implements TransactionFactory {

  /**
   * {@inheritDoc}
   */
  @Override
  public Transaction newTransaction(DataSource dataSource, TransactionIsolationLevel level, boolean autoCommit) {
    return new SpringManagedTransaction(dataSource);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Transaction newTransaction(Connection conn) {
    throw new UnsupportedOperationException("New Spring transactions require a DataSource");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setProperties(Properties props) {
    // not needed in this version
  }

}
