package online.sanen.unabo.template.jpa;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * <p>This is a class annotation that indicates that the entity class has an
 * injection priority rather than the query result.
 * 
 * <p>In a query, you might encounter a query result field that is not owned by the
 * entity class, This annotation is used to avoid this error and no longer
 * throws an exception to the input field. The entity class is injected as a
 * priority, and if the entity class does not contain the query field, then
 * skip.
 * 
 * @author LazyToShow <br>
 *         Date: 2018/02/18 <br>
 *         Time: 19:31
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Priority {

}
