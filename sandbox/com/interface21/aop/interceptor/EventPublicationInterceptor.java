
package com.interface21.aop.interceptor;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;


/**
 * Should really move to context package. AOP
 * shouldn't depend on context...
 */
public class EventPublicationInterceptor implements MethodInterceptor {

	/**
	 * @see Interceptor#invoke(Invocation)
	 */
	public Object invoke(MethodInvocation invocation) throws Throwable {
		return null;
	}

}
