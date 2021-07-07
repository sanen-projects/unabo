package online.sanen.unabo.core.handle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

import online.sanen.unabo.api.Handel;
import online.sanen.unabo.api.exception.QueryException;
import online.sanen.unabo.api.structure.ChannelContext;
import online.sanen.unabo.api.structure.Column;
import online.sanen.unabo.template.SqlTemplate;


/**
 * 
 * Extract field information from Sql statements
 *
 * @author LazyToShow <br>
 *         Date: 2018/09/10 <br>
 *         Time: PM 9:31:42
 */
public class SqlColumnsExtractHandler implements SimpleHandler,Handel {

	@Override
	public Object handel(ChannelContext structure, Object product) {

		String sql = structure.getSql().toString();

		SqlTemplate template = (SqlTemplate) structure.getTemplate();

		try (Connection connection = template.getDataSource().getConnection()) {

			PreparedStatement ps = connection.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);

			initFetchSize(ps, structure.productType());

			ResultSet rs = ps.executeQuery();

			// Assembly fields
			ResultSetMetaData metaData = rs.getMetaData();

			List<Column> dataFields = new ArrayList<>();

			for (int i = 0; i < metaData.getColumnCount(); i++) {

				Column dataField = new Column();

				dataField.setName(metaData.getColumnLabel(i + 1));
				dataField.setCls(metaData.getColumnClassName(i + 1));
				dataField.setType(metaData.getColumnTypeName(i + 1));
				dataFields.add(dataField);
			}

			return dataFields;

		} catch (Exception e) {
			throw new QueryException(e);
		}

	}

}
