package online.sanen.unabo.core.handle;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.mhdt.degist.Validate;
import com.mhdt.toolkit.Collections;

import online.sanen.unabo.api.Handel;
import online.sanen.unabo.api.structure.ChannelContext;
import online.sanen.unabo.api.structure.enums.QueryType;
import online.sanen.unabo.api.structure.enums.ResultType;
import online.sanen.unabo.core.RuntimeCache;
import online.sanen.unabo.template.DataRetrievalFailureException;
import online.sanen.unabo.template.GeneratedKeyHolder;
import online.sanen.unabo.template.InvalidDataAccessApiUsageException;
import online.sanen.unabo.template.KeyHolder;
import online.sanen.unabo.template.SqlRowSet;
import online.sanen.unabo.template.SqlTemplate;
import online.sanen.unabo.template.jpa.JPA.Primarykey;

/**
 * The execution result
 * 
 * @author LazyToShow <br>
 * 
 *         Date: 2017/10/21 <br>
 *         Time: 23:19
 */
public class ResultHandler implements SimpleHandler,Handel {

	ChannelContext context;

	@Override
	public Object handel(ChannelContext context, Object product) {

		this.context = context;

		QueryType queryType = context.getQueryType();

		// Modify the operating
		if (queryType != QueryType.select && queryType != QueryType.count) {

			if (context.isCache())
				noticeUpdate(context);

			return update(queryType, context);
		}

		// Initialize the underlying data
		String sql = context.getSql().toString();
		Collection<Object> paramers = context.getParamers();

		SqlTemplate template = (SqlTemplate) context.getTemplate();

		// Query operation
		ResultType resultType = context.getResultType();

		Object result = null;

		// Try to read the results from the cache
		if (context.isCache() && (result = tryReadFromCache(context)) != null)
			return processCacheResult(result, resultType);

		switch (resultType) {

		case Int:
			result = queryForInt(sql, template, paramers);
			break;

		case String:
			result = queryForString(sql, template, paramers);
			break;

		case List:
			result = queryForList(context, sql, template, paramers);
			break;

		case Maps:
			result = queryForMaps(sql, template, paramers);
			break;

		case Map:
			result = queryForMap(sql, template, paramers);
			break;

		case Object:
			result = queryForObject(context, sql, template, paramers);
			break;

		case Bean:
			result = queryForBean(context, sql, template, paramers);
			break;

		case Beans:
			result = queryForBeans(context, sql, template, paramers);
			break;

		default:
			break;
		}

		// The result set requires manual limitations ?
		result = processLimit(result, context);

		// Add the select cache
		if (context.isCache())
			noticeAdd(context, result);

		return result;
	}

	private Object update(QueryType queryType, ChannelContext context) {
		String sql = context.getSql().toString();
		Object[] paramers = context.getParamers().toArray();
		SqlTemplate template = (SqlTemplate) context.getTemplate();

		if (queryType.equals(QueryType.drop))
			RuntimeCache.removeTableCache(context);

		if (queryType.equals(QueryType.insert)) {

			try {

				return insertAndReturnKey(template, sql, paramers);
			} catch (InvalidDataAccessApiUsageException | DataRetrievalFailureException e) {
				return 0;
			}

			/**
			 * template.update(sql, paramers); return 0;
			 */

		} else {

			return template.update(sql, paramers);
		}
	}

	private Integer insertAndReturnKey(SqlTemplate template, String sql, Object[] paramers) {
		Primarykey primaryKey = context.getPrimaryKey();
		KeyHolder keyHolder = null;

		if (primaryKey != null && !Validate.isNullOrEmpty(primaryKey.getName()))
			keyHolder = new GeneratedKeyHolder(primaryKey.getName());
		else
			keyHolder = new GeneratedKeyHolder();

		template.update(con -> {
			PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);

			for (int i = 0; i < paramers.length; i++) {

				int parameterPosition = i + 1;
				Object argValue = paramers[i];

				if (Validate.isEnum(argValue))
					ps.setString(parameterPosition, String.valueOf(argValue));
				else
					ps.setObject(parameterPosition, argValue);

			}

			return ps;
		}, keyHolder);

