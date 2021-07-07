package online.sanen.unabo.template;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * 
 * <pre>
 *
 * @author online.sanen
 * Date:2018年10月14日
 * Time:下午6:54:32
 * </pre>
 */
public interface PreparedStatementCreator {
	
	/**
	 * Create a statement in this connection. Allows implementations to use
	 * PreparedStatements. The JdbcTemplate will close the created statement.
	 * @param con Connection to use to create statement
	 * @return a prepared statement
	 * @throws SQLException there is no need to catch SQLExceptions
	 * that may be thrown in the implementation of this method.
	 * The JdbcTemplate class will handle them.
	 */
	PreparedStatement createPreparedStatement(Connection con) throws SQLException;

}
