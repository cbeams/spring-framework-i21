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
 * <p>This class can be implemented by application developers,
 * although this is not necessary, as the default BeanNameUrlHandlerMapping
 * is available if no custom handlers are registered.
 *
 * <p>The ability to parameterize this mapping is a powerful and unusual
 * capability of this MVC framework. For example, it is possible to write
 * a custom mapping based on session state, cookie state or many other
 * variables. No other MVC framework seems to be equally flexible.
 *
 * <p>Note: Implementations can implement the Ordered interface to be able to
 * specify a sorting order and thus a priority for getting applied by
 * DispatcherServlet. Non-Ordered instances get treated as lowest priority.
 *
 * @author Rod Johnson
 * @see com.interface21.core.Ordered
 * @see com.interface21.web.servlet.handler.BeanNameUrlHandlerMapping
 * @see com.interface21.web.servlet.handler.SimpleUrlHandlerMapping
 */
public interface HandlerMapping extends ApplicationContextAware, LocaleResolverAware {
	
	/** 
	 * Will be invoked after setApplicationContext() and setLocaleResolver().
	 * Handler objects are normally taken from the BeanFactory.
	 * This means that implementing classes should never need to open
	 * configuration files for themselves.
	 * @throws ServletException if this handler mapping cannot initialize correctly
	 */
	void initHandlerMapping() throws ServletException;
	
	/**
	 * Return a handler for this request. The choice may be made on request URL,
	 * session state, or any factor the implementing class chooses.
	 * <p>This returns Object, rather than even a tag interface, so that handlers
	 * are not constrained in any way. For example, a HandlerAdaptercould be
	 * written to allow another framework's handler objects to be used.
	 * <p>Returns null if no match was found. This is not an error. The
	 * DispatcherServlet will query all registered HandlerMapping beans to find
	 * a match, and only decide there is an error if none can find a handler.
	 * @return a handler object, or null if none found
	 * @throws ServletException if there is an internal error
	 */
	Object getHandler(HttpServletRequest request) throws ServletException;

}

