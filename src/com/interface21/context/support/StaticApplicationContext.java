package com.interface21.context.support;

import java.util.HashMap;

import com.interface21.beans.BeansException;
import com.interface21.beans.PropertyValues;
import com.interface21.beans.factory.BeanFactory;
import com.interface21.beans.factory.ListableBeanFactory;
import com.interface21.beans.factory.support.DefaultRootBeanDefinition;
import com.interface21.beans.factory.support.ListableBeanFactoryImpl;
import com.interface21.context.ApplicationContext;
import com.interface21.context.ApplicationContextException;

/**
 * ApplicationContext to allow concrete registration
 * of Java objects in code, rather than from external configuration sources. 
 * Especially useful for testing.
 * @author Rod Johnson
 * @version $RevisionId$
 */
public class StaticApplicationContext extends AbstractApplicationContext {
 
	//---------------------------------------------------------------------
	// Instance data
	//---------------------------------------------------------------------
	/*package*/ ListableBeanFactoryImpl defaultBeanFactory = new ListableBeanFactoryImpl();

	/** Namespace --> name */	
	private HashMap beanFactoryHash = new HashMap(); 


	//---------------------------------------------------------------------
	// Constructor
	//---------------------------------------------------------------------	
	public StaticApplicationContext() throws BeansException, ApplicationContextException {
		// Register the message source bean
		defaultBeanFactory.registerBeanDefinition(MESSAGE_SOURCE_BEAN_NAME, 
			new DefaultRootBeanDefinition(StaticMessageSource.class, null, true));
			
		//refresh();			
	} 
	
	public StaticApplicationContext(ApplicationContext parent) throws BeansException, ApplicationContextException {
		super(parent);
		
		// Register the message source bean
		defaultBeanFactory.registerBeanDefinition(MESSAGE_SOURCE_BEAN_NAME, 
			new DefaultRootBeanDefinition(StaticMessageSource.class, null, true));
			
		//refresh();			
	}
	
	
	/**
	 * Must invoke when finished
	 */
	public void rebuild() throws ApplicationContextException {
		refresh();
	}

	//---------------------------------------------------------------------
	// Implementation of abstract methods
	//---------------------------------------------------------------------
	/**
	 * Return the BeanFactory for this namespace
	 * @see AbstractApplicationContext#loadBeanFactory(String)
	 */
	protected BeanFactory loadBeanFactory(String namespace) throws ApplicationContextException {
		BeanFactory bf = (BeanFactory) beanFactoryHash.get(namespace);
		if (bf == null)
			// No one's created it yet
			throw new ApplicationContextException("Unknown namespace '" + namespace + "'");
		return bf;
	}

	/**
	 *  Do nothing: we rely on callers to update our public methods
	 * @see AbstractApplicationContext#refreshDefaultBeanFactory()
	 */
	protected void refreshBeanFactory() throws ApplicationContextException {
	}

	/**
	 * @see ApplicationContext#getBeanFactory()
	 */
	protected ListableBeanFactory getBeanFactory() {
		return defaultBeanFactory;
	}
	
	
	//---------------------------------------------------------------------
	// Public methods
	//---------------------------------------------------------------------
	/**
	 * Register a bean with the default bean factory
	 */
	public void registerSingleton(String name, Class clazz, PropertyValues pvs) throws BeansException {
		defaultBeanFactory.registerBeanDefinition(name, 
			new DefaultRootBeanDefinition(clazz, pvs, true));
	}
	
	public void registerPrototype(String name, Class clazz, PropertyValues pvs) throws BeansException {
		defaultBeanFactory.registerBeanDefinition(name, 
			new DefaultRootBeanDefinition(clazz, pvs, false));
	}
	
	
	public String addMessage(String code, String message) {
		//messageSource.addMessage(code,message)
		throw new UnsupportedOperationException("Addmessage not yet implemeneted");
	}


}

