package online.sanen.unabo.template.jpa;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

import com.alibaba.fastjson.JSON;
import com.mhdt.degist.Validate;
import com.mhdt.exception.ReflectionException;
import com.mhdt.toolkit.Reflect;

/**
 * 
 * @author LazyToShow <br>
 *         Date: Jan 31, 2019 <br>
 *         Time: 9:54:33 AM <br>
 */
public class JPA {

	static Map<String, Field> alias = new HashMap<>();
	static Map<String, Function<Object, Object>> getConversion = new HashMap<>();
	static Map<String, Function<Object, Object>> setConversion = new HashMap<>();

	public static Field getFieldByAlias(Object obj, String column) {

		String key = obj.getClass().getName() + "$" + column;

		if (alias.containsKey(key)) {
			return alias.get(key);
		}

		Field[] fields = Reflect.getFields(obj, true);

		for (Field f : fields) {
			if (f.isAnnotationPresent(Column.class) && f.getAnnotation(Column.class).name().equals(column)) {
				f.setAccessible(true);
				alias.put(key, f);
				return f;
			}
		}

		return null;
	}

	static Map<Class<?>, String> bootstrapIdCache = new HashMap<>();

	public static String getBootStrapId(Class<?> cls) {

		if (bootstrapIdCache.containsKey(cls))
			return bootstrapIdCache.get(cls);

		cls = Reflect.getClassOfHasAnnotation(cls, BootstrapId.class);
		if (cls == null) {
			return null;
		} else {
			String result = cls.getAnnotation(BootstrapId.class).value();
			if (!Validate.isNullOrEmpty(result))
				bootstrapIdCache.put(cls, result);

			return result;
		}
	}

	static Map<Class<?>, String> schemaCache = new HashMap<>();

	public static String schema(Class<?> cls) {

		if (schemaCache.containsKey(cls))
			return schemaCache.get(cls);

		String result = null;

		cls = Reflect.getClassOfHasAnnotation(cls, Table.class);

		if (cls != null)
			result = cls.getAnnotation(Table.class).schema();

		bootstrapIdCache.put(cls, "".equals(result) ? null : result);
		return result;
	}

	/**
	 * 获取类注解值(tableName)
	 * 
	 * @param cls
	 * @return
	 */
	public static String getTableName(Class<?> cls) {
		cls = Reflect.getClassOfHasAnnotation(cls, Table.class);
		return cls == null ? null : cls.getAnnotation(Table.class).name();
	}

	static Map<Class<?>, Primarykey> PRIMARY_KEYS = new HashMap<>();

	public static synchronized Primarykey getId(Class<?> entryClass) {

		if (PRIMARY_KEYS.containsKey(entryClass)) {

			try {
				return PRIMARY_KEYS.get(entryClass).clone();
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
		}

		Field field = Reflect.getFieldOfHasAnnotation(entryClass, Id.class);

		if (field == null)
			throw new RuntimeException("The id annotation cannot be found in the class：" + entryClass);

		Primarykey primarykey = new Primarykey(field);
		PRIMARY_KEYS.put(entryClass, primarykey);

		return primarykey;
	}

	public static class Primarykey implements Cloneable {

		String name;

		Object value;

		Field field;

		@Override
		protected Primarykey clone() throws CloneNotSupportedException {
			return (Primarykey) super.clone();
		}

		@Override
		public String toString() {
			return "Primarykey [name=" + name + ", value=" + value + ", field=" + field + "]";
		}
		
		public Primarykey(Field field) {

			this.field = field;

			if (field.isAnnotationPresent(Column.class)
					&& !Validate.isNullOrEmpty(field.getAnnotation(Column.class).name())) {
				this.name = field.getAnnotation(Column.class).name();
			} else {
				this.name = field.getName();
			}

		}

		public Primarykey(String name, Object value) {
			this.name = name;
			this.value = value;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Object getValue(Object target) {

			return Reflect.getInject(target, field, null);
		}

		public Object getValue() {
			return value;
		}

		public void setValue(Object value) {
			this.value = value;
		}

	}

	public static Map<String, Class<?>> structured(Class<?> cls) {

		Map<String, Class<?>> map = new LinkedHashMap<>();

		Field[] fileds = Reflect.getFields(cls, true);

		for (Field field : fileds) {

			if (!Validate.isStatic(field) && !field.isAnnotationPresent(NoDB.class)) {
				field.setAccessible(true);
				map.put(field.getName(), field.getType().isEnum() ? String.class : field.getType());
			}
		}

		return map;
	}

	public static Object getInject(Object target, String columnName) {

		Field f = Reflect.getField(target, columnName);
		f = (f == null) ? getFieldByAlias(target, columnName) : f;

		String getConversionKey = target.getClass() + "&" + columnName;

		if (!getConversion.containsKey(getConversionKey)) {
			if (f.isAnnotationPresent(Column.class) && f.getAnnotation(Column.class).jsonSerialization()) {
				
				getConversion.put(getConversionKey, v -> {
					v = v == null ? null : JSON.toJSONString(v);
					return v;
				});
			}
				
		}
			

		return Reflect.getInject(target, f, getConversion.get(getConversionKey));
	}

	public static void setInject(Object target, String columnName, Object value) {

		Field f = Reflect.getField(target, columnName);

		f = (f == null) ? getFieldByAlias(target, columnName) : f;

		try {
			if (f.isAnnotationPresent(NoDB.class))
				return;
		} catch (NullPointerException e) {

			if (target.getClass().isAnnotationPresent(Priority.class))
				return;

			throw new ReflectionException("Cant match field for '" + columnName + "' from " + target.getClass(), e);
		}

		final Field field = f;

		String setConversionKey = target.getClass() + "&" + columnName;

		if (!setConversion.containsKey(setConversionKey) && f.isAnnotationPresent(Column.class) && f.getAnnotation(Column.class).jsonSerialization()) {
			
			Function<Object, Object> function = null;
			Class<?> fieldClass = field.getType();
			
			if (Validate.isArray(fieldClass) || Validate.isCollection(fieldClass))
				function = v -> JSON.parseArray(v==null?"":v.toString(), (Class<?>) Reflect.getGeneric(field)[0]);
			else
				function = v -> JSON.parseObject(v==null?"":v.toString(), fieldClass);
				
			setConversion.put(setConversionKey, function);
		}

		Function<Object, Object> function = setConversion.get(setConversionKey);
		Reflect.setInject(target, f.getName(), function == null ? value : function.apply(value));
	}

}
