package online.sanen.unabo.api.structure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.mhdt.degist.Validate;

import online.sanen.unabo.template.jpa.Priority;


/**
 * Gets the database field under the current connection
 * 
 * @author LazyToShow
 *
 */
@Priority
@Deprecated
public class DataField {

	String name;

	Object value;

	// abc 123
	String type;

	Class<?> cls;

	String comment;

	boolean isPrimary;

	boolean autogrowth;

	Integer pk;

	String column_key;

	public DataField() {

	}

	public DataField(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Class<?> getCls() {

		if (cls == null && type != null) {
			if (type.toLowerCase().equals("text") || type.toLowerCase().contains("varchar")
					|| type.toLowerCase().contains("clob")) {
				return String.class;
			} else {
				return Integer.class;
			}
		}

		return cls;
	}

	public void setCls(Class<?> cls) {
		this.cls = cls;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	@SuppressWarnings("unused")
	private void setPk(Integer pk) {
		if (pk != null && pk == 1)
			this.isPrimary = true;

		this.pk = pk;
	}

	@SuppressWarnings("unused")
	private void setColumn_key(String column_key) {

		if (!Validate.isNullOrEmpty(column_key) && column_key.equals("PRI"))
			this.isPrimary = true;

		this.column_key = column_key;
	}

	public boolean isAutogrowth() {
		return autogrowth;
	}

	public void setAutogrowth(boolean autogrowth) {
		this.autogrowth = autogrowth;
	}

	public boolean isPrimary() {
		return isPrimary;
	}

	public void setPrimary(boolean isPrimary) {
		this.isPrimary = isPrimary;
	}

	/** 字段可计算? */
	public static boolean isComputable(DataField dataField) {
		return !dataField.getType().equals("text");

	}

	/** 字段不可计算? */
	public static boolean isUnComputable(DataField dataField) {
		return dataField.getType().equals("text");

	}

	public static List<String> toNames(List<DataField> dataFields) {
		List<String> result = new ArrayList<>();

		for (DataField column : dataFields)
			result.add(Validate.isNullOrEmpty(column.getValue()) ? column.getName() : column.getValue().toString());

		Collections.sort(result, Comparator.comparing(Object::toString));
		return result;
	}

	public static boolean isSameName(List<DataField> comparetors) {
		String name = comparetors.get(0).getName();
		if (Validate.isNullOrEmpty(name))
			return false;

		return comparetors.stream().allMatch(column -> {
			return column.getName().equals(name);
		});
	}


	/**
	 * 
	 * @param table
	 * @param headers
	 * @return
	 */
	public static List<DataField> completionType(List<Map<String, Object>> table, List<DataField> headers) {

		outer: for (DataField dataField : headers) {

			boolean isInteger = true;

			for (Map<String, Object> map : table) {

				Object obj = map.get(dataField.getName());

				if (Validate.isNullOrEmpty(obj))
					continue;

				if (!Validate.isNumber(obj)) {
					dataField.setType("text");
					dataField.setCls(String.class);
					continue outer;
				} else if (isInteger) {

					try {
						Integer.parseInt(obj.toString());
					} catch (NumberFormatException e) {
						isInteger = false;
					}
				}
			}

			dataField.setType(isInteger ? "integer" : "double");
			dataField.setCls(isInteger ? Integer.class : Double.class);
		}

		return headers;

	}

	@Override
	public String toString() {
		return "DataField [name=" + name + ", value=" + value + ", type=" + type + ", cls=" + cls + ", comment="
				+ comment + ", isPrimary=" + isPrimary + ", autogrowth=" + autogrowth + ", pk=" + pk + ", column_key="
				+ column_key + "]";
	}

	
	
	

}
