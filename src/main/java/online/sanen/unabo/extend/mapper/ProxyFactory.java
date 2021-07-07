package online.sanen.unabo.extend.mapper;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * The agent factory <br>
 * Simple implementation of dynamic proxy objects<br>
 * if necessary, requires a {@link ProxyHandel} instance<br>
 * You can also set the name of the method specified for execution
 * 
 * @author 懒得出风头 <br>
 *         Date： 2017/09/21 <br>
 *         Time: 10:50
 */
public class ProxyFactory implements InvocationHandler {

	ProxyHandel proxyHander;

	private ProxyFactory() {

	}

	public static Object getInstance(ProxyHandel handel, Class<?>... clss) {
		ProxyFactory proxy = new ProxyFactory();
		proxy.proxyHander = handel;

		return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), clss, proxy);
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

		Object result = null;

		if (proxyHander != null)
			result = proxyHander.process(method, args);

		return result;
	}
}
