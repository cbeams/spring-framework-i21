package com.interface21.context.support;

import com.interface21.beans.factory.support.ListableBeanFactoryImpl;
import com.interface21.context.ApplicationContextAware;
import com.interface21.context.ApplicationContextException;

/**
 * Allows for custom modification of an application context's beans.
 * Useful for custom config files targetted at system administrators that
 * override bean properties configured in the application context.
 *
 * <p>For reading "beanName.property=value" configuration from a
 * properties file, consider using PropertyResourceConfigurer.
 *
 * @author Juergen Hoeller
 * @since 06.07.2003
 * @see PropertyResourceConfigurer
 */
public interface BeanFactoryPostProcessor extends ApplicationContextAware {

	/**
	 * Modify the application context's internal bean factory after its standard
	 * initialization. All bean definitions will have been loaded, but no beans
	 * will have been instantiated yet. This allows for overriding or adding
	 * properties even to eager-initializing beans.
	 * @param beanFactory the bean factory used by the application context
	 * @throws ApplicationContextException in case of initialization errors
	 */
	void postProcessBeanFactory(ListableBeanFactoryImpl beanFactory) throws ApplicationContextException;

}
