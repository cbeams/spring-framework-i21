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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import com.interface21.validation.Validator;
import com.interface21.web.bind.BindUtils;
import com.interface21.web.bind.ServletRequestDataBinder;

/**
 * Controller implementation that creates a Command object
 * on receipt of requests and attempts to populate the command's
 * JavaBean properties with request attributes.
 * Once created, commands can be validated using a Validator
 * associated with this class.
 * Type mismatches populating a command are treated as validation
 * errors, but caught by the framework, not application code.
 *
 * <p>Note: This class is the base class for both FormController and
 * AbstractCommandController (which in turn is the base class for
 * custom controller implementations).
 *
 * @author Rod Johnson, Juergen Hoeller
 */
public abstract class BaseCommandController extends AbstractController {

	public static final String DEFAULT_BEAN_NAME = "command";

	//-------------------------------------------------------------------------
	// Instance data
	//-------------------------------------------------------------------------
	private Class commandClass;

	private Validator validator;
	
	private String beanName = DEFAULT_BEAN_NAME;

	//-------------------------------------------------------------------------
	// Constructors
	//-------------------------------------------------------------------------	
	public BaseCommandController() {
	}

	protected void setCommandClass(Class commandClass) {
		checkValidator(this.validator, commandClass);
		this.commandClass = commandClass;
	}

	protected Class getCommandClass() {
		return this.commandClass;
	}

	//-------------------------------------------------------------------------
	// JavaBean properties
	//-------------------------------------------------------------------------
	public final void setCommandClassName(String name) throws ClassNotFoundException {
		this.commandClass = Class.forName(name);
	}
	
	protected String getCommandClassName() {
		return (this.commandClass != null ? this.commandClass.getName() : null);
	}

	public final void setBeanName(String beanName) {
		this.beanName = beanName;
	}
	
	protected final String getBeanName() {
		return this.beanName;
	}

	public final void setValidator(Validator validator) {
		checkValidator(validator, this.commandClass);
		this.validator = validator;
	}

	//-------------------------------------------------------------------------
	// Implementation methods
	//-------------------------------------------------------------------------

	private void checkValidator(Validator validator, Class commandClass) {
		if (validator != null && commandClass != null && !validator.supports(commandClass))
			throw new IllegalArgumentException(
				"Validator [" + validator + "] can't validate command class of type " + commandClass);
	}

	protected boolean checkCommand(Object command) {
		return (this.commandClass == null || this.commandClass.isInstance(command));
	}

	/**
	 * Create a new command instance for the command class of this controller.
	 * @return the new command instance
	 * @throws ServletException in case of instantiation errors
	 */
	protected final Object createCommand() throws ServletException {
		logger.info("Must create new command of " + commandClass);
		try {
			return commandClass.newInstance();
		}	catch (InstantiationException ex) {
			throw new ServletException(
				"Cannot instantiate command " + commandClass + "; does it have a public no arg constructor?",
				ex);
		} catch (IllegalAccessException ex) {
			throw new ServletException(
				"Cannot instantiate command " + commandClass + "; cannot access constructor",
				ex);
		}
	}

	/**
	 * Retrieve a user's command object for the given request.
	 * <p>Default implementation calls createCommand. Subclasses can override this.
	 * @param request current HTTP request
	 * @return object command to bind onto
	 * @see #createCommand
	 */
	protected Object userObject(HttpServletRequest request) throws ServletException {
		return createCommand();
	}

	/**
	 * Bind the parameters of the given request to the given command object.
	 * @param request tcurrent HTTP reqzest
	 * @param command command to bind onto
	 * @return ServletRequestDataBinder, for additional custom validation
	 * @throws ServletException
	 */
	protected final ServletRequestDataBinder bindAndValidate(HttpServletRequest request, Object command) throws ServletException {
		return BindUtils.bindAndValidate(request, command, this.beanName, this.validator);
	}

}
