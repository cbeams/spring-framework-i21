/**
 * Generic framework code included with 
 * <a href="http://www.amazon.com/exec/obidos/tg/detail/-/1861007841/">Expert One-On-One J2EE Design and Development</a>
 * by Rod Johnson (Wrox, 2002). 
 * This code is free to use and modify. However, please
 * acknowledge the source and include the above URL in each
 * class using or derived from this code. 
 * Please contact <a href="mailto:rod.johnson@interface21.com">rod.johnson@interface21.com</a>
 * for commercial support.
 */

package com.interface21.web.servlet;

import javax.servlet.http.HttpServletRequest;


/**
 * Support last modified HTTP requests to facilitate content caching.
 * Same contract as for Servlet API getLastModified() method.
 * Any resource within our MVC framework can implement this.
 */
public interface LastModified {
	
	/**
	 * Same contract as for Servlet.getLastModified
	 * Invoked <b>before</b> request processing.
	 * @return the time the underlying resource was last modified.
	 * @param request HTTP request
	 */
	long getLastModified(HttpServletRequest request);
	

}
