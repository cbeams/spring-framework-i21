/*
 * The Spring Framework is published under the terms
 * of the Apache Software License.
 */
 
package com.interface21.aop.framework;

import org.aopalliance.intercept.MethodInvocation;

/**
 * Interface to be implemented by objects that can cause
 * conditional invocation of an Interceptor depending on
 * the method, arguments and attributes passed.
 * This differs from a <i>static</i> pointcut, which depends only
 * on the static part of the joinpoint, and cannot involve arguments.
 * Static pointcuts can be expected to outperform dynamic pointcuts,
 * as it's possible to determine whether or not to apply the
 * pointcut before processing individual method calls, meaning that
 * a regular expression or the like may need to be evaluated only once.
 * @author Rod Johnson
 * @since 03-Apr-2003
 * @version $Id$
 */
public interface DynamicMethodPointcut extends MethodPointcut {

	/**
	 * Should the interceptor be invoked?
	 * This method is invoked before any interceptors have
	 * been invoked.
	 * @param invocation
	 * Some implementations may wish to decide whether their
	 * interceptor should be invoked based on the value of this object.
	 * @return boolean whether the interceptor referenced
	 * by this object should be invoked
	 */
	boolean applies(MethodInvocation invocation);
	
}
