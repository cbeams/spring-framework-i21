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

import com.interface21.validation.Errors;
import com.interface21.web.servlet.ModelAndView;

/**
 * Controller 
 */
public abstract class AbstractCommandController extends BaseCommandController {

	/**
	 * Constructor for AbstractCommandController.
	 * @param commandClass
	 */
	public AbstractCommandController(Class commandClass, String beanName) {
		setCommandClass(commandClass);
		setBeanName(beanName);
	}
	
	public AbstractCommandController(Class commandClass) {
		setCommandClass(commandClass);
	}

	/**
	 * Constructor for AbstractCommandController.
	 */
	public AbstractCommandController() {
	}

	/**
	 * @see AbstractController#handleRequestInternal(HttpServletRequest, HttpServletResponse)
	 */
	protected final ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		Object command = userObject(request);
		Errors errors = bindAndValidate(request, command);
		return handle(request, response, command, errors);
		
		// PUBLISH EVENT

	}

	/**
	 * Subclasses must check for errors
	 */
	protected abstract ModelAndView handle(
		HttpServletRequest request,
		HttpServletResponse response,
		Object command,
		Errors errors);

}