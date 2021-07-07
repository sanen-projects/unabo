package online.sanen.unabo.template;

import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mhdt.degist.Validate;
import com.mhdt.toolkit.NumberUtilly;

/**
 * 
 *
 * @author LazyToShow <br>
 *         Date: 2018年10月14日 <br>
 *         Time: 下午2:02:54
 */
public class JdbcUtils {
	
	

	private static final Logger logger =  LoggerFactory.getLogger(JdbcUtils.class);

	// Check for JDBC 4.1 getObject(int, Class) method - available on JDK 7 and
	// higher
	private static final boolean getObjectWithTypeAvailable = Validate.hasMethod(ResultSet.class, "getObject",
			int.class, Class.class);

	/**
	 * 确定要使用的列名。列名是根据使用ResultSetMetaData的查找确定的。 这个方法实现考虑了JDBC 4.0规范中最近的澄清:
	 * columnLabel——用SQL AS子句指定的列的标签。如果没有指定SQL AS子句，那么标签就是列的名称。
	 * 
	 * @param rsmd        当前要使用的元数据
	 * @param columnIndex 查找列的索引
	 * @return 要使用的列名
	 * @throws SQLException 如果查找失败
	 */
	public static String lookupColumnName(ResultSetMetaData rsmd, int columnIndex) throws SQLException {

		String name = rsmd.getColumnLabel(columnIndex);
		if (name == null || name.length() < 1)
			name = rsmd.getColumnName(columnIndex);

		return name;
	}

	public static Object getResultSetValue(ResultSet rs, int index) throws SQLException {
		Object obj = rs.getObject(index);
		String className = null;
		
		
		if (obj != null) {
			className = obj.getClass().getName();
		}
		
		if (obj instanceof Blob) {
			Blob blob = (Blob) obj;
			obj = blob.getBytes(1, (int) blob.length());
		} else if (obj instanceof Clob) {
			Clob clob = (Clob) obj;
			obj = clob.getSubString(1, (int) clob.length());
		} else if ("oracle.sql.TIMESTAMP".equals(className) || "oracle.sql.TIMESTAMPTZ".equals(className)) {
			obj = rs.getTimestamp(index);
		} else if (className != null && className.startsWith("oracle.sql.DATE")) {
			String metaDataClassName = rs.getMetaData().getColumnClassName(index);
			if ("java.sql.Timestamp".equals(metaDataClassName) || "oracle.sql.TIMESTAMP".equals(metaDataClassName)) {
				obj = rs.getTimestamp(index);
			} else {
				obj = rs.getDate(index);
			}
		} else if (obj instanceof java.sql.Date) {
			
			if ("java.sql.Timestamp".equals(rs.getMetaData().getColumnClassName(index))) {
				obj = rs.getTimestamp(index);
			}
		}else if(className!=null && className.equals("[B")) {
			InputStream binaryStream = rs.getBinaryStream(index);
			obj = binaryStream;
		}
		
		return obj;
	}
	
	

