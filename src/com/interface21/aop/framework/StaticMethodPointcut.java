/*
 * The Spring Framework is published under the terms
 * of the Apache Software License.
 */
 
package com.interface21.aop.framework;

import java.lang.reflect.Method;

import org.aopalliance.intercept.AttributeRegistry;

/**
 * Interface to be implemented by objects that can cause
 * conditional invocation of an Interceptor depending on
 * the method, and attributes passed.
 * This differs from a <i>dynamic</i> pointcut, which 
 * can also access arguments (the dynamic part of the joinpoint). 
 * Static pointcuts can be expected to outperform dynamic pointcuts,
 * as it's possible to determine whether or not to apply the
 * pointcut before processing individual method calls, meaning that
 * a regular expression or the like may need to be evaluated only once.
 * @author Rod Johnson
 * @since 03-Apr-2003
 * @version $Id$
 */
public interface StaticMethodPointcut extends MethodPointcut {

	/**
	 * Should the interceptor be invoked?
	 * This method is invoked before any interceptors have
	 * been invoked; potentially when the AOP proxy is constructed.
	 * @param m method being invoked
	 * @param attributeRegistry registry of attributes.
	 * Some implementations may wish to decide whether their
	 * interceptor should be invoked based on the value of this object.
	 * @return boolean whether the interceptor referenced
	 * by this object should be invoked
	 */
	boolean applies(Method m, AttributeRegistry attributeRegistry);
	
}
