package com.interface21.web.servlet.mvc;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.interface21.validation.BindException;
import com.interface21.validation.Errors;
import com.interface21.web.servlet.ModelAndView;
import com.interface21.web.util.WebUtils;

/**
 * Form controller for typical wizard-style workflows.
 *
 * <p>In contrast to classic forms, wizards have more than one form view page.
 * Therefore, there are various actions instead of one single submit action:
 * <ul>
 * <li>finish: trying to leave the wizard successfully, i.e. performing its
 * final action, and thus needing a valid state;
 * <li>cancel: leaving the wizard without performing its final action, and
 * thus without regard to the validity of its current state;
 * <li>page change: showing another wizard page, e.g. the next or previous
 * one, with regard to "dirty back" and "dirty forward".
 * </ul>
 *
 * <p>Finish and cancel actions can be triggered by request parameters, named
 * PARAM_FINISH ("_finish") and PARAM_CANCEL ("_cancel"), ignoring parameter
 * values to allow for HTML buttons. The target page for page changes can be
 * specified by PARAM_TARGET, appending the page number to the parameter name
 * (e.g. "_target1"). The action parameters are recognized when triggered by
 * image buttons too (via "_finish.x", "_abort.x", or "_target1.x").
 *
 * <p>The page can only be changed if it validates correctly, except if a
 * "dirty back" or "dirty forward" is allowed. At finish, all pages get
 * validated again to guarantee a consistent state. Note that a validator's
 * default validate method is not executed when using this class! Rather,
 * the validatePage implementation should call special validateXXX methods
 * that the validator needs to provide, validating certain pieces of the
 * object. These can be combined to validate the elements of individual pages.
 *
 * <p>Note: Page numbering starts with 0, to be able to hand an array
 * consisting of the respective view names to setPages.
 *
 * @author Juergen Hoeller
 * @since 25.04.2003
 * @see #setPages
 * @see #validatePage
 * @see #processFinish
 * @see #processCancel
 */
public abstract class AbstractWizardFormController extends AbstractFormController {

	/**
	 * Parameter triggering the finish action.
	 * Can be called from any wizard page!
	 */
	public static final String PARAM_FINISH = "_finish";

	/**
	 * Parameter triggering the cancel action.
	 * Can be called from any wizard page!
	 */
	public static final String PARAM_CANCEL = "_cancel";

	/**
	 * Parameter specifying the target page,
	 * appending the page number to the name.
	 */
	public static final String PARAM_TARGET = "_target";

	private String[] pages;

	private String pageAttribute;

	private boolean allowDirtyBack = true;

	private boolean allowDirtyForward = false;

	/**
	 * Create a new AbstractWizardFormController.
	 */
	public AbstractWizardFormController() {
		super();
		// always needs session to keep data from all pages
		setSessionForm(true);
		// never validate everything on binding ->
		// wizards validate individual pages
		setValidateOnBinding(false);
	}

	/**
	 * Set the wizard pages, i.e. the view names for the pages.
	 * The array index is interpreted as page number.
	 * @param pages view names for the pages
	 */
	public final void setPages(String[] pages) {
		if (pages == null | pages.length == 0)
			throw new IllegalArgumentException("No wizard pages defined");
		this.pages = pages;
	}

	/**
	 * Set the name of the page attribute in the model, containing
	 * an Integer with the current page number. This will be necessary
	 * for single views rendering multiple view pages.
	 * @param pageAttribute name of the page attribute
	 */
	public final void setPageAttribute(String pageAttribute) {
		this.pageAttribute = pageAttribute;
	}

	/**
	 * Set if "dirty back" is allowed, i.e. if moving to a former wizard
	 * page is allowed in case of validation errors for the current page.
	 * @param allowDirtyBack if "dirty back" is allowed
	 */
	public final void setAllowDirtyBack(boolean allowDirtyBack) {
		this.allowDirtyBack = allowDirtyBack;
	}