	public static Object getResultSetValue(ResultSet rs, int index, Class<?> requiredType) throws SQLException {
		if (requiredType == null) {
			return getResultSetValue(rs, index);
		}

		Object value;

		// Explicitly extract typed value, as far as possible.
		if (String.class == requiredType) {
			return rs.getString(index);
		} else if (boolean.class == requiredType || Boolean.class == requiredType) {
			value = rs.getBoolean(index);
		} else if (byte.class == requiredType || Byte.class == requiredType) {
			value = rs.getByte(index);
		} else if (short.class == requiredType || Short.class == requiredType) {
			value = rs.getShort(index);
		} else if (int.class == requiredType || Integer.class == requiredType) {
			value = rs.getInt(index);
		} else if (long.class == requiredType || Long.class == requiredType) {
			value = rs.getLong(index);
		} else if (float.class == requiredType || Float.class == requiredType) {
			value = rs.getFloat(index);
		} else if (double.class == requiredType || Double.class == requiredType || Number.class == requiredType) {
			value = rs.getDouble(index);
		} else if (BigDecimal.class == requiredType) {
			return rs.getBigDecimal(index);
		} else if (java.sql.Date.class == requiredType) {
			return rs.getDate(index);
		} else if (java.sql.Time.class == requiredType) {
			return rs.getTime(index);
		} else if (java.sql.Timestamp.class == requiredType || java.util.Date.class == requiredType) {
			return rs.getTimestamp(index);
		} else if (byte[].class == requiredType) {
			return rs.getBytes(index);
		} else if (Blob.class == requiredType) {
			return rs.getBlob(index);
		} else if (Clob.class == requiredType) {
			return rs.getClob(index);
		} else if (requiredType.isEnum()) {
			// Enums can either be represented through a String or an enum index value:
			// leave enum type conversion up to the caller (e.g. a ConversionService)
			// but make sure that we return nothing other than a String or an Integer.
			Object obj = rs.getObject(index);
			if (obj instanceof String) {
				return obj;
			} else if (obj instanceof Number) {
				// Defensively convert any Number to an Integer (as needed by our
				// ConversionService's IntegerToEnumConverterFactory) for use as index
				return NumberUtilly.convertNumberToTargetClass((Number) obj, Integer.class);
			} else {
				// e.g. on Postgres: getObject returns a PGObject but we need a String
				return rs.getString(index);
			}
		}

		else {
			// Some unknown type desired -> rely on getObject.
			if (getObjectWithTypeAvailable) {
				try {
					return rs.getObject(index, requiredType);
				} catch (AbstractMethodError err) {
					logger.debug("JDBC driver does not implement JDBC 4.1 'getObject(int, Class)' method");
				} catch (SQLFeatureNotSupportedException ex) {
					logger.debug("JDBC driver does not support JDBC 4.1 'getObject(int, Class)' method");
				} catch (SQLException ex) {
					logger.debug("JDBC driver has limited support for JDBC 4.1 'getObject(int, Class)' method");
				}
			}

			// Corresponding SQL types for JSR-310 / Joda-Time types, left up
			// to the caller to convert them (e.g. through a ConversionService).
			String typeName = requiredType.getSimpleName();
			if ("LocalDate".equals(typeName)) {
				return rs.getDate(index);
			} else if ("LocalTime".equals(typeName)) {
				return rs.getTime(index);
			} else if ("LocalDateTime".equals(typeName)) {
				return rs.getTimestamp(index);
			}

			// Fall back to getObject without type specification, again
			// left up to the caller to convert the value if necessary.
			return getResultSetValue(rs, index);
		}

		// Perform was-null check if necessary (for results that the JDBC driver returns
		// as primitives).
		return (rs.wasNull() ? null : value);
	}

	public static void closeStatement(Statement stmt) {
		
		if (stmt != null) {
			try {
				stmt.close();
			} catch (SQLException ex) {
				logger.debug("Could not close JDBC Statement");
			} catch (Throwable ex) {
				// We don't trust the JDBC driver: It might throw RuntimeException or Error.
				logger.debug("Unexpected exception on closing JDBC Statement");
			}
		}
	}

	public static void closeResultSet(ResultSet rs) {
		
		if (rs != null) {
			try {
				rs.close();
			}
			catch (SQLException ex) {
				logger.debug("Could not close JDBC ResultSet");
			}
			catch (Throwable ex) {
				// We don't trust the JDBC driver: It might throw RuntimeException or Error.
				logger.debug("Unexpected exception on closing JDBC ResultSet");
			}
		}
		
	}

	public static boolean supportsBatchUpdates(Connection connection) {
		
		try {
			DatabaseMetaData dbmd = connection.getMetaData();
			if (dbmd != null) {
				if (dbmd.supportsBatchUpdates()) {
					//logger.debug("JDBC driver supports batch updates");
					return true;
				}
				else {
					//logger.debug("JDBC driver does not support batch updates");
				}
			}
		}
		catch (SQLException ex) {
			logger.debug("JDBC driver 'supportsBatchUpdates' method threw exception");
		}
		return false;
	}

}
