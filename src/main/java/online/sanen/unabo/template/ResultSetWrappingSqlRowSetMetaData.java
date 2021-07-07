package online.sanen.unabo.template;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;


/**
 * 
 *
 * @author LazyToShow	<br>
 * Date:	2018年10月15日	<br>
 * Time:	上午10:57:41
 */
public class ResultSetWrappingSqlRowSetMetaData implements SqlRowSetMetaData {

	private final ResultSetMetaData resultSetMetaData;

	private String[] columnNames;


	/**
	 * Create a new ResultSetWrappingSqlRowSetMetaData object
	 * for the given ResultSetMetaData instance.
	 * @param resultSetMetaData
	 */
	public ResultSetWrappingSqlRowSetMetaData(ResultSetMetaData resultSetMetaData) {
		this.resultSetMetaData = resultSetMetaData;
	}


	public String getCatalogName(int column) throws InvalidResultSetAccessException {
		try {
			return this.resultSetMetaData.getCatalogName(column);
		}
		catch (SQLException se) {
			throw new InvalidResultSetAccessException(se);
		}
	}

	public String getColumnClassName(int column) throws InvalidResultSetAccessException {
		try {
			return this.resultSetMetaData.getColumnClassName(column);
		}
		catch (SQLException se) {
			throw new InvalidResultSetAccessException(se);
		}
	}

	public int getColumnCount() throws InvalidResultSetAccessException {
		try {
			return this.resultSetMetaData.getColumnCount();
		}
		catch (SQLException se) {
			throw new InvalidResultSetAccessException(se);
		}
	}

	public String[] getColumnNames() throws InvalidResultSetAccessException {
		if (this.columnNames == null) {
			this.columnNames = new String[getColumnCount()];
			for (int i = 0; i < getColumnCount(); i++) {
				this.columnNames[i] = getColumnName(i + 1);
			}
		}
		return this.columnNames;
	}

	public int getColumnDisplaySize(int column) throws InvalidResultSetAccessException {
		try {
			return this.resultSetMetaData.getColumnDisplaySize(column);
		}
		catch (SQLException se) {
			throw new InvalidResultSetAccessException(se);
		}
	}

	public String getColumnLabel(int column) throws InvalidResultSetAccessException {
		try {
			return this.resultSetMetaData.getColumnLabel(column);
		}
		catch (SQLException se) {
			throw new InvalidResultSetAccessException(se);
		}
	}

	public String getColumnName(int column) throws InvalidResultSetAccessException {
		try {
			return this.resultSetMetaData.getColumnName(column);
		}
		catch (SQLException se) {
			throw new InvalidResultSetAccessException(se);
		}
	}

	public int getColumnType(int column) throws InvalidResultSetAccessException {
		try {
			return this.resultSetMetaData.getColumnType(column);
		}
		catch (SQLException se) {
			throw new InvalidResultSetAccessException(se);
		}
	}

	public String getColumnTypeName(int column) throws InvalidResultSetAccessException {
		try {
			return this.resultSetMetaData.getColumnTypeName(column);
		}
		catch (SQLException se) {
			throw new InvalidResultSetAccessException(se);
		}
	}

	public int getPrecision(int column) throws InvalidResultSetAccessException {
		try {
			return this.resultSetMetaData.getPrecision(column);
		}
		catch (SQLException se) {
			throw new InvalidResultSetAccessException(se);
		}
	}

	public int getScale(int column) throws InvalidResultSetAccessException {
		try {
			return this.resultSetMetaData.getScale(column);
		}
		catch (SQLException se) {
			throw new InvalidResultSetAccessException(se);
		}
	}

	public String getSchemaName(int column) throws InvalidResultSetAccessException {
		try {
			return this.resultSetMetaData.getSchemaName(column);
		}
		catch (SQLException se) {
			throw new InvalidResultSetAccessException(se);
		}
	}

	public String getTableName(int column) throws InvalidResultSetAccessException {
		try {
			return this.resultSetMetaData.getTableName(column);
		}
		catch (SQLException se) {
			throw new InvalidResultSetAccessException(se);
		}
	}

	public boolean isCaseSensitive(int column) throws InvalidResultSetAccessException {
		try {
			return this.resultSetMetaData.isCaseSensitive(column);
		}
		catch (SQLException se) {
			throw new InvalidResultSetAccessException(se);
		}
	}

	public boolean isCurrency(int column) throws InvalidResultSetAccessException {
		try {
			return this.resultSetMetaData.isCurrency(column);
		}
		catch (SQLException se) {
			throw new InvalidResultSetAccessException(se);
		}
	}

	public boolean isSigned(int column) throws InvalidResultSetAccessException {
		try {
			return this.resultSetMetaData.isSigned(column);
		}
		catch (SQLException se) {
			throw new InvalidResultSetAccessException(se);
		}
	}

}

