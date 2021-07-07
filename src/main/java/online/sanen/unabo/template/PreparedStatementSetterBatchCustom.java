package online.sanen.unabo.template;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Parameterized callback interface used by the {@link SqlTemplate} class for
 * batch updates.
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
 * Time:下午1:59:23
 * </pre>
 */
public interface PreparedStatementSetterBatchCustom<T> {

	/**
	 * Set parameter values on the given PreparedStatement.
	 * @param ps
	 * @param argument
	 * @throws SQLException
	 */
	void setValues(PreparedStatement ps, T argument) throws SQLException;

}
