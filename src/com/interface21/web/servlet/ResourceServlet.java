
package com.interface21.web.servlet;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Simple servlet that can expose an internal resource, including a 
 * default URL if the specified resource is not found. An alternative,
 * for example, to trying and catching exceptions when using JSP include.
 * A typical usage would map all URLs with a .res extension onto an instance
 * of this servlet, and use the JSP include action to include 
 * with the resource parameter (specificed in a jsp:param sub-action)
 * indicating the actual path in the WAR.
 * <br>The defaultUrl bean property must be set to the
 * internal path of the default (placeholder) URL.
 * @author Rod Johnson
 */
public class ResourceServlet extends HttpServletBean {
	
	/**
	 * Name of the parameter that must contain the actual path. 
	 */
	public static final String PATH_PARAM = "resource";
	
	/**
	 * URL within the current web application from which to include
	 * content if the requested path isn't found.
	 */
	private String defaultUrl;


	/**
	 * Gets the defaultUrl.
	 * @return Returns a String
	 */
	public String getDefaultUrl() {
		return defaultUrl;
	}

	/**
	 * Sets the defaultUrl.
	 * @param defaultUrl The defaultUrl to set
	 */
	public void setDefaultUrl(String defaultUrl) {
		this.defaultUrl = defaultUrl;
	}
	
	
	
	/**
	 * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest, HttpServletResponse)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String path = request.getParameter(PATH_PARAM);
		if (path == null)
			throw new ServletException("Path is required");
		try {
			RequestDispatcher rd = request.getRequestDispatcher(path);
			rd.include(request, response);
			logger.debug("Included content of '" + path + "'");
		}
		catch (Exception ex) {
			RequestDispatcher rd = request.getRequestDispatcher(this.defaultUrl);
			rd.include(request, response);
			logger.warn("Failed to include content of '" + path + "'");
		}
	}

}	// class ResourceServlet
