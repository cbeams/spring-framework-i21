/*
 * The Spring Framework is published under the terms
 * of the Apache Software License.
 */
 
package com.interface21.aop.framework;

import org.aopalliance.intercept.AspectException;
import org.aopalliance.intercept.MethodInvocation;

/**
 * Class containing static methods used to obtain
 * information about the current AOP invocation.
 * The currentInvocation() method--the only public method in
 * this class--is usable only if the AOP framework is configured
 * to expose invocations. The framework does not expose invocation contexts
 * by default, as there is a performance cost in doing so.
 * <br>
 * The functionality in this class might be used by a target
 * object that needed access to resources on the invocation.
 * However, this approach should not be used when there is
 * a reasonable alternative, as it makes application code dependent on
 * usage under AOP and--specifically--the Spring AOP framework.
 * @author Rod Johnson
 * @since 13-Mar-2003
 * @version $Id$
 */
public abstract class AopContext {
	
	/**
	 * Invocation associated with this thread. Will be null unless the
	 * exposeInvocation property on the controlling proxy has been set to true.
	 * The default value for this property is false, for performance reasons.
	 */
	private static ThreadLocal currentInvocation = new ThreadLocal();

	/**
	 * Internal method that the AOP framework uses to set the current
	 * AOP context if it is configured to expose call contexts.
	 * @param invocation the current AOP invocation context
	 */
	static void setCurrentInvocation(MethodInvocation invocation) {
		currentInvocation.set(invocation);
	}

	/**
	 * Try to return the current AOP invocation. This method
	 * is usable only if the calling method has been invoked
	 * via AOP, and the AOP framework has been set to
	 * expose invocations. Otherwise, this method will throw
	 * AspectException.
	 * @return MethodInvocation the current AOP invocation.
	 * Never returns null.
	 * @throws AspectException if the invocation cannot be found,
	 * because the method was invoked outside an AOP invocation
	 * context or because the AOP framework has not been configured
	 * to expose the invocation context.
	 */
	public static MethodInvocation currentInvocation() {
		if (currentInvocation == null || currentInvocation.get() == null)
			throw new AspectException("Cannot find invocation: set 'exposeInvocation' property on AopProxy to make it available");
		return (MethodInvocation) currentInvocation.get();
	}

}
