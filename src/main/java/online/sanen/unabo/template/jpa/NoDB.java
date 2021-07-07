package online.sanen.unabo.template.jpa;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Pertaining to a field that ignores any DB operations
 *
 * @author LazyToShow <br>
 *         Date: Nov 27, 2018 <br>
 *         Time: 5:29:20 PM
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NoDB {

}
