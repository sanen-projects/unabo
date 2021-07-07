package online.sanen.unabo.core;

import java.util.HashMap;
import java.util.Map;

import com.mhdt.Print;
import com.mhdt.degist.Validate;

import online.sanen.unabo.api.Handel;
import online.sanen.unabo.api.PipelineFactory;
import online.sanen.unabo.api.component.Pipeline;
import online.sanen.unabo.api.exception.QueryException;
import online.sanen.unabo.api.structure.ChannelContext;
import online.sanen.unabo.api.structure.enums.QueryType;
import online.sanen.unabo.api.structure.enums.ResultType;
import online.sanen.unabo.core.handle.SimpleHandler;

/**
 * 
 * @author online.sanen <br>
 *         Date: 2017/10/21 <br>
 *         Time: 23:19
 */
public class Assembler implements SimpleHandler{
	
	private Assembler() {
		
	}
	
	static Assembler assembler;
	
	public static Assembler instance() {
		if(assembler == null)
			assembler = new Assembler();
		
		return assembler;
	}

	/**
	 * 
	 * @param queryType
	 * @param resultType
	 * @param context
	 * @param factory
	 * @return
	 */
	public  Object create(QueryType queryType, ResultType resultType, ChannelContext context,
			PipelineFactory factory) {

		long lastTime = System.currentTimeMillis();
		context.setQueryType(queryType);
		context.setResultType(resultType);
		context.setQueryId(String.valueOf(System.currentTimeMillis()));

		Object result = null;

		Pipeline pipeline = factory.getPipeline();

		try {

			for (Handel handel : pipeline.getHandels()) {
				Object message = handel.handel(context, result);
				result = (message == null ? result : message);

				if (pipeline.getLast() == handel) {
					print(lastTime, context);
					return result;
				}

			}

		} catch (QueryException e) {
			System.out.println(String.format("[WARN] Assembler error of sql:%s", getSql(context)));
			throw e;
		} catch (RuntimeException e) {
			System.out.println(String.format("[WARN] Assembler error of sql:%s", getSql(context)));
			throw e;
		} catch (Exception e) {
			throw e;
		}

		return null;
	}

	private static ThreadLocal<Map<String, StringBuilder>> threadLocal = new ThreadLocal<>();

	private synchronized  void print(long lastTime, ChannelContext context) {

		String queryId = context.getQueryId();

		if (threadLocal.get() == null) {
			threadLocal.set(new HashMap<>());
		}

		if (!context.isShowSql() || threadLocal.get().get(queryId) == null)
			return;

		StringBuilder sb = threadLocal.get().get(queryId);
		sb.append(
				Print.translate("WHITE", String.format("Time: %ss ", (System.currentTimeMillis() - lastTime) / 1000f)));

		if (context.isCache() && context.getQueryType().equals(QueryType.select))
			sb.append("\tcache:" + getCacheInfo(context));

		System.out.println(sb.append("\r\n").toString());
	}

	private  String getCacheInfo(ChannelContext structure) {

		try {
			String cacheName = Validate.isNullOrEmpty(structure.getTableName()) ? SimpleHandler.DEFAULT_CACHE
					: structure.getTableName();
			int size = CacheUtil.getInstance().getCache(cacheName).size();

			return cacheName + "/" + size + "/" + CacheUtil.getInstance().getCache(cacheName).getCacheSize();

		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}

	}

	public  StringBuilder getStringBuild(String queryId) {

		if (threadLocal.get() == null) {
			threadLocal.set(new HashMap<>());
		}

		StringBuilder builder = threadLocal.get().get(queryId);

		if (builder == null) {
			builder = new StringBuilder();
			threadLocal.get().put(queryId, builder);
		}

		builder.setLength(0);
		return builder;
	}

}
