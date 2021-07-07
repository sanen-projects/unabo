package online.sanen.unabo.core.infomation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.mhdt.toolkit.Assert;

import online.sanen.unabo.api.Bootstrap;
import online.sanen.unabo.api.structure.Column;
import online.sanen.unabo.api.structure.DataInformation;

public class PostgreInfomation extends DataInformation {

	public PostgreInfomation(Bootstrap bootstrap) {
		super(bootstrap);
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<String> getDatabases() {

		String sql = "SELECT datname FROM pg_database where datacl is NULL";

		return bootstrap.createSQL(sql).list();
	}

	@Override
	public List<String> getTableNames() {

		String sql = "SELECT tablename FROM pg_tables WHERE tablename NOT LIKE'pg%' AND tablename NOT LIKE'sql_%'";
		List<String> list = bootstrap.createSQL(sql).list();
		
		sql = "SELECT viewname FROM pg_views WHERE     schemaname ='public'";
		list.addAll(bootstrap.createSQL(sql).list());

		return list;
	}

	@Override
	public List<Column> beforeGetColumns(String tableName) {

		Assert.notNullOrEmpty(tableName, "TableName is null or empty");
		String sql = "SELECT COLUMN_NAME AS name,data_type AS type,COALESCE ( character_maximum_length, numeric_precision,- 1 ) AS length,numeric_scale AS scale,is_nullable AS isnullable,column_default AS defaultval,CASE WHEN POSITION ( 'nextval' IN column_default ) > 0 THEN 1 ELSE 0 END AS isidentity,CASE WHEN b.pk_name IS NULL THEN 'NO' ELSE 'YES' END AS ispk,C.DeText  as comment FROM information_schema.COLUMNS LEFT JOIN (SELECT pg_attr.attname AS colname,pg_constraint.conname AS pk_name FROM pg_constraint INNER JOIN pg_class ON pg_constraint.conrelid = pg_class.oid INNER JOIN pg_attribute pg_attr ON pg_attr.attrelid = pg_class.oid AND pg_attr.attnum = pg_constraint.conkey [1] INNER JOIN pg_type ON pg_type.oid = pg_attr.atttypid WHERE pg_class.relname =? AND pg_constraint.contype = 'p') b ON b.colname = information_schema.COLUMNS.COLUMN_NAME LEFT JOIN (SELECT attname,description AS DeText FROM pg_class LEFT JOIN pg_attribute pg_attr ON pg_attr.attrelid = pg_class.oid LEFT JOIN pg_description pg_desc ON pg_desc.objoid = pg_attr.attrelid AND pg_desc.objsubid = pg_attr.attnum WHERE pg_attr.attnum > 0 AND pg_attr.attrelid = pg_class.oid AND pg_class.relname=?) C ON C.attname = information_schema.COLUMNS.COLUMN_NAME WHERE table_schema = 'public' AND TABLE_NAME =? ORDER BY ordinal_position ASC";

		List<Map<String, Object>> maps = bootstrap.createSQL(sql, tableName, tableName, tableName).maps();
		List<Column> columns = new ArrayList<Column>(maps.size());

		maps.forEach(map -> {
			Column column = new Column();
			column.setName(map.get("name").toString());
			column.setType(map.get("type").toString());
			column.setLength(map.get("length")==null?null:map.get("length").toString());
			column.setScale(map.get("scale") == null ? null : Integer.parseInt(map.get("scale").toString()));
			column.setIsnullable(map.get("isnullable").toString().toLowerCase().equals("yes"));
			column.setDefaultval(map.get("defaultval"));
			column.setIspk(map.get("ispk").toString().toLowerCase().equals("yes"));
			column.setComment(map.get("comment") == null ? null : map.get("comment").toString());
			columns.add(column);
		});

		return columns;

	}

	@Override
	public String getTableComment(String tableName) {
		String sql = "select cast(obj_description(relfilenode,'pg_class') as varchar) as comment from pg_class c\r\n"
				+ "where relname in (select tablename from pg_tables where schemaname='public' and position('_2' in tablename)=0)\r\n"
				+ "and relname=?";

		return bootstrap.createSQL(sql, tableName).unique();
	}

}
