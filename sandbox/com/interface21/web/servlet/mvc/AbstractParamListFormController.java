/*
 * Créé le 17 juil. 2003
 */
package com.interface21.web.servlet.mvc;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.interface21.validation.BindException;
import com.interface21.web.servlet.ModelAndView;

/**
 * Additional abstract class  controller for CRUD operations in collaboration with a list view.
 * This class mainly adds parametrization for implementing via the beans properties file.
 * 
 * @author Jean-Pierre Pawlak
 */
public abstract class AbstractParamListFormController extends AbstractListFormController {

	private String listView;
	private String formView;
	private String insertConfirmView;
	private String updateConfirmView;
	private String removeConfirmView;

	/**
	 * 
	 */
	public AbstractParamListFormController() {
		super();
	}

	// --- Final methods

	/**
	 * Will call the right ConfirmView.
	 * It's final due to the simplicity of the task. Notice the new 
	 * referenceData signature used for additional elements in the model.
	 * 
	 * @see info.jppawlak.web.servlet.mvc.AbstractListFormController#showConfirm(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, com.interface21.validation.BindException, int)
	 */
	protected final ModelAndView showConfirm(
		HttpServletRequest request,
		HttpServletResponse response,
		Object command,
		BindException errors,
		int action)
		throws ServletException, IOException {
			
		Map model = referenceData(request, command, errors, action);
		if (null == model) {
			model = new HashMap();
		}
		model.putAll(errors.getModel());
		String viewName;
		switch (action) {
			case UPDATE: viewName = this.getUpdateConfirmView(); break;
			case REMOVE: viewName = this.getRemoveConfirmView(); break;
			default: viewName = this.getInsertConfirmView();
		}
		return new ModelAndView(viewName, model);
	}

	// --- Not final and abstract methods

	/**
	 * Will normally not be subclassed.
	 * 
	 * @see com.interface21.web.servlet.mvc.AbstractFormController#showForm(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, com.interface21.validation.BindException)
	 */
	protected ModelAndView showForm(
		HttpServletRequest request,
		HttpServletResponse response,
		BindException errors)
		throws ServletException, IOException {

		return showForm(request, errors, formView);
	}

	/**
	 * This signature of referenceData is used by the confirmViews, not the showForm.
	 * 
	 * @param request
	 * @param command
	 * @param errors
	 * @param action
	 * @return a Map of additional objects to put in the model
	 */
	protected Map referenceData (HttpServletRequest request, Object command, BindException errors, int action)  {
		return null;
	}
	
	// --- Accessors methods

	protected final String getFormView() {
		return formView;
	}

	protected final String getInsertConfirmView() {
		return insertConfirmView;
	}

	protected final String getListView() {
		return listView;
	}

	protected final String getRemoveConfirmView() {
		return removeConfirmView;
	}

	protected final String getUpdateConfirmView() {
		return updateConfirmView;
	}

	public final void setFormView(String string) {
		formView = string;
	}

	public final void setInsertConfirmView(String insertConfirmView) {
		this.insertConfirmView = insertConfirmView;
		this.setConfirmInsert(null != insertConfirmView && insertConfirmView.length() > 0);
	}

	public final void setRemoveConfirmView(String removeConfirmView) {
		this.removeConfirmView = removeConfirmView;
		this.setConfirmRemove(null != removeConfirmView && removeConfirmView.length() > 0);
	}

	public final void setUpdateConfirmView(String updateConfirmView) {
		this.updateConfirmView = updateConfirmView;
		this.setConfirmUpdate(null != updateConfirmView && updateConfirmView.length() > 0);
	}

	public final void setListView(String listView) {
		this.listView = listView;
	}

}
