package online.sanen.unabo.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import online.sanen.unabo.api.condition.Condition;
import online.sanen.unabo.api.condition.ConditionAble;
import online.sanen.unabo.api.condition.Condition.Cs;
import online.sanen.unabo.api.structure.Column;
import online.sanen.unabo.template.jpa.Id;

/**
 * <pre>
 * This will operate the SQL statement as you normally would
 * @author LazyToShow <br>
 * Date: 2017/10/21 <br>
 * Time: 23:19
 * </pre>
 */
public interface QuerySql extends ConditionAble, Stream {

	/**
	 * Add parameters to your SQL to replace the question mark?This is a more
	 * recommended way to replace string splices
	 * 
	 * @param paramers
	 * @return
	 */
	QuerySql addParamer(Object... paramers);

	int update();

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

	<T> List<T> list(Class<T> entityClass);

	/**
	 * 
	 * @return Returns a collection of entity class mappings.
	 */
	<T> List<T> entities(Class<T> entityClass);

	/**
	 * The result set encapsulated in the form of
	 * {@code List<Map<String,Object>>}<br>
	 * This will be an acceptable way to get the mapping of SQL result sets
	 * 
	 * @return {@link List}{@code <}{@link Map}{@code <}
	 *         {@link String},{@link Object}{@code >}{@code >}
	 */
	List<Map<String, Object>> maps();

	/**
	 * 
	 * @param alias - Aliased key-value pairs, returned as aliased key-value if
	 *              matched
	 * @return
	 * @since 2.24
	 */
	List<Map<String, Object>> maps(HashMap<String, String> alias);

	/**
	 * The result set encapsulated in the form of {@code Map<String,Object>}<br>
	 * This will be an acceptable way to get the mapping of SQL result sets
	 * 
	 * @return {@link Map}{@code <} {@link String},{@link Object}{@code >}
	 */
	Map<String, Object> map();

	/**
	 * Returns a single result;The return type is automatically determined based on
	 * the constructor, and if you want to return an entity type, add before that
	 * 
	 * @return
	 */
	<T> T unique();

	
	/**
	 * Specify entity class mappings
	 * @param entityClass - Be sure to include an {@link Id} annotation to declare the primary key
	 * @return
	 */
	<T> T unique(Class<T> entityClass);
	
	<T> T entity(Class<T> entityClass);

	/**
	 * Gets the columns contained in the query result.
	 * 
	 * @return
	 */
	List<Column> getQueryColumns();

	@Override
	QuerySql addCondition(Condition cond);

	@Override
	QuerySql addCondition(String fieldName, Cs cs);

	@Override
	QuerySql addCondition(String fieldName, Cs cs, Object value);

	@Override
	QuerySql addCondition(Consumer<List<Condition>> conds);

	/**
	 * An extension to the {@link Stream} interface，Returns the specified number of
	 * results from the SQL statement
	 * 
	 * @param count
	 */
	List<Map<String, Object>> stream(int count);

}