		return Optional.ofNullable(keyHolder.getKey()).map(mapper -> mapper.intValue()).orElse(1);
	}

	/**
	 * Resolve different problems with the same SQL result set<br>
	 * For example, the first query result is a list, and the second query result is
	 * object The second result is to be processed in the cache result
	 * 
	 * @param result
	 * @param resultType
	 * @return
	 */
	private Object processCacheResult(Object result, ResultType resultType) {

		if (result instanceof List && (resultType.equals(ResultType.Object) || resultType.equals(ResultType.Bean))) {
			return ((List<?>) result).get(0);
		}

		return result;
	}

	/**
	 * The result and the second limit,SQL that is not supported by the database
	 * Here we are implementing the interface manually
	 * 
	 * @param result
	 * @param structure
	 */
	private Object processLimit(Object result, ChannelContext structure) {
		// If result is not list type,need not process
		if (!(result instanceof List))
			return result;

		// If there is a limit operation but SQL does not support, simulate limit
		if (structure.hasLimitAble() && !structure.isSupportLimitAble()) {

			Integer[] limit = structure.getLimit();

			Integer start = 0;
			Integer end = 0;

			if (limit.length == 1) {
				end = limit[0];

			} else if (limit.length == 2) {

				start = limit[0];
				end = limit[1];
				if (end == null)
					end = -1;

			}

			// If the limit second parameter is null converted to -1, the number of access
			// is not limited
			result = Collections.limit((List<?>) result, start, end);
		}

		return result;
	}

	/**
	 * 
	 * @param context
	 * @param sql
	 * @param template
	 * @param paramers
	 * @return
	 */
	private Object queryForList(ChannelContext context, String sql, SqlTemplate template, Collection<Object> paramers) {

		if (context.getEntityClass() != null) {
			return queryForBeans(context, sql, template, paramers);
		} else {

			List<Map<String, Object>> list = queryForMaps(sql, template, paramers);

			if (list == null || list.isEmpty())
				return new ArrayList<>();

			return processList(list);
		}

	}

	private Object processList(List<Map<String, Object>> list) {

		if (list == null || list.isEmpty())
			return list;

		if (list.get(0).keySet().size() > 1)
			return list;

		List<Object> result = new LinkedList<Object>();

		for (Map<String, Object> map : list)
			result.add(map.values().toArray()[0]);

		return result;
	}

	/**
	 * 
	 * @param context
	 * @param sql
	 * @param template
	 * @param paramers
	 * @return
	 */
	private Object queryForBeans(ChannelContext context, String sql, SqlTemplate template,
			Collection<Object> paramers) {

		return template.queryForEntries(context.getEntityClass(), sql, paramers.toArray());
	}

	/**
	 * 
	 * @param structure
	 * @param sql
	 * @param template
	 * @param paramers
	 * @return
	 */
	private Object queryForBean(ChannelContext structure, String sql, SqlTemplate template,
			Collection<Object> paramers) {

		return template.queryForEntry(structure.getEntityClass(), sql, paramers.toArray());
	}

	/**
	 * 
	 * @param structure
	 * @param sql
	 * @param template
	 * @param paramers
	 * @return
	 */
	private Object queryForObject(ChannelContext structure, String sql, SqlTemplate template,
			Collection<Object> paramers) {

		if (structure.getEntityClass() != null) {
			return queryForBean(structure, sql, template, paramers);
		} else {

			List<Map<String, Object>> maps = queryForMaps(sql, template, paramers);

			if (maps == null || maps.isEmpty())
				return null;

			Map<String, Object> map = null;

			if (maps.size() > 0)
				map = maps.get(0);

			return map.size() > 1 ? map : processObjectByType(map.values().toArray()[0]);
		}

	}

	private Object processObjectByType(Object object) {

		if (object == null)
			return object;

		if (object instanceof Long) {

			long obj = (long) object;

			if (obj < Integer.MAX_VALUE) {
				return (int) obj;
			}
		}

		return object;
	}

	/**
	 * 
	 * @param sql
	 * @param template
	 * @param paramers
	 * @return
	 */
	private List<Map<String, Object>> queryForMaps(String sql, SqlTemplate template, Collection<Object> paramers) {

		List<Map<String, Object>> queryForList = template.queryForList(sql, context.getAlias(), paramers.toArray());

		if (queryForList == null || queryForList.isEmpty())
			return new ArrayList<Map<String, Object>>();

		return queryForList;
	}

	/**
	 * 
	 * @param sql
	 * @param template
	 * @param paramers
	 * @return
	 */
	private Map<String, Object> queryForMap(String sql, SqlTemplate template, Collection<Object> paramers) {
		return template.queryForMap(sql, context.getAlias(), paramers.toArray());

	}

	/**
	 * 
	 * @param sql
	 * @param template
	 * @param paramers
	 * @return
	 */
	private Object queryForString(String sql, SqlTemplate template, Collection<Object> paramers) {
		SqlRowSet rs = template.queryForRowSet(sql, paramers.toArray());
		while (rs.next()) {
			return rs.getString(1);
		}
		return null;
	}

	/**
	 * 
	 * @param sql
	 * @param template
	 * @param paramers
	 * @return
	 */
	private Object queryForInt(String sql, SqlTemplate template, Collection<Object> paramers) {

		SqlRowSet rs = template.queryForRowSet(sql, paramers.toArray());
		while (rs.next())
			return rs.getInt(1);

		return -1;
	}

}
