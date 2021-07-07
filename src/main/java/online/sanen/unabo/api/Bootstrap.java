package online.sanen.unabo.api;

import java.io.InputStream;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;

import online.sanen.unabo.api.component.Manager;
import online.sanen.unabo.api.structure.DataInformation;

/***
 * <b>Boot device</b>,There are several patterns
 * 
 * <pre>
 * {@link #createSQL(String)} ：Write the Sql statement directly
 * {@link #query(Object)} ：Operation object instance for addition/deletion/modification (batch operation)
 * {@link #queryPk(Class, Object)} ： Operation entity type for primary key operation
 * {@link #queryTable(String)} ：Direct operation table for query/delete/modification
 * {@link #queryMap(String, Map)}: Manipulate map modifications/inserts
 * </pre>
 * 
 * 
 * @see <a href="http://unabo.sanen.online">Unabo website</a>
 * 
 * @author lazyToShow <br>
 *         Date: 2017/10/21 <br>
 *         Time: 23:19
 */
public interface Bootstrap {

	/**
	 * Customize Sql operations
	 */
	QuerySql createSQL(String sql);

	/**
	 * Customize Sql operations
	 * 
	 * @param sql      - Usage of parameters in SQL<strong> ? </strong>Take the
	 *                 place of
	 * @param paramers - Parameters (ordered) are not required
	 */
	QuerySql createSQL(String sql, Object... paramers);

	/**
	 * If you don't want to generate entity classes, another option is to manipulate
	 * the map instance, {@link QueryMap} provides Add and delete operations are
	 * provided.
	 * 
	 * @param tableName
	 * @param map
	 * @return
	 */
	QueryMap queryMap(String tableName, Map<String, Object> map);

	/**
	 * * If you don't want to generate entity classes, another option is to
	 * manipulate the map instance, {@link QueryMap} provides Add and delete
	 * operations are provided.
	 * 
	 * @param tableName
	 * @param maps
	 * @return
	 */
	<T extends Map<String, Object>> QueryMap queryMap(String tableName, Collection<T> maps);

	/**
	 * 
	 * @param entrys
	 * @return
	 */
	<T> QueryEntity query(Collection<T> entrys);

	/**
	 * 
	 * @param entry
	 * @return
	 */
	<T> QueryEntity query(T entry);

	/**
	 * Primary key operation, Remove (Remove), query single case (find)
	 * 
	 * @param entry    - Entity class
	 * @param keyValue - The primary key value can be an int or String type
	 */
	<T> QueryPK<T> queryPk(Class<T> entry, Object keyValue);

	/**
	 * Custom table operation, delete query (List, UniqResult)
	 * 
	 * @param tableName - The name of the table
	 */
	QueryTable queryTable(String tableName);

	/**
	 * Replace the table name with the class name
	 * 
	 * @param cls - Entity class type
	 * @see #queryTable(String)
	 * @return {@link QueryTable}
	 */
	<T> QueryTable queryTable(Class<T> cls);

	/**
	 * Get the {@link DataInformation},Through this interface you can get some
	 * connection information, such as database name, table name, and field details
	 * 
	 * @return
	 */
	DataInformation dataInformation();

	/**
	 * Gets the current link information.
	 * 
	 * @return
	 */
	Manager manager();


	String getLastSql();

	/**
	 * 
	 * @param <T>
	 * @param inputStream
	 * @param cls
	 * @return
	 */
	<T> T mapper(InputStream inputStream, Class<T> cls);

	void openSession();
	
	public void commit() throws SQLException;
	
	public void rollback() throws SQLException;
}
