/*
 * The Spring Framework is published under the terms
 * of the Apache Software License.
 */
 
package com.interface21.aop.framework;

import org.aopalliance.intercept.Interceptor;

/**
 * Interface to be implemented by interceptors that have
 * a proxy target.
 * @author Rod Johnson
 * @since 14-Mar-2003
 * @version $Revision$
 */
public interface ProxyInterceptor extends Interceptor {
	
	Object getTarget();

}
