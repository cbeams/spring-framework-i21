package com.interface21.web.context.support;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import com.interface21.context.ApplicationContextException;
import com.interface21.context.support.StaticApplicationContext;
import com.interface21.web.context.WebApplicationContext;

/**
 * WebApplicationContext implementation for testing.
 * Not for use in production applications.
 */
public class StaticWebApplicationContext extends StaticApplicationContext implements WebApplicationContext {

	private ServletContext sc;
	
	
	/** Allow superclass to throw exception
	 */
	public StaticWebApplicationContext() throws Exception {
	}
	
	/**
	 * Normally this would cause loading, but this
	 * class doesn't rely on loading
	 * @see WebApplicationContext#setServletContext(ServletContext)
	 */
	public void setServletContext(ServletContext servletContext) throws ServletException {
		this.sc = servletContext;
		try {
			refresh();
			WebApplicationContextUtils.publishConfigObjects(this);
		}
		catch (ApplicationContextException ex) {
			// TODO nest properly
			throw new ServletException(ex.getMessage());
		}
		
		// Expose as a ServletContext object
		WebApplicationContextUtils.publishWebApplicationContext(this);
	}
	

	/**
	 * @see WebApplicationContext#getServletContext()
	 */
	public ServletContext getServletContext() {
		return sc;
	}

}

