/*
 * Créé le 17 juil. 2003
 */
package com.interface21.samples.crud.web;

import info.jppawlak.web.servlet.mvc.AbstractParamListFormController;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sis.sirene.appli.ISireneAppli;
import org.sis.sirene.appli.objet.sirene.ISirene;
import org.sis.sirene.appli.objet.utilisateur.IUtilisateur;
import org.sis.sirene.appli.objet.vo.Etat;
import org.sis.sirene.appli.objet.vo.Role;
import org.sis.sirene.beans.propertyeditors.EtatEditor;
import org.sis.sirene.beans.propertyeditors.RoleEditor;
import org.sis.sirene.beans.propertyeditors.SireneEditor;

import com.interface21.dao.DataAccessException;
import com.interface21.dao.DataIntegrityViolationException;
import com.interface21.dao.DeadlockLoserDataAccessException;
import com.interface21.dao.InvalidDataAccessApiUsageException;
import com.interface21.dao.InvalidDataAccessResourceUsageException;
import com.interface21.dao.OptimisticLockingFailureException;
import com.interface21.dao.TypeMismatchDataAccessException;
import com.interface21.util.PagedListSourceProvider;
import com.interface21.util.RefreshablePagedListHolder;
import com.interface21.validation.BindException;
import com.interface21.validation.Errors;
import com.interface21.web.bind.BindUtils;
import com.interface21.web.bind.ServletRequestDataBinder;
import com.interface21.web.servlet.ModelAndView;
import com.interface21.web.servlet.support.RequestContextUtils;

/**
 * @author Jean-Pierre Pawlak
 */
public class AdminUtilisateursControleur extends AbstractParamListFormController {

	public static final String MESSAGE_NAME = "message";
	public static final String MSG_CANCELED = "canceled";
	public static final String MSG_INSERTED = "inserted";
	public static final String MSG_UPDATED = "updated";
	public static final String MSG_REMOVED = "removed";
	public static final String MSG_UNKNOWN = "unknown";

	public static final String LIST_NAME = "utilisateurs";

	public static final String IMPLEMENTATION_EXCEPTION = "ImplementationException";
	
	/**
	 * 
	 */
	public AdminUtilisateursControleur() {
		super();
		// To keep current values after errors.
		this.setBindOnNewForm(true);
		// Set session use
		this.setSessionForm(true);
		// Prevents caching
		this.setCacheSeconds(0);
	}

	/**
	 * @see com.interface21.web.servlet.mvc.AbstractFormController#formBackingObject(javax.servlet.http.HttpServletRequest)
	 */
	protected Object formBackingObject(HttpServletRequest request)
		throws ServletException {

		String sid = request.getParameter("id");
		if (null != sid ) {
			long id = 0;
			try {
				id = Long.parseLong(sid);
			} catch (NumberFormatException e) {
				return getAppli().getBlankSirene();
			}
			return getAppli().getUtilisateur(id, RequestContextUtils.getLocale(request));
		}
		return getAppli().getBlankUtilisateur();
	}

	/**
	 * @see info.jppawlak.web.servlet.mvc.AbstractListFormController#showList(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.String)
	 */
	protected ModelAndView showList(
		HttpServletRequest request,
		HttpServletResponse response,
		String message)
		throws ServletException, IOException {

		logger.info("showList " + message);
		RefreshablePagedListHolder listHolder = (RefreshablePagedListHolder) request.getSession(true).getAttribute(this.getListSessionAttributeName());
			if (null == listHolder) {
				listHolder = new RefreshablePagedListHolder();
				listHolder.setSourceProvider(new UtilisateursProvider());
				request.getSession(true).setAttribute(this.getListSessionAttributeName(), listHolder);
			}
			BindException ex = BindUtils.bind(request, listHolder, LIST_NAME);
			listHolder.setLocale(RequestContextUtils.getLocale(request));
			boolean forceRefresh = 
				request.getParameter("forceRefresh") != null 
				|| (message != null && !message.equals(MSG_CANCELED));
			listHolder.refresh(forceRefresh);
			Map model = new HashMap();
			model.putAll(ex.getModel());
			if (null != message) {
				model.put(MESSAGE_NAME, message);
			}
			logger.info("model " + ex.getModel());
			return new ModelAndView(getListView(), model);
	}

