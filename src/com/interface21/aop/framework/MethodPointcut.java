/*
 * The Spring Framework is published under the terms
 * of the Apache Software License.
 */
 
package com.interface21.aop.framework;

import java.lang.reflect.Method;

import org.aopalliance.AttributeRegistry;
import org.aopalliance.MethodInterceptor;

/**
 * Interface to be implemented by objects that can cause
 * conditional invocation of an Interceptor depending on
 * the method, arguments and attributes passed.
 * @author Rod Johnson
 * @since 03-Apr-2003
 * @version $Revision$
 */
public interface MethodPointcut {

	/**
	 * Return the interceptor to run conditionally
	 * @return MethodInterceptor
	 */
	MethodInterceptor getInterceptor();
	
	/**
	 * Should the interceptor be invoked?
	 * This method is invoked before any interceptors have
	 * been invoked.
	 * @param m method being invoked
	 * @param args arguments to the method
	 * @param attributeRegistry registry of attributes.
	 * Some implementations may wish to decide whether their
	 * interceptor should be invoked based on the value of this object.
	 * @return boolean whether the interceptor referenced
	 * by this object should be invoked
	 */
	boolean applies(Method m, Object[] args, AttributeRegistry attributeRegistry);
	
	/**
	 * Arbitrary int value. Depends on other values.
	 * Higher values cause interceptors to be invoked earlier
	 * in the invocation chain.
	 * @return int
	 */
	//int getPrecedence();
}
