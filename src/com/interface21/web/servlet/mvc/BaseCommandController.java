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

import com.interface21.validation.DataBinder;
import com.interface21.validation.Validator;
import com.interface21.web.bind.HttpServletRequestDataBinder;

/**
 * Controller implementation that creates a Command object
 * on receipt of requests and attempts to populate the command's
 * JavaBean properties with request attribtes. 
 * Once created, commands can be validated using a Validator
 * associated with this class.
 * Type mismatches populating a command are treated as validation
 * errors, but caught by the framework, not application code.
 * @author Rod Johnson 
 */
public abstract class BaseCommandController extends AbstractController {

	//-------------------------------------------------------------------------
	// Instance data
	//-------------------------------------------------------------------------
	private Class commandClass;

	private Validator validator;
	
	private String beanName;


	//-------------------------------------------------------------------------
	// Constructors
	//-------------------------------------------------------------------------	
	public BaseCommandController() {
	}
	
	protected void setCommandClass(Class commandClass) {
		this.commandClass = commandClass;
	}

	
	//-------------------------------------------------------------------------
	// JavaBean properties
	//-------------------------------------------------------------------------
	public final void setCommandClassName(String name) throws ClassNotFoundException {
		this.commandClass = Class.forName(name);
	}
	
	public final void setBeanName(String beanName) {
		this.beanName = beanName;
	}
	
	protected final String getBeanName() {
		return this.beanName;
	}
	

	public final void setValidator(Validator validator) throws IllegalArgumentException {
		if (!validator.supports(this.commandClass))
			throw new IllegalArgumentException(
				"Validator [" + validator + "] can't validate command class of type " + commandClass);
		this.validator = validator;
	}

	
	//-------------------------------------------------------------------------
	// Implementation methods
	//-------------------------------------------------------------------------
	/**
	 * Subclasses can override this
	 * @return object to bind onto
	 */
	protected Object userObject(HttpServletRequest request) throws ServletException {
		logger.info("Must create new command of " + commandClass);
		try {
			Object command = commandClass.newInstance();
			return command;
		}
		catch (Exception ex) {
			throw new ServletException(
				"Cannot instantiate command " + commandClass + "; does it have a public no arg constructor?",
				ex);
		}
	}
	
	
	// *TODO: must be able to parameterize the binding process.
	// without depending on DataBinder
	
	protected final DataBinder bindAndValidate(HttpServletRequest request, Object command) throws ServletException {
		HttpServletRequestDataBinder binder = new HttpServletRequestDataBinder(command, this.beanName);
		binder.bind(request);

		
		// TODO: do we still want to invoke validator!????
		// it would need to check if there already was an error for each field

		if (this.validator != null) {
			logger.info("Invoking validator [" + this.validator + "]");
			validator.validate(command, binder);
			if (binder.hasErrors()) {
				logger.info("Validator found " + binder.getErrorCount() + " errors: going to resubmit form");
					
			}
			else {
				logger.debug("Validator found no errors");
			}
		}
		
		// May throw exception
		// DO WE WANT THIS? we may still want to invoke validator...
		//binder.close();
		
		return binder;		
	}

}