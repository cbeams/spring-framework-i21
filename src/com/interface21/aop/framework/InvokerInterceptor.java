/*
 * The Spring Framework is published under the terms
 * of the Apache Software License.
 */
 
package com.interface21.aop.framework;

import java.lang.reflect.InvocationTargetException;

import org.aopalliance.intercept.AspectException;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * Implementation of Interceptor interface that 
 * invokes a local target object using reflection.
 * This should always be the last interceptor in the chain.
 * It does not invoke proceed() on the MethodInvocation. 
 * @author Rod Johnson
 * @version $Id$
 */
public class InvokerInterceptor implements MethodInterceptor, ProxyInterceptor {

	/** Target invoked using reflection */	
	private Object target;
	
	public InvokerInterceptor() {
	}
	
	public InvokerInterceptor(Object target) {
		this.target = target;
	}
	
	public void setTarget(Object target) {
		this.target = target;
	}
	
	public Object getTarget() {
		return this.target;
	}

	/**
	 * @see org.aopalliance.intercept.MethodInterceptor#invoke(MethodInvocation)
	 */
	public Object invoke(MethodInvocation invocation) throws Throwable {
		
		// Set the target on the invocation
		if (invocation instanceof MethodInvocationImpl) {
			((MethodInvocationImpl) invocation).setTarget(this.target);
		}
		
		// Use reflection to invoke the method
		try {
			Object rval = invocation.getMethod().invoke(this.target, invocation.getArguments());
			return rval;
		}
		catch (InvocationTargetException ex) {
			// Invoked method threw a checked exception. 
			// We must rethrow it. The client won't see the interceptor
			Throwable t = ex.getTargetException();
			throw t;
		}
		catch (IllegalAccessException ex) {
			throw new AspectException("Couldn't access method " + invocation.getMethod() + ", ", ex);
		}
	}

}
