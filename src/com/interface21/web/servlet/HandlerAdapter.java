package com.interface21.web.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.interface21.context.ApplicationContextAware;

/**
 * MVC framework SPI interface, allowing parameterization of core MVC workflow.
 *
 * <p>Interface that must be implemented for each handler type to handle a request.
 * This interface is used to allow the DispatcherServlet to be indefinitely
 * extensible. The DispatcherServlet accesses all installed handlers through this
 * interface, meaning that it does not contain code specific to any handler type.
 *
 * <p>Note that a handler can be of type Object. This is to enable handlers from
 * other frameworks to be integrated with this framework without custom coding.
 *
 * <p>This interface is not intended for application developers. It is available
 * to handlers who want to develop their own web workflow.
 *
 * <p>Note: Implementations can implement the Ordered interface to be able to
 * specify a sorting order and thus a priority for getting applied by
 * DispatcherServlet. Non-Ordered instances get treated as lowest priority.
 *
 * @author Rod Johnson
 */
public interface HandlerAdapter extends ApplicationContextAware {
	
	/**
	 * Given a handler instance, return whether or not this HandlerAdapter can
	 * support it. Usually HandlerAdapters will base the decision on the handler
	 * type. HandlerAdapters will normally support only one handler type.
	 * <p>A typical implementation:
	 * <code>
	 * return handler != null && MyHandler.class.isAssignableFrom(handler.getClass());
	 * </code>
	 * @param handler handler object to check
	 * @return whether or not this object can use the given handler
	 */
	boolean supports(Object handler); 
	
	/**
	 * Use the given handler to handle this request.
	 * The workflow that is required may vary widely.
	 * Can also perform authorization checks.
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @param handler handler to use. This object must have previously been passed
	 * to the supports() method of this interface, which must have returned true.
	 * Implementations that generate output themselves (and return null
	 * from this method) may encounter IOExceptions.
	 * @throws ServletException if there is a general error
	 * @throws IOException in case of I/O errors
	 * @return ModelAndView object with the name of the view and the required
	 * model data, or null if the request has been handled directly.
	 */
	ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler)
	    throws ServletException, IOException;

	/**
	 * Same contract as for HttpServlet.getLastModified.
	 * Can simply return -1 if there's no support in the handler class.
	 * @param request current HTTP request
	 * @param handler handler to use
	 * @return the lastModified value for the given handler
	 * @see javax.servlet.http.HttpServlet#getLastModified
	 */
	long getLastModified(HttpServletRequest request, Object handler);

}
