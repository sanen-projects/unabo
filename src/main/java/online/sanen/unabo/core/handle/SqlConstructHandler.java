package online.sanen.unabo.core.handle;

import static com.mhdt.toolkit.Assert.notNullOrEmpty;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

import com.mhdt.degist.Validate;

import online.sanen.unabo.api.Handel;
import online.sanen.unabo.api.QuerySql;
import online.sanen.unabo.api.exception.ConditionException;
import online.sanen.unabo.api.exception.QueryException;
import online.sanen.unabo.api.structure.ChannelContext;
import online.sanen.unabo.api.structure.enums.ProductType;
import online.sanen.unabo.core.SqlConversion;
import online.sanen.unabo.template.jpa.JPA;
import online.sanen.unabo.template.jpa.JPA.Primarykey;

/**
 * 根据structue构造sql
 * 
 * @author LazyToShow <br>
 *         Date: 2017/10/21 <br>
 *         Time: 23:19
 */
public class SqlConstructHandler implements SimpleHandler,Handel {

	@Override
	public Object handel(ChannelContext context, Object product) {

		context.getParamers().clear();

		// 如果指定了sql 直接返回
		if (context.getCls() != null && context.getClass().isAssignableFrom(QuerySql.class))
			return null;

		context.getSql().setLength(0);

		// 获取表名
		String tableName = this.modifiedTableName(context.getTableName(), context.getSchema(), context.productType());

		// 表名为空判定为严重异常，终止进程
		if (Validate.isNullOrEmpty(tableName))
			throw new ConditionException("Cant get class from setTableName method or entry");

		// 根据query类别来生成Sql
		switch (context.getQueryType()) {
		case insert:
			createInsertSql(tableName, context);
			break;

		case delete:
			createRemoveSql(tableName, context);
			break;

		case update:
			createUpdateSql(tableName, context);
			break;

		case select:
			createSelectSql(tableName, context);
			break;

		case count:
			createCountSql(tableName, context);
			break;

		case create:
			createCreateSql(tableName, context);
			break;

		case drop:
			createDropSql(tableName, context);
			break;

		default:
			break;
		}

		return null;
	}

	private void createDropSql(String tableName, ChannelContext structure) {

		structure.setSql("DROP TABLE " + tableName);
	}

	private void createCreateSql(String tableName, ChannelContext context) {

		Map<String, Class<?>> map = new LinkedHashMap<>();

		// Convert Entry into the structure ( name-> typeClass )
		if (context.getEntityMap() != null || context.getEntityMaps() != null) {

			Map<String, Object> entityMap = context.getEntityMap() != null ? context.getEntityMap()
					: context.getEntityMaps().stream().findFirst().get();

			for (Map.Entry<String, Object> entry : entityMap.entrySet()) {

				if (entry.getValue() == null)
					map.put(entry.getKey(), String.class);
				else
					map.put(entry.getKey(), entry.getValue().getClass());
			}

		} else {
			map = new LinkedHashMap<>(JPA.structured(context.getEntityClass()));
			context.setPrimaryKey(this.getPrimaryKey(context.getEntityClass()));
		}

		// Filter field
		if (context.getFields() != null)
			map.keySet().retainAll(context.getFields());
		else if (context.getExceptes() != null)
			map.keySet().removeAll(context.getExceptes());

		notNullOrEmpty(map, "An empty table cannot be created because no valid reserved fields are specified.");

		String organizationFields = organizationFields(context.getPrimaryKey(), context.productType(), map);
		String sql = String.format("CREATE TABLE %s (%s)", tableName, organizationFields);

		context.setSql(sql);
	}

	/**
	 * 
	 * @param primarykey
	 * @param map
	 * @return
	 * @throws SQLException
	 */
	private  String organizationFields(Primarykey primarykey, ProductType productType,
			Map<String, Class<?>> map) {
		StringBuilder sb = new StringBuilder();

		String modifer = ProductType.applyTableModifier(productType);

		for (Map.Entry<String, Class<?>> entry : map.entrySet()) {

			sb.append(modifer + entry.getKey() + modifer + " ");
			sb.append("${" + entry.getValue().getSimpleName().toUpperCase() + "}");

			if (primarykey != null && primarykey.getName().equals(entry.getKey())
					&& Validate.isNumber(entry.getValue()))
				sb.append(" ${PRIMARY}");

			sb.append(",");
		}

		if (sb.length() > 0)
			sb.setLength(sb.length() - 1);

		return new SqlConversion() {
			@Override
			public ProductType applyProductType() {
				return productType;
			}

			@Override
			public String applyPrimaryKey() {
				return primarykey.getName();
			}
		}.apply(sb.toString());
	}

	private void createSelectSql(String tableName, ChannelContext context) {

		StringBuilder sb = context.getSql();
		String modifer = ProductType.applyTableModifier(context.productType());

		sb.append("SELECT ");

		for (String field : context.getCommonFields()) {

			if (context.isQualifier()) {
				sb.append(String.format("%s%s%s,", modifer, field, modifer));
			} else {
				sb.append(String.format("%s,", field));
			}
		}

		sb.setLength(sb.length() - 1);
		sb.append(" FROM " + tableName);
	}

	private void createCountSql(String tableName, ChannelContext context) {
		StringBuilder sb = context.getSql();
		String modifer = ProductType.applyTableModifier(context.productType());

		sb.append(String.format("SELECT COUNT(%s) FROM %s%s%s", 
				Validate.isNullOrEmpty(context.getCountField()) ? "1" : modifer+context.getCountField()+modifer, modifer,
				context.getTableName(), modifer));
	}

	private void createUpdateSql(String tableName, ChannelContext context) {

		String modifer = ProductType.applyTableModifier(context.productType());

		context.getSql().append("UPDATE " + tableName + " SET ");

		for (String field : context.getCommonFields()) {

			if (context.isQualifier()) {
				context.getSql().append(modifer + field + modifer + "=?,");
			} else {
				context.getSql().append(field + "=?,");
			}

		}

		context.getSql().setLength(context.getSql().length() - 1);

	}

	private void createRemoveSql(String tableName, ChannelContext context) {
		context.getSql().append("DELETE FROM " + tableName);

	}

	private void createInsertSql(String tableName, ChannelContext context) {

		if (context.getCommonFields().isEmpty())
			throw new QueryException("Cannot get generic fields from table: " + tableName);

		String modifier = ProductType.applyTableModifier(context.productType());

		String sql = "INSERT INTO " + tableName + " (";
		String sql1 = " values (";

		for (String field : context.getCommonFields()) {

			if (context.isQualifier()) {
				sql += modifier + field + modifier + ",";
			} else {
				sql += field + ",";
			}

			sql1 += "?,";
		}

		sql = sql.substring(0, sql.lastIndexOf(",")) + ")";
		sql1 = sql1.substring(0, sql1.lastIndexOf(",")) + ")";

		context.getSql().append(sql + sql1);
	}

}
