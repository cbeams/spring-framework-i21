/*
 * Generic framework code included with 
 * <a href="http://www.amazon.com/exec/obidos/tg/detail/-/1861007841/">Expert One-On-One J2EE Design and Development</a>
 * by Rod Johnson (Wrox, 2002). 
 * This code is free to use and modify. However, please
 * acknowledge the source and include the above URL in each
 * class using or derived from this code. 
 * Please contact <a href="mailto:rod.johnson@interface21.com">rod.johnson@interface21.com</a>
 * for commercial support.
 */

package com.interface21.web.servlet.mvc.multiaction;

import javax.servlet.http.HttpServletRequest;

/**
 * Interface that parameterizes the MultiActionController class
 * using the <b>Strategy</b> GoF Design pattern, allowing
 * the mapping from incoming request to handler method name
 * to be varied without affecting other application code.
 * <br>Illustrates how delegation can be more flexible than
 * subclassing.
 * @author Rod Johnson
 */
public interface MethodNameResolver {
	
	/**
	 * Return a method name that can handle this request. Such
	 * mappings are typically, but not necessarily, based on URL.
	 * @return a method name that can handle this request.
	 * Never returns null; throws exception
	 * @throws NoSuchRequestHandlingMethodException if no method
	 * can be found for this URL
	 */
	String getHandlerMethodName(HttpServletRequest request) throws NoSuchRequestHandlingMethodException;
}