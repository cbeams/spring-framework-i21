package com.interface21.web.servlet.mvc;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.interface21.validation.BindException;
import com.interface21.validation.Errors;
import com.interface21.web.servlet.ModelAndView;

/**
 * Concrete FormController implementation that provides configurable
 * form and success views, and an onSubmit chain for convenient overriding.
 *
 * <p>Automatically resubmits to the form view in case of validation errors,
 * and renders the success view in case of a valid submission.
 *
 * <p>The submit behavior can be customized by overriding one of the onSubmit
 * methods. Submit actions can be used as custom validation if necessary
 * (e.g. login), by calling showForm in case of validation errors.
 *
 * @author Juergen Hoeller
 * @since 05.05.2003
 */
public class SimpleFormController extends AbstractFormController {

	private String formView;

	private String successView;

	/**
	 * Create a new SimpleFormController.
	 * <p>Subclasses should set the following properties, either in the
	 * constructor or via a BeanFactory: commandClass, beanName,
	 * sessionForm, formView, successView.
	 * Note that commandClass doesn't need to be set when overriding
	 * formBackingObject, as this determines the class anyway.
	 * @see #setCommandClass
	 * @see #setBeanName
	 * @see #setSessionForm
	 * @see #setFormView
	 * @see #setSuccessView
	 */
	public SimpleFormController() {
		super();
	}

	/**
	 * Set the name of the view that should be used for form display.
	 */
	public final void setFormView(String formView) {
		this.formView = formView;
	}

	/**
	 * Return the name of the view that should be used for form display.
	 */
	protected final String getFormView() {
		return this.formView;
	}

	/**
	 * Set the name of the view that should be shown on successful submit.
	 */
	public final void setSuccessView(String successView) {
		this.successView = successView;
	}

	/**
	 * Return the name of the view that should be shown on successful submit.
	 */
	protected final String getSuccessView() {
		return this.successView;
	}

	/**
	 * Create a reference data map for the given request, command, and errors,
	 * consisting of bean name/bean instance pairs as expected by ModelAndView.
	 * <p>Default implementation delegates to referenceData(request).
	 * Subclasses can override this to set reference data used in the view.
	 * @param request current HTTP request
	 * @param command form object with request parameters bound onto it
	 * @param errors binder containing current errors, if any
	 * @return a Map with reference data entries, or null if none
	 * @throws ServletException in case of invalid state or arguments
	 * @see ModelAndView
	 */
	protected Map referenceData(HttpServletRequest request, Object command, Errors errors)
	    throws ServletException {
		return referenceData(request);
	}

	/**
	 * Create a reference data map for the given request.
	 * Called by referenceData version with all parameters.
	 * <p>Default implementation returns null.
	 * Subclasses can override this to set reference data used in the view.
	 * @param request current HTTP request
	 * @return a Map with reference data entries, or null if none
	 * @throws ServletException in case of invalid state or arguments
	 * @see ModelAndView
	 */
	protected Map referenceData(HttpServletRequest request) throws ServletException {
		return null;
	}

	/**
	 * This implementation shows the configured form view.
	 * Can be called within onSubmit implementations, to redirect back to the form
	 * in case of custom validation errors (i.e. not determined by the validator).
	 * @see #setFormView
	 */
	protected final ModelAndView showForm(HttpServletRequest request, HttpServletResponse response,
	                                      BindException errors) throws ServletException {
		return showForm(request, errors, getFormView());
	}

	/**
	 * This implementation calls showForm in case of errors,
	 * and delegates to onSubmit's full version else.
	 * <p>This should only be overridden to check for an action that should
	 * be executed without respect to binding errors, like a cancel action.
	 * @see #showForm
	 * @see #onSubmit(HttpServletRequest,HttpServletResponse,Object,BindException)
	 */
	protected ModelAndView processSubmit(HttpServletRequest request, HttpServletResponse response,
	                                     Object command, BindException errors)
	    throws ServletException, IOException {
		if (errors.hasErrors()) {
			logger.debug("Data binding errors: " + errors.getErrorCount());
			return showForm(request, response, errors);
		}
		else {
			logger.debug("No errors -> processing submit");
			return onSubmit(request, response, command, errors);
		}
	}

	/**
	 * Submit callback with all parameters. Called in case of submit without errors
	 * reported by the registered validator resp. on every submit if no validator.
	 * <p>Default implementation delegates to onSubmit(command,errors).
	 * Subclasses can override this to provide custom submission handling
	 * like triggering a custom action. They can also provide custom validation
	 * and call showForm/super.onSubmit accordingly.
	 * @param request current servlet request
	 * @param response current servlet response
	 * @param command form object with request parameters bound onto it
	 * @param errors binder without errors (subclass can add errors if it wants to)
	 * @return the prepared model and view, or null
	 * @throws ServletException in case of invalid state or arguments
	 * @throws IOException in case of I/O errors
	 * @see #onSubmit(Object)
	 * @see #showForm
	 */
	protected ModelAndView onSubmit(HttpServletRequest request,	HttpServletResponse response,
																	Object command,	BindException errors)
			throws ServletException, IOException {
		return onSubmit(command);
	}

	/**
	 * Simplest onSubmit version. Called by the default implementation of
	 * the onSubmit version with all parameters.
	 * <p>Default implementation prepares success view.
	 * @param command form object with request parameters bound onto it
	 * @return the prepared model and view, or null
	 * @throws ServletException in case of invalid state or arguments
	 * @see #setSuccessView
	 */
	protected ModelAndView onSubmit(Object command) throws ServletException {
		if (getSuccessView() == null)
			throw new ServletException("successView isn't set");
		return new ModelAndView(getSuccessView(), getBeanName(), command);
	}

}
