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
 * This interface is used to allow the ControllerServlet to be indefinitely
 * extensible. The ControllerServlet accesses all installed handlers through this
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
 * ControllerServlet. Non-Ordered instances get treated as lowest priority.
 *
 * @author Rod Johnson
 */
public interface HandlerAdapter extends ApplicationContextAware {
	
	/**
	 * Given a handler instance, return whether or not this* HandlerAdapter can
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
	 * Same contract as for Servlet.getLastModified.
	 * Can simply return -1 if there's no support in the delegate class.
	 */
	long getLastModified(HttpServletRequest request, Object delegate);
	
	/**
	 * Use the given handler to handle this request.
	 * The workflow that is required may vary widely.
	 * @param delegate handler to use. This object must have previously been passed
	 * to the supports() method of this interface, which must have returned true.
	 * Implementations that generate output themselves (and return null
	 * from this method) may encounter IOExceptions.
	 * @throws IOException in case of I/O errors
	 * @throws ServletException if there is an error
	 * @return ModelAndView object with the name of the view and the required
	 * model data, or null if the request has been handled directly.
	 */
	ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object delegate)
	    throws IOException, ServletException;

}
