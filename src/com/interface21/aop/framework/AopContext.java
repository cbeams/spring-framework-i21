/*
 * The Spring Framework is published under the terms
 * of the Apache Software License.
 */
 
package com.interface21.aop.framework;

import org.aopalliance.AspectException;
import org.aopalliance.MethodInvocation;

/**
 * 
 * @author Rod Johnson
 * @since 13-Mar-2003
 * @version $Revision$
 */
public abstract class AopContext {
	
	/**
	 * Invocation associated with this thread. Will be null unless the
	 * exposeInvocation property on the controlling proxy has been set to true.
	 * The default value for this property is false, for performance reasons.
	 */
	private static ThreadLocal currentInvocation = new ThreadLocal();

	static void setCurrentInvocation(MethodInvocation invocation) {
		currentInvocation.set(invocation);
	}

	public static MethodInvocation currentInvocation() {
		if (currentInvocation == null || currentInvocation.get() == null)
			throw new AspectException("Cannot find invocation: set 'exposeInvocation' property on AopProxy to make it available");
		return (MethodInvocation) currentInvocation.get();
	}

}
