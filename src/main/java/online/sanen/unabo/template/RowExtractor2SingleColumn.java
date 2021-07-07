package online.sanen.unabo.template;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import com.mhdt.toolkit.NumberUtilly;

public class RowExtractor2SingleColumn<T> implements RowExtractor<T> {
	
	private Class<?> requiredType;

	public RowExtractor2SingleColumn(Class<T> requiredType) {
		this.requiredType = requiredType;
	}

	@SuppressWarnings("unchecked")
	public T mapRow(ResultSet rs, int rowNum) throws SQLException {
		// Validate column count.
				ResultSetMetaData rsmd = rs.getMetaData();
				int nrOfColumns = rsmd.getColumnCount();
				if (nrOfColumns != 1) {
					throw new SQLException("The column value of the query should be 1, but it is actually :"+nrOfColumns);
				}

				// Extract column value from JDBC ResultSet.
				Object result = getColumnValue(rs, 1, this.requiredType);
				if (result != null && this.requiredType != null && !this.requiredType.isInstance(result)) {
					// Extracted value does not match already: try to convert it.
					try {
						return (T) convertValueToRequiredType(result, this.requiredType);
					}catch (IllegalArgumentException ex) {
						throw new SQLException(
								"Type mismatch affecting row number " + rowNum + " and column type '" +
								rsmd.getColumnTypeName(1) + "': " + ex.getMessage());
					}
				}
				
				return (T) result;
	}
	
	protected Object getColumnValue(ResultSet rs, int index, Class<?> requiredType) throws SQLException {
		if (requiredType != null) {
			return JdbcUtils.getResultSetValue(rs, index, requiredType);
		}
		else {
			// No required type specified -> perform default extraction.
			return getColumnValue(rs, index);
		}
	}
	
	protected Object getColumnValue(ResultSet rs, int index) throws SQLException {
		return JdbcUtils.getResultSetValue(rs, index);
	}
	
	@SuppressWarnings("unchecked")
	protected Object convertValueToRequiredType(Object value, Class<?> requiredType) {
		if (String.class == requiredType) {
			return value.toString();
		}
		else if (Number.class.isAssignableFrom(requiredType)) {
			if (value instanceof Number) {
				// Convert original Number to target Number class.
				return NumberUtilly.convertNumberToTargetClass(((Number) value), (Class<Number>) requiredType);
			}
			else {
				// Convert stringified value to target Number class.
				return NumberUtilly.parseNumber(value.toString(),(Class<Number>) requiredType);
			}
		}
		else {
			throw new IllegalArgumentException(
					"Value [" + value + "] is of type [" + value.getClass().getName() +
					"] and cannot be converted to required type [" + requiredType.getName() + "]");
		}
	}

}
