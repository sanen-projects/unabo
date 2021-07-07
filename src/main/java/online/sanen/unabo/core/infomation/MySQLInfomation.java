package online.sanen.unabo.core.infomation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.mhdt.toolkit.Assert;

import online.sanen.unabo.api.Bootstrap;
import online.sanen.unabo.api.structure.Column;
import online.sanen.unabo.api.structure.DataInformation;

/**
 * 
 * @author LazyToShow Date: 2018/06/12 Time: 09:17
 */
public class MySQLInfomation extends DataInformation {

	public MySQLInfomation(Bootstrap bootstrap) {
		super(bootstrap);
	}

	@Override
	public List<String> getDatabases() {

		String sql = "SHOW DATABASES";

		return bootstrap.createSQL(sql).list();
	}

	@Override
	public List<String> getTableNames() {

		String sql = "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA =?";

		return bootstrap.createSQL(sql, bootstrap.manager().databaseName()).list();
	}

	@Override
	public List<Column> beforeGetColumns(String tableName) {

		Assert.notNullOrEmpty(tableName, "TableName is null or empty");

		String sql = "SELECT column_name name,data_type type,character_maximum_length length,IS_NULLABLE isnullable,column_default defaultval,case when column_key=\"PRI\" THEN \"YES\" ELSE \"NO\" END ispk,column_comment comment FROM information_schema.COLUMNS WHERE TABLE_NAME=? AND TABLE_SCHEMA=?";
		
		List<Map<String, Object>> maps = bootstrap.createSQL(sql, tableName,bootstrap.manager().databaseName()).maps();
		
		List<Column> columns = new ArrayList<Column>(maps.size());

		maps.forEach(map -> {
			Column column = new Column();
			column.setName(map.get("name").toString());
			column.setType(map.get("type").toString());
			column.setLength(map.get("length")==null?null:map.get("length").toString());
			column.setIsnullable(map.get("isnullable").toString().toLowerCase().equals("yes"));
			column.setDefaultval(map.get("defaultval"));
			column.setIspk(map.get("ispk").toString().toLowerCase().equals("yes"));
			column.setComment(map.get("comment") == null ? null : map.get("comment").toString());
			columns.add(column);
		});

		return columns;
	}

}
