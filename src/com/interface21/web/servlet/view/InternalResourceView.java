/*
 * Generic framework code included with 
 * <a href="http://www.amazon.com/exec/obidos/tg/detail/-/1861007841/">Expert One-On-One J2EE Design and Development</a>
 * by Rod Johnson (Wrox, 2002). 
 * This code is free to use and modify. However, please
 * acknowledge the source and include the above URL in each
 * class using or derived from this code. 
 * Please contact <a href="mailto:rod.johnson@interface21.com">rod.johnson@interface21.com</a>
 * for commercial support.
 */
 
package com.interface21.web.servlet.view;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.interface21.web.servlet.ControllerServlet;


/**
 * Wrapper for a JSP or other resource within the WAR.
 * We set request attributes and forward to request to the
 * specified resource using a RequestDispatcher.
 * @author  Rod Johnson	
 */
public class InternalResourceView extends AbstractView {
	
	public final static String DEBUG_JSP = "/jsp/debug/trace.jsp"; 
	
	
	//---------------------------------------------------------------------
	// Instance data
	//---------------------------------------------------------------------
	/** URL of the resource within the WAR we'll use a RequestDispatcher
	 * to forward to */
	private String url;
	

	//---------------------------------------------------------------------
	// Constructors
	//---------------------------------------------------------------------
	/** Constructor for use as a bean
	 */
	public InternalResourceView() {
	}
	 
	 
	/** Create a new InternalResourceView, specifying
	 * the given url
	 * @param url url to forward to
	 */
	public InternalResourceView(String url) {
		setUrl(url);
	}
	
	//---------------------------------------------------------------------
	// Bean properties
	//---------------------------------------------------------------------
	/** Set the URL of the resource this view forwards to
	 * @param url the URL of the resource this view forwards to
	 */
	public void setUrl(String url) {
		this.url = url;
	}
	
	
	/** 
	 * Get the URL of the resource this view forwards to
	 * @return the URL of the resource this view forwards to
	 */
	public String getUrl() {
		return url;
	}
	
 
	//---------------------------------------------------------------------
	// Implementation of abstract methods
	//---------------------------------------------------------------------
	/**
	 * Renders the view given the specified model.  There can be many types of
	 * view.<br/>
	 * The first take will be preparing the request: this may include setting the model
	 * as an attribute, in the case of a JSP view.
	 */
	public void renderMergedOutputModel(Map model, HttpServletRequest request, HttpServletResponse response) throws ServletException {
			
		if (getUrl() == null)
			throw new ServletException("InternalResourceView is not configured: URL cannot be null");
			
		exposeModelsAsRequestAttributes(model, request);
		
		// Let the target resource set the content type
				
		try {
			if (ControllerServlet.isDebugMode(request)) {
				logger.debug("Showing debug information, and including JSP with url '" + getUrl() + "' in InternalResourceView with name '" + getName() + "'");
				request.getRequestDispatcher(getUrl()).include(request, response);
				request.getRequestDispatcher(DEBUG_JSP).include(request, response);
			}
			else {
				// Simply forward to the JSP
				RequestDispatcher rd = request.getRequestDispatcher(getUrl());
				if (rd == null)
					throw new ServletException("Can't get RequestDispatcher for '" + getUrl() + "': check that this file exists within your WAR");
				rd.forward(request, response);
				logger.debug("Forwarded OK to resource within current application with url '" + getUrl() + "' in InternalResource view with name '" + getName() + "'");
			}
			
		} 
		catch (IOException ex) {
			//Category.getInstance(getClass()).error("Couldn't dispatch to JSP with url '" + getUrl() + "' defined in view with name '" + cr.getViewName() + "': " + ex, ex);
			throw new ServletException("Couldn't dispatch to JSP with url '" + getUrl() + "' in InternalResourceView with name '" + getName() + "'", ex);
		}
	}	// renderMergedOutputModel
	
	
	/**
	 * Expose the models in the given map as request attributes. Names will be
	 * taken from the map. This method can be used by different view types.
	 * @param model Map of models to expose
	 * @param request HttpServletRequest to preprocess.
	 */
	protected final void exposeModelsAsRequestAttributes(Map model,  HttpServletRequest request) {
		if (model != null) {
			Set keys = model.keySet();
			Iterator itr = keys.iterator();
			while (itr.hasNext()) {
				String modelname = (String) itr.next();
				Object val = model.get(modelname);
				// NULL!?

				if (logger.isDebugEnabled()) {
					String msg = "Added model with name '" + modelname + "' to request in InternalResourceView with name '" + getName() + "' ";
					msg += (val != null) ? "and class " + val.getClass() : "(null)";
					logger.debug(msg);
				}
				request.setAttribute(modelname, val);
			}
		}
		else {
			logger.debug("Model is null. Nothing to expose to request");
		}
	}
	
}	// class InternalResourceView
