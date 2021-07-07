package online.sanen.unabo.template;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Map;

import com.mhdt.structure.LinkedCaseInsensitiveMap;

/**
 * 
 *
 * @author LazyToShow	<br>
 * Date:	2018年10月14日	<br>
 * Time:	下午1:18:48
 */
public class RowExtractor2Map implements RowExtractor<Map<String, Object>> {

	Map<String,String> alias;
	
	public RowExtractor2Map() {
		
	}
	
	public RowExtractor2Map(Map<String, String> alias) {
		this.alias = alias;
	}

	public Map<String, Object> mapRow(ResultSet rs, int rowNum) throws SQLException {
		
		
		ResultSetMetaData rsmd = rs.getMetaData();
		
		int columnCount = rsmd.getColumnCount();
		
		Map<String, Object> mapOfColValues = createColumnMap(columnCount);
		
		for (int i = 1; i <= columnCount; i++) {
			String key = getColumnKey(JdbcUtils.lookupColumnName(rsmd, i));
			if(alias!=null && alias.containsKey(key))
				key = alias.get(key);
			Object obj = getColumnValue(rs, i);
			
			mapOfColValues.put(key, obj);
		}
		
		return mapOfColValues;
	}

	/**
	 * Create a Map instance to be used as column map.
	 * <p>
	 * By default, a linked case-insensitive Map will be created.
	 * @param columnCount
	 * @return
	 */
	protected Map<String, Object> createColumnMap(int columnCount) {
		return new LinkedCaseInsensitiveMap<Object>(columnCount);
	}

	/**
	 * Determine the key to use for the given column in the column Map.
	 * 
	 * @param columnName the column name as returned by the ResultSet
	 * @return the column key to use
	 * @see java.sql.ResultSetMetaData#getColumnName
	 */
	protected String getColumnKey(String columnName) {
		return columnName;
	}

	/**
	 * Retrieve a JDBC object value for the specified column.
	 * <p>
	 * The default implementation uses the {@code getObject} method. Additionally,
	 * this implementation includes a "hack" to get around Oracle returning a non
	 * standard object for their TIMESTAMP datatype.
	 * @param rs
	 * @param index
	 * @return
	 * @throws SQLException
	 */
	protected Object getColumnValue(ResultSet rs, int index) throws SQLException {
		return JdbcUtils.getResultSetValue(rs, index);
	}

}
