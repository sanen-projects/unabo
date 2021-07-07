package online.sanen.unabo.template.jpa;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Entity class class annotations
 *
 * @author LazyToShow <br>
 *         Date: Nov 27, 2018 <br>
 *         Time: 5:25:49 PM
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Table {

	/**
	 * The name of the table corresponding to the entity, and the default name of
	 * the table is the name of the entity.
	 * 
	 * @return
	 */
	public String name() default "";

	/**
	 * The specified directory name
	 * 
	 * @return
	 */
	public String catalog() default "";

	/**
	 * The specified database name
	 * 
	 * @return
	 */
	public String schema() default "";

}
