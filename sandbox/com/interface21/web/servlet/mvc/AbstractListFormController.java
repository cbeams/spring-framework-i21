/*
 * Créé le 17 juil. 2003
 */
package com.interface21.web.servlet.mvc;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.interface21.validation.BindException;
import com.interface21.web.servlet.ModelAndView;
import com.interface21.web.servlet.mvc.AbstractFormController;
import com.interface21.web.util.WebUtils;

/**
 * Base abstract controller for CRUD operations in collaboration with a list view.
 * 
 * <br>For now, only session mode works due to the use of the handleInvalidSubmit method. 
 * The parent class should be modified a bit for working without Session.
 * 
 * This is just a brief description of the primarily process. Any parts are in practice done 
 * in the concrete class and are therefore customizable differently.
 * 
 * Due to the custom isFormSubmit and handleInvalidSubmit, when the Controller is 
 * accessed first (ie a GET call without parameter), the showList method will be called.
 * This method is intended for providing a view with a list of existing records.  
 * On the list view, calling the same URL (ie the Controller) with <code>_edit</code> 
 * parameter will call the showForm with an empty record. If adding an <code>id</code>
 * parameter the showForm will be pre-populated  with the existing record from the database.  
 * 
 * The formView is normally subitiing data with a submit button or image giving an additional 
 * parameter: _cancel, _insert, _update or _remove. The _edit parameter must be forgetten 
 * since the isFormSubmit method will go the wrong way.
 * 
 * Depending on booleans confirmXXX values, a showConfirm form will be called or directly 
 * processSubmit, both with the action parameter.
 * 
 * If a confirmation view is used, it will have button Parameter as _cancel and _confXXX.
 *  
 *  
 * @author Jean-Pierre Pawlak
 */
public abstract class AbstractListFormController extends AbstractFormController {

	public static final String PARAM_CANCEL = "_cancel";
	public static final String PARAM_INSERT = "_insert";
	public static final String PARAM_UPDATE = "_update";
	public static final String PARAM_REMOVE = "_remove";
	public static final String PARAM_CONFIRM_INSERT = "_confinsert";
	public static final String PARAM_CONFIRM_UPDATE = "_confupdate";
	public static final String PARAM_CONFIRM_REMOVE = "_confremove";
	public static final String PARAM_EDIT = "_edit";
	public static final String ERROR_DUPLICATE = "duplicateFormSubmission";

	protected static final int UNKNOWN = 0;
	protected static final int CANCEL = 1;
	protected static final int INSERT = 2;
	protected static final int UPDATE = 3;
	protected static final int REMOVE = 4;
	protected static final String[] ACTIONS = {"UNKNOWN","CANCEL","INSERT","UPDATE","REMOVE"};

	private boolean confirmInsert;
	private boolean confirmUpdate;
	private boolean confirmRemove;

	/**
	 * 
	 */
	public AbstractListFormController() {
		super();
	}

	// --- Final methods

	/**
	 * Return the name of the session attribute that holds
	 * the pagedlist object for this controller.
	 * @return the name of the form session attribute,
	 * or null if not in session form mode.
	 */
	protected final String getListSessionAttributeName() {
		return isSessionForm() ? getClass() + ".list." + getBeanName() : null;
	}

	/**
	 * @see com.interface21.web.servlet.mvc.AbstractFormController#processSubmit(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, com.interface21.validation.BindException)
	 */
	protected final ModelAndView processSubmit(
		HttpServletRequest request,
		HttpServletResponse response,
		Object command,
		BindException errors)
		throws ServletException, IOException {

		logger.info("processSubmit");
		logger.info("cancel ?");
		// cancel ?
		if (WebUtils.hasSubmitParameter(request, PARAM_CANCEL)) {
			logger.info("Cancelling form bean " + getBeanName());
			return processSubmit(request, response, command, errors, CANCEL);
		}
		
		logger.info("insert ?");
		// insert ?
		if (WebUtils.hasSubmitParameter(request, PARAM_CONFIRM_INSERT) 
			|| (!confirmInsert && WebUtils.hasSubmitParameter(request, PARAM_INSERT))) {
			logger.info("Inserting form bean " + getBeanName());
			return processSubmit(request, response, command, errors, INSERT);
		}
		if (WebUtils.hasSubmitParameter(request, PARAM_INSERT)) {
			logger.info("Confirming insert form bean " + getBeanName());
			// If we are here, the sessionForm is already set to true as confirmInsert is also true.
			request.getSession().setAttribute(getFormSessionAttributeName(), command);
			return showConfirm(request, response, command, errors, INSERT);
		}

		// update ?
		logger.info("update ?");
		if (WebUtils.hasSubmitParameter(request, PARAM_CONFIRM_UPDATE) 
			|| (!confirmUpdate && WebUtils.hasSubmitParameter(request, PARAM_UPDATE))) {
			logger.info("Updating form bean " + getBeanName());
			return processSubmit(request, response, command, errors, UPDATE);
		}
		if (WebUtils.hasSubmitParameter(request, PARAM_UPDATE)) {
			logger.info("Confirming update form bean " + getBeanName());
			// If we are here, the sessionForm is already set to true as confirmUpdate is also true.
			request.getSession().setAttribute(getFormSessionAttributeName(), command);
			return showConfirm(request, response, command, errors, UPDATE);
		}

		// remove ?
		logger.info("remove ?");
		if (WebUtils.hasSubmitParameter(request, PARAM_CONFIRM_REMOVE) 
			|| (!confirmRemove && WebUtils.hasSubmitParameter(request, PARAM_REMOVE))) {
			logger.info("Removing form bean " + getBeanName());
			return processSubmit(request, response, command, errors, REMOVE);
		}
		if (WebUtils.hasSubmitParameter(request, PARAM_REMOVE)) {
			logger.info("Confirming remove form bean " + getBeanName());
			// If we are here, the sessionForm is already set to true as confirmRemove is also true.
			request.getSession().setAttribute(getFormSessionAttributeName(), command);
			return showConfirm(request, response, command, errors, REMOVE);
		}

		// Unknown situation
		logger.info("unknown");
		logger.debug("Processing unknown form bean " + getBeanName());
		return processSubmit(request, response, command, errors, UNKNOWN);
	}

