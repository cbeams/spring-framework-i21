/*
 * The Spring Framework is published under the terms
 * of the Apache Software License.
 */
 
package com.interface21.aop.framework;

import org.aopalliance.MethodInvocation;


/**
 * Mixin to provide stateful locking functionality.
 * Test/demonstration of AOP mixin support rather than a
 * useful interceptor in its own right.
 * 
 * @author Rod Johnson
 * @since 10-Jul-2003
 * @version $Id$
 */
public class LockMixin extends DelegatingIntroductionInterceptor implements Lockable {
	
	/** This field demonstrates additional state in the mixin */
	private boolean locked;
	
	public void lock() {
		this.locked = true;
	}
	
	public void unlock() {
		this.locked = false;
	}

	/**
	 * @see com.interface21.aop.framework.AopProxyTests.Lockable#locked()
	 */
	public boolean locked() {
		return this.locked;
	}

	/**
	 * Note that we need to override around advice.
	 * If the method is a setter and we're locked, prevent execution.
	 * Otherwise let super.invoke() handle it, and do normal
	 * Lockable(this) then target behaviour.
	 * @see org.aopalliance.MethodInterceptor#invoke(org.aopalliance.MethodInvocation)
	 */
	public Object invoke(MethodInvocation invocation) throws Throwable {
		if (locked() && invocation.getMethod().getName().indexOf("set") != -1)
			throw new LockedException();
		return super.invoke(invocation);
	}

}