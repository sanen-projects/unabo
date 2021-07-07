package online.sanen.unabo.extend.mapper.enums;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import online.sanen.unabo.extend.mapper.MapperScannerRegistrar;

/**
 * Mapper is enabled on spring-boot
 * @author lazyToShow <br>
 * Date: 2020年12月18日 <br>
 * Time: 上午9:33:40 <br>
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({MapperScannerRegistrar.class})
public @interface UnaboMapperScan {

}
