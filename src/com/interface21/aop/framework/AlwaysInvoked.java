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
 * @version $Id$
 */
public class AlwaysInvoked extends AbstractMethodPointcut implements StaticMethodPointcut {
	
	public AlwaysInvoked(MethodInterceptor interceptor) {
		super(interceptor);
	}

	/**
	 * @see com.interface21.aop.framework.StaticMethodPointcut#applies(java.lang.reflect.Method, java.lang.Object[], AttributeRegistry)
	 */
	public boolean applies(Method m, AttributeRegistry ar) {
		return true;
	}

}
