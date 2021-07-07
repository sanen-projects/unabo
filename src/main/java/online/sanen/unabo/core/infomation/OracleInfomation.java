package online.sanen.unabo.core.infomation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import online.sanen.unabo.api.Bootstrap;
import online.sanen.unabo.api.structure.Column;
import online.sanen.unabo.api.structure.DataInformation;

/**
 * 
 * @author LazyToShow Date： 2018年9月5日 Time: 下午5:41:20
 */
public class OracleInfomation extends DataInformation {

	public OracleInfomation(Bootstrap bootstrap) {
		super(bootstrap);
	}

	@Override
	public List<String> getDatabases() {
		String sql = "select tablespace_name from dba_tablespaces;";
		return bootstrap.createSQL(sql).list();
	}

	@Override
	public List<String> getTableNames() {
		String sql = "select table_name from user_tables where TABLESPACE_NAME is not null and  user=?";
		return bootstrap.createSQL(sql,bootstrap.manager().getConfiguration().username().toUpperCase()).list();
	}

	@Override
	public List<Column> beforeGetColumns(String tableName) {

		if (tableName.contains(".")) {
			tableName = tableName.split("\\.")[1];
		}

		String sql = "select t1.column_name name,t1.data_type type,t1.owner,t1.DATA_LENGTH from all_tab_columns t1 where t1.table_name=?  and t1.owner=?";

		List<Map<String, Object>> maps = bootstrap.createSQL(sql, tableName,bootstrap.manager().getConfiguration().username().toUpperCase()).maps();
		List<Column> columns = new ArrayList<Column>(maps.size());

		maps.forEach(map -> {
			Column column = new Column();
			column.setName(map.get("name").toString());
			column.setType(map.get("type").toString());
			column.setComment(map.get("comment") == null ? null : map.get("comment").toString());
			column.setLength(map.get("DATA_LENGTH")==null?null:map.get("DATA_LENGTH").toString());
			columns.add(column);
		});

		sql = "SELECT col.column_name FROM all_constraints con,all_cons_columns col WHERE con.constraint_name=col.constraint_name AND con.constraint_type='P' AND col.table_name=?";

		List<String> pks = bootstrap.createSQL(sql, tableName).list();

		if (pks != null)
			columns.forEach(item -> {
				if (pks.contains(item.getName()))
					item.setIspk(true);
			});

		return columns;
	}

}
