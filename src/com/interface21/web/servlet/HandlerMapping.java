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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import com.interface21.context.ApplicationContextAware;

/**
 * Interface to be implemented by objects that define a mapping between
 * requests and handler objects.
 *
 * <p>This class can be implemented by application developers, although this
 * is not necessary, as BeanNameUrlHandlerMapping and SimpleUrlHandlerMapping
 * are included in the framework. The former is the default if no
 * HandlerMapping bean is registered in the application context.
 *
 * <p>HandlerMapping implementations can support mapped interceptors but do
 * not have to. A handler will always be wrapped in a HandlerExecutionChain
 * instance, optionally accompanied by some HandlerInterceptor instances.
 * The DispatcherServlet will first call each HandlerInterceptor's preHandle
 * method in the given order, finally invoking the handler itself if all
 * preHandle method have returned "true".
 *
 * <p>The ability to parameterize this mapping is a powerful and unusual
 * capability of this MVC framework. For example, it is possible to write
 * a custom mapping based on session state, cookie state or many other
 * variables. No other MVC framework seems to be equally flexible.
 *
 * <p>Note: Implementations can implement the Ordered interface to be able
 * to specify a sorting order and thus a priority for getting applied by
 * DispatcherServlet. Non-Ordered instances get treated as lowest priority.
 *
 * @author Rod Johnson
 * @see com.interface21.core.Ordered
 * @see com.interface21.web.servlet.handler.AbstractHandlerMapping
 * @see com.interface21.web.servlet.handler.BeanNameUrlHandlerMapping
 * @see com.interface21.web.servlet.handler.SimpleUrlHandlerMapping
 */
public interface HandlerMapping extends ApplicationContextAware {
	
	/**
	 * Return a handler and any interceptors for this request. The choice may be made
	 * on request URL, session state, or any factor the implementing class chooses.
	 * <p>This returns Object, rather than even a tag interface, so that handlers
	 * are not constrained in any way. For example, a HandlerAdapter could be
	 * written to allow another framework's handler objects to be used.
	 * <p>Returns null if no match was found. This is not an error. The
	 * DispatcherServlet will query all registered HandlerMapping beans to find
	 * a match, and only decide there is an error if none can find a handler.
	 * @param request current HTTP request
	 * @return a HandlerExecutionChain instance containing handler object and
	 * any interceptors, or null if no mapping found
	 * @throws ServletException if there is an internal error
	 */
	HandlerExecutionChain getHandler(HttpServletRequest request) throws ServletException;

}
