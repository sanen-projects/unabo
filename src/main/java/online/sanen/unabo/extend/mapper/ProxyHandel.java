package online.sanen.unabo.extend.mapper;

import java.lang.reflect.Method;

/**
 * 
 * @author lazyToShow <br>
 *         Date: 2020年12月17日 <br>
 *         Time: 上午9:00:27 <br>
 */
public interface ProxyHandel {

	Object process(Method method, Object[] args);

}
