package com.interface21.aop.framework;

import java.util.ArrayList;
import java.util.List;

import org.aopalliance.AttributeRegistry;
import org.aopalliance.Interceptor;
import org.aopalliance.MethodInterceptor;
import org.aopalliance.ProxyInterceptor;
import org.apache.log4j.Logger;

import com.interface21.beans.factory.InitializingBean;

/** 
* Superclass for dynamic proxies.
* Example:
* we set the bean class name which will be exposed,
* and then take another property to be the concrete
* class we'll construct and proxy to.
* 
* any other way to get bean factory!? but must all be available
* TODO programmatic also like old AopProxy
*/
public class DefaultProxyConfig implements ProxyConfig, InitializingBean {


	private AttributeRegistry attributeRegistry;

	protected final Logger logger = Logger.getLogger(getClass().getName());
	

	private MethodInterceptor[] interceptors = new MethodInterceptor[0];
	
	/** Interfaces to be implemented by the proxy */
	private Class[] interfaces = new Class[0];

	/**
	 * May be null. Reassessed on adding an interceptor.
	 */
	private Object target;

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
	 * Sets the exposeInvocation.
	 * @param exposeInvocation The exposeInvocation to set
	 */
	public final void setExposeInvocation(boolean exposeInvocation) {
		this.exposeInvocation = exposeInvocation;
	}


	/** 
	 * Can only be called once up and running
	 * @see com.interface21.aop.ProxyConfig#addInterceptor(org.aopalliance.Interceptor)
	 */
	public final void addInterceptor(Interceptor interceptor) {
		int pos = (this.interceptors != null) ? this.interceptors.length : 0;
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
	 * @see com.interface21.aop.ProxyConfig#addInterceptor(int, org.aopalliance.Interceptor)
	 */
	public final void addInterceptor(int pos, Interceptor interceptor) {
		
		if (!(interceptor instanceof MethodInterceptor))
			throw new AopConfigException(getClass().getName() + " only handles MethodInterceptors");
		
		List l = arrayToList(this.interceptors);
		l.add(pos, interceptor);
		
		 // If we added it to the end of the list, we may need to update target
		 computeTargetAndCheckValidity(l);

		// We may need to add interfaces
		 if (interceptor instanceof AspectInterfaceInterceptor) {
			 AspectInterfaceInterceptor aii = (AspectInterfaceInterceptor) interceptor;
			 for (int i = 0; i < aii.getAspectInterfaces().length; i++) {
				 addInterface(aii.getAspectInterfaces()[i]);
			 }
		 }
			 		
		// Convert back to array
		this.interceptors = (MethodInterceptor[]) l.toArray(new MethodInterceptor[l.size()]);
	}	// addInterceptor
	
	/**
	 * Work out target from list of interceptors
	 * @param interceptorList list of interceptors
	 * @throws AopConfigException if the chain is invalid
	 */
	private void computeTargetAndCheckValidity(List interceptorList) throws AopConfigException {
		this.target = null;
		for (int i = 0; i < interceptorList.size(); i++) {
			if (interceptorList.get(i) instanceof ProxyInterceptor) {
				if (i < interceptorList.size() -1) {
					throw new AopConfigException("Can only have ProxyInterceptor at end of list");
				}
				else {
					// Interceptor is at end of list
					this.target = ((ProxyInterceptor) interceptorList.get(i)).getTarget();
					logger.info("Detected target when adding ProxyInterceptor to end of interceptor array");
				}
			}
		}
	}	// computeTargetAndCheckValidity
	
	public final boolean removeInterceptor(Interceptor interceptor) {
		List l = arrayToList(this.interceptors);
		boolean removed = l.remove(interceptor);
		if (removed) {
			this.interceptors = (MethodInterceptor[]) l.toArray(new MethodInterceptor[l.size()]);
			//	We may need to add interfaces
			 if (interceptor instanceof AspectInterfaceInterceptor) {
				 AspectInterfaceInterceptor aii = (AspectInterfaceInterceptor) interceptor;
				 for (int i = 0; i < aii.getAspectInterfaces().length; i++) {
					 removeInterface(aii.getAspectInterfaces()[i]);
				 }
			 }
		}
		computeTargetAndCheckValidity(l);
		return removed;
	}
	
	protected final void addInterface(Class newInterface) {
		List l = arrayToList(this.interfaces);
		l.add(newInterface);
		this.interfaces = (Class[]) l.toArray(new Class[l.size()]);
	}
	
	
	/**
	 * Does nothing if it isn't there
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
	 * @see com.interface21.aop.ProxyConfig#getInterceptors()
	 */
	public final Interceptor[] getInterceptors() {
		return this.interceptors;
	}

	/**
	 * @see com.interface21.aop.ProxyConfig#getProxiedInterfaces()
	 */
	public final Class[] getProxiedInterfaces() {
		return this.interfaces;
	}
	

	/**
	 * @see com.interface21.aop.ProxyConfig#getAttributeRegistry()
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

}