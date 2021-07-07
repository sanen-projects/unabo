package online.sanen.unabo.core.handle;

import online.sanen.unabo.api.Handel;

/**
 * 
 *<pre>
 * @author LazyToShow
 * Date: 2017/10/21
 * Time: 23:19
 * </pre>
 */
public class Handlers {
	
	static Handel sqlHandel;
	
	static Handel tableFieldHandel;
	
	static Handel conditionHandel;
	
	static Handel queryHandel;
	
	static Handel paramerHandel;
	
	static Handel debugHandel;
	
	public static Handel getSqlHandel() {
		if(sqlHandel==null)
			sqlHandel = new SqlConstructHandler();
		
		return sqlHandel;
	}
	
	
	public static Handel getCommonFieldHandel() {
		if(tableFieldHandel==null)
			tableFieldHandel = new CommonFieldsHandler();
		
		return tableFieldHandel;
	}
	
	public static Handel getConditionHandel() {
		if(conditionHandel==null)
			conditionHandel = new ConditionHandler();
		
		return conditionHandel;
	}
	
	
	public static Handel getResultHandel() {
		if(queryHandel==null)
			queryHandel = new ResultHandler();
		
		return queryHandel;
	}
	
	public static Handel getParamerHandel() {
		if(paramerHandel==null)
			paramerHandel = new ModifyParametersHandler();
		
		return paramerHandel;
	}
	
	public static Handel getDebugHandel() {
		if(debugHandel==null)
			debugHandel = new DebugHandler();
		
		return debugHandel;
	}
	
	

}
