package online.sanen.unabo.core.factory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import com.mhdt.degist.Validate;
import com.mhdt.structure.cache.Cache;
import com.mhdt.structure.cache.LRUCache;
import com.mhdt.toolkit.Assert;
import com.mhdt.toolkit.Reflect;

import online.sanen.unabo.api.Bootstrap;
import online.sanen.unabo.api.exception.StructuralException;
import online.sanen.unabo.api.structure.SimpleConfiguration;
import online.sanen.unabo.api.structure.Configuration.DataSouseType;
import online.sanen.unabo.api.structure.enums.DriverOption;
import online.sanen.unabo.core.BootstrapDevice;
import online.sanen.unabo.template.jpa.JPA;
import online.sanen.unabo.template.jpa.Table;
import online.sanen.unabo.template.transaction.Transaction;

/**
 * A {@link Bootstrap} instance is quickly created to replace the <b>new</b>
 * method, and it also has certain container functions
 * 
 * @author LazyToShow <br>
 *         Date: 2018/06/12 <br>
 *         Time: 09:17
 */
public class Unabo {

	static Cache<Object, Bootstrap> exists = new LRUCache<>(10);

	public static boolean contains(Object key) {
		return exists.contains(key);
	}

	public static Bootstrap get(Object key) {

		return exists.get(key);
	}

	/**
	 * 
	 * @param bootStrapId
	 * @param bootstrap
	 * @return
	 */
	public static Bootstrap put(Object bootStrapId, Bootstrap bootstrap) {

		exists.put(bootStrapId, bootstrap);
		return bootstrap;

	}

	public void  remove(String bootstrapId) {
		 exists.remove(bootstrapId);
	}

	/**
	 * 
	 * @param configuration
	 * @return
	 */
	public static Bootstrap load(Consumer<SimpleConfiguration> configuration) {
		return load(null, configuration);
	}

	
	/**
	 * 
	 * @param id
	 * @param configuration
	 * @return
	 */
	public static Bootstrap load(Object id, Consumer<SimpleConfiguration> configuration) {

		if (exists.contains(id)) {
			return exists.get(id);
		} else {
			SimpleConfiguration conf = new SimpleConfiguration();
			configuration.accept(conf);

			Bootstrap bootstrap = new BootstrapDevice(conf, id);

			Assert.notNull(bootstrap, "Bootstrap is null");

			if (id != null)
				exists.put(id, bootstrap);

			return bootstrap;
		}
	}

	/**
	 * Whether the BootStrap instance is unique
	 * 
	 * @return boolean
	 */
	public static boolean isUniqueness() {
		return exists.size() == 1;
	}

	/**
	 * Returns the first {@link Bootstrap} instance, or null if the cache is empty.
	 * 
	 * @return {@link Bootstrap}
	 */
	public static Bootstrap getFirst() {

		try {
			
			return exists.get(exists.keySet().stream().findFirst().get());
		} catch (NullPointerException e) {
			throw new StructuralException(
					"The default Bootstrap instance cannot be obtained from the Bootstrap factory,Reason may be that instance to not Bootstraps trust or no BootstrapId specified");
		}

	}

	/**
	 * 
	 */
	static Map<Bootstrap, Transaction> transactions = new HashMap<>();

	/**
	 * 
	 * @param bootstrap
	 * @param transaction
	 */
	public static void registedTransaction(Bootstrap bootstrap, Transaction transaction) {

		transactions.put(bootstrap, transaction);
	}

	/**
	 * 
	 * @param bootstrapId
	 * @return
	 */
	public static BootstrapBuilder newBuilder(String bootstrapId) {
		return new BootstrapBuilder(bootstrapId);
	}

	/**
	 * 
	 * @author LazyToShow <br>
	 *         Date: Mar 6, 2019 <br>
	 *         Time: 3:44:12 PM <br>
	 */
	public static class BootstrapBuilder {

