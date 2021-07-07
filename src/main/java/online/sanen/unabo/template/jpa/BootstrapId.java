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
public @interface BootstrapId {
	
	public String value() default "";

}