	/**
	 * Set if "dirty forward" is allowed, i.e. if moving to a later wizard
	 * page is allowed in case of validation errors for the current page.
	 * @param allowDirtyForward if "dirty forward" is allowed
	 */
	public final void setAllowDirtyForward(boolean allowDirtyForward) {
		this.allowDirtyForward = allowDirtyForward;
	}

	/**
	 * Call page-specific onBindAndValidate method.
	 */
	protected final void onBindAndValidate(HttpServletRequest request, Object command, BindException errors)
	    throws ServletException {
		super.onBindAndValidate(request, command, errors);
		onBindAndValidate(request, command, errors, getCurrentPage(request));
	}

	/**
	 * Callback for custom postprocessing in terms of binding and validation.
	 * Called on each submit, after standard binding and validation,
	 * and before error evaluation.
	 * @param request current HTTP request
	 * @param command bound command
	 * @param errors binder for additional custom validation
	 * @param page current wizard page
	 * @throws ServletException in case of invalid state or arguments
	 * @see #bindAndValidate
	 */
	protected void onBindAndValidate(HttpServletRequest request, Object command, BindException errors, int page)
	    throws ServletException {
	}

	/**
	 * Call page-specific referenceData method.
	 */
	protected final Map referenceData(HttpServletRequest request, Object command, Errors errors)
	    throws ServletException {
		return referenceData(request, command, errors, getCurrentPage(request));
	}

	/**
	 * Create a reference data map for the given request, consisting of
	 * bean name/bean instance pairs as expected by ModelAndView.
	 * <p>Default implementation returns null.
	 * Subclasses can override this to set reference data used in the view.
	 * @param request current HTTP request
	 * @param command form object with request parameters bound onto it
	 * @param errors binder containing current errors, if any
	 * @param page current wizard page
	 * @return a Map with reference data entries, or null if none
	 * @throws ServletException in case of invalid state or arguments
	 * @see ModelAndView
	 */
	protected Map referenceData(HttpServletRequest request, Object command, Errors errors, int page)
	    throws ServletException {
		return null;
	}

	/**
	 * Show first page as form view.
	 */
	protected final ModelAndView showForm(HttpServletRequest request, HttpServletResponse response, BindException errors)
	    throws ServletException {
		return showPage(request, errors, getInitialPage(request));
	}

	/**
	 * Prepare the form model and view, including reference and error data,
	 * for the given page. Can be used in processFinish implementations,
	 * to show the respective page in case of validation errors.
	 * @param request current HTTP request
	 * @param errors binder containing errors
	 * @param page number of page to show
	 * @return the prepared form view
	 * @throws ServletException in case of invalid state or arguments
	 */
	protected final ModelAndView showPage(HttpServletRequest request, BindException errors, int page)
	    throws ServletException {
		if (page >= 0 && page < this.pages.length) {
			logger.debug("Showing wizard page " + page + " (form bean: " + getBeanName() + ")");
			// set page session attribute for tracking
			request.getSession().setAttribute(getPageSessionAttributeName(), new Integer(page));
			// set page request attribute for evaluation by views
			Map controlModel = new HashMap();
			if (this.pageAttribute != null) {
				controlModel.put(this.pageAttribute, new Integer(page));
			}
			return showForm(request, errors, this.pages[page], controlModel);
		}
		else {
			throw new ServletException("Invalid page number: " + page);
		}
	}

	/**
	 * Return the initial page of the wizard, i.e. the page shown at wizard startup.
	 * Default implementation returns 0 for first page.
	 * @param request current HTTP request
	 * @return the initial page number
	 */
	protected int getInitialPage(HttpServletRequest request) {
		return 0;
	}

	/**
	 * Return the name of the session attribute that holds
	 * the page object for this controller.
	 * @return the name of the page session attribute
	 */
	protected final String getPageSessionAttributeName() {
		return getClass() + ".page." + getBeanName();
	}

