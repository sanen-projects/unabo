package online.sanen.unabo.extend.spring;

import java.io.IOException;
import java.net.URISyntaxException;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import com.mhdt.degist.Validate;
import com.mhdt.io.FileIO;
import com.mhdt.toolkit.Reflect;

import online.sanen.unabo.api.Bootstrap;
import online.sanen.unabo.core.factory.Unabo;
import online.sanen.unabo.extend.spring.transaction.SpringManagedTransactionFactory;
import online.sanen.unabo.template.transaction.TransactionFactory;

@Configuration
@EnableConfigurationProperties(UnaboProperties.class)
@ConditionalOnWebApplication
@ConditionalOnProperty(prefix = "unabo", value = "enabled", matchIfMissing = true)
public class UnaboAutoConfiguration implements BeanFactoryAware {

	@Autowired
	UnaboProperties unaboProperties;
	
	private ConfigurableListableBeanFactory beanFactory;

	public UnaboAutoConfiguration(UnaboProperties unaboProperties) {

		this.unaboProperties = unaboProperties;
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		if (!(beanFactory instanceof ConfigurableListableBeanFactory))
			throw new IllegalArgumentException(
					"AutowiredAnnotationBeanPostProcessor requires a ConfigurableListableBeanFactory: " + beanFactory);

		this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
	}

	@Bean
	public Bootstrap defaultBootstrap(ApplicationContext applicationContext) throws IOException, URISyntaxException,
			ClassNotFoundException, InstantiationException, IllegalAccessException {

		// Create bootstrap

		Bootstrap bootstrap = Unabo.load(unaboProperties.getId(), configuration -> {
			configuration.setDriverOption(unaboProperties.getDriverOption());
			configuration.setUrl(unaboProperties.getUrl());
			configuration.setUsername(unaboProperties.getUsername());
			configuration.setPassword(unaboProperties.getPassword());
			configuration.setShowSql(unaboProperties.isShowSql());
			configuration.setMaxActive(unaboProperties.getMaxActive());
			configuration.setFormat(unaboProperties.isFormat());
			configuration.setValidationQuery(unaboProperties.getValidationQuery());
			configuration.setDataSouseType(unaboProperties.getDatasouseType());
			configuration.setMapperLocations(unaboProperties.getMapperLocations());
			configuration.setRemoveAbandoned(unaboProperties.isRemoveAbandoned());

		});

		// Init sql
		if (unaboProperties.getInitSql() != null && unaboProperties.getInitSql().exists()
				&& unaboProperties.getInitSql().isFile()) {
			String[] sqls = FileIO.getContent(unaboProperties.getInitSql()).split(";");
			for (String sql : sqls)
				bootstrap.createSQL(sql).update();
		}

		// init transction

		if (!Validate.isNullOrEmpty(unaboProperties.getTransaction())) {
			Class<?> classForTransaction =unaboProperties.getTransaction().getCls();
			bootstrap.manager().getTemplate().bindTransaction( ((TransactionFactory) Reflect.newInstance(classForTransaction)));

			if (classForTransaction == SpringManagedTransactionFactory.class) {
				BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(DataSourceTransactionManager.class);
				GenericBeanDefinition definition = (GenericBeanDefinition) builder.getRawBeanDefinition();
				definition.getConstructorArgumentValues().addGenericArgumentValue(bootstrap.manager().getTemplate().getDataSource());
				BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;
				definition.setAutowireMode(GenericBeanDefinition.AUTOWIRE_BY_TYPE);
				registry.registerBeanDefinition(DataSourceTransactionManager.class.getSimpleName(), definition);
			}

		}

		return bootstrap;
	}
}
