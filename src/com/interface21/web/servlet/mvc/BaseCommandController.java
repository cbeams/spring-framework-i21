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

import com.interface21.validation.ValidationUtils;
import com.interface21.validation.Validator;
import com.interface21.validation.BindException;
import com.interface21.web.bind.ServletRequestDataBinder;

/**
 * <p>Controller implementation creating a certain object (the command object)
 * on receipt of request and attempt to populate this object with request
 * parameters.</p>
 * 
 * <p>This controller is the base for all controller wishing to populate
 * JavaBeans based on request parameters, validate the content of such
 * JavaBeans using {@link com.interface21.validation.Validator Validators}
 * and use custom editors (in the form of
 * {@link java.beans.PropertyEditor PropertyEditors}) to transform for instance
 * object into strings and vice versa. Three notion are mentioned here:</p>
 *
 * <p><b>Command class:</b><br>
 * The command class is the object that will be created filled using the request
 * parameters. What the actual command class is, is customizable in many ways,
 * through extending controllers or overriding methods. Command classes should
 * preferrable be JavaBeans in order to be able to fill instance of the classes
 * using the request parameters.</p>
 *
 * <p><b>Populating using request parameters and PropertyEditors:</b><br>
 * Upon receiving a request, any BaseCommandController will attempt to fill the
 * command class using the request parameters. This is done using the typical
 * and wellknown JavaBeans property notation. When a request parameter named
 * <code>'firstName'</code> exists, the framework will attempt to call the
 * <code>setFirstName([value])</code> passing the value of the parameter. Nested properties
 * as of course supported. For instance a parameter named <code>'address.city'</code>
 * will result in a <code>getAddress().setCity([value])</code> call on the
 * command class.</p>
 *
 * <p>Important to know here is that you are not limited to String arguments in
 * your JavaBeans. Using the PropertyEditor-notion as supplied by the
 * java.beans package, you will be able to transform Strings to Objects and
 * the other way around. For instance <code>setLocale(Locale loc)</code> is
 * perfectly possible for a request parameter named <code>locale</code> having
 * a value of <code>en</code>, as long as you register the appropriate
 * PropertyEditor in the Controller (see
 * {@link #initBinder(javax.servlet.http.HttpServletRequest,
 * com.interface21.web.bind.ServletRequestDataBinder) initBinder()}
 * for more information on that matter.</p>
 *
 * <p><b>Validators:</b>
 * After the controller has successfully filled the command object with
 * the parameters from the request, it will attempt use any configured validators
 * to validate the object. Validation results will be put in a
 * {@link com.interface21.validation.Errors Errors} object which can be used
 * in for instance a View to render any input problems.</p>
 *
 * <p><b><a name="workflow">Workflow
 * (<a href="AbstractController.html#workflow">and that defined by superclass</a>):</b><br>
 * Since this class is an abstract base class for more specific implementation,
 * it does not override the handleRequestInternal() method and also has no
 * actual workflow. Implementing classes like
 * {@link AbstractFormController AbstractFormController},
 * {@link AbstractCommandController AbstractcommandController},
 * {@link SimpleFormController SimpleFormController} and
 * {@link AbstractWizardFormController AbstractWizardFormController}
 * provide actual functionality and workflow.
 * More information on workflow performed by superclasses can be found
 * <a href="AbstractController.html#workflow">here</a>.</p>
 *
 * <p><b><a name="config">Exposed configuration properties</a>
 * (<a href="AbstractController.html#config">and those defined by superclass</a>):</b><br>
 * <table border="1">
 *  <tr>
 *      <td><b>name</b></th>
 *      <td><b>default</b></td>
 *      <td><b>description</b></td>
 *  </tr>
 *  <tr>
 *      <td>beanName</td>
 *      <td>command</td>
 *      <td>the name to use when binding the instantiated command class
 *          to the request</td>
 *  </tr>
 *  <tr>
 *      <td>commandClass</td>
 *      <td><i>null</i></td>
 *      <td>the class to use upon receiving a request and which to fill
 *          using the request parameters. What object is used and
 *          whether or not it should be created is defined by extending classes
 *          and their configuration properties and methods</td>
 *  </tr>
 *  <tr>
 *      <td>validator</td>
 *      <td><i>null</i></td>
 *      <td>Validator bean (usually passed in using a &lt;ref bean="beanId"/&gt;
 *          property. The validator will be called somewhere in the workflow
 *          of implementing classes (have a look at those for more info) to
 *          validate the command object</td>
 *  </tr>
 *  <tr>
 *      <td>validateOnBinding</td>
 *      <td>true</td>
 *      <td>Indicates whether or not to validate the command object
 *          after the object has been filled using the request parameters</td>
 *  </tr>
 * </table>
 * </p>
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 */
public abstract class BaseCommandController extends AbstractController {

