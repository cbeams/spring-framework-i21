
package com.interface21.web.servlet.mvc;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.interface21.beans.factory.InitializingBean;
import com.interface21.web.servlet.ModelAndView;

/**
 * Trivial controller that always returns a named view.
 * An alternative to sending a request straight to a view
 * such as a JSP.
 * @author Rod Johnson 
 */
public class ParameterizableViewController extends AbstractController 
		implements InitializingBean {
	
	private String successView;
	

	/* Require only success view name */
	public ParameterizableViewController() {
	}
	
	
	//---------------------------------------------------------------------
	// Bean properties
	//---------------------------------------------------------------------
	/**
	 * Gets the viewName.
	 * Used in this class: other properties are for subclasses only.
	 * @return Returns a String
	 */
	public String getViewName() {
		return successView;
	}

	/**
	 * Sets the viewName.
	 * @param viewName The viewName to set
	 */
	public void setViewName(String viewName) {
		this.successView = viewName;
	}
	


	/**
	 * @see AbstractController#handleRequest(HttpServletRequest, HttpServletResponse)
	 */
	protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		return new ModelAndView(this.successView);
	}
	
	/**
	 * Ensure at least successView is set!?
	 * @see InitializingBean#afterPropertiesSet()
	 */
	public void afterPropertiesSet() throws Exception {
		if (successView == null)
			throw new ServletException("viewName bean property must be set in " + getClass().getName());
	}

}
