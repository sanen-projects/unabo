package online.sanen.unabo.core.handle;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.mhdt.toolkit.Reflect;

import online.sanen.unabo.api.Handel;
import online.sanen.unabo.api.exception.QueryException;
import online.sanen.unabo.api.structure.ChannelContext;
import online.sanen.unabo.api.structure.enums.ProductType;
import online.sanen.unabo.api.structure.enums.QueryType;
import online.sanen.unabo.template.SqlTemplate;
import online.sanen.unabo.template.jpa.JPA;
import online.sanen.unabo.template.jpa.JPA.Primarykey;

/**
 * 
 * @author LazyToShow <br>
 *         Date: 2017/11/29 <br>
 *         Time： 11：51
 */
public class BatchOperationHandler implements SimpleHandler,Handel {

	/**
	 * The original return value of batch operations is an int array, and now it is
	 * omitted to return 1 or -1 to represent whether an exception has occurred
	 */
	@Override
	public Object handel(ChannelContext context, Object product) {

		try {

			if ((context.getEntities() == null || context.getEntities().isEmpty())
					&& (context.getEntityMaps() == null || context.getEntityMaps().isEmpty()))
				return new NullPointerException("Batch operation data source is null");

			QueryType type = context.getQueryType();
			SqlTemplate template = (SqlTemplate) context.getTemplate();

			// Batch delete does not exist, this is to unify the interface to do the
			// adaptation,
			// delete the way to use in function, separate processing
			if (type.equals(QueryType.delete)) {

				Primarykey primaryKey = context.getPrimaryKey();

				return batchRemove(template, context.getEntities(), context.getSql(), primaryKey);
			}

			// if update or insert should add where condition by primary key
			if (type.equals(QueryType.update)) {

				try {

					appendPrimaryCondition(context.getSql(), context.getPrimaryKey());

				} catch (NullPointerException e) {
					e.printStackTrace();
				}

			}

			batchUpdate(template, context);
			return 1;

		} catch (Exception e) {
			throw new QueryException(e);
		}

	}

	private void batchUpdate(SqlTemplate template, ChannelContext context) throws SQLException,
			IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {

		if (context.getEntityMaps() != null
				&& (context.productType() == ProductType.SQLITE || context.productType() == ProductType.MYSQL)
				&& context.getQueryType().equals(QueryType.insert)) {
			storedProcedure(context, template);
		} else {
			template.batchUpdate(context.getSql().toString(), commonBatch(context, template));
		}

	}

	private LinkedList<Object[]> commonBatch(ChannelContext context, SqlTemplate template)
			throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		QueryType type = context.getQueryType();
		LinkedList<Object[]> paramer_objects = new LinkedList<>();

		if (context.getEntityMaps() != null && !context.getEntityMaps().isEmpty()) {

			for (Map<String, Object> map : context.getEntityMaps()) {
				LinkedList<Object> paramers = new LinkedList<>();

				context.getCommonFields().forEach(field -> paramers.add(map.get(field)));

				if (QueryType.update == type)
					paramers.add(map.get(context.getPrimaryKey().getName()));

				paramer_objects.add(paramers.toArray());
			}

		} else {

			for (Object entity : context.getEntities()) {

				// if insert or update should add fields
				LinkedList<Object> paramers = new LinkedList<>();

				if (type.equals(QueryType.insert) || type.equals(QueryType.update))
					paramers.addAll(getValues(entity, context.getCommonFields()));

				// if update should add primary key
				if (type.equals(QueryType.update))
					paramers.add(getPrimaryValua(entity));

				paramer_objects.add(paramers.toArray());
			}

		}

		return paramer_objects;

	}

	private void storedProcedure(ChannelContext context, SqlTemplate template) throws SQLException {

		if (context.productType() == ProductType.SQLITE)
			template.execute("PRAGMA synchronous = OFF");

		try (Connection conn = template.getDataSource().getConnection();
				PreparedStatement pst = conn.prepareStatement(context.getSql().toString())) {
			conn.setAutoCommit(false);

			for (Map<String, Object> map : context.getEntityMaps()) {
				int i = 0;
				for (String field : context.getCommonFields())
					pst.setObject(++i, map.get(field));
				pst.executeUpdate();
			}

			conn.commit();
		}

	}

	/**
	 * Batch delete use in function
	 * 
	 * @param template
	 * @param entrys
	 * @param sql
	 * @param basicBean
	 * @return
	 */
	private Object batchRemove(SqlTemplate template, Collection<?> entrys, StringBuilder sql, Primarykey primarykey) {
		try {

			List<Object> paramers = new ArrayList<>();

			sql.append(" where " + primarykey.getName() + " in(");

			for (Object entry : entrys) {
				sql.append("?,");
				paramers.add(primarykey.getValue(entry));
			}

			sql.setLength(sql.length() - 1);
			sql.append(")");

			template.update(sql.toString(), paramers.toArray());
			return 1;

		} catch (Exception e) {
			throw new QueryException(e);
		}

	}

	private void appendPrimaryCondition(StringBuilder sql, Primarykey primaryKey) {
		sql.append(" where " + primaryKey.getName() + "=?");
	}

	/**
	 * Get the primary key
	 * 
	 * @param entity
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 */
	private Object getPrimaryValua(Object entity)
			throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {

		Primarykey primaryKey = this.getPrimaryKey(entity.getClass());
		return primaryKey.getValue(entity);

	}

	/**
	 * Gets the value of the common field
	 * 
	 * @param entity
	 * @param commonFields
	 * @return
	 */
	private List<Object> getValues(Object entity, Collection<String> commonFields) {

		List<Object> list = new ArrayList<>();

		try {

			for (String field : commonFields) {
				Field f = Reflect.getField(entity, field);
				f = (f == null ? JPA.getFieldByAlias(entity, field) : f);
				list.add(f.get(entity));
			}

		} catch (Exception e) {
			throw new QueryException(e);
		}

		return list;
	}

}
