package online.sanen.unabo.core.handle;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.mhdt.degist.Validate;

import online.sanen.unabo.api.Handel;
import online.sanen.unabo.api.structure.ChannelContext;
import online.sanen.unabo.api.structure.enums.QueryType;
import online.sanen.unabo.core.RuntimeCache;
import online.sanen.unabo.template.jpa.Column;
import online.sanen.unabo.template.jpa.NoDB;
import online.sanen.unabo.template.jpa.NoInsert;
import online.sanen.unabo.template.jpa.NoUpdate;

/**
 * Field processing, which takes the intersection of entry and database tables,
 * the type of which is the entry .
 * 
 * @author LazyToShow <br>
 *         Date: 2017/10/21 <br>
 *         Time: 23:19
 */
public class CommonFieldsHandler implements Handel {

	@Override
	public Object handel(ChannelContext context, Object product) {

		// If the fields are specified
		if (context.getFields() != null && context.getFields().size() > 0) {
			context.setCommonFields(context.getFields());
			return null;
		}

		// Get the table field
		String tableName = context.getTableName();

		// The query table has all the fields
		List<String> tableFields = RuntimeCache.getTableFields(tableName, context);
		List<String> entryFields = this.getEntryField(context);
		
		
		// Table fields and class fields are collected and set
		if (entryFields == null)
			entryFields = tableFields;
		else {
			entryFields = new ArrayList<>(entryFields.stream().filter(item -> {
				for (String s : tableFields)
					if (s.equalsIgnoreCase(item))
						return true;

				return false;
			}).collect(Collectors.toList()));
		}
		

		if (context.getExceptes() != null)
			entryFields.removeAll(context.getExceptes());

		
		context.setCommonFields(entryFields);

		return null;
	}

	private List<String> getEntryField(ChannelContext context) {
		
		
		if (context.getEntityMap() != null) {
			
			Set<String> fs = context.getEntityMap().keySet();

			if (context.getExceptes() != null)
				fs.removeAll(context.getExceptes());

			return new ArrayList<String>(fs);
		}

		if (context.getEntityMaps() != null) {
			

			Set<String> fs = context.getEntityMaps().stream().findFirst().get().keySet();

			if (context.getExceptes() != null)
				fs.removeAll(context.getExceptes());

			return new ArrayList<String>(fs);
		}

		
		Class<?> cls = context.getEntityClass();
		

		if (cls == null)
			return null;

		QueryType queryType = context.getQueryType();
		List<String> columns = new LinkedList<>();
		
		for (Field field : RuntimeCache.getFields(cls)) {

			// Annotations to skip
			if (Validate.hasAnnotation(field, NoDB.class))
				continue;

			// Annotations to skip
			if (queryType.equals(QueryType.update) && Validate.hasAnnotation(field, NoUpdate.class))
				continue;

			// Annotations to skip
			if (queryType.equals(QueryType.insert) && Validate.hasAnnotation(field, NoInsert.class))
				continue;

			// Annotations to skip
			if (context.getExceptes() != null && context.getExceptes().stream()
					.anyMatch(item -> item.toLowerCase().equals(field.getName().toLowerCase())))
				continue;

			// The alias is preferred
			if (Validate.hasAnnotation(field, Column.class)
					&& !Validate.isNullOrEmpty(field.getAnnotation(Column.class).name())) {

				columns.add(field.getAnnotation(Column.class).name());
				continue;
			}

			// Add field name
			columns.add(field.getName());
		}

		com.mhdt.Print.info(columns);
		
		return columns;

	}

}
