package online.sanen.unabo.template;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.mhdt.toolkit.Assert;

/**
 * Adapter implementation of the ResultSetExtractor interface that delegates to
 * a RowMapper which is supposed to create an object for each row. Each object
 * is added to the results List of this ResultSetExtractor.
 *
 * <p>
 * Useful for the typical case of one object per row in the database table. The
 * number of entries in the results list will match the number of rows.
 *
 * <p>
 * Note that a RowMapper object is typically stateless and thus reusable; just
 * the RowMapperResultSetExtractor adapter is stateful.
 *
 * <p>
 * A usage example with JdbcTemplate:
 *
 * <pre class="code">
 * JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource); // reusable object
 * RowMapper rowMapper = new UserRowMapper(); // reusable object
 *
 * List allUsers = (List) jdbcTemplate.query("select * from user", new RowMapperResultSetExtractor(rowMapper, 10));
 *
 * User user = (User) jdbcTemplate.queryForObject("select * from user where id=?", new Object[] { id },
 * 		new RowMapperResultSetExtractor(rowMapper, 1));
 * </pre>
 *
 * <p>
 * Alternatively, consider subclassing MappingSqlQuery from the
 * {@code jdbc.object} package: Instead of working with separate JdbcTemplate
 * and RowMapper objects, you can have executable query objects (containing
 * row-mapping logic) there.
 * 
 * @author LazyToShow <br>
 *         Date: 2018年10月14日 <br>
 *         Time: 下午2:33:10
 */
public class ResultSetExtractor2List<T> implements ResultSetExtractor<List<T>> {

	private final RowExtractor<T> rowMapper;

	private final int rowsExpected;

	/**
	 * Create a new RowMapperResultSetExtractor.
	 * 
	 * @param rowMapper the RowMapper which creates an object for each row
	 */
	public ResultSetExtractor2List(RowExtractor<T> rowMapper) {
		this(rowMapper, 0);
	}

	/**
	 * Create a new RowMapperResultSetExtractor.
	 * 
	 * @param rowMapper    the RowMapper which creates an object for each row
	 * @param rowsExpected the number of expected rows (just used for optimized
	 *                     collection handling)
	 */
	public ResultSetExtractor2List(RowExtractor<T> rowMapper, int rowsExpected) {
		Assert.notNull(rowMapper, "RowMapper is required");
		this.rowMapper = rowMapper;
		this.rowsExpected = rowsExpected;
	}

	public List<T> extractData(ResultSet rs) throws SQLException {
		
		List<T> results = (this.rowsExpected > 0 ? new ArrayList<T>(this.rowsExpected) : new ArrayList<T>());
		int rowNum = 0;
		while (rs.next()) {
			results.add(this.rowMapper.mapRow(rs, rowNum++));
		}
		return results;
	}

}
