package online.sanen.unabo.template;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import com.mhdt.toolkit.Assert;

import lombok.extern.slf4j.Slf4j;
import online.sanen.unabo.template.transaction.Transaction;
import online.sanen.unabo.template.transaction.TransactionFactory;

/**
 * 
 *
 * @author LazyToShow <br>
 *         Date: 2018年10月14日 <br>
 *         Time: 上午11:29:25
 */
@Slf4j
public class SqlTemplate implements JdbcOperations {

	private DataSource dataSource;

	private int fetchSize = -1;

	private int maxRows = -1;

	private int queryTimeout = -1;

	public SqlTemplate(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public int update(String sql, Object... args) {
		return update(sql, newArgPreparedStatementSetter(args));
	}

	public int update(String sql, PreparedStatementSetter pss) throws DataAccessException {
		return update(new SimplePreparedStatementCreator(sql), pss);
	}

	protected int update(final PreparedStatementCreator psc, final PreparedStatementSetter pss)
			throws DataAccessException {

		log.debug("Executing prepared SQL update");

		return execute(psc, new PreparedStatementCallback<Integer>() {

			public Integer doInPreparedStatement(PreparedStatement ps) throws SQLException {
				if (pss != null) {
					pss.setValues(ps);
				}
				int rows = ps.executeUpdate();
				if (log.isDebugEnabled()) {
					log.debug("SQL update affected {} rows", rows);
				}
				return rows;
			}
		});
	}

	public List<Map<String, Object>> queryForList(String sql) {
		return query(sql, getColumnMapRowMapper());
	}

	public List<Map<String, Object>> queryForList(String sql, Object... args) throws DataAccessException {
		return query(sql, args, getColumnMapRowMapper());
	}

	public List<Map<String, Object>> queryForList(String sql, Map<String, String> alias, Object... args)
			throws DataAccessException {
		return query(sql, args, getColumnMapRowMapper(alias));
	}

	public <T> List<T> queryForList(String sql, Class<T> elementType, Object... args) {
		return query(sql, args, getSingleColumnRowMapper(elementType));
	}

	private <T> RowExtractor<T> getEntryRowMapper(Class<T> entryType) {
		return new RowExtractor2Entry<T>(entryType);
	}

	protected <T> RowExtractor<T> getSingleColumnRowMapper(Class<T> requiredType) {
		return new RowExtractor2SingleColumn<T>(requiredType);
	}

	protected RowExtractor<Map<String, Object>> getColumnMapRowMapper() {
		return new RowExtractor2Map();
	}

	private RowExtractor<Map<String, Object>> getColumnMapRowMapper(Map<String, String> alias) {
		return new RowExtractor2Map(alias);
	}

	public Map<String, Object> queryForMap(String sql) {
		return queryForObject(sql, getColumnMapRowMapper());
	}
	
	public Map<String, Object> queryForMap(String sql,Map<String,String> alias) {
		return queryForObject(sql, getColumnMapRowMapper(alias));
	}

	public Map<String, Object> queryForMap(String sql, Object... args) {
		return queryForObject(sql, args, getColumnMapRowMapper());
	}
	
	public Map<String, Object> queryForMap(String sql, Map<String,String> alias,Object... args) {
		return queryForObject(sql, args, getColumnMapRowMapper(alias));
	}

	public <T> T queryForObject(String sql, RowExtractor<T> rowMapper) {
		List<T> results = query(sql, rowMapper);
		return DataAccessUtils.requiredSingleResult(results);
	}

	@Override
	public <T> List<T> queryForEntries(Class<T> entryType, String sql, Object... args) throws DataAccessException {
		return query(sql, args, getEntryRowMapper(entryType));
	}

	@Override
	public <T> T queryForEntry(Class<T> entryType, String sql, Object... args) throws DataAccessException {
		List<T> results = query(sql, args, getEntryRowMapper(entryType));
		return DataAccessUtils.requiredSingleResult(results);
	}

	public <T> T queryForObject(String sql, Object[] args, Class<T> requiredType) throws DataAccessException {
		return queryForObject(sql, args, getSingleColumnRowMapper(requiredType));
	}

	public <T> T queryForObject(String sql, Object[] args, RowExtractor<T> rowMapper) throws DataAccessException {
		List<T> results = query(sql, args, new ResultSetExtractor2List<T>(rowMapper, 1));
		return DataAccessUtils.requiredSingleResult(results);
	}

	public <T> T query(String sql, Object[] args, ResultSetExtractor<T> rse) throws DataAccessException {
		return query(sql, newArgPreparedStatementSetter(args), rse);
	}

	public <T> T query(String sql, PreparedStatementSetter pss, ResultSetExtractor<T> rse) throws DataAccessException {
		return query(new SimplePreparedStatementCreator(sql), pss, rse);
	}

	public <T> List<T> query(String sql, RowExtractor<T> rowMapper) throws DataAccessException {
		return query(sql, new ResultSetExtractor2List<T>(rowMapper));
	}

	public <T> T query(PreparedStatementCreator psc, final PreparedStatementSetter pss, final ResultSetExtractor<T> rse)
			throws DataAccessException {

		Assert.notNull(rse, "ResultSetExtractor must not be null");
		// logger.debug("Executing prepared SQL query");

		return execute(psc, new PreparedStatementCallback<T>() {

			public T doInPreparedStatement(PreparedStatement ps) throws SQLException {
				ResultSet rs = null;
				try {
					if (pss != null)
						pss.setValues(ps);

					rs = ps.executeQuery();

					return rse.extractData(rs);
				} finally {
					JdbcUtils.closeResultSet(rs);
				}
			}
		});
	}

	public <T> List<T> query(String sql, Object[] args, RowExtractor<T> rowMapper) throws DataAccessException {
		return query(sql, args, new ResultSetExtractor2List<T>(rowMapper));
	}

	public <T> T query(final String sql, final ResultSetExtractor<T> rse) throws DataAccessException {

		Assert.notNull(sql, "SQL must not be null");
		Assert.notNull(rse, "ResultSetExtractor must not be null");

		// if (logger.isDebugEnable()) {
		// logger.debug("Executing SQL query [" + sql + "]");
		// }

		class QueryStatementCallback implements StatementCallback<T>, SqlProvider {

			public T doInStatement(Statement stmt) throws SQLException {
				ResultSet rs = null;
				try {
					rs = stmt.executeQuery(sql);
					return rse.extractData(rs);
				} finally {
					JdbcUtils.closeResultSet(rs);
				}
			}

			public String getSql() {
				return sql;
			}
		}

		return execute(new QueryStatementCallback());
	}

	protected PreparedStatementSetter newArgPreparedStatementSetter(Object[] args) {
		return new ArgumentPreparedStatementSetter(args);
	}

	public <T> T execute(StatementCallback<T> action) throws DataAccessException {

		Assert.notNull(action, "Callback object must not be null");

		Connection con = null;
		Statement stmt = null;

		try {
			con = DataSourceUtils.getConnection(getTransAction(), getDataSource());
			stmt = con.createStatement();
			applyStatementSettings(stmt);
			T result = action.doInStatement(stmt);
			return result;
		} catch (SQLException ex) {
			// Release Connection early, to avoid potential connection pool deadlock
			// in the case when the exception translator hasn't been initialized yet.
			JdbcUtils.closeStatement(stmt);
			stmt = null;
			DataSourceUtils.releaseConnection(getTransAction(), con);
			con = null;
			throw new DataAccessException("StatementCallback:" + getSql(action), ex);
		} finally {
			JdbcUtils.closeStatement(stmt);
			DataSourceUtils.releaseConnection(getTransAction(), con);
		}

	}

	/**
	 * Simple adapter for PreparedStatementCreator, allowing to use a plain SQL
	 * statement.
	 */
	private static class SimplePreparedStatementCreator implements PreparedStatementCreator, SqlProvider {

		private final String sql;

		public SimplePreparedStatementCreator(String sql) {
			Assert.notNull(sql, "SQL must not be null");
			this.sql = sql;
		}

		public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
			return con.prepareStatement(this.sql);
		}

		public String getSql() {
			return this.sql;
		}
	}

