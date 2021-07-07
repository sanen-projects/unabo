package online.sanen.unabo.extend.mapper;

import org.springframework.beans.factory.FactoryBean;

import online.sanen.unabo.api.Bootstrap;

public class MapperFactory<T> implements FactoryBean<T>{
	
	
	private Class<T> interfaceType;
	private Bootstrap bootstrap;

	public MapperFactory(Class<T> interfaceType,Bootstrap bootstrap) {
		  this.interfaceType = interfaceType;
		  this.bootstrap = bootstrap;
	}

	@Override
	public T getObject() throws Exception {
		@SuppressWarnings("unchecked")
		T mapperInstance = (T) ProxyFactory.getInstance(new YAMLProxyHandel(bootstrap, interfaceType), interfaceType);

		return (T) mapperInstance;
	}

	@Override
	public Class<?> getObjectType() {
		return interfaceType;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

}
