package online.sanen.unabo.extend.mapper;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mhdt.toolkit.Assert;
import com.mhdt.yaml.YAMLParse;

/**
 * 
 * @author lazyToShow <br>
 *         Date: 2020年12月16日 <br>
 *         Time: 下午4:21:21 <br>
 */
public class YAMLMapperConfiguring extends HashMap<String, Map<String, Object>> {

	static final Map<String, Map<String, Object>> mappers = new HashMap<>();

	private static final long serialVersionUID = 1L;

	/**
	 * 
	 * @param nameSpace
	 * @param inputStream
	 */
	public static void load(String nameSpace, InputStream inputStream) {

		if (mappers.containsKey(nameSpace))
			return;

		JSONObject jsonObject = YAMLParse.parseJSON(inputStream);
		String mapperFileNameSpace = jsonObject.getString("namespace");

		if (!nameSpace.equals(mapperFileNameSpace))
			throw new MapperLoadException(
					String.format("Namespaces are not uniform %s!=%s", nameSpace, mapperFileNameSpace));

		JSONArray methods = jsonObject.getJSONArray("methods");
		Map<String, Object> attributes = new HashMap<>();

		for (int i = 0; i < methods.size(); i++) {
			JSONObject method = methods.getJSONObject(i);
			method.forEach((k, v) -> {
				attributes.put(k.toString(), v.toString());
			});
		}

		mappers.put(nameSpace, attributes);

	}

	public static String getScript(String namespace, String methodName) {

		Assert.state(mappers.containsKey(namespace), "There is no namespace %s", namespace);
		Assert.state(mappers.get(namespace).containsKey(methodName), "There is no method %s", methodName);

		return mappers.get(namespace).get(methodName).toString();
	}

	/**
	 * 
	 * @param path
	 * @param inputStream
	 */
	public static void load1(String path, InputStream inputStream) {

		JSONObject jsonObject = YAMLParse.parseJSON(inputStream);
		String namespace = null;
		try {
			namespace = jsonObject.getString("namespace");
		} catch (NullPointerException e) {
			throw new MapperLoadException(
					String.format("\r\n Mapper's `namespace` attribute does not exist at  %s", path));
		}

		JSONArray methods = jsonObject.getJSONArray("methods");
		Map<String, Object> attributes = new HashMap<>();

		if (methods != null) {
			for (int i = 0; i < methods.size(); i++) {
				JSONObject method = methods.getJSONObject(i);
				method.forEach((k, v) -> {
					attributes.put(k.toString(), v.toString());
				});
			}
		}

		if (mappers.containsKey(namespace))
			throw new MapperLoadException(String.format("\r\nNamespace duplication for `%s` at `%s`", namespace, path));

		mappers.put(namespace, attributes);

//		info("-------------------");
//		mappers.forEach((k, v) -> {
//			info(k + " " + v.toString());
//		});
//		info("-------------------");

	}

	static class MapperLoadException extends RuntimeException {

		private static final long serialVersionUID = 1L;

		public MapperLoadException(String message) {
			super(message);
		}

	}

	public static Map<String, Map<String, Object>> getMappers() {
		return mappers;
	}

	public static boolean containsNameSpace(String namespace) {
		return mappers.containsKey(namespace);
	}

	public static boolean containsMethod(String namespace, String methodName) {
		Assert.state(mappers.containsKey(namespace), "There is no namespace %s", namespace);
		return mappers.get(namespace).containsKey(methodName);
	}

}
