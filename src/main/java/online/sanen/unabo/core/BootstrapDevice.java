package online.sanen.unabo.core;

import java.io.InputStream;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;

import javax.sql.DataSource;

import com.mhdt.degist.Template;
import com.mhdt.degist.Validate;
import com.mhdt.toolkit.Assert;
import com.mhdt.toolkit.Reflect;

import lombok.extern.slf4j.Slf4j;
import online.sanen.unabo.api.Bootstrap;
import online.sanen.unabo.api.QueryEntity;
import online.sanen.unabo.api.QueryMap;
import online.sanen.unabo.api.QueryPK;
import online.sanen.unabo.api.QuerySql;
import online.sanen.unabo.api.QueryTable;
import online.sanen.unabo.api.component.Manager;
import online.sanen.unabo.api.exception.QueryException;
import online.sanen.unabo.api.exception.SupportsException;
import online.sanen.unabo.api.structure.Configuration;
import online.sanen.unabo.api.structure.DataInformation;
import online.sanen.unabo.core.factory.DataSourceFactory;
import online.sanen.unabo.core.factory.Unabo;
import online.sanen.unabo.core.handle.SimpleHandler;
import online.sanen.unabo.core.infomation.MSInfomation;
import online.sanen.unabo.core.infomation.MySQLInfomation;
import online.sanen.unabo.core.infomation.OracleInfomation;
import online.sanen.unabo.core.infomation.PostgreInfomation;
import online.sanen.unabo.core.infomation.SQLiteInfomation;
import online.sanen.unabo.extend.mapper.ProxyFactory;
import online.sanen.unabo.extend.mapper.YAMLMapperConfiguring;
import online.sanen.unabo.extend.mapper.YAMLProxyHandel;
import online.sanen.unabo.template.SqlTemplate;
import online.sanen.unabo.template.transaction.TransactionFactory;

/**
 * 
 * @author online.sanen <br>
 *         Date: 2017/10/21 <br>
 *         Time: 23:19
 */
@Slf4j
public class BootstrapDevice implements Bootstrap, SimpleHandler {

	Manager manager;

	public BootstrapDevice(Configuration configuration, Object id) {

		try {
			manager = new ManagerDevice(id);
			manager.setConfiguration(configuration);

			DataSource dataSource = DataSourceFactory.create(configuration);
			setTemplate(new SqlTemplate(dataSource));
				
			if (!Validate.isNullOrEmpty(configuration.getTransactionFactory())) {
				Class<?> classForTransaction = configuration.getTransactionFactory().getCls();
				this.manager().getTemplate().bindTransaction(((TransactionFactory) Reflect.newInstance(classForTransaction)));
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	public void setTemplate(SqlTemplate template) {

		manager.setTemplate(template);
		Configuration configuration = manager.getConfiguration();

		if (configuration.isLog()) {
			Template tem = new Template(BootstrapDevice.class.getClassLoader().getResourceAsStream("info.ftl"));
			tem.setParamer("dataBase", manager.productType().toString());
			tem.setParamer("url", manager.getUrl());
			log.info(tem.getText());
		}

	}

	@Override
	public QuerySql createSQL(String sql, Object... paramers) {

		if (Validate.isNullOrEmpty(sql))
			throw new NullPointerException("Sql is null");

		return new QuerySqlDevice(manager, sql, paramers);
	}

	@Override
	public QuerySql createSQL(String sql) {

		if (Validate.isNullOrEmpty(sql))
			throw new NullPointerException("Sql is null");

		return new QuerySqlDevice(manager, sql);
	}

	@Override
	public <T> QueryEntity query(T entry) {

		if (entry == null)
			throw new NullPointerException("Entry is null");

		if (manager == null)
			throw new NullPointerException("manager is null");

		return new QueryEntityDevice(manager, entry);
	}

	@Override
	public QueryTable queryTable(String tableName) {

		if (Validate.isNullOrEmpty(tableName))
			throw new NullPointerException("Query table name is null or empty.");

		return new QueryTableDevice(manager, tableName);
	}

	@Override
	public <T> QueryPK<T> queryPk(Class<T> entryCls, Object primarykey) {

		if (entryCls == null)
			throw new NullPointerException("Entry class is null");

		if (primarykey == null)
			throw new NullPointerException("Primary Key is null");

		return new QueryPKDevice<T>(manager, entryCls, primarykey);
	}

	@Override
	public <T> QueryEntity query(Collection<T> entities) {

		if (entities == null || entities.isEmpty())
			throw new QueryException("entities is null or empty");

		return new QueryEntityDevice(manager, entities);
	}

	@Override
	public Manager manager() {
		return manager;
	}

	DataInformation dataInformation;

	@Override
	public DataInformation dataInformation() {

		if (dataInformation != null)
			return dataInformation;

		switch (manager().productType()) {
		case MYSQL:
			dataInformation = new MySQLInfomation(this);
			break;

		case MICROSOFT_SQL_SERVER:
			dataInformation = new MSInfomation(this);
			break;

		case SQLITE:
			dataInformation = new SQLiteInfomation(this);
			break;

		case ORACLE:
			dataInformation = new OracleInfomation(this);
			break;

		case POSTGRESQL:
			dataInformation = new PostgreInfomation(this);
			break;

		default:
			throw new SupportsException(manager().productType());
		}

		return dataInformation;
	}

	@Override
	public QueryMap queryMap(String tableName, Map<String, Object> map) {

		Assert.notNull(tableName, "TableName is null");
		Assert.notNull(map, "Map is null");
		Assert.state(!map.isEmpty(), "Map is empty");

		return new QueryMapDevice(manager, tableName, map);
	}

	@Override
	public <T extends Map<String, Object>> QueryMap queryMap(String tableName, Collection<T> maps) {

		Assert.state(maps != null && !maps.isEmpty(), "Entrys is null or empty");

		return new QueryMapDevice(manager, tableName, maps);
	}

	@Override
	public <T> QueryTable queryTable(Class<T> cls) {
		return queryTable(Unabo.tableNameByClass(cls));
	}

	@Override
	public String getLastSql() {
		return manager.getLastSql();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T mapper(InputStream inputStream, Class<T> cls) {

		YAMLMapperConfiguring.load(cls.getName(), inputStream);
		T t = (T) ProxyFactory.getInstance(new YAMLProxyHandel(this, cls), cls);

		return t;
	}

	@Override
	public void openSession() {
		manager().getTemplate().openSession();
	}

	@Override
	public void commit() throws SQLException {
		manager().getTemplate().commit();
	}

	@Override
	public void rollback() throws SQLException {
		manager().getTemplate().rollback();
	}

}