		private Object bootStrapId;
		private String driver;
		private String username;
		private String password;
		private Boolean isShowSql;
		private String url;
		private String validationQuery;
		private DataSouseType dataSouseType;
		private Boolean isFormat;
		private Boolean isCache;
		private String mapper_locations;

		private BootstrapBuilder(String bootstrapId2) {

			Assert.notNull(bootstrapId2, "bootstrap cannot be null");
			this.bootStrapId = bootstrapId2;
		}

		public BootstrapBuilder setBootstrapId(Object bootStrapId) {
			this.bootStrapId = bootStrapId;
			return this;
		}

		public BootstrapBuilder setDriver(DriverOption driverOption) {
			this.driver = driverOption.getValue();
			return this;
		}

		public BootstrapBuilder setUsername(String username) {
			this.username = username;
			return this;
		}

		public BootstrapBuilder setPassword(String password) {
			this.password = password;
			return this;
		}

		public BootstrapBuilder setShowSql(boolean isShowSql) {
			this.isShowSql = isShowSql;
			return this;
		}

		public BootstrapBuilder setIsCache(boolean isCache) {
			this.isCache = isCache;
			return this;
		}

		public BootstrapBuilder setIsFormat(boolean isFormat) {
			this.isFormat = isFormat;
			return this;
		}

		/**
		 * Set the JDBC connection url
		 * 
		 * - Enumerate the different database connections:
		 * 
		 * <pre>
		 * 				<code>
		 *            jdbc:oracle:thin:@//127.0.0.1:1521/orcl
		 *            jdbc:mysql://127.0.0.1:3306/test?useSSL=false
		 *            jdbc:sqlite:tmp/data/test.sqlite
		 *            jdbc:sqlserver://127.0.0.1:1433;DatabaseName=test
		 *        		 </code>
		 * </pre>
		 */
		public BootstrapBuilder setUrl(String url) {
			this.url = url;
			return this;
		}

		public BootstrapBuilder setValidationQuery(String validationQuery) {
			this.validationQuery = validationQuery;
			return this;
		}

		public BootstrapBuilder setDataSouseType(DataSouseType dataSouseType) {
			this.dataSouseType = dataSouseType;
			return this;
		}

		public Bootstrap build() {

			return Unabo.load(bootStrapId, configuration -> {

				if (!Validate.isNullOrEmpty(driver))
					configuration.setDriverOption(driver);

				if (!Validate.isNullOrEmpty(username))
					configuration.setUsername(username);

				if (!Validate.isNullOrEmpty(password))
					configuration.setPassword(password);

				if (!Validate.isNullOrEmpty(isShowSql))
					configuration.setShowSql(isShowSql);

				if (!Validate.isNullOrEmpty(isCache))
					configuration.setShowSql(isCache);

				if (!Validate.isNullOrEmpty(isFormat))
					configuration.setShowSql(isFormat);

				if (!Validate.isNullOrEmpty(url))
					configuration.setUrl(url);

				if (!Validate.isNullOrEmpty(validationQuery))
					configuration.setValidationQuery(validationQuery);

				if (!Validate.isNullOrEmpty(dataSouseType))
					configuration.setDataSouseType(dataSouseType);

				if (!Validate.isNullOrEmpty(mapper_locations))
					configuration.setMapperLocations(mapper_locations);

			});

		}

	}

	public static Set<Object> keys() {
		return exists.keySet();
	}
	
	public static String tableNameByClass(Class<?> entryClass) {

		if (Reflect.hasAnnotation(entryClass, Table.class)) {

			String tableName = JPA.getTableName(entryClass);

			if (!Validate.isNullOrEmpty(tableName))
				return tableName;
		}

		return entryClass.getSimpleName();
	}

	


	public static String schema(Class<?> entityClass) {
		Assert.notNull(entityClass, "Entry class is null");

		return JPA.schema(entityClass);
	}

}
