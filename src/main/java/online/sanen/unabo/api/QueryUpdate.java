package online.sanen.unabo.api;


import java.util.List;
import java.util.function.Consumer;

import online.sanen.unabo.api.condition.Condition;
import online.sanen.unabo.api.condition.ConditionAble;
import online.sanen.unabo.api.condition.Condition.Cs;
import online.sanen.unabo.template.jpa.Table;


/**
 * <pre>
 * QueryEn's derivative interface for handling modification operations,
 * Used to add conditions to the modified operation
 * 
 * @author LazyToShow
 * Date: 2017/11/29
 * Time: 21:00
 * </pre>
 */
public interface QueryUpdate extends ConditionAble{

	/**
	 * Set the table name, which has the highest priority to override the bean name
	 * and {@link Table} annotation
	 * 
	 * @param tableName
	 * @return
	 */
	QueryUpdate setTableName(String tableName);

	/**
	 * Sets the fields that participate in the operation, with the highest priority
	 * to override the fields of the entity class
	 * 
	 * @param fields
	 * @return
	 */
	QueryUpdate setFields(String... fields);
	

	/**
	 * All other fields are involved
	 * 
	 * @param fields
	 * @return
	 */
	QueryUpdate setExceptFields(String... fields);
	
	public QueryUpdate addCondition(Condition cond);

	public QueryUpdate addCondition(String fieldName, Cs cs);

	public QueryUpdate addCondition(String fieldName, Cs cs, Object value);
	
	public QueryUpdate addCondition(Consumer<List<Condition>> consumer);

	int update();

}
