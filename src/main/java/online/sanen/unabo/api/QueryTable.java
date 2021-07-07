package online.sanen.unabo.api;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import online.sanen.unabo.api.condition.Condition;
import online.sanen.unabo.api.condition.ConditionAble;
import online.sanen.unabo.api.condition.Condition.Associated;
import online.sanen.unabo.api.condition.Condition.Cs;
import online.sanen.unabo.api.structure.enums.Sorts;

/**
 * @author LazyToShow Date: 2017/10/21 Time: 23:19
 */
public interface QueryTable extends ConditionAble, Stream {

	@Override
	public QueryTable addCondition(Condition cond);

	@Override
	public QueryTable addCondition(String fieldName, Cs cs);

	@Override
	public QueryTable addCondition(String fieldName, Cs cs, Associated associated);

	@Override
	public QueryTable addCondition(String fieldName, Cs cs, Object value);

	@Override
	public QueryTable addCondition(String fieldName, Cs cs, Object value, Associated associated);

	@Override
	public QueryTable addCondition(Consumer<List<Condition>> conds);
	
	
	public QueryTable alias(Map<String, String> alias);


	/**
	 * Sets the fields that participate in the operation, with the <strong>highest
	 * priority</strong> to override the fields of the entity class
	 * 
	 * @param fields
	 * @return
	 */
	QueryTable setFields(String... fields);

	/**
	 * All other fields are involved
	 * 
	 * @param fields
	 * @return
	 */
	QueryTable setExceptFields(String... fields);

	/**
	 * To add sorting
	 * 
	 * @param sorts
	 * @param fields
	 * @return
	 */
	QueryTable sort(Sorts sorts, String... fields);

	/**
	 * Returns a single result;The return type is automatically determined based on
	 * the constructor, and if you want to return an <strong>entity</strong> type,
	 * add before that
	 * 
	 * @return
	 */
	<T> T unique();
	
	<T> Optional<T> uniqueOptional();

	<T> T unique(Class<T> cls);
	
	<T> Optional<T> uniqueOptional(Class<T> cls);

	/**
	 * 
	 * <p>
	 * If you need to return a result set instead of a single item; The return type
	 * is automatically determined by the constructor. If you want to return an
	 * entity type, add the entity type before the {@link #list(Class)} method
	 * 
	 * <p>
	 * The method is equivalent to the integrator of two methods
	 * 
	 * @see #entities(Class)
	 * @see #maps()
	 * 
	 */
	<T> List<T> list();

	<T> List<T> list(Class<T> cls);

	/**
	 * 
	 * @return Returns a collection of entity class mappings.
	 */
	<T> List<T> entities(Class<T> cls);
	
	public Map<String, Object> map();
	
	public Optional<Map<String, Object>> mapOptional();

	/**
	 * 
	 * @return Returns the result set key pair.
	 */
	List<Map<String, Object>> maps();
	
	/**
	 * 
	 * @param alias - Aliased key-value pairs, returned as aliased key-value if matched
	 * @return
	 * @since 2.24
	 */
	List<Map<String, Object>> maps(Map<String,String> alias);

	/**
	 * This is a extraction function <blockquote>limit(0,10)</blockquote> This
	 * method is usually followed by a {@link #maps()} or {@link #list()} method<br>
	 * <br>
	 * Different databases will have different implementations, so refer to the
	 * database functions you're currently using, and if you don't support limit,
	 * you'll default to pulling the corresponding result set from the result set
	 * 
	 * @param args - If you're using <b>MYSQL</b> or <b>ORACLE</b> or <b>SQLITE</b>
	 *             you can go to <b>limit(0,100)</b>, if you're using
	 *             <b>SQLSERVER</b> you'll go to <b>limit(100)</b> and it'll convert
	 *             to <b>top 100</b>
	 * @return {@link QueryTable}
	 */
	QueryTable limit(Integer... args);

	/**
	 * Condition deletion, if no condition, is equivalent to clearing the table.
	 * 
	 * @return
	 */
	int delete();

	/**
	 * Determine whether the table exists.
	 * 
	 * @return
	 */
	boolean isExsites();

	/**
	 * Clear the table
	 * 
	 * @return
	 */
	int clear();

	/**
	 * Delete table
	 * 
	 * @return
	 */
	int drop();

	/**
	 * Modify table name
	 * 
	 * @param newName
	 * @return
	 */
	int updateName(String newName);

	/**
	 * Add fields to the table
	 * 
	 * @param columnName
	 * @param type
	 * @return
	 */
	int addColumn(String columnName, String type);

	/**
	 * 
	 * @param newTableName
	 * @return
	 */
	String copyTo(String newTableName);

	/**
	 * Whether the qualifier is turned on or not, it is turned on by default
	 * @param flag
	 * @return
	 */
	QueryTable setQualifier(boolean flag);

	int count();

	int count(String field);

	

	


	
}
