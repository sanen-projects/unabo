package online.sanen.unabo.template.jpa;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * This field is ignored when creating the table
 *
 * @author LazyToShow	<br>
 * Date:	Nov 27, 2018	<br>
 * Time:	5:24:40 PM
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NoCreate {

}
