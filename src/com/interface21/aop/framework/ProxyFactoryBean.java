package com.interface21.aop.framework;

import org.aopalliance.AspectException;
import org.aopalliance.AttributeRegistry;
import org.aopalliance.Interceptor;
import org.apache.log4j.Logger;

import com.interface21.beans.BeansException;
import com.interface21.beans.PropertyValues;
import com.interface21.beans.factory.BeanFactory;
import com.interface21.beans.factory.FactoryBean;
import com.interface21.beans.factory.Lifecycle;
import com.interface21.util.StringUtils;

/** 
* FactoryBean implementation for use to source
* AOP proxies from a Spring BeanFactory. 
* @author Rod Johnson
* TODO get pointcuts from bean factory, enabling anything declared
* to run.
*/
public class ProxyFactoryBean extends DefaultProxyConfig implements FactoryBean, Lifecycle {


	private AttributeRegistry attributeRegistry;

	private Logger logger = Logger.getLogger(getClass().getName());
	
	private boolean singleton = true;
	
	/**
	 * Singleton instance if we're using a singleton
	 */
	private Object singletonInstance;

	/** Alternative way to configure interceptors */
	private String[] interceptorNames;


	/**
	 * Set the name of the interface we're proxying
	 */
	public void setProxyInterfaces(String[] interfaceNames) throws AspectException, ClassNotFoundException {
		Class[] interfaces = new Class[interfaceNames.length];
		for (int i = 0; i < interfaceNames.length; i++) {
			interfaces[i] = Class.forName(interfaceNames[i]);
			// Check it's an interface
			if (!interfaces[i].isInterface())
				throw new AspectException("Can proxy only interfaces: " + interfaces[i] + " is a class");
		}
		setInterfaces(interfaces);
	}


	public void setInterceptorNames(String csv) {
		this.interceptorNames = StringUtils.commaDelimitedListToStringArray(csv);
	}
	
	

	/**
	 * @see com.interface21.beans.factory.Lifecycle#setBeanFactory(com.interface21.beans.factory.BeanFactory)
	 */
	public void setBeanFactory(BeanFactory beanFactory) {
		
		if (this.interceptorNames == null)
			throw new AopConfigException("Interceptor names must be set before using ProxyFactoryBean");
			
			
		// TODO
		// configure attribute registry from bean factory
		// if well-known bean...
		this.attributeRegistry = new Attrib4jAttributeRegistry();
		
		logger.info("Set BeanFactory. Will configure interceptor beans...");
		
		// Materialize interceptor chain from bean names
		for (int i = 0; i < this.interceptorNames.length; i++) {
			logger.debug("Configuring interceptor '" + this.interceptorNames[i] + "'");
			
			Object next = beanFactory.getBean(this.interceptorNames[i]);
			
			//	if (!(currentInterceptor instanceof ChainInterceptor))
			//		throw new AopException("Illegal interceptor chain: cannot add interceptors after [" + currentInterceptor + "]");
			
			if (next instanceof MethodPointcut) {
				addMethodPointcut((MethodPointcut) next);
			}
			else if (next instanceof Interceptor) {
				addInterceptor((Interceptor) next);
			}
			else {
				throw new AopConfigException("Illegal type: bean '" + this.interceptorNames[i] + "' must be of type MethodPointcut or Interceptor");
			}
		}
		
		// Create singleton instance if necessary
		if (isSingleton()) {
			this.singletonInstance = createInstance();
		}
	} 

	/**
	 * @see com.interface21.beans.factory.FactoryBean#getObject()
	 */
	public Object getObject() throws BeansException {
		if (this.singleton) {
			// Return singleton
			return this.singletonInstance;
		}
		else {
			// Create new interface
			return createInstance();
		}
	}
	
	
	private Object createInstance() {
		AopProxy proxy = new AopProxy(this);
		return AopProxy.getProxy(getClass().getClassLoader(), proxy);
	}

	/**
	 * This factory doesn't support pass through properties.
	 * @see com.interface21.beans.factory.FactoryBean#getPropertyValues()
	 */
	public PropertyValues getPropertyValues() {
		return null;
	}

	/**
	 * @see com.interface21.beans.factory.FactoryBean#isSingleton()
	 */
	public boolean isSingleton() {
		return this.singleton;
	}
	
	public void setSingleton(boolean singleton) {
		this.singleton = singleton;
	}
	
}