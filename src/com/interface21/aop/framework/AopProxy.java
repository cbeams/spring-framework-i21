/*
 * Generic framework code included with 
 * <a href="http://www.amazon.com/exec/obidos/tg/detail/-/1861007841/">Expert One-On-One J2EE Design and Development</a>
 * by Rod Johnson (Wrox, 2002). 
 * This code is free to use and modify. However, please
 * acknowledge the source and include the above URL in each
 * class using or derived from this code. 
 * Please contact <a href="mailto:rod.johnson@interface21.com">rod.johnson@interface21.com</a>
 * for commercial support.
 */
 
package com.interface21.aop.framework;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

import org.aopalliance.MethodInterceptor;
import org.apache.log4j.Logger;

/**
 * 
 * <br/>Dynamic proxies implemented using this class can be threadsafe if the
 * underlying (target) class is threadsafe.
 * @author Rod Johnson
 * @version $Revision$
 */
public class AopProxy implements InvocationHandler {
	
	private static Method EQUALS_METHOD;
	
	// We need a static block to handle checked exceptions
	static {
		try {
			EQUALS_METHOD = Object.class.getMethod("equals", new Class[] { Object.class});
		} 
		catch (Exception e) {
			// Cannot happen
		} 
	}
	
	//---------------------------------------------------------------------
	// Static methods
	//---------------------------------------------------------------------
	
	/**
	 * @param clazz interface to proxy
	 * @return a new Proxy object for the given object proxying
	 * the given interface
	 */
	public static Object getProxy(AopProxy aop) {
		return getProxy(Thread.currentThread().getContextClassLoader(), 
					 aop);
	}
	
	/**
	 * @param obj object to proxy
	 * @param clazz interface to proxy
	 * @return a new Proxy object for the given object proxying
	 * the given interface
	 */
	public static Object getProxy(ClassLoader cl, AopProxy aop) {
		//System.out.println(StringUtils.arrayToDelimitedString(aop.config.getProxiedInterfaces(), "/"));
		Object proxy =
			Proxy.newProxyInstance(cl, aop.config.getProxiedInterfaces(), aop);
		return proxy;
	}
	

	//---------------------------------------------------------------------
	// Instance data
	//---------------------------------------------------------------------
	/**
	* Create a logging category that is available
	* to subclasses. 
	*/
	protected final Logger logger = Logger.getLogger(getClass().getName());

	private ProxyConfig config;
	


	//---------------------------------------------------------------------
	// Constructor
	//---------------------------------------------------------------------
	/**
	 * 
	 * @throws AopConfigException if the config is invalid. We try
	 * to throw an informative exception in this case, rather than let
	 * a mysterious failure happen later.
	 */
	public AopProxy(ProxyConfig config) throws AopConfigException {
		if (config == null)
			throw new AopConfigException("Cannot create AopProxy with null ProxyConfig");
		if (config.getInterceptors() == null || config.getInterceptors().length == 0)
			throw new AopConfigException("Cannot create AopProxy with null interceptors");
		this.config = config;
	}
	
	// EXPOSE AS SUPER METHOD!?
	//public ProxyConfig getConfig() {
	//	return this.config;
	//}
	

	//---------------------------------------------------------------------
	// Implementation of InvocationHandler
	//---------------------------------------------------------------------
	/**
	 * Implementation of InvocationHandler.invoke.
	 * Callers will see exactly the exception thrown by the target, unless a hook
	 * method throws an exception.
	 */
	public final Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
	
		// TODO refactor into InvocationFactory?
		MethodInvocationImpl invocation = new MethodInvocationImpl(proxy, config.getTarget(), 
									method.getDeclaringClass(), //?
									method, args,
									(MethodInterceptor[]) this.config.getInterceptors(), // could customize here
									this.config.getAttributeRegistry());
		
		if (this.config.getExposeInvocation()) {
			// Make invocation available if necessary
			AopContext.setCurrentInvocation(invocation);
		}
		
		try {
			if (EQUALS_METHOD.equals(invocation.getMethod())) {
				
				// What if equals throws exception!?
				logger.debug("Intercepting equals() method in proxy");
				return invocation.getMethod().invoke(this, invocation.getArguments());
			}
			
			Object retVal = invocation.invokeNext();
			if (retVal != null && retVal == invocation.getTarget()) {
				// Special case: it returned this
				// Note that we can't help if the target sets
				// a reference to itself in another returned object
				logger.debug("Replacing 'this' with reference to proxy");
				retVal = proxy;
			}
			return retVal;
		}
		finally {
			if (this.config.getExposeInvocation()) {
				AopContext.setCurrentInvocation(null);
			}
		
			if (logger.isDebugEnabled()) {
				logger.debug("Processed invocation [" + invocation + "]");
			}
		}
	}	// invoke
	
	
	/**
	 * Equality means interceptors and interfaces are ==
	 * @see java.lang.Object#equals(java.lang.Object)
	 * @param other may be a dynamic proxy wrapping an instance
	 * of this class
	 */
	public boolean equals(Object other) {		
		if (other == null) 
			return false;
		if (other == this)
			return true;
		
		AopProxy aopr2 = null;
		if (other instanceof AopProxy) {
			aopr2 = (AopProxy) other;
		}
		else if (Proxy.isProxyClass(other.getClass())) {
			InvocationHandler ih = Proxy.getInvocationHandler((Proxy) other);
			if (!(ih instanceof AopProxy))
				return false;
			aopr2 = (AopProxy) ih; 
		}
		else {
			// Not a valid comparison
			return false;
		}
		
		// If we get here, aopr2 is the other AopProxy
		if (this == aopr2)
			return true;
			
		if (!Arrays.equals(aopr2.config.getProxiedInterfaces(), this.config.getProxiedInterfaces()))
			return false;
		
		// List equality is cool
		if (!aopr2.config.getInterceptors().equals(this.config.getInterceptors()))
			return false;
			
		return true;
	}	// equals
	
}	// class AopProxy