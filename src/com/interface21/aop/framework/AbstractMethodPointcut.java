/*
 * The Spring Framework is published under the terms
 * of the Apache Software License.
 */
 
package com.interface21.aop.framework;

import org.aopalliance.MethodInterceptor;

/**
 * Abstract convenience superclass for implementations of
 * DynamicMethodPointcut or StaticMethodPointcut. Handles
 * interceptor.
 * @author Rod Johnson
 * @since July 22, 2003
 * @version $Id$
 */
public abstract class AbstractMethodPointcut implements MethodPointcut {

	private final MethodInterceptor interceptor;
	
	protected AbstractMethodPointcut(MethodInterceptor interceptor) {
		this.interceptor = interceptor;
	}
	/**
	 * @see com.interface21.aop.framework.MethodPointcut#getInterceptor()
	 */
	public MethodInterceptor getInterceptor() {
		return this.interceptor;
	}

}
