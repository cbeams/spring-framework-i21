/*
 * The Spring Framework is published under the terms
 * of the Apache Software License.
 */
 
package com.interface21.aop.framework;

import java.lang.reflect.Method;

import org.aopalliance.AttributeRegistry;
import org.aopalliance.MethodInterceptor;

/**
 * MethodPointcut implementation wrapping an
 * Interceptor that should always run.
 * @author Rod Johnson
 * @since 04-Apr-2003
 * @version $Revision$
 */
public class AlwaysInvoked implements MethodPointcut {

	private final MethodInterceptor interceptor;
	
	public AlwaysInvoked(MethodInterceptor interceptor) {
		this.interceptor = interceptor;
	}
	/**
	 * @see com.interface21.aop.framework.MethodPointcut#getInterceptor()
	 */
	public MethodInterceptor getInterceptor() {
		return this.interceptor;
	}

	/**
	 * @see com.interface21.aop.framework.MethodPointcut#applies(java.lang.reflect.Method, java.lang.Object[], AttributeRegistry)
	 */
	public boolean applies(Method m, Object[] args, AttributeRegistry ar) {
		return true;
	}

}
