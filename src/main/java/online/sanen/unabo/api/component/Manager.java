package online.sanen.unabo.api.component;

import online.sanen.unabo.api.structure.Configuration;
import online.sanen.unabo.api.structure.enums.ProductType;
import online.sanen.unabo.template.SqlTemplate;

/**
 * Global configuration，So far, it hasn't done its job .
 * 
 * @author LazyToShow <br>
 *         Date: 2017/10/21 <br>
 *         Time: 23:19
 */
public interface Manager {

	void setTemplate(SqlTemplate template);

	SqlTemplate getTemplate();

	boolean isShowSql();

	boolean isSqlFormat();

	boolean isCache();

	boolean isLog();

	String databaseName();

	ProductType productType();

	Configuration getConfiguration();

	void setConfiguration(Configuration configuration);

	String getUrl();

	String getId();

	void setId(Object id);

	String getLastSql();

	void setLastSql(String sql);
}
