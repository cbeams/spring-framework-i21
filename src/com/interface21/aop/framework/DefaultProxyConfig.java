/*
 * The Spring Framework is published under the terms
 * of the Apache Software License.
 */
 
package com.interface21.aop.framework;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.aopalliance.AttributeRegistry;
import org.aopalliance.Interceptor;
import org.aopalliance.MethodInterceptor;
import org.aopalliance.ProxyInterceptor;
import org.apache.log4j.Logger;

import com.interface21.beans.factory.InitializingBean;

/** 
* Superclass for AOP Proxy configuration objects.
* Subclasses are normally factories from which AOP proxy instances
* are obtained directly.
* Implements InitializingBean interface. If using outside
* a BeanFactory (which will call this lifecycle method automatically)
* check subclass documentation as to whether it's necessary to call
* a method such as afterPropertiesSet() to complete initialization after
* all JavaBean properties has been set.
* <br>
* This class frees subclasses of the housekeeping of interceptors and pointcuts,
* but doesn't actually create AOP proxies.
* @author Rod Johnson
* @version $Id$
*/
public class DefaultProxyConfig implements ProxyConfig, InitializingBean {

	private AttributeRegistry attributeRegistry;

	protected final Logger logger = Logger.getLogger(getClass().getName());

	/** List of MethodPointcut */
	private List pointcuts = new LinkedList();
	
	/** Interfaces to be implemented by the proxy */
	private Class[] interfaces = new Class[0];

	/**
	 * May be null. Reassessed on adding an interceptor.
	 */
	private Object target;

	/**
	 * Should proxies obtained from this configuration expose
	 * Invocation for the AopContext class to retrieve for targets?
	 * The default is false, as enabling this property may
	 * impair performance.
	 */
	private boolean exposeInvocation;
	
	/**
	 *  No arg constructor to allow use as a Java bean
	 *
	 */
	public DefaultProxyConfig() {
	}
	
	public DefaultProxyConfig(Class[] intfs, boolean exposeInvocation, AttributeRegistry attributeRegistry) {
		setInterfaces(intfs);
		setExposeInvocation(exposeInvocation);
		setAttributeRegistry(attributeRegistry);
	}

	
	/**
	 * Gets the exposeInvocation.
	 * @return Returns a boolean
	 */
	public boolean getExposeInvocation() {
		return exposeInvocation;
	}

	/**
	 * Sets the exposeInvocation property, governing
	 * whether the AopContext class will be usable
	 * by target objects.
	 * @param exposeInvocation The exposeInvocation to set
	 */
	public final void setExposeInvocation(boolean exposeInvocation) {
		this.exposeInvocation = exposeInvocation;
	}


	/** 
	 * @see com.interface21.aop.framework.ProxyConfig#addInterceptor(org.aopalliance.Interceptor)
	 */
	public void addInterceptor(Interceptor interceptor) {
		int pos = (this.pointcuts != null) ? this.pointcuts.size() : 0;
		addInterceptor(pos, interceptor);
	}
	
	/**
	 * Convenient method to convert array to
	 * a list for easier manipulation
	 * @return List
	 */
	private List arrayToList(Object[] array) {
		// Allow some leeway for manipulation
		List l = new ArrayList(array.length + 5);
		for (int i = 0; i < array.length; i++)
			l.add(array[i]);
		return l;
	}
	
	/**
	 * New interfaces will only be available when a new proxy is obtained
	 * through getObject().
	 * The same goes for removing interfaces.
	 * @see com.interface21.aop.framework.ProxyConfig#addInterceptor(int, org.aopalliance.Interceptor)
	 */
	public final void addInterceptor(int pos, Interceptor interceptor) {
		if (!(interceptor instanceof MethodInterceptor))
			throw new AopConfigException(getClass().getName() + " only handles MethodInterceptors");
		addMethodPointcut(pos, new AlwaysInvoked((MethodInterceptor) interceptor));
	}

