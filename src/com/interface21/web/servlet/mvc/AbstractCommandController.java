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

package com.interface21.web.servlet.mvc;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.interface21.validation.BindException;
import com.interface21.web.servlet.ModelAndView;

/**
 * Abstract base class for custom command controllers. Autopopulates a
 * command bean from the request. For command validation, a validator
 * (property inherited from BaseCommandController) can be used.
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see #setCommandClass
 * @see #setBeanName
 * @see #setValidator
 */
public abstract class AbstractCommandController extends BaseCommandController {

	/**
	 * Create a new AbstractCommandController.
	 */
	public AbstractCommandController() {
	}

	/**
	 * Create a new AbstractCommandController.
	 * @param commandClass class of the command bean
	 */
	public AbstractCommandController(Class commandClass) {
		setCommandClass(commandClass);
	}

	/**
	 * Create a new AbstractCommandController.
	 * @param commandClass class of the command bean
	 * @param beanName name of the command bean
	 */
	public AbstractCommandController(Class commandClass, String beanName) {
		setCommandClass(commandClass);
		setBeanName(beanName);
	}
	
	protected final ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Object command = userObject(request);
		BindException errors = bindAndValidate(request, command);
		return handle(request, response, command, errors);
	}

	/**
	 * Template method for request handling, providing a populated and validated instance
	 * of the command class, and an Errors object containing binding and validation errors.
	 * <p>Can call errors.getModel() to populate the ModelAndView model with the command
	 * and the errors instance, under the specified bean name.
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @param command the populated command object
	 * @param errors the binder containing errors
	 * @return a ModelAndView to render, or null if handled directly
	 */
	protected abstract ModelAndView handle(HttpServletRequest request, HttpServletResponse response,
	                                       Object command, BindException errors)
	    throws ServletException, IOException;

}
