/*
 * The Spring Framework is published under the terms
 * of the Apache Software License.
 */
 
package com.interface21.aop.framework;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.aopalliance.AspectException;
import org.aopalliance.Interceptor;

import com.interface21.beans.BeansException;
import com.interface21.beans.PropertyValues;
import com.interface21.beans.factory.BeanFactory;
import com.interface21.beans.factory.FactoryBean;
import com.interface21.beans.factory.BeanFactoryAware;
import com.interface21.beans.factory.ListableBeanFactory;
import com.interface21.beans.factory.support.BeanFactoryUtils;
import com.interface21.core.OrderComparator;

/** 
 * FactoryBean implementation for use to source AOP proxies from a Spring BeanFactory.
 *
 * <p>Interceptors are identified by a list of bean names in the current bean factory.
 * These beans should be of type Interceptor or MethodPointcut. The last entry in
 * the list can be the name of any bean in the factory. If it's neither an Interceptor
 * or a MethodPointcut, a new InvokerInterceptor is added to wrap it.
 *
 * <p>Global interceptors can be added at the factory level. The specified ones are
 * expanded in an interceptor list where an "xxx*" entry is included in the list,
 * matching the given prefix with the bean names (e.g. "global*" would match both
 * "globalBean1" and "globalBean2", "*" all defined interceptors). The matching
 * interceptors get applied according to their returned order value, if they
 * implement the Ordered interface. An interceptor name list may not conclude
 * with a global "xxx*" pattern, as global interceptors cannot invoke targets.
 *
 * <p>Creates a J2SE proxy when proxy interfaces are given, a CGLIB proxy for the
 * actual target class if not. Note that the latter will only work if the target class
 * does not have final methods, as a dynamic subclass will be created at runtime.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @version $Id$
 * @see #setInterceptorNames
 * @see #setProxyInterfaces
 */
public class ProxyFactoryBean extends DefaultProxyConfig implements FactoryBean, BeanFactoryAware {

	/**
	 * This suffix in a value in an interceptor list indicates to expand globals.
	 */
	public static final String GLOBAL_SUFFIX = "*";

	private boolean singleton = true;
	
	/**
	 * Owning bean factory, which cannot be changed after this
	 * object is initialized.
	 */
	private BeanFactory beanFactory;
	
	/**
	 * Singleton instance if we're using a singleton
	 */
	private Object singletonInstance;
	
	/** 
	 * Map from PointCut or interceptor to bean name or null,
	 * depending on where it was sourced from. If it's sourced
	 * from a bean name, it will need to be refreshed each time a
	 * new prototype instance is created.
	 */
	private Map sourceMap = new HashMap();
	
	/** 
	 * Names of interceptor and pointcut beans in the factory.
	 * Default is for globals expansion only.
	 */
	private String[] interceptorNames = null;


	/**
	 * Set the names of the interfaces we're proxying. If no interface
	 * is given, a CGLIB for the actual class will be created.
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

	/**
	 * Set the list of Interceptor/MethodPointcut bean names. This must
	 * always be set to use this factory bean in a bean factory.
	 */
	public void setInterceptorNames(String[] interceptorNames) {
		this.interceptorNames = interceptorNames;
	}
	
	/**
	 * @see com.interface21.beans.factory.BeanFactoryAware#setBeanFactory(com.interface21.beans.factory.BeanFactory)
	 */
	public void setBeanFactory(BeanFactory beanFactory) {	
		this.beanFactory = beanFactory;
			
		logger.info("Set BeanFactory. Will configure interceptor beans...");
		createInterceptorChain();
		
		// Eagerly create singleton proxy instance if necessary
		if (isSingleton()) {
			this.singletonInstance = createInstance();
		}
	}


	/**
	 * Create the interceptor chain. The interceptors that
	 * are sourced from a BeanFactory will be refreshed each time
	 * a new prototype instance is added. Interceptors
	 * added programmatically through the factory API are
	 * unaffected by such changes.
	 */
	private void createInterceptorChain() throws AopConfigException, BeansException {
		
		if (this.interceptorNames == null || this.interceptorNames.length == 0)
			throw new AopConfigException("Interceptor names are required");
			
		// Globals can't be last
		if (this.interceptorNames[this.interceptorNames.length - 1].endsWith(GLOBAL_SUFFIX)) {
			throw new AopConfigException("Target required after globals");
		}

		// Materialize interceptor chain from bean names
		for (int i = 0; i < this.interceptorNames.length; i++) {
			String name = this.interceptorNames[i];
			logger.debug("Configuring interceptor '" + name + "'");
			
			if (name.endsWith(GLOBAL_SUFFIX)) {
				if (!(this.beanFactory instanceof ListableBeanFactory)) {
					throw new AopConfigException("Can only use global pointcuts or interceptors with a ListableBeanFactory");
				}
				else {
					addGlobalInterceptorsAndPointcuts((ListableBeanFactory) this.beanFactory,
					                                  name.substring(0, name.length() - GLOBAL_SUFFIX.length()));
				}
			}
			else {
				// Add a named interceptor
				addPointcutOrInterceptor(this.beanFactory.getBean(this.interceptorNames[i]), this.interceptorNames[i]);
			}
		}
	}