	public static final String DEFAULT_BEAN_NAME = "command";

	private String beanName = DEFAULT_BEAN_NAME;

	private Class commandClass;

	private Validator validator;

	private boolean validateOnBinding = true;

	/**
	 * Set the bean name of the command.
	 * The command instance will be included in the model under this name.
	 */
	public final void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	/**
	 * Return the bean name of the command.
	 */
	protected final String getBeanName() {
		return this.beanName;
	}

	/**
	 * Set the command class for this controller.
	 * An instance of this class gets populated and validated on each request.
	 */
	public final void setCommandClass(Class commandClass) {
		checkValidator(this.validator, commandClass);
		this.commandClass = commandClass;
	}

	/**
	 * Return the command class for this controller.
	 */
	protected final Class getCommandClass() {
		return this.commandClass;
	}

	/**
	 * Set the validator for this controller (can also be null).
	 * The validator must support the specified command class.
	 */
	public final void setValidator(Validator validator) {
		checkValidator(validator, this.commandClass);
		this.validator = validator;
	}

	/**
	 * Return the validator for this controller.
	 */
	protected final Validator getValidator() {
		return validator;
	}

	/**
	 * Set if the validator should get applied when binding.
	 */
	public final void setValidateOnBinding(boolean validateOnBinding) {
		this.validateOnBinding = validateOnBinding;
	}

	/**
	 * Return if the validator should get applied when binding.
	 */
	protected final boolean isValidateOnBinding() {
		return validateOnBinding;
	}

	/**
	 * Check if the given validator and command class match.
	 * @param validator validator instance
	 * @param commandClass command class
	 */
	private void checkValidator(Validator validator, Class commandClass) throws IllegalArgumentException {
		if (validator != null && commandClass != null && !validator.supports(commandClass))
			throw new IllegalArgumentException(
				"Validator [" + validator + "] can't validate command class of type " + commandClass);
	}

	/**
	 * Check if the given command object is a valid for this controller,
	 * i.e. its command class.
	 * @param command command object to check
	 * @return if the command object is valid for this controller
	 */
	protected final boolean checkCommand(Object command) {
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
	 * @param request current HTTP request
	 * @param command command to bind onto
	 * @return the ServletRequestDataBinder instance for additional custom validation
	 * @throws ServletException in case of invalid state or arguments
	 */
	protected final ServletRequestDataBinder bindAndValidate(HttpServletRequest request, Object command) throws ServletException {
		ServletRequestDataBinder binder = createBinder(request, command);
		binder.bind(request);
		if (isValidateOnBinding()) {
			ValidationUtils.invokeValidator(getValidator(), command, binder);
		}
		onBindAndValidate(request, command, binder);
		return binder;
	}

	/**
	 * Create a new binder instance for the given command and request.
	 * @param command command to bind onto
	 * @param request current request
	 * @return the new binder instance
	 * @throws ServletException in case of invalid state or arguments
	 * @see #bindAndValidate
	 */
	protected ServletRequestDataBinder createBinder(HttpServletRequest request, Object command)
			throws ServletException {
		ServletRequestDataBinder binder = new ServletRequestDataBinder(command, getBeanName());
		initBinder(request, binder);
		return binder;
	}

	/**
	 * Initialize the given binder instance, e.g. with custom editors.
	 * Called by createBinder. This method allows you to register custom
     * editors for certain fields of your command class. For instance, you will
     * be able to transform Date objects into a String pattern and back, in order
     * to allow your JavaBeans to have Date properties and still be able to
     * set and display them in for instance an HTML interface.
	 * @param request current request
	 * @param binder new binder instance
	 * @throws ServletException in case of invalid state or arguments
	 * @see #createBinder
     * @see com.interface21.web.bind.ServletRequestDataBinder
	 */
	protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder)
			throws ServletException {
	}

	/**
	 * Callback for custom post-processing in terms of binding and validation.
	 * Called on each submit, after standard binding and validation,
	 * and before error evaluation.
	 * @param request current HTTP request
	 * @param command bound command
	 * @param errors binder for additional custom validation
	 * @throws ServletException in case of invalid state or arguments
	 * @see #bindAndValidate
	 */
	protected void onBindAndValidate(HttpServletRequest request, Object command, BindException errors)
	    throws ServletException {
	}

}