	/**
	 * We're going to add the given interceptor. Add any aspect interfaces it
	 * means we'll additionally proxy.
	 * @param interceptor
	 */
	private void addAspectInterfacesIfNecessary(Interceptor interceptor) {
		 if (interceptor instanceof AspectInterfaceInterceptor) {
		 	System.out.println("Added new aspect interface");
			 AspectInterfaceInterceptor aii = (AspectInterfaceInterceptor) interceptor;
			 for (int i = 0; i < aii.getAspectInterfaces().length; i++) {
				 addInterface(aii.getAspectInterfaces()[i]);
			 }
		 }
	}	
	
	
	
	/**
	 * Work out target from list of interceptors
	 * @param interceptorList list of interceptors
	 * @throws AopConfigException if the chain is invalid
	 */
	private void computeTargetAndCheckValidity() throws AopConfigException {
		this.target = null;
		
		for (int i = 0; i < this.pointcuts.size(); i++) {
			MethodPointcut pc = (MethodPointcut) this.pointcuts.get(i);
			if (pc.getInterceptor() instanceof ProxyInterceptor) {
				if (i < pointcuts.size() -1) {
					//System.out.println("HaCK: commented out");
					throw new AopConfigException("Can only have ProxyInterceptor at end of list: had " + i + " and " + pointcuts.size());
				}
				else {
					// Interceptor is at end of list
					this.target = ((ProxyInterceptor) pc.getInterceptor()).getTarget();
					logger.info("Detected target when adding ProxyInterceptor to end of interceptor array");
				}
			}
		}
	}	// computeTargetAndCheckValidity
	
	/**
	 * Remove the given interceptor
	 * @see com.interface21.aop.framework.ProxyConfig#removeInterceptor(org.aopalliance.Interceptor)
	 */
	public final boolean removeInterceptor(Interceptor interceptor) {
		boolean removed = false;
		for (int i = 0; i < this.pointcuts.size() && !removed; i++) {
			MethodPointcut pc = (MethodPointcut) this.pointcuts.get(i);
			if (pc.getInterceptor() == interceptor) {
				this.pointcuts.remove(i);
				removed = true;
			}
		}
	
		if (removed) {
			//	We may need to remove interfaces if it was an AspectInterceptor
			 if (interceptor instanceof AspectInterfaceInterceptor) {
				 AspectInterfaceInterceptor aii = (AspectInterfaceInterceptor) interceptor;
				 for (int i = 0; i < aii.getAspectInterfaces().length; i++) {
					 removeInterface(aii.getAspectInterfaces()[i]);
				 }
			 }
			computeTargetAndCheckValidity();
		}
		
		return removed;
	}	// removeInterceptor
	
	/**
	 * Add a new proxied interface.
	 * @param newInterface additional interface to proxy.
	 */
	protected final void addInterface(Class newInterface) {
		List l = arrayToList(this.interfaces);
		l.add(newInterface);
		this.interfaces = (Class[]) l.toArray(new Class[l.size()]);
	}
	
	
	/**
	 * Remove a proxied interface.
	 * Does nothing if it isn't proxied.
	 * @param intf
	 * @return boolean
	 */
	protected final boolean removeInterface(Class intf) {
		List l = arrayToList(this.interfaces);
		boolean removed = l.remove(intf);
		if (removed) {
			this.interfaces = (Class[]) l.toArray(new Class[l.size()]);
		}
		return removed;
	}

	/**
	 * @see com.interface21.aop.framework.ProxyConfig#getProxiedInterfaces()
	 */
	public final Class[] getProxiedInterfaces() {
		return this.interfaces;
	}
	

	/**
	 * @see com.interface21.aop.framework.ProxyConfig#getAttributeRegistry()
	 */
	public AttributeRegistry getAttributeRegistry() {
		return this.attributeRegistry;
	}