	/**
	 * Refresh named beans from the interceptor chain.
	 * We need to do this every time a new prototype instance is
	 * returned, to return distinct instances of prototype interfaces
	 * and pointcuts.
	 */
	private void refreshInterceptorChain() {
		List pointcuts = getMethodPointcuts();
		for (Iterator iter = pointcuts.iterator(); iter.hasNext();) {
			DynamicMethodPointcut pc = (DynamicMethodPointcut) iter.next();
			String beanName = (String) this.sourceMap.get(pc);
			if (beanName != null) {
				logger.info("Refreshing bean named '" + beanName + "'");
			
				Object bean = this.beanFactory.getBean(beanName);
				DynamicMethodPointcut pc2 = null;
				// Bean may be a MethodPointcut or a target to wrap
				if (bean instanceof DynamicMethodPointcut) {
					pc2 = (DynamicMethodPointcut) bean;
				}
				else {
					// The special case when the object was a target
					// object, not an invoker or pointcut.
					// We need to create a fresh invoker interceptor wrapping
					// the new target.
					InvokerInterceptor ii = new InvokerInterceptor(bean);
					pc2 = new AlwaysInvoked(ii);
				}
				
				// What about aspect interfaces!? we're only updating
				replaceMethodPointcut(pc, pc2);
			}
		}
	}

	/**
	 * Add all global interceptors and pointcuts.
	 */
	private void addGlobalInterceptorsAndPointcuts(ListableBeanFactory beanFactory, String prefix) {
		Collection globalPointcutNames = BeanFactoryUtils.beanNamesIncludingAncestors(DynamicMethodPointcut.class, beanFactory);
		Collection globalInterceptorNames = BeanFactoryUtils.beanNamesIncludingAncestors(Interceptor.class, beanFactory);
		List beans = new ArrayList(globalPointcutNames.size() + globalInterceptorNames.size());
		Map names = new HashMap();
		for (Iterator itr = globalPointcutNames.iterator(); itr.hasNext();) {
			String name = (String) itr.next();
			Object bean = beanFactory.getBean(name);
			beans.add(bean);
			names.put(bean, name);
		}
		for (Iterator itr = globalInterceptorNames.iterator(); itr.hasNext();) {
			String name = (String) itr.next();
			Object bean = beanFactory.getBean(name);
			beans.add(bean);
			names.put(bean, name);
		}
		Collections.sort(beans, new OrderComparator());
		for (Iterator it = beans.iterator(); it.hasNext();) {
			Object bean = it.next();
			String name = (String) names.get(bean);
			if (name.startsWith(prefix)) {
				addPointcutOrInterceptor(bean, name);
			}
		}
	}


	/**
	 * Add the given interceptor, pointcut or object to the interceptor list.
	 * Because of these three possibilities, we can't type the signature
	 * more strongly.
	 * @param next interceptor, pointcut or target object. 
	 * @param name bean name from which we obtained this object in our owning
	 * bean factory.
	 */
	private void addPointcutOrInterceptor(Object next, String name) {
		logger.debug("Adding pointcut or interceptor [" + next + "] with name [" + name + "]");
		if (next instanceof DynamicMethodPointcut) {
			addMethodPointcut((DynamicMethodPointcut) next);
		}
		else if (next instanceof Interceptor) {
			addInterceptor((Interceptor) next);
		}
		else {
			// It's not a pointcut or interceptor.
			// It's a bean that needs an invoker around it.
			// TODO how do these get refreshed
			InvokerInterceptor ii = new InvokerInterceptor(next);
			addInterceptor(ii);
			//throw new AopConfigException("Illegal type: bean '" + name + "' must be of type MethodPointcut or Interceptor");
		}
		
		// Record the ultimate object as descended from the given bean name.
		// This tells us how to refresh the interceptor list, which we'll need to
		// do if we have to create a new prototype instance. Otherwise the new
		// prototype instance wouldn't be truly independent, because it might reference
		// the original instances of prototype interceptors.
		this.sourceMap.put(next, name);
	}

	/**
	 * Return a proxy. Invoked when clients obtain beans
	 * from this factory bean.
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
	
	
	/**
	 * Create an instance of the AOP proxy to be returned by this factory. 
	 * The instance will be cached for a singleton, and create on each call to 
	 * getObject() for a proxy.
	 * @return Object a fresh AOP proxy reflecting the current
	 * state of this factory
	 */
	private Object createInstance() {
		refreshInterceptorChain();
		AopProxy proxy = new AopProxy(this);
		return proxy.getProxy(getClass().getClassLoader());
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
	
	/**
	 * Set the value of the singleton property. Governs whether this factory
	 * should always return the same proxy instance (which implies the same target)
	 * or whether it should return a new prototype instance, which implies that
	 * the target and interceptors may be new instances also, if they are obtained
	 * from prototype bean definitions.
	 * This allows for fine control of independence/uniqueness in the object graph.
	 * @param singleton
	 */
	public void setSingleton(boolean singleton) {
		this.singleton = singleton;
	}
	
}
