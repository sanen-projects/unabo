package online.sanen.unabo.template;

import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * <p>
 * JdbcTemplate的查询方法使用的回调接口。
 * 这个接口的实现执行从java.sql中提取结果的实际工作。
 * 结果集，但不需要担心异常处理。SQLExceptions将被调用JdbcTemplate捕获和处理。
 * 
 * <p>
 * 这个接口主要在JDBC框架内部使用。
 * RowMapper通常是ResultSet处理的更简单的选择，每行映射一个结果对象，而不是整个ResultSet的一个结果对象。
 * 
 * <p>
 * 注意:与RowCallbackHandler不同的是，ResultSetExtractor对象通常是无状态的，因此是可重用的，
 * 只要它不访问有状态资源(例如流化LOB内容时的输出流)或在对象中保持结果状态。
 *
 *
 * @author LazyToShow <br>
 *         Date: 2018年10月14日 <br>
 *         Time: 下午2:24:01
 */
public interface ResultSetExtractor<T> {

	/**
	 * Implementations must implement this method to process the entire ResultSet.
	 * 
	 * @param rs ResultSet to extract data from. Implementations should not close
	 *           this: it will be closed by the calling JdbcTemplate.
	 * @return an arbitrary result object, or {@code null} if none (the extractor
	 *         will typically be stateful in the latter case).
	 * @throws SQLException        if a SQLException is encountered getting column
	 *                             values or navigating (that is, there's no need to
	 *                             catch SQLException)
	 * @throws DataAccessException in case of custom exceptions
	 */
	T extractData(ResultSet rs) throws SQLException, DataAccessException;

}
