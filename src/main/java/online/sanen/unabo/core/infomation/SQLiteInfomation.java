package online.sanen.unabo.core.infomation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import online.sanen.unabo.api.Bootstrap;
import online.sanen.unabo.api.structure.Column;
import online.sanen.unabo.api.structure.DataInformation;

/**
 * 
 * @author LazyToShow
 * Date: 2018/06/12
 * Time: 09:17
 */
public class SQLiteInfomation extends DataInformation{
	
	public SQLiteInfomation(Bootstrap bootstrap) {
		super(bootstrap);
	}

	@Override
	public List<String> getDatabases() {
		
		return Arrays.asList("main");
	}

	@Override
	public List<String> getTableNames() {
		
		String sql = "SELECT name FROM sqlite_master where name<>'sqlite_sequence'";
		return bootstrap.createSQL(sql).list();
	}

	@Override
	public List<Column> beforeGetColumns(String tableName) {
		String sql = "PRAGMA table_info('"+tableName+"')";
		List<Map<String, Object>> maps = bootstrap.createSQL(sql).maps();
		List<Column> columns = new ArrayList<Column>(maps.size());

		maps.forEach(map -> {
			Column column = new Column();
			column.setName(map.get("name").toString());
			column.setType(map.get("type").toString());
			column.setLength(column.getType());
			column.setIsnullable(Integer.parseInt(map.get("notnull").toString())==0);
			column.setDefaultval(map.get("dflt_value"));
			column.setIspk(Integer.parseInt(map.get("pk").toString())==1);
			columns.add(column);
		});

		return columns;
	}

}
