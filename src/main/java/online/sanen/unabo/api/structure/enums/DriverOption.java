package online.sanen.unabo.api.structure.enums;

/**
 * 
 * @author LazyToShow <br>
 *         Date: 2018/6/11 <br>
 *         Time: 17:36
 *
 */
public enum DriverOption{

	/**
	 * <B>com.mysql.cj.jdbc.Driver </B><br>
	 * Whether to use MYSQL's new/old drivers depends on the JAR version
	 */
	MYSQL("com.mysql.jdbc.Driver"),
	
	POSTGRESQL("org.postgresql.Driver"),

	/**
	 * <B>com.mysql.cj.jdbc.Driver </B><br>
	 * Whether to use MYSQL's new/old drivers depends on the JAR version.<br>
	 * If the following prompt appears, try using the new driver:<br>
	 * 
	 * Loading class `com.mysql.jdbc.Driver'. This is deprecated. The new
	 * driver class is `com.mysql.cj.jdbc.Driver'. The driver is automatically
	 * registered via the SPI and manual loading of the driver class is generally
	 * unnecessary.
	 */
	MYSQL_CJ("com.mysql.cj.jdbc.Driver"),

	SQLITE("org.sqlite.JDBC"),

	/**
	 * <a href=
	 * "http://www.oracle.com/technetwork/cn/database/features/jdbc/index-093096-zhs.html">
	 * ORACLE driver download address</a>
	 */
	ORACLE("oracle.jdbc.OracleDriver"),

	/** SQLServer 6.5, 7, 2000, 2005, 2008, 2012 are supported */
	JTDS("net.sourceforge.jtds.jdbc.Driver");

	String value;

	private DriverOption(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
