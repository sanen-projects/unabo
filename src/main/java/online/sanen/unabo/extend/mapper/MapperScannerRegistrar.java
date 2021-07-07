package online.sanen.unabo.extend.mapper;

import java.io.IOException;
import java.net.URISyntaxException;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;

import com.mhdt.degist.Validate;

import online.sanen.unabo.api.Bootstrap;

/**
 * 
 * @author lazyToShow <br>
 *         Date: 2020年12月17日 <br>
 *         Time: 下午5:41:29 <br>
 */
public class MapperScannerRegistrar extends InstantiationAwareBeanPostProcessorAdapter implements BeanFactoryAware {

	private ConfigurableListableBeanFactory beanFactory;

	@Override
	public void setBeanFactory(BeanFactory beanFactory) {

		if (!(beanFactory instanceof ConfigurableListableBeanFactory))
			throw new IllegalArgumentException("AutowiredAnnotationBeanPostProcessor requires a ConfigurableListableBeanFactory: " + beanFactory);

		this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;

		/**
		 * The instantiated target bean is displayed by an active call to the
		 * beanFactory#getBean
		 */
		Bootstrap bootstrap = this.beanFactory.getBean(Bootstrap.class);
		String mapperLocations = bootstrap.manager().getConfiguration().getMapperLocations();

		if (!Validate.isNullOrEmpty(mapperLocations)) {
			doScan(mapperLocations);
			processBeanDefinitions(bootstrap);
		}
	}

	/**
	 * 
	 * @param bootstrap
	 */
	private void processBeanDefinitions(Bootstrap bootstrap) {

		YAMLMapperConfiguring.getMappers().forEach((k, v) -> {

			Class<?> cls = null;
			try {
				cls = Class.forName(k);
			} catch (ClassNotFoundException e) {
				throw new MapperScannerException(e);
			}

			BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(cls);
			GenericBeanDefinition definition = (GenericBeanDefinition) builder.getRawBeanDefinition();
			definition.getConstructorArgumentValues().addGenericArgumentValue(cls);
			definition.getConstructorArgumentValues().addGenericArgumentValue(bootstrap);
			definition.setBeanClass(MapperFactory.class);

			BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;

			definition.setAutowireMode(GenericBeanDefinition.AUTOWIRE_BY_TYPE);
			registry.registerBeanDefinition(cls.getSimpleName(), definition);
		});
	}

	/**
	 * 
	 * @param mapperLocations
	 */
	private void doScan(String mapperLocations) {

		ResourceLoad resourceLoad = new ResourceLoad();

		try {
			resourceLoad.load(mapperLocations);
		} catch (IOException e) {
			throw new MapperScannerException(e);
		} catch (URISyntaxException e) {
			throw new MapperScannerException(e);
		}

	}

	static class MapperScannerException extends RuntimeException {

		private static final long serialVersionUID = 1L;

		public MapperScannerException(Throwable e) {
			super(e);
		}
	}

}
