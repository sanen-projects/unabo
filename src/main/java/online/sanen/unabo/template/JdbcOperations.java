package online.sanen.unabo.template;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * The interface specifies the basic set of Jdbc operations. Implemented by
 * {@link SqlTemplate}.
 *
 * @author LazyToShow <br>
 *         Date: 2018年10月14日 <br>
 *         Time: 下午2:12:38
 */
public interface JdbcOperations {

	// For object

	Map<String, Object> queryForMap(String sql) throws DataAccessException;

	Map<String, Object> queryForMap(String sql, Object... args) throws DataAccessException;

	<T> T queryForObject(String sql, RowExtractor<T> rowMapper) throws DataAccessException;

	<T> T queryForObject(String sql, Object[] args, RowExtractor<T> rowMapper) throws DataAccessException;

	<T> T queryForObject(String sql, Object[] args, Class<T> requiredType) throws DataAccessException;

	// For list

	List<Map<String, Object>> queryForList(String sql) throws DataAccessException;

	List<Map<String, Object>> queryForList(String sql, Object... args) throws DataAccessException;

	<T> List<T> queryForList(String sql, Class<T> elementType, Object... args) throws DataAccessException;

	// For Entry

	<T> T queryForEntry(Class<T> entryType, String sql, Object... args) throws DataAccessException;

	<T> List<T> queryForEntries(Class<T> entryType, String sql, Object... args) throws DataAccessException;

	// For SqlRowSet

	SqlRowSet queryForRowSet(String sql, Object... args) throws DataAccessException;

	// Update

	int update(String sql, Object... args) throws DataAccessException;

	int update(String sql, PreparedStatementSetter pss) throws DataAccessException;

	int update(final PreparedStatementCreator psc, final KeyHolder generatedKeyHolder) throws DataAccessException;

	int[] batchUpdate(String sql, List<Object[]> batchArgs) throws DataAccessException;

	int[] batchUpdate(String sql, final PreparedStatementSetterBatch pss) throws DataAccessException;

	<T> int[][] batchUpdate(String sql, Collection<T> batchArgs, int batchSize,
			PreparedStatementSetterBatchCustom<T> pss) throws DataAccessException;

	// Query

	<T> T query(final String sql, final ResultSetExtractor<T> rse) throws DataAccessException;

	<T> T query(String sql, Object[] args, ResultSetExtractor<T> rse) throws DataAccessException;

	<T> T query(String sql, PreparedStatementSetter pss, ResultSetExtractor<T> rse) throws DataAccessException;

	<T> T query(PreparedStatementCreator psc, final PreparedStatementSetter pss, final ResultSetExtractor<T> rse)
			throws DataAccessException;

	<T> List<T> query(String sql, RowExtractor<T> rowMapper) throws DataAccessException;

	<T> List<T> query(String sql, Object[] args, RowExtractor<T> rowMapper) throws DataAccessException;

	// Execute

	void execute(final String sql) throws DataAccessException;

	<T> T execute(String sql, PreparedStatementCallback<T> action) throws DataAccessException;

	<T> T execute(StatementCallback<T> action) throws DataAccessException;

	<T> T execute(PreparedStatementCreator psc, PreparedStatementCallback<T> action) throws DataAccessException;

}
