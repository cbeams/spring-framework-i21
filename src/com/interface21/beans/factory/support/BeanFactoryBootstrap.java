package com.interface21.beans.factory.support;

import com.interface21.beans.BeansException;
import com.interface21.beans.factory.BeanFactory;
import com.interface21.beans.factory.NoSuchBeanDefinitionException;

/**
 * One singleton to rule them all
 * @author Rod Johnson
 * @since 02-Dec-02
 */
public class BeanFactoryBootstrap {
	
	public static final String BEAN_FACTORY_BEAN_NAME = "bootstrapBeanFactory";

	private static BeanFactoryBootstrap instance;
	
	private static BeansException startupException;
	
	private static void initializeSingleton() {
		try {
			instance = new BeanFactoryBootstrap();
		}
		catch (BeansException ex) {
			startupException = ex;
		}
	}
	
	static {
		initializeSingleton();
	}
	
	
	public static BeanFactoryBootstrap getInstance() throws BeansException {
		if (startupException != null)
			throw startupException;
		// Really an assertion
		if (instance == null)
			throw new BootstrapException("Anomaly: instance and exception null", null);
		return instance;
	}
	
	
	/**
	 * For testing only. Cleans and reinitalizes the instance.
	 * Do not use in a production application!
	 */
	static void reinitialize() {
		instance = null;
		startupException = null;
		initializeSingleton();
	}
	
	private BeanFactory bootstrapFactory;
	
	// ALSO NEED TO TEST WITH REAL SYSTEM PROPERTIES...
	
	/**
	 * Apply rules to load factory
	 */
	private BeanFactoryBootstrap() throws BeansException {
		
		
		ListableBeanFactoryImpl startupFactory = new ListableBeanFactoryImpl();
		try {
			startupFactory.registerBeanDefinitions(System.getProperties(), "");
			this.bootstrapFactory = (BeanFactory) startupFactory.getBean(BEAN_FACTORY_BEAN_NAME);
		}
		catch (ClassCastException ex) {
			throw new BootstrapException("Bootstrap bean factory class does not implement BeanFactory interface", ex);
		}
		catch (NoSuchBeanDefinitionException ex) {
			throw new BootstrapException("No bean named '" + BEAN_FACTORY_BEAN_NAME + "' in system properties: [" + startupFactory + "]", null);
		}
		catch (BeansException ex) {
			throw new BootstrapException("Failed to bootstrap bean factory", ex);
		}
	}
	
	
	public BeanFactory getBeanFactory() {
		return bootstrapFactory;
	}
	
}
