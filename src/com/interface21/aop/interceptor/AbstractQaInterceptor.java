package com.interface21.aop.interceptor;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public abstract class AbstractQaInterceptor implements MethodInterceptor {

	/**
	 * @see Interceptor#invoke(Invocation)
	 */
	public Object invokeInternal(MethodInvocation invocation) throws Throwable {
		Object result = invocation.proceed();
		checkInvariants(invocation.getThis());
		return result;
	}
	
	protected abstract void checkInvariants(Object target);

}
