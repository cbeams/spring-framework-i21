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
 * Sets request attributes and forwards the request to the specified
 * specified resource using a RequestDispatcher.
 *
 * @author Rod Johnson
 */
public class InternalResourceView extends AbstractView {

	private String url;

	private String debugUrl;

	/**
	 * Constructor for use as a bean
	 */
	public InternalResourceView() {
	}
	 
	/**
	 * Create a new InternalResourceView with the given URL.
	 * @param url url to forward to
	 */
	public InternalResourceView(String url) {
		setUrl(url);
	}

	/**
	 * Set the resource URL that this view forwards to.
	 * @param url the URL of the resource this view forwards to
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	protected String getUrl() {
		return url;
	}

	/**
	 * Set a debug URL that gets included at the end of the response,
	 * if the ControllerServlet is in debug mode.
	 * @param debugUrl the URL of the debug resource
	 * @see ControllerServlet#setDebug
	 */
	public void setDebugUrl(String debugUrl) {
		this.debugUrl = debugUrl;
	}

	/**
	 * Render the internal resource given the specified model.
	 * This includes setting the model as request attributes.
	 */
	protected void renderMergedOutputModel(Map model, HttpServletRequest request, HttpServletResponse response) throws ServletException {
			
		if (this.url == null)
			throw new ServletException("InternalResourceView is not configured: URL cannot be null");
			
		exposeModelsAsRequestAttributes(model, request);

		// Let the target resource set the content type
				
		try {
			if (ControllerServlet.isDebugMode(request)) {
				logger.debug("Showing debug information, and including JSP with url '" + this.url + "' in InternalResourceView with name '" + getName() + "'");
				request.getRequestDispatcher(this.url).include(request, response);
				request.getRequestDispatcher(this.debugUrl).include(request, response);
			}
			else {
				// Simply forward to the JSP
				RequestDispatcher rd = request.getRequestDispatcher(this.url);
				if (rd == null)
					throw new ServletException("Can't get RequestDispatcher for '" + this.url + "': check that this file exists within your WAR");
				rd.forward(request, response);
				logger.debug("Forwarded OK to resource within current application with url '" + this.url + "' in InternalResource view with name '" + getName() + "'");
			}
			
		} 
		catch (IOException ex) {
			//Category.getInstance(getClass()).error("Couldn't dispatch to JSP with url '" + this.url + "' defined in view with name '" + cr.getViewName() + "': " + ex, ex);
			throw new ServletException("Couldn't dispatch to JSP with url '" + this.url + "' in InternalResourceView with name '" + getName() + "'", ex);
		}
	}	// renderMergedOutputModel
	
	
	/**
	 * Expose the models in the given map as request attributes.
	 * Names will be taken from the map.
	 * This method is suitable for all resources reachable by RequestDispatcher.
	 * @param model Map of models to expose
	 * @param request HttpServletRequest to preprocess.
	 */
	protected void exposeModelsAsRequestAttributes(Map model, HttpServletRequest request) {
		if (model != null) {
			Set keys = model.keySet();
			Iterator itr = keys.iterator();
			while (itr.hasNext()) {
				String modelname = (String) itr.next();
				Object val = model.get(modelname);
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
	
}
