
package com.interface21.aop.framework;

import org.aopalliance.Invocation;

/**
 * @author rod.johnson
 */
public class AopUtils {
	
	/**
	 * Return arguments
	 */
	public static Object[] getArguments(Invocation invocation) {
		// TODO make portable
		return ((MethodInvocationImpl) invocation).getArguments();
	}

}
