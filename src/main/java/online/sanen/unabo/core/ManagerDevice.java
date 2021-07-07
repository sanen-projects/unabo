package online.sanen.unabo.core;

import java.sql.Connection;
import java.sql.SQLException;

import com.mhdt.toolkit.StringUtility;

import online.sanen.unabo.api.component.Manager;
import online.sanen.unabo.api.exception.QueryException;
import online.sanen.unabo.api.exception.StructuralException;
import online.sanen.unabo.api.structure.Configuration;
import online.sanen.unabo.api.structure.enums.ProductType;
import online.sanen.unabo.template.SqlTemplate;


/**
 * 
 * @author LazyToShow <br>
 *         Date: 2017/10/21 <br>
 *         Time: 23:19
 */
public class ManagerDevice implements Manager {

	SqlTemplate template;
	
	Configuration configuration;

	String id;

	public ManagerDevice(Object id) {
		setId(id);
	}

	@Override
	public void setTemplate(SqlTemplate template) {
		this.template = template;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(Object id) {
		this.id = id == null ? null : String.valueOf(id);
	}

	@Override
	public boolean isShowSql() {
		return configuration.isShowSql();
	}

	public SqlTemplate getTemplate() {
		if(template==null)
			throw new NullPointerException("template is null");
		
		return template;
	}

	String productName;

	@Override
	public ProductType productType() {

		if (productName == null) {

			try (Connection conn = getTemplate().getDataSource().getConnection()) {
				productName = conn.getMetaData().getDatabaseProductName().toUpperCase();
			} catch (Exception e) {
				throw new StructuralException(e);
			}
		}

		return ProductType.valueOf(StringUtility.removeBlankChar(productName.replaceAll(" ", "_")));
	}

	@Override
	public String databaseName() {

		try (Connection conn = template.getDataSource().getConnection()) {
			return conn.getCatalog();
		} catch (Exception e) {
			throw new QueryException(e);
		}

	}

	@Override
	public boolean isCache() {
		return configuration.isCache();
	}

	public boolean isLog() {
		return configuration.isLog();
	}


	@Override
	public boolean isSqlFormat() {
		return configuration.isFormat();
	}

	@Override
	public String getUrl() {

		try (Connection conn = ((SqlTemplate) getTemplate()).getDataSource().getConnection()) {
			return conn.getMetaData().getURL();
		} catch (SQLException e) {
			throw new QueryException(e.getMessage());
		}
	}

	@Override
	public Configuration getConfiguration() {
		return configuration;
	}

	@Override
	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

	String lastSql;
	
	@Override
	public String getLastSql() {
		return lastSql;
	}

	@Override
	public void setLastSql(String sql) {
		this.lastSql = sql;
	}

}
