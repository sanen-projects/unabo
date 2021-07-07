package online.sanen.unabo.api.component;

import online.sanen.unabo.api.structure.Configuration;
import online.sanen.unabo.api.structure.enums.ProductType;
import online.sanen.unabo.template.SqlTemplate;

/**
 * 
 * @author LazyToShow 
 * Date: 2017/10/21 
 * Time: 23:19
 */
public class ManagerBridge implements Manager {

	Manager manager;

	public ManagerBridge(Manager manager) {
		this.manager = manager;
	}

	@Override
	public SqlTemplate getTemplate() {
		return manager.getTemplate();
	}

	@Override
	public void setTemplate(SqlTemplate template) {
		manager.setTemplate(template);
	}

	public ProductType productType() {
		try {
			return manager.productType();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean isCache() {
		return manager.isCache();
	}

	@Override
	public boolean isShowSql() {
		return manager.isShowSql();
	}

	@Override
	public boolean isLog() {
		return manager.isLog();
	}

	@Override
	public String databaseName() {
		return manager.databaseName();
	}

	@Override
	public String getUrl() {
		return manager.getUrl();
	}

	@Override
	public String getId() {
		return manager.getId();
	}

	@Override
	public void setId(Object id) {
		manager.setId(id);
	}

	@Override
	public boolean isSqlFormat() {
		return manager.isSqlFormat();
	}

	@Override
	public Configuration getConfiguration() {
		return manager.getConfiguration();
	}

	@Override
	public void setConfiguration(Configuration configuration) {
		manager.setConfiguration(configuration);
	}

	@Override
	public String getLastSql() {
		return manager.getLastSql();
	}

	@Override
	public synchronized void setLastSql(String sql) {
		manager.setLastSql(sql);
	}
}
