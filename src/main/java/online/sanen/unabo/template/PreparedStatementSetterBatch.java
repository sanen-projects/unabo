package online.sanen.unabo.template;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Batch update callback interface used by the {@link SqlTemplate} class.
 *
 * <p>This interface sets values on a {@link java.sql.PreparedStatement} provided
 * by the JdbcTemplate class, for each of a number of updates in a batch using the
 * same SQL. Implementations are responsible for setting any necessary parameters.
 * SQL with placeholders will already have been supplied.
 *
 * <p>Implementations <i>do not</i> need to concern themselves with SQLExceptions
 * that may be thrown from operations they attempt. The JdbcTemplate class will
 * catch and handle SQLExceptions appropriately.
 * <pre>
 *
 * @author online.sanen
 * Date:2018年11月10日
 * Time:下午1:54:08
 * </pre>
 */
public interface PreparedStatementSetterBatch {

	/**
	 * Set parameter values on the given PreparedStatement.
	 * @param ps the PreparedStatement to invoke setter methods on
	 * @param i index of the statement we're issuing in the batch, starting from 0
	 * @throws SQLException if a SQLException is encountered
	 * (i.e. there is no need to catch SQLException)
	 */
	void setValues(PreparedStatement ps, int i) throws SQLException;

	/**
	 * Return the size of the batch.
	 * @return the number of statements in the batch
	 */
	int getBatchSize();

}