	/**
	 * @see com.interface21.web.servlet.mvc.AbstractFormController#handleInvalidSubmit(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected final ModelAndView handleInvalidSubmit(
		HttpServletRequest request,
		HttpServletResponse response)
		throws ServletException, IOException {

		logger.info("handleInvalidSubmit");
		String msg = null;
		if (WebUtils.hasSubmitParameter(request, PARAM_INSERT)
			|| WebUtils.hasSubmitParameter(request, PARAM_UPDATE)
			|| WebUtils.hasSubmitParameter(request, PARAM_REMOVE)
			|| WebUtils.hasSubmitParameter(request, PARAM_CONFIRM_INSERT)
			|| WebUtils.hasSubmitParameter(request, PARAM_CONFIRM_UPDATE)
			|| WebUtils.hasSubmitParameter(request, PARAM_CONFIRM_REMOVE)
		) {
			msg = ERROR_DUPLICATE;
		}
		return showList(request, response, msg);
	}

	// --- Not final and abstract methods

	abstract protected ModelAndView processSubmit(
		HttpServletRequest request,
		HttpServletResponse response,
		Object command,
		BindException errors,
		int action)
		throws ServletException, IOException;

	abstract protected ModelAndView showList(
		HttpServletRequest request, 
		HttpServletResponse response, 
		String message)
		throws ServletException, IOException;

	/**
	 * Show the confirmation page. Has to be  by subclasses 
	 * To have a confirmation page displayed, redefine this method in the subclassd
	 * and set the property confirmInsert, confirmUpdate or confirmRemove to true. 
	 * @param request
	 * @param response
	 * @param command
	 * @param errors
	 * @return the ModelAndView
	 * @throws ServletException
	 * @throws IOException
	 */
	protected ModelAndView showConfirm(
		HttpServletRequest request,
		HttpServletResponse response,
		Object command,
		BindException errors,
		int action)
		throws ServletException, IOException {
			
			// In a real confirm page, the session attribute is let in the session.
			if (this.isSessionForm()) {
				request.getSession().removeAttribute(getFormSessionAttributeName());
			}
			return processSubmit(request, response, command, errors, action);
	}

	/**
	 * @see com.interface21.web.servlet.mvc.AbstractFormController#isFormSubmission(javax.servlet.http.HttpServletRequest)
	 */
	protected boolean isFormSubmission(HttpServletRequest request) {

		boolean b = ! WebUtils.hasSubmitParameter(request, PARAM_EDIT);
		logger.info("isFormSubmission = " + b);
		return b;
	}

	// --- Accessors methods

	protected final boolean isConfirmInsert() {
		return confirmInsert;
	}

	protected final boolean isConfirmRemove() {
		return confirmRemove;
	}

	protected final boolean isConfirmUpdate() {
		return confirmUpdate;
	}

	public final void setConfirmInsert(boolean confirmInsert) {
		this.confirmInsert = confirmInsert;
		if (confirmInsert) setSessionForm(true);
	}

	public final void setConfirmRemove(boolean confirmRemove) {
		this.confirmRemove = confirmRemove;
		if (confirmRemove) setSessionForm(true);
	}

	public final void setConfirmUpdate(boolean confirmUpdate) {
		this.confirmUpdate = confirmUpdate;
		if (confirmUpdate) setSessionForm(true);
	}

}
