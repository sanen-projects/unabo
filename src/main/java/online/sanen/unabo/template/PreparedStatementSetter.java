package online.sanen.unabo.template;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * 
 * <pre>
 *
 * @author online.sanen
 * Date:2018年10月14日
 * Time:下午6:58:53
 * </pre>
 */
public interface PreparedStatementSetter {

	/**
	 * Set parameter values on the given PreparedStatement.
	 * @param ps the PreparedStatement to invoke setter methods on
	 * @throws SQLException if a SQLException is encountered
	 * (i.e. there is no need to catch SQLException)
	 */
	void setValues(PreparedStatement ps) throws SQLException;

}
