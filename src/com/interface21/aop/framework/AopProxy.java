/*
 * The Spring Framework is published under the terms
 * of the Apache Software License.
 */
 
package com.interface21.aop.framework;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

import net.sf.cglib.Enhancer;
import net.sf.cglib.MethodInterceptor;
import net.sf.cglib.MethodProxy;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * InvocationHandler implementation for the Spring AOP framework,
 * based on either J2SE 1.3+ dynamic proxies or CGLIB proxies.
 *
 * <p>Creates a J2SE proxy when proxied interfaces are given, a CGLIB proxy
 * for the actual target class if not. Note that the latter will only work
 * if the target class does not have final methods, as a dynamic subclass
 * will be created at runtime.
 *
 * <p>Objects of this type should be obtained through proxy factories,
 * configured by a ProxyConfig implementation. This class is internal
 * to the Spring framework and need not be used directly by client code.
 *
 * <p>Proxies created using this class can be threadsafe if the
 * underlying (target) class is threadsafe.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @version $Id$
 * @see java.lang.reflect.Proxy
 * @see net.sf.cglib.Enhancer
 */
public class AopProxy implements InvocationHandler {
	
	private static Method EQUALS_METHOD;
	
	// We need a static block to handle checked exceptions
	static {
		try {
			EQUALS_METHOD = Object.class.getMethod("equals", new Class[] { Object.class});
		} 
		catch (NoSuchMethodException e) {
			// Cannot happen
		} 
	}
	
	protected final Log logger = LogFactory.getLog(getClass());

	/** Config used to configure this proxy */
	private ProxyConfig config;
	
	/**
	 * 
	 * @throws AopConfigException if the config is invalid. We try
	 * to throw an informative exception in this case, rather than let
	 * a mysterious failure happen later.
	 */
	public AopProxy(ProxyConfig config) throws AopConfigException {
		if (config == null)
			throw new AopConfigException("Cannot create AopProxy with null ProxyConfig");
		if (config.getMethodPointcuts() == null || config.getMethodPointcuts().size() == 0)
			throw new AopConfigException("Cannot create AopProxy with null interceptors");
		this.config = config;
	}
	
	/**
	 * Implementation of InvocationHandler.invoke.
	 * Callers will see exactly the exception thrown by the target, unless a hook
	 * method throws an exception.
	 */
	public final Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
	
		// Create a new invocation object
		// TODO refactor into InvocationFactory?
		MethodInvocationImpl invocation = new MethodInvocationImpl(proxy,
		              this.config.getTarget(), method.getDeclaringClass(),
									method, args,
									this.config.getMethodPointcuts(), // could customize here
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
			
			Object retVal = invocation.proceed();
			if (retVal != null && retVal == invocation.getThis()) {
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
		}
	}

	/**
	 * Creates a new Proxy object for the given object, proxying
	 * the given interface. Uses the thread context class loader.
	 */
	public Object getProxy() {
		return getProxy(Thread.currentThread().getContextClassLoader());
	}

	/**
	 * Creates a new Proxy object for the given object, proxying
	 * the given interface. Uses the given class loader.
	 */
	public Object getProxy(ClassLoader cl) {
		if (this.config.getProxiedInterfaces() != null && this.config.getProxiedInterfaces().length > 0) {
			// proxy specific interfaces: J2SE Proxy is sufficient
			logger.info("Creating J2SE proxy for [" + this.config.getTarget() + "]");
			return Proxy.newProxyInstance(cl, this.config.getProxiedInterfaces(), this);
		}
		else {
			if (this.config.getTarget() == null) {
				throw new IllegalArgumentException("Either an interface or a target is required for proxy creation");
			}
			// proxy the given class itself: CGLIB necessary
			logger.info("Creating CGLIB proxy for [" + this.config.getTarget() + "]");
			// delegate to inner class to avoid AopProxy runtime dependency on CGLIB
			// --> J2SE proxies work without cglib.jar then
			return (new CglibProxyFactory()).createProxy();
		}
	}

	/**
	 * Equality means interceptors and interfaces are ==.
	 * This will only work with J2SE dynamic proxies,	not with CGLIB ones
	 * (as CGLIB doesn't delegate equals calls to proxies).
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
			InvocationHandler ih = Proxy.getInvocationHandler(other);
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
		if (!aopr2.config.getMethodPointcuts().equals(this.config.getMethodPointcuts()))
			return false;
			
		return true;
	}


	/**
	 * Putting CGLIB proxy creation in an inner class allows to avoid an AopProxy
	 * runtime dependency on CGLIB --> J2SE proxies work without cglib.jar then.
	 */
	private class CglibProxyFactory {

		private Object createProxy() {
			return Enhancer.enhance(config.getTarget().getClass(), config.getProxiedInterfaces(),
				new MethodInterceptor() {
					public Object intercept(Object handler, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
						return invoke(handler, method, objects);
					}
				}
			);
		}
	}

}
