package online.sanen.unabo.api.structure.enums;

import java.sql.Connection;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import com.mhdt.toolkit.Assert;

import online.sanen.unabo.api.exception.ConditionException;
import online.sanen.unabo.api.exception.QueryException;
import online.sanen.unabo.api.structure.ChannelContext;
import online.sanen.unabo.template.SqlRowSet;
import online.sanen.unabo.template.SqlTemplate;

/**
 * Database of different manufacturers enumeration, at the same time because of
 * different manufacturers caused differences, also try to resolve in this type
 * 
 * @author LazyToShow <br>
 *         Date： 2018年8月20日 <br>
 *         Time: 上午11:09:31
 */
public enum ProductType {

	MYSQL("Mysql"), SQLITE("Sqlite"), MICROSOFT_SQL_SERVER("Microsoft Sql Server"), ORACLE("Oracle"),
	POSTGRESQL("Postgresql");

	String name;

	private ProductType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	/**
	 * In SQL queries, if the table name contains a special symbol that needs to be
	 * wrapped in a layer of modifiers, different database vendor modifiers will be
	 * different, and this method is designed to provide uniformly, and by default,
	 * return an empty string
	 * 
	 * @param productType
	 * @return
	 */
	public static String applyTableModifier(ProductType productType) {

		if (productType == ProductType.MYSQL) {
			return "`";
		} else {
			return "\"";
		}

	}

	/**
	 * 
	 * @param productType
	 * @param tableName
	 * @param newName
	 * @return
	 */
	public static String updateTableNameSQL(ProductType productType, String tableName, String schema, String newName) {

		String modifier = ProductType.applyTableModifier(productType);

		if (productType == ProductType.MICROSOFT_SQL_SERVER)
			return String.format("EXEC sp_rename %s%s%s,'%s'", modifier, tableName, modifier, newName);
		else if (schema != null)
			return String.format("ALTER TABLE %s%s%s.%s%s%s RENAME TO %s%s%s", modifier, schema, modifier, modifier,
					tableName, modifier, modifier, newName, modifier);
		else
			return String.format("ALTER TABLE %s%s%s RENAME TO %s%s%s", modifier, tableName, modifier, modifier,
					newName, modifier);

	}

	/**
	 * 
	 * @param productType
	 * @param template
	 * @param tableName
	 * @return
	 */
	public static List<String> getColumnsFromTableName(ProductType productType, SqlTemplate template, String tableName,
			String schema) {

		SqlRowSet rs = null;

		switch (productType) {

		case MYSQL:

			try {

				if (schema == null) {
					try (Connection conn = template.getDataSource().getConnection()) {
						schema = conn.getCatalog();
					}
				}

				rs = template.queryForRowSet(
						"SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = ? AND TABLE_SCHEMA = ?;",
						tableName, schema);
				
				
			} catch (Exception e) {
				throw new QueryException(e);
			}

			break;

		case SQLITE:
			rs = template.queryForRowSet("PRAGMA table_info('" + tableName + "')");
			break;

		case POSTGRESQL:
			rs = template.queryForRowSet(
					"SELECT A.attname FROM pg_class AS C,pg_attribute AS A WHERE C.relname = ? AND A.attrelid = C.oid AND A.attnum > 0 AND A.atttypid >0",
					tableName);
			break;

		case MICROSOFT_SQL_SERVER:
			rs = template.queryForRowSet("SELECT column_name FROM INFORMATION_SCHEMA.columns WHERE TABLE_NAME=? ",
					tableName);
			break;

		case ORACLE:
			if (tableName.contains("."))
				tableName = tableName.split("\\.")[1];
			rs = template.queryForRowSet("SELECT t.COLUMN_NAME FROM ALL_TAB_COLUMNS t where t.TABLE_NAME=?", tableName);
			break;

		default:
			throw new RuntimeException(String.format(
					"You may not support the current connection database:%s type, but you can use the createSQL interface to continue the operation.",
					productType));
		}

		List<String> columns = new LinkedList<>();

		while (rs.next()) {
			if (productType.equals(ProductType.SQLITE)) {
				String name = rs.getString(2);
				columns.add(name);
			} else {
				columns.add(rs.getString(1));
			}

		}

		if (columns.isEmpty())
			throw new QueryException(String.format(
					"The table name is not correct or does not have the right to access the table information. Try: check the table name or be case sensitive or try to use this database account to access the table in another way. (schema: %s, table: %s)",
					schema, tableName));

		return columns;
	}

	/**
	 * 
	 * @param context.productType()
	 * @param sql
	 * @param limit
	 * @return
	 */
	public static boolean processLimit(ChannelContext context, StringBuilder sql, Integer[] limit) {

		if (context.productType() == null || sql == null || limit == null || limit[0] == null)
			return false;

		switch (context.productType()) {
		case MYSQL:
			proceessCommonLimit(sql, limit);
			return true;

		case SQLITE:
			proceessCommonLimit(sql, limit);
			return true;

		case MICROSOFT_SQL_SERVER:
			proceessSqlServerLimit(sql, limit);
			return true;

		case ORACLE:
			proceessOracleLimit(sql, limit, context.getCommonFields());
			return true;

		case POSTGRESQL:
			processPostgreSQL(sql, limit);
			return true;

		default:
			return false;
		}
	}

	private static void proceessOracleLimit(StringBuilder sql, Integer[] limit, Collection<String> collection) {

		Assert.state(sql.toString().contains("FROM"), "Is not select sql:%s", sql);

		StringBuilder sb = new StringBuilder();
		collection.forEach(action -> sb.append("\"" + action + "\","));
		sb.setLength(sb.length() - 1);

		int start = limit.length == 1 ? 0 : limit[0];
		int end = start + (limit.length == 1 ? limit[0] : limit[1]);

		String preffix = String.format(
				"SELECT %s FROM (SELECT \"NAVICAT_TABLE\".*,ROWNUM \"NAVICAT_ROWNUM\" FROM (%s) \"NAVICAT_TABLE\" WHERE ROWNUM<=%d) WHERE \"NAVICAT_ROWNUM\"> %d",
				sb.toString(), sql.toString(), end, start);

		sql.setLength(0);
		sql.append(preffix);

	}

	private static void proceessCommonLimit(StringBuilder sql, Integer[] limit) {

		sql.append(" LIMIT " + limit[0]);
		if (limit.length > 1 && limit[1] != null && limit[1] > 0)
			sql.append("," + limit[1]);

	}

	private static void processPostgreSQL(StringBuilder sql, Integer[] limit) {

		if (limit.length > 1) {
			if (limit[1] == null || limit[1] <= 0)
				throw new ConditionException("limit[1] is null or equals 0");

			sql.append(String.format(" LIMIT %d OFFSET %d", limit[1], limit[0]));
		} else
			sql.append(" LIMIT " + limit[0]);

	}

	private static void proceessSqlServerLimit(StringBuilder sql, Integer[] limit) {

		sql.replace(0, 6, "SELECT TOP " + limit[0]).toString();
	}

	public static Optional<ProductType> index(String name) {

		Optional<ProductType> optional = Optional.empty();

		for (ProductType productType : values()) {
			if (!productType.getName().equalsIgnoreCase(name))
				continue;

			optional = Optional.of(productType);
			break;
		}

		return optional;
	}

}
