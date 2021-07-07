package online.sanen.unabo.core.handle;

import java.lang.reflect.Field;

import com.mhdt.degist.Validate;

import online.sanen.unabo.api.Handel;
import online.sanen.unabo.api.condition.C;
import online.sanen.unabo.api.structure.ChannelContext;
import online.sanen.unabo.core.RuntimeCache;
import online.sanen.unabo.template.jpa.Column;
import online.sanen.unabo.template.jpa.Id;


public class EntityConditionHandle implements Handel {

	@Override
	public Object handel(ChannelContext context, Object message) {

		Object entity = context.getEntity();

		for (Field field : RuntimeCache.getFields(entity.getClass())) {

			try {

				field.setAccessible(true);
				Object value = field.get(entity);

				if (value == null)
					continue;

				if ((value.equals(0) || value.equals("")) && Validate.hasAnnotation(field, Id.class))
					continue;

				String key = (Validate.hasAnnotation(field, Column.class)
						&& !Validate.isNullOrEmpty(field.getAnnotation(Column.class).name()))
								? field.getAnnotation(Column.class).name()
								: field.getName();

				context.addCondition(C.eq(key, value));

			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}

		}

		return null;
	}

}
