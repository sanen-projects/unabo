package online.sanen.unabo.api;

import java.util.List;
import java.util.function.Consumer;

import online.sanen.unabo.api.condition.Condition;
import online.sanen.unabo.api.condition.ConditionAble;
import online.sanen.unabo.api.condition.Condition.Associated;
import online.sanen.unabo.api.condition.Condition.Cs;
import online.sanen.unabo.template.jpa.Table;

/**
 * <pre>
 * Custom table operations
 * &#64;author LazyToShow
 * Date: 2017/10/21 
 * Time: 23:19
 * </pre>
 */
public interface QueryEntity extends ConditionAble {

	/**
	 * Set the table name, which has the highest priority to override the bean name
	 * and {@link Table} annotation
	 * 
	 * @param tableName
	 * @return
	 */
	QueryEntity setTableName(String tableName);

	/**
	 * Sets the fields that participate in the operation, with the highest priority
	 * to override the fields of the entity class
	 * 
	 * @param fields
	 * @return
	 */
	QueryEntity setFields(String... fields);

	/**
	 * All other fields are involved
	 * 
	 * @param fields
	 * @return
	 */
	QueryEntity setExceptFields(String... fields);

	@Override
	public QueryUpdate addCondition(Condition cond);

	@Override
	public QueryUpdate addCondition(String fieldName, Cs cs);

	@Override
	public QueryUpdate addCondition(String fieldName, Cs cs, Associated associated);

	@Override
	public QueryUpdate addCondition(String fieldName, Cs cs, Object value);

	@Override
	public QueryUpdate addCondition(String fieldName, Cs cs, Object value, Associated associated);

	@Override
	public QueryUpdate addCondition(Consumer<List<Condition>> conds);

	int insert();

	int update();
	
	int updateBy(String column);

	<T> T unique();

	<T> List<T> list();

	int create();

	int delete();

	

}
