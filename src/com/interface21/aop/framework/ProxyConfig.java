/*
 * The Spring Framework is published under the terms
 * of the Apache Software License.
 */
 
package com.interface21.aop.framework;

import org.aopalliance.AttributeRegistry;
import org.aopalliance.Interceptor;

/**
 * 
 * @author Rod Johnson
 * @since 13-Mar-2003
 * @version $Revision$
 */
public interface ProxyConfig {
	
	boolean getExposeInvocation();
	
	AttributeRegistry getAttributeRegistry();
	
	Interceptor[] getInterceptors();
	
	Class[] getProxiedInterfaces();
	
	/**
	 * 
	 * Add to tail
	 * @param interceptor
	 */
	void addInterceptor(Interceptor interceptor);
	
	/**
	 * 
	 * @param pos index from 0 (head).
	 * @param interceptor
	 */
	void addInterceptor(int pos, Interceptor interceptor);
	
	/**
	 * Remove the interceptor
	 * @param interceptor
	 * @return if the interceptor was found and removed
	 */
	boolean removeInterceptor(Interceptor interceptor);
	
	/**
	 * Can return null if now target. Returns true if we have
	 * a target interceptor. A target interceptor must be the last
	 * interceptor. Implementations should be efficient, as this
	 * will be invoked on each invocation.
	 * @return Object
	 */
	Object getTarget();
	
	// from factory?
	// getPointcuts();

}
