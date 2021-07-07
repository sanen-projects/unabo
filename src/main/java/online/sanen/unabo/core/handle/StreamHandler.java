package online.sanen.unabo.core.handle;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import com.mhdt.degist.Validate;
import com.mhdt.toolkit.Reflect;

import online.sanen.unabo.api.Handel;
import online.sanen.unabo.api.exception.QueryException;
import online.sanen.unabo.api.structure.ChannelContext;
import online.sanen.unabo.api.structure.Column;
import online.sanen.unabo.api.structure.StreamConsumer;
import online.sanen.unabo.template.JdbcUtils;
import online.sanen.unabo.template.SqlTemplate;

/**
 * 
 *
 * @author LazyToShow <br>
 *         Date: 2018年9月11日 <br>
 *         Time: 上午10:56:44
 */
public class StreamHandler implements SimpleHandler,Handel {

	private Consumer<List<Map<String, Object>>> consumer;

	private Map<String, String> aliases;

	private int bufferSize = 10000;

	private int count;

	public StreamHandler(int bufferSize, Consumer<List<Map<String, Object>>> datas) {
		this(bufferSize, datas, null);
	}

	public StreamHandler(int bufferSize, Consumer<List<Map<String, Object>>> consumer, Map<String, String> aliases) {
		this.consumer = consumer;
		this.aliases = aliases;
		if (bufferSize > 0)
			this.bufferSize = bufferSize;
	}

	public StreamHandler(int count) {
		this.count = count;
	}

	Function<List<Column>, Object> function_column_process;

	StreamConsumer streamConsumer;

	public StreamHandler(int bufferSize, Function<List<Column>, Object> consumer, StreamConsumer datas,
			Map<String, String> aliases) {
		if (bufferSize > 0)
			this.bufferSize = bufferSize;

		this.function_column_process = consumer;
		this.streamConsumer = datas;
		this.aliases = aliases;
	}
	
	
	@Override
	public Object handel(ChannelContext context, Object product) {

		String sql = context.getSql().toString();

		Collection<Object> paramers = context.getParamers();

		SqlTemplate template = (SqlTemplate) context.getTemplate();

		try (Connection connection = template.getDataSource().getConnection()) {

			// connection.setAutoCommit(false);
			PreparedStatement ps = connection.prepareStatement(sql);

			initFetchSize(ps, context.productType());

			// Set the parameters
			int index = 1;
			for (Iterator<Object> iterator = paramers.iterator(); iterator.hasNext();) {
				Object object = (Object) iterator.next();

				try {

					Method method = Reflect.getMethod(ps, "set" + object.getClass().getSimpleName(), int.class,object.getClass());
					method.invoke(ps, index++, object);

				} catch (Exception e) {
					ps.setString(index++, object.toString());
				}
			}

			ps.execute();
			ResultSet rs = ps.getResultSet();

			// Assembly fields
			ResultSetMetaData metaData = rs.getMetaData();

			List<Column> dataFields = new ArrayList<>();

			List<String> columns = new ArrayList<>();
			for (int i = 0; i < metaData.getColumnCount(); i++) {

				Column dataField = new Column();
				dataField.setName(metaData.getColumnLabel(i + 1));
				dataField.setCls(metaData.getColumnClassName(i + 1));
				dataField.setType(metaData.getColumnTypeName(i + 1));
				dataFields.add(dataField);
				columns.add(dataField.getName());
			}

			
			
			List<Map<String, Object>> list = new LinkedList<>();

			Object object = null;

			if (function_column_process != null)
				object = function_column_process.apply(dataFields);

			if (count > 0) {

				while (rs.next()) {
					
					list.add(populate(rs, columns));
					if (list.size() == count)
						break;

				}

				return list;

			} else {

				while (rs.next()) {
					Map<String, Object> row = populate(rs, columns);
					list.add(row);
					
					// Writes cached data
					if (list.size() == bufferSize) {
						consumeDatas(object, list);
						list.clear();
					}
				}

				if (list.size() > 0)
					consumeDatas(object, list);
			}

		} catch (QueryException e) {
			throw e;
		} catch (Exception e) {
			throw new QueryException(e);
		}

		return null;
	}

	private void consumeDatas(Object object, List<Map<String, Object>> list) {
		
		if (streamConsumer != null)
			streamConsumer.accept(object, list);
		else
			consumer.accept(list);
	}

	private Map<String, Object> populate(ResultSet rs, List<String> columns) {

		Map<String, Object> map = new LinkedHashMap<>(columns.size());

		columns.forEach(column -> {

			try {

				String key = (aliases != null && aliases.containsKey(column) && !Validate.isNullOrEmpty(aliases.get(column))) ? aliases.get(column) : column;
				Object value = JdbcUtils.getResultSetValue(rs, rs.findColumn(column));

				map.put(key, value);

			} catch (SQLException e) {
				throw new QueryException(e);
			}
		});

		return map;
	}

}
