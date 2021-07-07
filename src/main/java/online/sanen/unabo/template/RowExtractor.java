package online.sanen.unabo.template;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * <pre>
 * JdbcTemplate用来映射java.sql行的接口。按行计算结果集。
 * 此接口的实现执行将每一行映射到结果对象的实际工作，但不需要担心异常处理。
 * SQLExceptions将被调用JdbcTemplate捕获和处理。
 * 通常用于JdbcTemplate的查询方法或存储过程的out参数。
 * RowMapper对象通常是无状态的，因此可以重用;它们是在单个位置实现行映射逻辑的理想选择。
 * </pre>
 * @author LazyToShow <br>
 *         Date: 2018年10月14日 <br>
 *         Time: 下午1:07:46
 */
public interface RowExtractor<T> {

	/**
	 * 实现必须实现此方法来映射ResultSet中的每一行数据。这个方法不应该调用ResultSet上的next();它只应该映射当前行的值。
	 * 
	 * @param rs     要映射的结果集(为当前行预初始化)
	 * @param rowNum 当前行数
	 * @return the result 对象(可以是{@code null})
	 * @throws SQLException 如果遇到获取列值的SQLException(即，不需要捕获SQLException)
	 */
	T mapRow(ResultSet rs, int rowNum) throws SQLException;

}
