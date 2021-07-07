package online.sanen.unabo.template.jpa;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <a href="https://blog.csdn.net/u012493207/article/details/50846616">参考链接</a>
 * @author LazyToShow <br>
 * Date: 2019/01/25 <br>
 * Time: 5:42
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GeneratedValue {

}
