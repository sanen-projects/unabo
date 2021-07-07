package online.sanen.unabo.extend.spring;

import java.io.File;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;
import online.sanen.unabo.api.structure.Configuration.DataSouseType;
import online.sanen.unabo.api.structure.Configuration.TransactionFactoryEnum;
import online.sanen.unabo.api.structure.enums.DriverOption;

/**
 * 
 * @author lazyToShow <br>
 *         Date 2019/09/11 <br>
 *         Time 11:06 <br>
 * @since 2.2.5
 */
@Data
@ConfigurationProperties(prefix = "unabo")
public class UnaboProperties {

	boolean enabled = false;

	String id = "default";

	/**
	 * Number of priorities greater than DriverOption
	 */
	String driver;

	/**
	 * Drive the enumeration
	 */
	DriverOption driverOption = DriverOption.SQLITE;

	String url = "jdbc:sqlite:def-data.sqlite";

	String username = "";

	String password = "";

	boolean showSql = true;

	TransactionFactoryEnum transaction;

	/**
	 * Database connection pool enumeration
	 */
	DataSouseType datasouseType = DataSouseType.Dbcp;

	File initSql;

	/**
	 * Whether to format the output to SQL
	 */
	boolean format;

	int maxActive = 5;

	String validationQuery;

	String mapperLocations;
	
	boolean removeAbandoned = true;

}
