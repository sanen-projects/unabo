package online.sanen.unabo.core.factory;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import online.sanen.unabo.api.Handel;
import online.sanen.unabo.api.structure.Column;
import online.sanen.unabo.api.structure.StreamConsumer;
import online.sanen.unabo.core.handle.BatchOperationHandler;
import online.sanen.unabo.core.handle.CommonFieldsHandler;
import online.sanen.unabo.core.handle.ConditionHandler;
import online.sanen.unabo.core.handle.CreateAndInsertSqlHandler;
import online.sanen.unabo.core.handle.DebugHandler;
import online.sanen.unabo.core.handle.EntityConditionHandle;
import online.sanen.unabo.core.handle.ModifyParametersHandler;
import online.sanen.unabo.core.handle.PrimaryKeyAsConditionHandler;
import online.sanen.unabo.core.handle.QueryResultLimitHandler;
import online.sanen.unabo.core.handle.ResultHandler;
import online.sanen.unabo.core.handle.SqlColumnsExtractHandler;
import online.sanen.unabo.core.handle.SqlConstructHandler;
import online.sanen.unabo.core.handle.StreamHandler;


/**
 * 
 *<pre>
 * @author LazyToShow
 * Date: 2017/10/21
 * Time: 23:19
 * </pre>
 */
public class HandelFactory {
	
	static Handel sqlHandel;
	
	public static Handel sqlHandel() {
		if(sqlHandel==null)
			sqlHandel = new SqlConstructHandler();
		
		return sqlHandel;
	}
	
	static Handel tableFieldHandel;
	
	public static Handel commonFieldHandel() {
		if(tableFieldHandel==null)
			tableFieldHandel = new CommonFieldsHandler();
		
		return tableFieldHandel;
	}
	
	static Handel conditionHandel;
	
	public static Handel conditionHandel() {
		if(conditionHandel==null)
			conditionHandel = new ConditionHandler();
		
		return conditionHandel;
	}
	
	static Handel queryHandel;
	
	public static Handel resultHandel() {
		if(queryHandel==null)
			queryHandel = new ResultHandler();
		
		return queryHandel;
	}
	
	static Handel paramerHandel;
	
	public static Handel paramerHandel() {
		if(paramerHandel==null)
			paramerHandel = new ModifyParametersHandler();
		
		return paramerHandel;
	}
	
	static Handel debugHandel;
	
	public static Handel debugHandel() {
		if(debugHandel==null)
			debugHandel = new DebugHandler();
		
		return debugHandel;
	}
	
	static PrimaryKeyAsConditionHandler primaryKeyAsConditionHandel;
	
	public static Handel primaryKeyHandel() {
		if(primaryKeyAsConditionHandel==null)
			primaryKeyAsConditionHandel = new PrimaryKeyAsConditionHandler();
		
		return primaryKeyAsConditionHandel;
	}


	static Handel limitHandel;
	
	public static Handel limitHandel() {
		if(limitHandel==null)
			limitHandel = new QueryResultLimitHandler();
		
		return limitHandel;
	}
	
	static Handel entityConditionHandle;
	
	public static Handel entityConditionHandel() {
		if(entityConditionHandle==null)
			entityConditionHandle = new EntityConditionHandle();
		
		return entityConditionHandle;
	}
	
	
	static Handel batchUpdate;

	public static Handel batchUpdate() {
		if(batchUpdate==null)
			batchUpdate = new BatchOperationHandler();
		
		
		return batchUpdate;
	}
	
	public static Handel streamHandel(int bufferSize, Function<List<Column>, Object> consumer, StreamConsumer datas,Map<String, String> aliases) {
		return new StreamHandler(bufferSize,consumer,datas,aliases);
	}

	public static Handel streamHandel(int bufferSize,Consumer<List<Map<String,Object>>> datas, Map<String, String> aliases) {
		
		return new StreamHandler(bufferSize,datas,aliases);
	}

	
	public static Handel streamHandel(int count) {
		return new StreamHandler(count);
	}
	
	
	static Handel resultColumnsHandel;
	
	public static Handel resultColumnsHandel() {
		
		if(resultColumnsHandel==null)
			resultColumnsHandel = new SqlColumnsExtractHandler();
		
		return resultColumnsHandel;
	}

	
	public static Handel createAndInsert(String newTableName) {
		
		return new CreateAndInsertSqlHandler(newTableName);
	}

	

}
