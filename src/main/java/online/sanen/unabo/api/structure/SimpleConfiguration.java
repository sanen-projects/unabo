package online.sanen.unabo.api.structure;

import java.util.function.Supplier;
import com.mhdt.degist.Validate;
import online.sanen.unabo.api.structure.enums.DriverOption;

/**
 * 
 * @author lazyToShow<br>
 *         Date: 2019/01/24<br>
 *         Time: 15:33
 *
 */
public class SimpleConfiguration implements Configuration {

	String url;

	/**
	 * Set the JDBC connection url
	 * 
	 * - Enumerate the different database connections:
	 * 
	 * <pre>
	 * 		<code>
	 *            jdbc:oracle:thin:@//127.0.0.1:1521/orcl;
	 *            jdbc:mysql://127.0.0.1:3306/test?useSSL=false&amp;serverTimezone=UTC&amp;allowPublicKeyRetrieval=true
	 *            jdbc:postgresql://localhost:5432/postgres
	 *            jdbc:sqlite:tmp/data/test.sqlite;
	 *            jdbc:jtds:sqlserver://127.0.0.1:1433;DatabaseName=test;
	 *            
	 *       </code>
	 * </pre>
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String url() {
		return url;
	}

	String driver;

	public void setDriverOption(DriverOption driverOption) {
		this.driver = driverOption.getValue();
	}

	public void setDriverOption(String driver) {
		this.driver = driver;
	}

	@Override
	public String driver() {
		return driver;
	}

	String username = "";

	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public String username() {
		return username;
	}

	String password = "";

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String password() {
		return password;
	}

	boolean isShowSql = true;

	Supplier<Boolean> isShowSqlSupplier;

	public void setShowSql(boolean isShowSql) {
		this.isShowSql = isShowSql;
	}

	public void bindShowSql(Supplier<Boolean> consumer) {
		this.isShowSqlSupplier = consumer;
	}

	@Override
	public boolean isShowSql() {

		return isShowSqlSupplier == null ? isShowSql
				: isShowSqlSupplier.get() == null ? false : isShowSqlSupplier.get();
	}

	boolean format;

	public void setFormat(boolean format) {
		this.format = format;
	}

	@Override
	public boolean isFormat() {
		return format;
	}

	boolean isLog;

	public void setLog(boolean isLog) {
		this.isLog = isLog;
	}

	@Override
	public boolean isLog() {
		return isLog;
	}

	String validationQuery;

	public void setValidationQuery(String validationQuery) {
		this.validationQuery = validationQuery;
	}

	@Override
	public String validationQuery() {

		if (Validate.isNullOrEmpty(validationQuery)) {

			if (driver.equals("oracle.jdbc.OracleDriver")) {
				return "select 1 from dual";
			} else if (driver.equals("org.postgresql.Driver")) {
				return "select version()";
			} else {
				return Configuration.super.validationQuery();
			}
		}

		return validationQuery;
	}

	DataSouseType dataSouseType = DataSouseType.Dbcp;

	public void setDataSouseType(DataSouseType dataSouseType) {
		this.dataSouseType = dataSouseType;
	}

	@Override
	public DataSouseType dataSouseType() {
		return dataSouseType;
	}

	boolean isCache;

	public void setCache(boolean isCache) {
		this.isCache = isCache;
	}

	Integer maxActive;

	public void setMaxActive(int maxActive) {
		this.maxActive = maxActive;
	}

	@Override
	public boolean isCache() {
		return isCache;
	}

	@Override
	public int maxActive() {
		return this.maxActive != null && maxActive > 0 ? this.maxActive : Configuration.super.maxActive();
	}

	String mapper_locations;

	public String getMapperLocations() {
		return mapper_locations;
	}

	/**
	 * such: <code>
	 * classpath:com/grid/nocode/mapper/*.yml
	 * </code> or file: <code>
	 * D:\workspace\nocode-service-tenant\target\classes\com\grid\nocode\mapper\*.yml
	 * </code>
	 * 
	 * @param mapper_locations
	 */
	public void setMapperLocations(String mapper_locations) {
		this.mapper_locations = mapper_locations;
	}

	Boolean isRemoveAbandoned;

	@Override
	public boolean isRemoveAbandoned() {
		// TODO Auto-generated method stub
		return isRemoveAbandoned == null ? Configuration.super.isRemoveAbandoned() : this.isRemoveAbandoned;
	}

	public void setRemoveAbandoned(boolean removeAbandoned) {
		this.isRemoveAbandoned = removeAbandoned;
	}

	TransactionFactoryEnum transactionFactory;

	@Override
	public TransactionFactoryEnum getTransactionFactory() {
		return transactionFactory;
	}

	public void setTransactionFactory(TransactionFactoryEnum transactionFactoryEnum) {
		this.transactionFactory = transactionFactoryEnum;
	}

}
