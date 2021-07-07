package online.sanen.unabo.core.handle;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.stream.Collectors;

import com.mhdt.degist.DegistTool;
import com.mhdt.degist.DegistTool.Encode;
import com.mhdt.degist.Validate;
import com.mhdt.structure.cache.Cache;
import com.mhdt.toolkit.Assert;

import online.sanen.unabo.api.structure.ChannelContext;
import online.sanen.unabo.api.structure.enums.ProductType;
import online.sanen.unabo.api.structure.enums.QueryType;
import online.sanen.unabo.core.CacheUtil;
import online.sanen.unabo.template.jpa.JPA;
import online.sanen.unabo.template.jpa.JPA.Primarykey;

/**
 * <pre>
 * 
 * &#64;author LazyToShow
 *  Date: 2017/10/21 
 *  Time: 23:19
 * </pre>
 */
public interface SimpleHandler {

	default void noticeUpdate(ChannelContext structure) {
		if (structure.getQueryType().equals(QueryType.select))
			return;

		String tabelName = Validate.isNullOrEmpty(structure.getTableName()) ? DEFAULT_CACHE : structure.getTableName();
		Cache<String, Object> cache = CacheUtil.getInstance().getCache(tabelName);
		if (cache == null)
			return;

		cache.clear();
	}

	default void noticeAdd(ChannelContext structure, Object result) {
		if (!structure.getQueryType().equals(QueryType.select))
			return;

		String tabelName = Validate.isNullOrEmpty(structure.getTableName()) ? DEFAULT_CACHE : structure.getTableName();
		String md5 = DegistTool.md5(this.getSql(structure), Encode.HEX);

		Cache<String, Object> cache = CacheUtil.getInstance().getCache(tabelName);
		cache.put(md5, result);
	}

	default Object tryReadFromCache(ChannelContext structure) {
		if (!structure.getQueryType().equals(QueryType.select))
			return null;
		String tabelName = Validate.isNullOrEmpty(structure.getTableName()) ? DEFAULT_CACHE : structure.getTableName();
		String md5 = DegistTool.md5(this.getSql(structure), Encode.HEX);
		return CacheUtil.getInstance().get(tabelName, md5);

	}

	/**
	 * Process library name/namespace (Oracle). Table name
	 * 
	 * @param tableName.
	 * @param schema
	 * @param productType
	 * @return
	 */
	default String modifiedTableName(String tableName, String schema, ProductType productType) {

		String modifer = ProductType.applyTableModifier(productType);
		return schema == null ? String.format("%s%s%s", modifer, tableName, modifer)
				: String.format("%s%s%s.%s%s%s", modifer, schema, modifer, modifer, tableName, modifer);
	}

	public static String DEFAULT_CACHE = "DEFAULT_CACHE";

	// 换行且缩进
	static String[] Keywords = new String[] { "select", "insert into", "delete", "update", "from", "group by", "where",
			" values", "order by", "limit", "set" };
	// 换行
	static String[] Keywords2 = new String[] { " and ", " having " };

	// 大写
	static String[] Keywords3 = new String[] { " id ", " no ", " desc ", " set ", " top " };

	default String formatSql(ChannelContext context) {

		String sql = context.getSql().toString();

		for (String keyword : Keywords)
			sql = sql.replaceAll(keyword + "[\\s]+", "\r\n" + keyword.toUpperCase() + "\r\n ");

		for (String keyword : Keywords2)
			sql = sql.replaceAll(keyword, "\r\n  " + keyword.toUpperCase());

		for (String keyword : Keywords3)
			sql = sql.replaceAll(keyword, keyword.toUpperCase());

		context.setSql(sql);

		return this.getSql(context);
	}

	default Primarykey getPrimaryKey(Class<?> entityClass) {

		Assert.notNull(entityClass, "Entry class is null");

		return JPA.getId(entityClass);
	}

	default String getSql(ChannelContext context) {

		String sql = context.getSql().toString();

		if (context.getParamers().isEmpty()) {
			return sql;
		} else {
			sql = sql.replace("%", "{1}");
			sql = sql.replace("?", "%s");

			Object[] params = context.getParamers().stream().map(param -> translateParamerInSql(param))
					.collect(Collectors.toList()).toArray();
			sql = String.format(sql, params);
			sql = sql.replace("{1}", "%");

			return sql;

		}
	}

	default String translateParamerInSql(Object obj) {
		if (Validate.isNullOrEmpty(obj))
			return "null";

		if (obj instanceof byte[]) {
			return "[BYTES]";
		}

		String str = obj.toString();
		if (str.length() > 100)
			str = "[TEXT TOO LONG]";

		return (obj instanceof Integer) ? str : String.format("%s%s%s", "'", str, "'");
	}
	
	default void initFetchSize(PreparedStatement ps, ProductType productType) throws SQLException {
		if (productType == ProductType.MYSQL) {
			ps.setFetchSize(Integer.MIN_VALUE);
		} else {
			ps.setFetchSize(3000);
		}
	}

}
