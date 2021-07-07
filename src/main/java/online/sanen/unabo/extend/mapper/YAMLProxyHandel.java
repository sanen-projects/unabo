package online.sanen.unabo.extend.mapper;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.groovy.control.CompilationFailedException;

import com.mhdt.degist.Validate;
import com.mhdt.groovy.GroovyTemplate;
import com.mhdt.toolkit.Reflect;

import online.sanen.unabo.api.Bootstrap;
import online.sanen.unabo.template.jpa.Param;

/**
 * 
 * @author lazyToShow <br>
 *         Date: 2020年12月17日 <br>
 *         Time: 下午4:50:51 <br>
 */
public class YAMLProxyHandel implements ProxyHandel {

	private Bootstrap bootstrap;
	private Class<?> mapperClass;
	private Map<Method, SqlParserHoder> sqlPaserHoders = new HashMap<>();

	public YAMLProxyHandel(Bootstrap bootstrap, Class<?> mapperClass) {
		this.bootstrap = bootstrap;
		this.mapperClass = mapperClass;
	}

	private  String[] getMethodParams(Method method) {

		Parameter[] parameters = method.getParameters();
		String[] array = new String[parameters.length];

		String[] array2 = Reflect.getMethodParamers(method);

		for (int i = 0; i < parameters.length; i++) {
			if (!parameters[i].isAnnotationPresent(Param.class))
				array[i] = array2[i];
			else
				array[i] = parameters[i].getAnnotation(Param.class).value();
		}

		return array;
	}
	
	@Override
	public Object process(Method method, Object[] args) {

		SqlParserHoder sqlParserHoder = null;

		if (sqlPaserHoders.containsKey(method)) {
			sqlParserHoder = sqlPaserHoders.get(method);
		} else {
			sqlParserHoder = new SqlParserHoder();
			sqlParserHoder.setCrud_annotation(method);
			sqlParserHoder.setScript(mapperClass, method.getName());
			sqlParserHoder.setNames(this.getMethodParams(method));
			sqlPaserHoders.put(method, sqlParserHoder);
		}

		Map<String, Object> map = new HashMap<>();
		for (int i = 0; i < sqlParserHoder.getNames().length; i++)
			map.put(sqlParserHoder.getNames()[i], args[i]);

		String sql = null;

		try {
			sql = GroovyTemplate.parse(sqlParserHoder.getScript(), map);
		} catch (CompilationFailedException | ClassNotFoundException | IOException e) {
			throw new GroovyTemplateException(e);
		}

		Class<?> returnType = method.getReturnType();

		// Update sql
		if (sqlParserHoder.isUpdate())
			return bootstrap.createSQL(sql).update();

		// Select sql
		if (returnType == Object.class) {
			return returnObject(sql);
		}

		if (Validate.isList(returnType)) {

			Type genericReturnType = method.getGenericReturnType();

			ParameterizedType pt = (ParameterizedType) genericReturnType;
			if (pt.getActualTypeArguments()[0] instanceof ParameterizedType) {
				ParameterizedType type = (ParameterizedType) pt.getActualTypeArguments()[0];
				return returnList((Class<?>) type.getRawType(), sql);
			} else {
				return returnList((Class<?>) pt.getActualTypeArguments()[0], sql);
			}

		} else {

			return returnUnique(returnType, sql);
		}
	}

	static class GroovyTemplateException extends RuntimeException {

		private static final long serialVersionUID = 1L;

		public GroovyTemplateException(Throwable e) {
			super(e);
		}
	}

	private Object returnObject(String sql) {
		return bootstrap.createSQL(sql).list();
	}

	private Object returnUnique(Class<?> returnType, String sql) {

		if (Validate.isBaseType(returnType) || Validate.isMap(returnType)) {
			return bootstrap.createSQL(sql).unique();
		} else {
			return bootstrap.createSQL(sql).unique(returnType);
		}
	}

	private Object returnList(Class<?> cls, String sql) {

		if (Validate.isBaseType(cls) || Validate.isMap(cls)) {
			return bootstrap.createSQL(sql).list();
		} else {
			return bootstrap.createSQL(sql).list(cls);
		}
	}

}