	/**
	 * Sets the attributeRegistry.
	 * @param attributeRegistry The attributeRegistry to set
	 */
	public void setAttributeRegistry(AttributeRegistry attributeRegistry) {
		this.attributeRegistry = attributeRegistry;
	}

	/**
	 * Sets the interfaces to be proxied.
	 * This method must be called before using the proxy.
	 * @param interfaces The interfaces to set
	 */
	protected void setInterfaces(Class[] interfaces) {
		this.interfaces = interfaces;
	}

	/**
	 * Lifecycle method. This implementation merely checks
	 * that configuration is complete: it doesn't do anything.
	 * @see com.interface21.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	public void afterPropertiesSet() throws Exception {
		if (this.interfaces == null)
			throw new AopConfigException("Must set interfaces");
	}

	/**
	 * @see com.interface21.aop.framework.ProxyConfig#getTarget()
	 */
	public Object getTarget() {
		return this.target;
	}

	/**
	 * @see com.interface21.aop.framework.ProxyConfig#addMethodPointcut(int, com.interface21.aop.framework.MethodPointcut)
	 */
	public void addMethodPointcut(int pos, MethodPointcut pc) {
		this.pointcuts.add(pos, pc);
		// If we added it to the end of the list, we may need to update target
		try {
			computeTargetAndCheckValidity();

			addAspectInterfacesIfNecessary(pc.getInterceptor());
		}
		catch (AopConfigException ex) {
			// rollback the change. bit of a hack
			this.pointcuts.remove(pc);
			throw ex;
		}
	} 
	
	/**
	 * @see com.interface21.aop.framework.ProxyConfig#addMethodPointcut(com.interface21.aop.framework.MethodPointcut)
	 */
	public void addMethodPointcut(MethodPointcut pc) {
		int pos = (this.pointcuts != null) ? this.pointcuts.size() : 0;
		addMethodPointcut(pos, pc);
	}

	/**
	 * @see com.interface21.aop.framework.ProxyConfig#getMethodPointcuts()
	 */
	public List getMethodPointcuts() {
		// TODO make this list unmodifiable?
		return this.pointcuts;
	}
	
	/**
	 * Replace the given pointcut
	 * @param pc1 pointcut to replace
	 * @param pc2 pointcut to replace it with
	 * @return boolean whether it was replaced. If the pointcut
	 * wasn't found in the list of pointcuts, this method
	 * returns false and does nothing.
	 */
	public final boolean replaceMethodPointcut(MethodPointcut pc1, MethodPointcut pc2) {
		if (!this.pointcuts.contains(pc1))
			return false;
		this.pointcuts.set(this.pointcuts.indexOf(pc1), pc2);
		return true;
	}
	
	/**
	 * Is this interceptor included in any pointcut?
	 * @param mi interceptor to check inclusion of
	 * @return boolean whether this interceptor instance could be
	 * run in an invocation.
	 */
	public final boolean interceptorIncluded(MethodInterceptor mi) {
		if (this.pointcuts.size() == 0)
			return false;
		for (int i = 0; i < this.pointcuts.size(); i++) {
			MethodPointcut pc = (MethodPointcut) this.pointcuts.get(i);
			if (pc.getInterceptor() == mi) 
				return true;
		}
		return false;
	}
	
	/**
	 * Count interceptors of the given class
	 * @param interceptorClass class of the interceptor to check
	 * @return int the count of the interceptors of this class or
	 * subclasses
	 */
	public final int countInterceptorsOfType(Class interceptorClass) {
		if (this.pointcuts.size() == 0)
			return 0;
		int count = 0;
		for (int i = 0; i < this.pointcuts.size(); i++) {
			MethodPointcut pc = (MethodPointcut) this.pointcuts.get(i);
			if (interceptorClass.isAssignableFrom(pc.getInterceptor().getClass())) 
				++count;
		}
		return count;
	}
	
}	// DefaultProxyConfig