	protected void applyStatementSettings(Statement stmt) throws SQLException {
		int fetchSize = getFetchSize();

		if (fetchSize != -1) {
			stmt.setFetchSize(fetchSize);
		}
		int maxRows = getMaxRows();
		if (maxRows != -1) {
			stmt.setMaxRows(maxRows);
		}

		DataSourceUtils.applyTimeout(stmt, getDataSource(), getQueryTimeout());
	}

	private static String getSql(Object sqlProvider) {
		if (sqlProvider instanceof SqlProvider) {
			return ((SqlProvider) sqlProvider).getSql();
		} else {
			return "";
		}
	}

	public int getFetchSize() {
		return fetchSize;
	}

	public void setFetchSize(int fetchSize) {
		this.fetchSize = fetchSize;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public int getMaxRows() {
		return maxRows;
	}

	public void setMaxRows(int maxRows) {
		this.maxRows = maxRows;
	}

	public int getQueryTimeout() {
		return queryTimeout;
	}

	public void setQueryTimeout(int queryTimeout) {
		this.queryTimeout = queryTimeout;
	}

	public int[] batchUpdate(String sql, final PreparedStatementSetterBatch pss) throws DataAccessException {

		if (log.isDebugEnabled()) {
			log.debug("Executing SQL batch update [" + sql + "]");
		}

		return execute(sql, new PreparedStatementCallback<int[]>() {

			public int[] doInPreparedStatement(PreparedStatement ps) throws SQLException {

				int batchSize = pss.getBatchSize();
				
				if (JdbcUtils.supportsBatchUpdates(ps.getConnection())) {

					for (int i = 0; i < batchSize; i++) {
						pss.setValues(ps, i);
						ps.addBatch();
					}
					return ps.executeBatch();

				} else {

					List<Integer> rowsAffected = new ArrayList<Integer>();

					for (int i = 0; i < batchSize; i++) {
						pss.setValues(ps, i);
						rowsAffected.add(ps.executeUpdate());
					}

					int[] rowsAffectedArray = new int[rowsAffected.size()];

					for (int i = 0; i < rowsAffectedArray.length; i++) {
						rowsAffectedArray[i] = rowsAffected.get(i);
					}

					return rowsAffectedArray;
				}
			}
		});
	}

	public int[] batchUpdate(String sql, List<Object[]> batchArgs) throws DataAccessException {
		return BatchUpdateUtils.executeBatchUpdate(sql, batchArgs, this);
	}

	public <T> int[][] batchUpdate(String sql, final Collection<T> batchArgs, final int batchSize,
			final PreparedStatementSetterBatchCustom<T> pss) throws DataAccessException {

		if (log.isDebugEnabled()) {
			log.debug("Executing SQL batch update [" + sql + "] with a batch size of " + batchSize);
		}

		return execute(sql, new PreparedStatementCallback<int[][]>() {
			public int[][] doInPreparedStatement(PreparedStatement ps) throws SQLException {
				List<int[]> rowsAffected = new ArrayList<int[]>();
				boolean batchSupported = true;
				if (!JdbcUtils.supportsBatchUpdates(ps.getConnection())) {
					batchSupported = false;
					log.warn("JDBC Driver does not support Batch updates; resorting to single statement execution");
				}
				int n = 0;
				for (T obj : batchArgs) {
					pss.setValues(ps, obj);
					n++;
					if (batchSupported) {
						ps.addBatch();
						if (n % batchSize == 0 || n == batchArgs.size()) {
							if (log.isDebugEnabled()) {
								int batchIdx = (n % batchSize == 0) ? n / batchSize : (n / batchSize) + 1;
								int items = n
										- ((n % batchSize == 0) ? n / batchSize - 1 : (n / batchSize)) * batchSize;
								log.debug("Sending SQL batch update #" + batchIdx + " with " + items + "items");
							}
							rowsAffected.add(ps.executeBatch());
						}
					} else {
						int i = ps.executeUpdate();
						rowsAffected.add(new int[] { i });
					}
				}
				int[][] result = new int[rowsAffected.size()][];
				for (int i = 0; i < result.length; i++) {
					result[i] = rowsAffected.get(i);
				}
				return result;
			}
		});
	}

	public <T> T execute(String sql, PreparedStatementCallback<T> action) throws DataAccessException {
		return execute(new SimplePreparedStatementCreator(sql), action);
	}

	public <T> T execute(PreparedStatementCreator psc, PreparedStatementCallback<T> action) throws DataAccessException {

		Assert.notNull(psc, "PreparedStatementCreator must not be null");
		Assert.notNull(action, "Callback object must not be null");
		if (log.isDebugEnabled()) {
			String sql = getSql(psc);
			log.debug("Executing prepared SQL statement [{}]", sql != null ? sql : "");
		}

		Connection con = null;
		PreparedStatement ps = null;

		try {
			con = DataSourceUtils.getConnection(getTransAction(), getDataSource());
			Connection conToUse = con;
			ps = psc.createPreparedStatement(conToUse);
			applyStatementSettings(ps);
			PreparedStatement psToUse = ps;
			T result = action.doInPreparedStatement(psToUse);
			return result;
		} catch (SQLException ex) {
			String sql = getSql(psc);
			psc = null;
			JdbcUtils.closeStatement(ps);
			ps = null;
			DataSourceUtils.releaseConnection(getTransAction(), con);
			con = null;
			
			if(ex.getMessage().contains("Duplicate entry")) {
				throw new DuplicateException(String.format("PreparedStatementCallback %s sql:%s", ex.getMessage(), sql),ex);
			}else {
				throw new DataAccessException(String.format("PreparedStatementCallback %s sql:%s", ex.getMessage(), sql),ex);
			}
			
			
		} finally {
			JdbcUtils.closeStatement(ps);
			DataSourceUtils.releaseConnection(getTransAction(), con);
		}
	}

	public SqlRowSet queryForRowSet(String sql, Object... args) throws DataAccessException {
		return query(sql, args, new ResultSetExtractor2SqlRowSet());
	}

	public void execute(final String sql) throws DataAccessException {

		if (log.isDebugEnabled()) {
			log.debug("Executing SQL statement [" + sql + "]");
		}

		class ExecuteStatementCallback implements StatementCallback<Object>, SqlProvider {
			public Object doInStatement(Statement stmt) throws SQLException {
				stmt.execute(sql);
				return null;
			}

			public String getSql() {
				return sql;
			}
		}

		execute(new ExecuteStatementCallback());
	}

	public int update(PreparedStatementCreator psc, final KeyHolder generatedKeyHolder) throws DataAccessException {

		Assert.notNull(generatedKeyHolder, "KeyHolder must not be null");
		log.debug("Executing SQL update and returning generated keys");

		return execute(psc, new PreparedStatementCallback<Integer>() {

			public Integer doInPreparedStatement(PreparedStatement ps) throws SQLException {
				int rows = ps.executeUpdate();
				List<Map<String, Object>> generatedKeys = generatedKeyHolder.getKeyList();
				generatedKeys.clear();

				ResultSet keys = ps.getGeneratedKeys();

				if (keys != null) {
					try {

						ResultSetExtractor2List<Map<String, Object>> rse = new ResultSetExtractor2List<Map<String, Object>>(
								getColumnMapRowMapper(), 1);

						generatedKeys.addAll(rse.extractData(keys));

					} finally {
						JdbcUtils.closeResultSet(keys);
					}
				}
				if (log.isDebugEnabled()) {
					log.debug("SQL update affected " + rows + " rows and returned " + generatedKeys.size() + " keys");
				}

				return rows;
			}
		});
	}

	boolean isOpenSession;
	
	private  TransactionFactory transactionFactory;

	public void bindTransaction(TransactionFactory factory) {
		this.transactionFactory = factory;
	}

	boolean isAutoCommit = true;

	public void openSession() {
		this.isAutoCommit = false;
		TransactionManager.getTransaction(this.transactionFactory,dataSource, null, this.isAutoCommit);
	}

	public void commit() throws SQLException {

		Transaction transaction = TransactionManager.getTransaction(this.transactionFactory,dataSource, null, isAutoCommit);
		Assert.notNull(transaction, "transaction is null");
		transaction.commit();
		TransactionManager.closeSqlTransaction(getDataSource());
		isAutoCommit = true;
	}

	public void rollback() throws SQLException {
		Transaction transaction = TransactionManager.getTransaction(this.transactionFactory,dataSource, null, isAutoCommit);
		Assert.notNull(transaction, "transaction is null");
		transaction.rollback();
		TransactionManager.closeSqlTransaction(getDataSource());
		isAutoCommit = true;
	}

	private Transaction getTransAction() {

		Transaction transaction = TransactionManager.getTransaction(this.transactionFactory,dataSource, null, isAutoCommit);
		return transaction;
	}

}
