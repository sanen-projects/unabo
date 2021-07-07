package online.sanen.unabo.extend.mapper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import com.mhdt.degist.Validate;

import lombok.Data;
import online.sanen.unabo.extend.mapper.enums.Delete;
import online.sanen.unabo.extend.mapper.enums.Insert;
import online.sanen.unabo.extend.mapper.enums.Select;
import online.sanen.unabo.extend.mapper.enums.Update;

@Data
public class SqlParserHoder {

	String script;
	String[] names;

	Annotation crud_annotation;
	String annotation_script;
	boolean isUpdate;

	/**
	 * 
	 * @param method
	 */
	public void setCrud_annotation(Method method) {

		if (method.isAnnotationPresent(Select.class)) {

			Select select = method.getAnnotation(Select.class);
			setAnnotation_script(select.value());
			this.crud_annotation = select;

		} else if (method.isAnnotationPresent(Insert.class)) {

			Insert insert = method.getAnnotation(Insert.class);
			setAnnotation_script(insert.value());
			this.crud_annotation = insert;

		} else if (method.isAnnotationPresent(Delete.class)) {

			Delete delete = method.getAnnotation(Delete.class);
			setAnnotation_script(delete.value());
			this.crud_annotation = delete;

		} else if (method.isAnnotationPresent(Update.class)) {

			Update update = method.getAnnotation(Update.class);
			setAnnotation_script(update.value());
			this.crud_annotation = update;
		}
	}

	/**
	 * 
	 * @param mapperClass
	 * @param methodName
	 */
	public void setScript(Class<?> mapperClass, String methodName) {

		if (!YAMLMapperConfiguring.containsNameSpace(mapperClass.getName()))
			throw new MapperException(String.format("class %s has no mapper file", mapperClass.getName()));

		if (YAMLMapperConfiguring.containsMethod(mapperClass.getName(), methodName)) {
			setScript(YAMLMapperConfiguring.getScript(mapperClass.getName(), methodName));
		} else if (crud_annotation != null && !Validate.isNullOrEmpty(annotation_script)) {
			setScript(annotation_script);
		} else {
			throw new MapperException(String.format("The method script cannot be found of class %s,method: %s",
					mapperClass.getName(), methodName));
		}

		if ((crud_annotation != null
				&& (crud_annotation.annotationType() == Update.class || crud_annotation.annotationType() == Insert.class
						|| crud_annotation.annotationType() == Delete.class))
				|| (script.toUpperCase().startsWith("INSERT") || script.toUpperCase().startsWith("DELETE")
						|| script.toUpperCase().startsWith("UPDATE"))) {
			isUpdate = true;
		}

	}

	static class MapperException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		public MapperException(String message) {
			super(message);
		}
	}
}
