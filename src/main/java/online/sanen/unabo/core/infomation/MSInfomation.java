package online.sanen.unabo.core.infomation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import online.sanen.unabo.api.Bootstrap;
import online.sanen.unabo.api.structure.Column;
import online.sanen.unabo.api.structure.DataInformation;

/**
 * 
 * @author LazyToShow Date: 2018/06/12 Time: 09:17
 */
public class MSInfomation extends DataInformation {

	public MSInfomation(Bootstrap bootstrap) {
		super(bootstrap);
	}

	@Override
	public List<String> getDatabases() {
		String sql = "SELECT name FROM  master..sysdatabases WHERE name NOT IN ( 'master', 'model', 'msdb', 'tempdb', 'northwind','pubs' )  ";
		return bootstrap.createSQL(sql).list();
	}

	@Override
	public List<String> getTableNames() {
		String sql = "SELECT  o.name FROM sysobjects o LEFT JOIN sysusers u ON o.uid = u.uid LEFT JOIN ::fn_listextendedproperty('MS_Description', 'USER', 'dbo', 'TABLE', NULL, NULL, NULL) p ON o.name = p.objname COLLATE database_default LEFT JOIN sysindexes i ON o.id = i.id AND i.indid < 2 WHERE u.name = 'dbo' AND ( o.xtype= 'U' OR o.xtype= 'S' OR o.xtype= 'V')  ORDER BY o.name";
		return bootstrap.createSQL(sql).list();
	}

	@Override
	public List<Column> beforeGetColumns(String tableName) {

		String sql = "SELECT 表名=CASE WHEN a.colorder=1 THEN d.name ELSE '' END,表说明=CASE WHEN a.colorder=1 THEN isnull(f.value,'') ELSE '' END,字段序号=a.colorder,name=a.name,标识=CASE WHEN COLUMNPROPERTY(a.id,a.name,'IsIdentity')=1 THEN '√' ELSE '' END,主键 =CASE WHEN EXISTS (SELECT 1 FROM sysobjects WHERE xtype='PK' AND parent_obj=a.id AND name IN (SELECT name FROM sysindexes WHERE indid IN (SELECT indid FROM sysindexkeys WHERE id=a.id AND colid=a.colid))) THEN '√' ELSE '' END,type=b.name,占用字节数 =a.length, 长度=COLUMNPROPERTY(a.id,a.name,'PRECISION'),小数位数=isnull(COLUMNPROPERTY(a.id,a.name,'Scale'),0),允许空=CASE WHEN a.isnullable=1 THEN '√' ELSE '' END,默认值=isnull(e.text,''),comment=isnull(g.[value],'') FROM syscolumns a LEFT JOIN systypes b ON a.xusertype=b.xusertype INNER JOIN sysobjects d ON a.id=d.id  AND d.name<> 'dtproperties' LEFT JOIN syscomments e ON a.cdefault=e.id LEFT JOIN sys.extended_properties g ON a.id=G.major_id AND a.colid=g.minor_id LEFT JOIN sys.extended_properties f ON d.id=f.major_id AND f.minor_id=0 WHERE d.name=? ORDER BY a.id,a.colorder";

		List<Map<String, Object>> maps = bootstrap.createSQL(sql, tableName).maps();
		List<Column> columns = new ArrayList<Column>(maps.size());

		maps.forEach(map -> {
			Column column = new Column();
			column.setName(map.get("name").toString());
			column.setType(map.get("type").toString());
			column.setComment(map.get("comment") == null ? null : map.get("comment").toString());
			columns.add(column);
		});
		return columns;
	}

}
