package online.sanen.unabo.api.structure;

import java.util.List;
import java.util.stream.Collectors;

import online.sanen.unabo.api.Bootstrap;
import online.sanen.unabo.core.RuntimeCache;

/**
 * 
 * @author LazyToShow <br>
 *         Date： 2018年7月18日 <br>
 *         Time: 下午3:41:18
 */
public abstract class DataInformation {

	protected Bootstrap bootstrap;

	public DataInformation(Bootstrap bootstrap) {
		this.bootstrap = bootstrap;
	}

	/**
	 * Gets the name of the library under the current connection
	 * 
	 * @return
	 */
	public abstract List<String> getDatabases();

	/**
	 * Gets the name of the table under the current connection (library)
	 * 
	 * @return
	 */
	public abstract List<String> getTableNames();

	/**
	 * Gets the current database connection field.The internal table field cache can
	 * also be refreshed
	 * 
	 * @param tableName - eg: <code>User</code>
	 * @return
	 */
	public final List<Column> getColumns(String tableName) {

		List<Column> columns = beforeGetColumns(tableName);
		
		if (columns != null && !columns.isEmpty())
			RuntimeCache.refreshTableFields(tableName, bootstrap.manager().getUrl(),
					columns.stream().map(mapper -> mapper.getName()).collect(Collectors.toList()));

		return columns;
	}

	public abstract List<Column> beforeGetColumns(String tableName);

	/**
	 * Determines whether a table is included in the current connection (table name
	 * are case insensitive)
	 * 
	 * @param tableName
	 * @return
	 */
	public boolean containsTable(String tableName) {

		List<String> tableNames = getTableNames();
		return tableNames != null
				&& tableNames.stream().anyMatch(it -> it.toUpperCase().equals(tableName.toUpperCase()));
	}

	public String getTableComment(String tableName) {
		return null;
	}

}