	/**
	 * Return the current page number.
	 * Mainly useful for page-specific onBindAndValidate implementations,
	 * as methods like validatePage explicitly feature a page parameter.
	 * @throws IllegalStateException if the page attribute isn't in the session
	 * anymore, i.e. when called after processSubmit.
	 */
	protected final int getCurrentPage(HttpServletRequest request) throws IllegalStateException {
		Integer pageAttr = (Integer) request.getSession().getAttribute(getPageSessionAttributeName());
		if (pageAttr == null) {
			throw new IllegalStateException("Page attribute isn't in session anymore - called after processSubmit?");
		}
		return pageAttr.intValue();
	}

	/**
	 * Apply wizard workflow: finish, cancel, page change.
	 */
	protected final ModelAndView processSubmit(HttpServletRequest request, HttpServletResponse response,
	                                           Object command, BindException errors)
	    throws ServletException, IOException {

		int page = getCurrentPage(request);
		request.getSession().removeAttribute(getPageSessionAttributeName());

		// cancel?
		if (WebUtils.hasSubmitParameter(request, PARAM_CANCEL)) {
			logger.debug("Cancelling wizard (form bean: " + getBeanName() + ")");
			return processCancel(request, response, command, errors);
		}

		// finish?
		if (WebUtils.hasSubmitParameter(request, PARAM_FINISH)) {
			logger.debug("Finishing wizard (form bean: " + getBeanName() + ")");
			return validatePagesAndFinish(request, response, command, errors);
		}

		// normal submit: validate current page and show specified target page
		logger.debug("Validating wizard page " + page + " (form bean: " + getBeanName() + ")");
		validatePage(command, errors, page);

		Enumeration paramNames = request.getParameterNames();
		while (paramNames.hasMoreElements()) {
			String paramName = (String) paramNames.nextElement();
			if (paramName.startsWith(PARAM_TARGET)) {
				if (paramName.endsWith(WebUtils.SUBMIT_IMAGE_SUFFIX)) {
					paramName = paramName.substring(0, paramName.length() - WebUtils.SUBMIT_IMAGE_SUFFIX.length());
				}
				int target = Integer.parseInt(paramName.substring(PARAM_TARGET.length()));
				if (!errors.hasErrors() || (this.allowDirtyBack && target < page) ||
				    (this.allowDirtyForward && target > page)) {
					// allowed to go to target page
					return showPage(request, errors, target);
				}
			}
		}

		// showing current page again
		return showPage(request, errors, page);
	}

	/**
	 * Validate all pages and process finish.
	 * If there are page validation errors, show the respective view page.
	 */
	private ModelAndView validatePagesAndFinish(HttpServletRequest request, HttpServletResponse response,
	                                            Object command, BindException errors)
	    throws ServletException, IOException {
		for (int page = 0; page < pages.length; page++) {
			validatePage(command, errors, page);
			// in case of field errors on a page -> show the page
			if (errors.getErrorCount() - errors.getGlobalErrorCount() > 0) {
				return showPage(request, errors, page);
			}
		}
		// no field errors -> maybe global errors, or none at all
		return processFinish(request, response, command, errors);
	}

	/**
	 * Template method for custom validation logic for individual pages.
	 * Implementations will typically call fine-granular validateXXX methods
	 * of this instance's validator, combining them to validation of the
	 * respective pages. The validator's default validate method will not be
	 * called by a wizard controller!
	 * @param command form object with the current wizard state
	 * @param errors binder containing errors
	 * @param page number of page to show
	 */
	protected abstract void validatePage(Object command, Errors errors, int page);

	/**
	 * Template method for processing the final action of this wizard.
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @param command form object with the current wizard state
	 * @param errors binder containing errors
	 * @return the finish view
	 * @throws ServletException in case of invalid state or arguments
	 */
	protected abstract ModelAndView processFinish(HttpServletRequest request, HttpServletResponse response,
	                                              Object command, BindException errors)
	    throws ServletException, IOException;

	/**
	 * Template method for processing the cancel action of this wizard.
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @param command form object with the current wizard state
	 * @param errors binder containing errors
	 * @return the finish view
	 * @throws ServletException in case of invalid state or arguments
	 */
	protected abstract ModelAndView processCancel(HttpServletRequest request, HttpServletResponse response,
	                                              Object command, BindException errors)
	    throws ServletException, IOException;

}
