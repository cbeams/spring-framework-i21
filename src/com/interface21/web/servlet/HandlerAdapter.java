package com.interface21.web.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.interface21.context.ApplicationContextAware;


/**
 * <b>MVC framework SPI interface, allowing parameterization of
 * core MVC workflow.</b>
 * Interface that must be implemented for each handler type to
 * use the handler to handle a request.
 * This interface is used to allow the ControllerServlet to be indefinitely
 * extensible. The ControllerServlet accesses all installed handlers
 * through this interface, meaning that it does not contain code specific
 * to any handler type.
 * <br/>Note that a handler can be of type Object. This is
 * to enable handlers from other frameworks to be integrated
 * with this framework without custom coding.
 * <br/>This interface is not intended for application developers.</b>
 * It is available to handlers who want to develop their own web workflow.
 * Need one of these for each fancy handler type.
 *
 * <p>Note: Implementations can implement the Ordered interface to be
 * able to specify a sorting order and thus a priority for getting
 * applied by ControllerServlet. Non-Ordered instances get treated as
 * lowest priority.
 *
 * @author Rod Johnson
 */
public interface HandlerAdapter extends ApplicationContextAware {
	
	/** Given a handler instance, return whether or not this
	 * HandlerAdapter can support it. Usually HandlerAdapters will
	 * base the decision on the handler type. HandlerAdapters
	 * will normally support only one handler type.
	 * <br/>A typical implementation:
	 * <code>
	 * return handler != null && RequestHandler.class.isAssignableFrom(handler.getClass());
	 * </code>
	 * @param handler handler object to check
	 * @return whether or not this object can use the given handler
	 */
	boolean supports(Object handler); 
	
	/**
	 * Same contract as for Servlet.getLastModified
	 * Can simply return -1 if there's no support in the 
	 * delegate class
	 */
	long getLastModified(HttpServletRequest request, Object delegate);
	
	/**
	 * Use the given handler to handle this request.
	 * The workflow that is required may vary widely.
	 * @param delegate handler to use. This object must have
	 * previously been passed to the supports() method of this interface,
	 * which must have returned true.
	 * @throws IOException this exception will not normally be thrown.
	 * Implementations that generate output themselves (and return null
	 * from this method) may encounter and need not catch IOExceptions.
	 * @throws ServletException if there is an error
	 * @return ModelAndView object with the name of the view and the required
	 * model data. Return null if the request was been handled directly.
	 * DO WE WANT TO ALLOW THIS
	 */
	ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object delegate) throws IOException, ServletException;

}	// interface HandlerAdapter
