package online.sanen.unabo.core;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.mhdt.degist.DegistTool;
import com.mhdt.degist.DegistTool.Encode;
import com.mhdt.toolkit.Reflect;

import online.sanen.unabo.api.structure.ChannelContext;
import online.sanen.unabo.api.structure.enums.ProductType;
import online.sanen.unabo.template.SqlTemplate;

public class RuntimeCache {

	private static Map<String, List<String>> tableColumnsCache = new HashMap<>();

	public static Map<Class<?>, Field[]> filedsCache = new HashMap<>();

	public static Field[] getFields(Class<?> cls) {
		
		if (filedsCache.containsKey(cls))
			return filedsCache.get(cls);

		Field[] list = Reflect.getFields(cls,true);
		filedsCache.put(cls, list);

		return list;
	}

	public static void removeTableCache(ChannelContext context) {
		tableColumnsCache.remove(DegistTool.md5(context.getUrl() + context.getTableName() + "Unabo", Encode.HEX));
	}

	/**
	 * 
	 * @param tableName
	 * @param context
	 * @return
	 */
	public static List<String> getTableFields(String tableName, ChannelContext context) {

		String md5 = DegistTool.md5(context.getUrl() + tableName + "Unabo", Encode.HEX);

		// If there is a cache, return directly
		if (tableColumnsCache.containsKey(md5)) {
			return tableColumnsCache.get(md5).stream().collect(Collectors.toList());
		}

		// Find all the fields of the table through Sql
		SqlTemplate template = (SqlTemplate) context.getTemplate();
		ProductType productType = context.productType();
		List<String> result = ProductType.getColumnsFromTableName(productType, template, tableName,context.getSchema());

		
		// Join the cache
		tableColumnsCache.put(md5,result);

		return result.stream().collect(Collectors.toList());
	}

	public static void refreshTableFields(String tableName, String url, List<String> fields) {
		String md5 = DegistTool.md5(url + tableName + "Unabo", Encode.HEX);
		tableColumnsCache.put(md5, fields);
	}

}
