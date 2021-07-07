package online.sanen.unabo.template.jpa;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * @author LazyTosHow <br>
 *         Date： 2017/01/06 <br>
 *         Time: 13:47
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Column {

	public String name() default "";

	/**
	 * The length of the field
	 * 
	 * @return
	 */
	public int length() default 255;

	/**
	 * Whether or not the only
	 * 
	 * @return
	 */
	public boolean unique() default false;

	/**
	 * Can is empty
	 * 
	 * @return
	 */
	public boolean nullable() default true;

	/**
	 * The query results are converted to Java objects when queried, and are
	 * inserted as JSON strings when inserted
	 * 
	 * @return
	 */
	public boolean jsonSerialization() default false;

}
