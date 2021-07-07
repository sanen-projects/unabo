package online.sanen.unabo.api.structure;

/**
 * Abstract configuration instance objects, in addition to the <strong> Driver,
 * Url, Username, Password</strong>, are initialized with the default values
 * 
 * @author LazyToShow <br>
 *         Date: 2018/1/29 <br>
 *         Time: 11:36
 *
 */
public interface Configuration {

	public enum DataSouseType {
		Dbcp, Druid, C3p0, HikariCP;
	}

	public enum TransactionFactoryEnum {

		SpringManagedTransactionFactory(online.sanen.unabo.extend.spring.transaction.SpringManagedTransactionFactory.class),
		JdbcTransactionFactory(online.sanen.unabo.template.transaction.JdbcTransactionFactory.class);

		private Class<?> cls;

		private TransactionFactoryEnum(Class<?> cls) {
			this.cls = cls;
		}

		public Class<?> getCls() {
			return cls;
		}

		public void setCls(Class<?> cls) {
			this.cls = cls;
		}
	}

	public default DataSouseType dataSouseType() {
		return null;
	}

	public default String driver() {
		return null;
	}

	public default String url() {
		return null;
	}

	public default String username() {
		return "";
	}

	public default String password() {
		return "";
	}

	public default boolean isLog() {
		return false;
	}

	public default boolean isFormat() {
		return true;
	}

	public default boolean isCache() {
		return false;
	}

	public default boolean isShowSql() {
		return true;
	}

	public default String validationQuery() {
		return "SELECT 1";
	}

	public default int maxActive() {
		return 5;
	}

	public default String getMapperLocations() {

		return null;
	}

	public default boolean isRemoveAbandoned() {

		return true;
	}

	public default TransactionFactoryEnum getTransactionFactory() {
		return null;
	}

}
