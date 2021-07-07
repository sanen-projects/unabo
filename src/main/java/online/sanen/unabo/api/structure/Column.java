package online.sanen.unabo.api.structure;

import java.util.HashMap;
import java.util.Map;

import online.sanen.unabo.api.exception.QueryException;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;

/**
 * The database table field property wraps the class
 * 
 * @author LazyToShow <br>
 *         Date: 2019年6月18日 <br>
 *         Time: 上午10:58:07 <br>
 */
public class Column {

	String name;

	String type;

	String length;

	Integer scale;

	Boolean isnullable;

	Object defaultval;

	Boolean ispk;

	String comment;

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

	public String getLength() {
		return length;
	}

	public void setLength(String length) {
		this.length = length;
	}

	public Integer getScale() {
		return scale;
	}

	public void setScale(Integer scale) {
		this.scale = scale;
	}

	public Boolean getIsnullable() {
		return isnullable;
	}

	public void setIsnullable(Boolean isnullable) {
		this.isnullable = isnullable;
	}

	public Object getDefaultval() {
		return defaultval;
	}

	public void setDefaultval(Object defaultval) {
		this.defaultval = defaultval;
	}

	public Boolean getIspk() {
		return ispk;
	}

	public void setIspk(Boolean ispk) {
		this.ispk = ispk;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@Override
	public String toString() {
		return "Column [name=" + name + ", type=" + type + ", length=" + length + ", scale=" + scale + ", isnullable="
				+ isnullable + ", defaultval=" + defaultval + ", ispk=" + ispk + ", comment=" + comment + ", cls=" + cls
				+ "]";
	}

	String cls;

	public void setCls(String cls) {
		
		this.cls = cls;
	}

	private static final Map<Class<?>, String[]> contrast = new HashMap<Class<?>, String[]>() {
		private static final long serialVersionUID = 1L;

		{
			put(String.class, new String[] { "character", "varchar", "text", "clob", "char" ,"raw"});
			put(Integer.class, new String[] { "integer", "int4", "int", "number", "timestamp" });
			put(Double.class, new String[] { "double" });
			put(Date.class, new String[] { "date" });
			put(BigDecimal.class,new String[] {"decimal"});
			put(Boolean.class, new String[] { "boolean" });
			put(Time.class, new String[] { "time zone" });

		}
	};

	public Class<?> getClsByDataType() {

		for (Map.Entry<Class<?>, String[]> entry : contrast.entrySet())

			for (String item : entry.getValue())
				if (getType().toLowerCase().contains(item))
					return entry.getKey();

		
		throw new QueryException(String.format("The correct Java type is not matched for %s", getType()));
	}

}
