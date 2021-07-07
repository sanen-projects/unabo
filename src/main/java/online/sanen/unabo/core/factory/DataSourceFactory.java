package online.sanen.unabo.core.factory;

import java.sql.Connection;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbcp2.BasicDataSourceFactory;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mhdt.degist.Validate;
import com.mhdt.toolkit.Assert;
import com.zaxxer.hikari.HikariDataSource;

import online.sanen.unabo.api.structure.Configuration;
import online.sanen.unabo.api.structure.Configuration.DataSouseType;

/**
 * 
 * @author LazyToShow <br>
 *         Date: 2018/06/12 <br>
 *         Time: 09:17
 */
public class DataSourceFactory {

	public static DataSource create(final Configuration configuration) throws Exception {

		DataSouseType dataSouseType = configuration.dataSouseType();

		switch (dataSouseType) {

		case HikariCP:

			HikariDataSource hikariDataSource = new HikariDataSource();
			hikariDataSource.setJdbcUrl(configuration.url());
			hikariDataSource.setDriverClassName(configuration.driver());
			hikariDataSource.setUsername(configuration.username());
			hikariDataSource.setPassword(configuration.password());
			hikariDataSource.setConnectionTestQuery(configuration.validationQuery());
			hikariDataSource.setMaximumPoolSize(configuration.maxActive());

			hikariDataSource.setMaxLifetime(60000);
			hikariDataSource.setConnectionTimeout(5000);
			hikariDataSource.setValidationTimeout(1000);
			hikariDataSource.setConnectionTestQuery(configuration.validationQuery());

			try (Connection conn = hikariDataSource.getConnection()) {

			}

			return hikariDataSource;

		case C3p0:

			ComboPooledDataSource comboPooledDataSource = new ComboPooledDataSource();
			comboPooledDataSource.setJdbcUrl(configuration.url());
			comboPooledDataSource.setDriverClass(configuration.driver());
			comboPooledDataSource.setUser(configuration.username());
			comboPooledDataSource.setPassword(configuration.password());
			comboPooledDataSource.setPreferredTestQuery(configuration.validationQuery());

			comboPooledDataSource.setMaxPoolSize(configuration.maxActive());
			comboPooledDataSource.setMaxIdleTime(configuration.isRemoveAbandoned() ? 180 : 0);

			// MySQL 8 hour problem
			comboPooledDataSource.setTestConnectionOnCheckout(false);
			comboPooledDataSource.setTestConnectionOnCheckin(true);
			comboPooledDataSource.setIdleConnectionTestPeriod(600);

			try (Connection conn = comboPooledDataSource.getConnection()) {

			}

			return comboPooledDataSource;

		case Druid:
			DruidDataSource druidDataSource = (DruidDataSource) DruidDataSourceFactory
					.createDataSource(createProperties(configuration));

			druidDataSource.setRemoveAbandoned(configuration.isRemoveAbandoned());
			druidDataSource.setRemoveAbandonedTimeout(180);
			druidDataSource.setMaxWait(5000);
			druidDataSource.setMaxActive(configuration.maxActive());

			druidDataSource.setValidationQuery(configuration.validationQuery());
			druidDataSource.setValidationQueryTimeout(5000);

			// MySQL 8 hour problem
			druidDataSource.setTestOnBorrow(false);
			druidDataSource.setLogAbandoned(true);
			druidDataSource.setTestWhileIdle(true);
			druidDataSource.setTimeBetweenEvictionRunsMillis(60000);

			try (Connection conn = druidDataSource.getConnection()) {
				druidDataSource.validateConnection(conn);
			}

			return druidDataSource;

		default:

			BasicDataSource dataSource = BasicDataSourceFactory.createDataSource(createProperties(configuration));
			dataSource.setValidationQuery(configuration.validationQuery());
			dataSource.setValidationQueryTimeout(5);
			dataSource.setMaxIdle(configuration.maxActive());
			dataSource.setRemoveAbandonedOnBorrow(configuration.isRemoveAbandoned());
			dataSource.setRemoveAbandonedTimeout(180);

			// MySQL 8 hour problem
			dataSource.setMinEvictableIdleTimeMillis(3600000);
			dataSource.setTimeBetweenEvictionRunsMillis(600000);

			try (Connection conn = dataSource.getConnection()) {
				dataSource.invalidateConnection(conn);
			}

			return dataSource;
		}

	}

	private static Properties createProperties(Configuration configuration) {

		Assert.notNull(configuration.driver(), "Driver is null from Obstract");
		Assert.notNull(configuration.url(), "Url is null from Obstract");

		if (!configuration.driver().contains("sqlite")) {
			Assert.notNull(configuration.username(), "Username is null from Obstract");
			Assert.notNull(configuration.password(), "Password is null from Obstract");
		}

		return new Properties() {
			private static final long serialVersionUID = 1L;
			{
				setProperty("driverClassName", configuration.driver());
				setProperty("url", configuration.url());
				if (!Validate.isNullOrEmpty(configuration.username()))
					setProperty("username", configuration.username());
				if (!Validate.isNullOrEmpty(configuration.password()))
					setProperty("password", configuration.password());
				setProperty("validationQuery", "select 1");
			}
		};

	}

}