	private ISireneAppli getAppli() {
		return (ISireneAppli) this.getApplicationContext().getBean(ISireneAppli.BEAN); 	
	}

	/**
	 * Process the submit. This method dispatch on action parameter.
	 * 
	 * @see info.jppawlak.web.servlet.mvc.AbstractListFormController#processSubmit(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, com.interface21.validation.BindException, int)
	 */
	protected ModelAndView processSubmit(
		HttpServletRequest request,
		HttpServletResponse response,
		Object command,
		BindException errors,
		int action)
		throws ServletException, IOException {

		IUtilisateur utilisateur = (IUtilisateur)command;
		switch(action) {
			case CANCEL: return processCancel(request, response, utilisateur, errors);
			case INSERT: return processInsert(request, response, utilisateur, errors);
			case UPDATE: return processUpdate(request, response, utilisateur, errors);
			case REMOVE: return processRemove(request, response, utilisateur, errors);
			default: return processUnknown(request, response, utilisateur, errors);
		}
	}


	/**
	 * Process the CANCEL action.
	 * 
	 * @param request The ServletRequest
	 * @param response The ServletResponse
	 * @param utilisateur The IUtilisateur object
	 * @param errors
	 * @return the ModelAndView
	 * @throws ServletException
	 * @throws IOException
	 */
	private ModelAndView processCancel(
		HttpServletRequest request,
		HttpServletResponse response,
		IUtilisateur utilisateur,
		BindException errors)
		throws ServletException, IOException {

		return showList(request, response, MSG_CANCELED);
	}

	/**
	 * Process the INSERT action.
	 * 
	 * @param request The ServletRequest
	 * @param response The ServletResponse
	 * @param utilisateur The IUtilisateur object
	 * @param errors
	 * @return the ModelAndView
	 * @throws ServletException
	 * @throws IOException
	 */
	private ModelAndView processInsert(
		HttpServletRequest request,
		HttpServletResponse response,
		IUtilisateur utilisateur,
		BindException errors)
		throws ServletException, IOException {

		String msg = MSG_INSERTED;
		try {
			getAppli().createUtilisateur(utilisateur, RequestContextUtils.getLocale(request));
		} catch (DataAccessException e) {
			return handleError(request, response, utilisateur, e, INSERT);
		}
		return showList(request, response, msg);
	}

	/**
	 * Process the UPDATE action.
	 * 
	 * @param request The ServletRequest
	 * @param response The ServletResponse
	 * @param utilisateur The IUtilisateur object
	 * @param errors
	 * @return the ModelAndView
	 * @throws ServletException
	 * @throws IOException
	 */
	private ModelAndView processUpdate(
		HttpServletRequest request,
		HttpServletResponse response,
		IUtilisateur utilisateur,
		BindException errors)
		throws ServletException, IOException {

		String msg = MSG_UPDATED;
		try {
			getAppli().updateUtilisateur(utilisateur, RequestContextUtils.getLocale(request));
		} catch (DataAccessException e) {
			return handleError(request, response, utilisateur, e, UPDATE);
		}
		return showList(request, response, msg);
	}

	/**
	 * Process the REMOVE action.
	 * 
	 * @param request The ServletRequest
	 * @param response The ServletResponse
	 * @param utilisateur The IUtilisateur object
	 * @param errors
	 * @return the ModelAndView
	 * @throws ServletException
	 * @throws IOException
	 */
	private ModelAndView processRemove(
		HttpServletRequest request,
		HttpServletResponse response,
		IUtilisateur utilisateur,
		BindException errors)
		throws ServletException, IOException {

		String msg = MSG_REMOVED;
		try {
			getAppli().removeUtilisateur(utilisateur);
		} catch (DataAccessException e) {
			return handleError(request, response, utilisateur, e, REMOVE);
		}
		return showList(request, response, msg);
	}

