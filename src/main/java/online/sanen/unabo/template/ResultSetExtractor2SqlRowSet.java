package online.sanen.unabo.template;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;

/**
 * Create a SqlRowSet that wraps the given ResultSet, representing its data in a
 * disconnected fashion.
 * <p>
 * This implementation creates a Spring ResultSetWrappingSqlRowSet instance that
 * wraps a standard JDBC CachedRowSet instance. Can be overridden to use a
 * different implementation.
 * 
 * <pre>
 *
 * &#64;author online.sanen
 * Date:2018年11月10日
 * Time:下午1:55:00
 * </pre>
 */
public class ResultSetExtractor2SqlRowSet implements ResultSetExtractor<SqlRowSet> {

	private static final CachedRowSetFactory cachedRowSetFactory;

	static {
		cachedRowSetFactory = new StandardCachedRowSetFactory();
	}

	public SqlRowSet extractData(ResultSet rs) throws SQLException {
		return createSqlRowSet(rs);
	}

	/**
	 * Create a SqlRowSet that wraps the given ResultSet, representing its data in a
	 * disconnected fashion.
	 * <p>
	 * This implementation creates a Spring ResultSetWrappingSqlRowSet instance that
	 * wraps a standard JDBC CachedRowSet instance. Can be overridden to use a
	 * different implementation.
	 * 
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	protected SqlRowSet createSqlRowSet(ResultSet rs) throws SQLException {
		CachedRowSet rowSet = newCachedRowSet();
		rowSet.populate(rs);
		return new SqlRowSetWrapp(rowSet);
	}

	/**
	 * Create a new CachedRowSet instance, to be populated by the
	 * {@code createSqlRowSet} implementation.
	 * <p>
	 * The default implementation uses JDBC 4.1's RowSetProvider when running on JDK
	 * 7 or higher, falling back to Sun's {@code com.sun.rowset.CachedRowSetImpl}
	 * class on older JDKs.
	 * 
	 * @return a new CachedRowSet instance
	 * @throws SQLException
	 *             if thrown by JDBC methods
	 * @see #createSqlRowSet
	 */
	protected CachedRowSet newCachedRowSet() throws SQLException {
		return cachedRowSetFactory.createCachedRowSet();
	}

	/**
	 * Internal strategy interface for the creation of CachedRowSet instances.
	 */
	private interface CachedRowSetFactory {

		CachedRowSet createCachedRowSet() throws SQLException;
	}

	/**
	 * Inner class to avoid a hard dependency on JDBC 4.1 RowSetProvider class.
	 */
	private static class StandardCachedRowSetFactory implements CachedRowSetFactory {

		private final RowSetFactory rowSetFactory;

		public StandardCachedRowSetFactory() {
			try {
				this.rowSetFactory = RowSetProvider.newFactory();
			} catch (SQLException ex) {
				throw new IllegalStateException("Cannot create RowSetFactory through RowSetProvider", ex);
			}
		}

		public CachedRowSet createCachedRowSet() throws SQLException {
			return this.rowSetFactory.createCachedRowSet();
		}
	}

}
