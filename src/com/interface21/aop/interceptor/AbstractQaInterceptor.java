package com.interface21.aop.interceptor;

import org.aopalliance.MethodInterceptor;
import org.aopalliance.MethodInvocation;

public abstract class AbstractQaInterceptor implements MethodInterceptor {

	/**
	 * @see Interceptor#invoke(Invocation)
	 */
	public Object invokeInternal(MethodInvocation invocation) throws Throwable {
		Object result = invocation.invokeNext();
		checkInvariants(invocation.getInvokedObject());
		return result;
	}
	
	protected abstract void checkInvariants(Object target);

}