	/**
	 * Process the UNKNOWN action.
	 * 
	 * @param request The ServletRequest
	 * @param response The ServletResponse
	 * @param utilisateur The IUtilisateur object
	 * @param errors
	 * @return the ModelAndView
	 * @throws ServletException
	 * @throws IOException
	 */
	private ModelAndView processUnknown(
		HttpServletRequest request,
		HttpServletResponse response,
		IUtilisateur utilisateur,
		BindException errors)
		throws ServletException, IOException {

		 return showList(request, response, MSG_UNKNOWN);
	}

	/**
	 * @see com.interface21.web.servlet.mvc.BaseCommandController#initBinder(javax.servlet.http.HttpServletRequest, com.interface21.web.bind.ServletRequestDataBinder)
	 */
	protected void initBinder(
		HttpServletRequest request,
		ServletRequestDataBinder binder)
		throws ServletException {

		super.initBinder(request, binder);
		binder.registerCustomEditor(
			Etat.class, 
			new EtatEditor(
				true,
				this.getAppli().getEtatDao(), 
				RequestContextUtils.getLocale(request)
			)
		);
		binder.registerCustomEditor(
			Role.class, 
			new RoleEditor(
				true,
				this.getAppli().getRoleDao(), 
				RequestContextUtils.getLocale(request)
			)
		);
		binder.registerCustomEditor(
			ISirene.class, 
			new SireneEditor(
				true,
				this.getAppli().getSireneDao(), 
				RequestContextUtils.getLocale(request)
			)
		);
	}

	/**
	 * @see com.interface21.web.servlet.mvc.AbstractFormController#referenceData(javax.servlet.http.HttpServletRequest, java.lang.Object, com.interface21.validation.Errors)
	 */
	protected Map referenceData(
		HttpServletRequest request,
		Object command,
		Errors errors)
		throws ServletException {

		Map model = new HashMap();
		model.put("roles",getAppli().getAllRoles(RequestContextUtils.getLocale(request)));
		model.put("etats",getAppli().getAllEtats(RequestContextUtils.getLocale(request)));
		model.put("sirenes",getAppli().getAllSirenes(RequestContextUtils.getLocale(request)));
		// TODO themes
		return model;
	}

	/**
	 * @param request
	 * @param response
	 * @param command
	 * @param e
	 * @param action
	 * @return the ModelAndView
	 * @throws ServletException
	 * @throws IOException
	 */
	private ModelAndView handleError(
		HttpServletRequest request,
		HttpServletResponse response,
		Object command,
		DataAccessException e,
		int action) throws ServletException, IOException {

		// In any case, we log first the error.
		logger.error("process" + ACTIONS[action] + " ERROR: " + e.getClass().getName() + ", " + e.getMessage());
		String name = e.getClass().getName();
		name = name.substring(name.lastIndexOf(".") + 1);

		// Recoverable errors.
		// We return on the form page with an error message.
		if (e instanceof DataIntegrityViolationException
			|| e instanceof DeadlockLoserDataAccessException
			|| e instanceof OptimisticLockingFailureException) {
		
			// request.getSession().setAttribute(getFormSessionAttributeName(), command);
			ServletRequestDataBinder errors = createBinder(request, command);
			errors.reject(name, null, name);
			return this.showForm(request, response, errors);
		}
		
		// Uncecoverable errors.
		// We go on the list page with an error message.
		
		// Check for implementation errors.
		if (e instanceof InvalidDataAccessResourceUsageException
			|| e instanceof InvalidDataAccessApiUsageException
			|| e instanceof TypeMismatchDataAccessException) {
		
			name = IMPLEMENTATION_EXCEPTION;
		}

		// We go
		return showList(request, response, name);
	}


	// Embedded classes
	private class UtilisateursProvider implements PagedListSourceProvider {
		public List loadList(Locale loc, Object filter) {
			return getAppli().getAllUtilisateurs(loc);
		}
	}

}
