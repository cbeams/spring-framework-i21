/**
 * Generic framework code included with 
 * <a href="http://www.amazon.com/exec/obidos/tg/detail/-/1861007841/">Expert One-On-One J2EE Design and Development</a>
 * by Rod Johnson (Wrox, 2002). 
 * This code is free to use and modify. However, please
 * acknowledge the source and include the above URL in each
 * class using or derived from this code. 
 * Please contact <a href="mailto:rod.johnson@interface21.com">rod.johnson@interface21.com</a>
 * for commercial support.
 */

package com.interface21.web.context;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

/**
 * Servlet to bootstrap the root WebApplicationContext object.
 * <br/>This servlet requires a single config parameter to be provided in the
 * web.xml deployment descriptor:
 * <li>contextClass: the class name of the WebApplicationContext implementation to provide
 * a context for this application. Note that this class must have a public no arg constructor.
 * WebApplicationContext implementations are responsible for loading their own config parameters
 * from the application's ServletContext: this servlet merely instantiates the class and 
 * provides it with the current ServletContext object.
 * <p/>This servlet must be set to load on startup as the first servlet initiated in the
 * application. All other servlets, including the MVC ControllerServlet, depend on this servlet.
 * @author Rod Johnson
 * @version $Id$
 */
public class ContextLoaderServlet extends HttpServlet {
	
	/** Config param to this servlet for the WebApplicationContext
	 * implementation class to use
	 */
	public static final String CONTEXT_CLASS_PARAM = "contextClass";
	
	/** URL within the WAR of this servlet's status page
	 */
	public static final String STATUS_URL = "_context/status.jsp";
	
	//---------------------------------------------------------------------
	// Instance data
	//---------------------------------------------------------------------
	/**
	* Create a logging category that is available
	* to subclasses. 
	*/
	protected Logger logger = Logger.getLogger(getClass().getName());
	
	/** Object we reflectively instantiate providing this application's
	 * context
	 */
	private WebApplicationContext webApplicationContext;
	
	/** Identification String for this servlet */
	private String identifier;
	
	
	//---------------------------------------------------------------------
	// Overridden methods
	//---------------------------------------------------------------------
	/**
	 * Bind the WebApplicationContext
	 * implementation as a ServletContext attribute
	 * @throws ServletException if startup fails
	 */
	public void init() throws ServletException {
		this.identifier = "ContextLoaderServlet with name '" + getServletConfig().getServletName() + "' ";
		
		String contextClass = getServletConfig().getInitParameter(CONTEXT_CLASS_PARAM);
		if (contextClass == null)
			throw new ServletException("Cannot load context class param '" + CONTEXT_CLASS_PARAM + "'");
		
		
		// Now we must load the WebApplicationContext. 
		// It configures itself: all we need to do is construct the class with a no-arg
		// constructor, and invoke setServletContext 
		try {
			Class clazz = Class.forName(contextClass); 			
			this.webApplicationContext = (WebApplicationContext) clazz.newInstance();						
			webApplicationContext.setServletContext(getServletContext());						
		}
		catch (ClassNotFoundException ex) {
			String mesg = getIdentifier() + "Failed to load config class '" + contextClass + "'";
			logger.error(mesg, ex);
			throw new ServletException(mesg + ": " + ex);
		}
		catch (InstantiationException ex) {
			String mesg = getIdentifier() + "Failed to instantiate config class '" + contextClass + "': does it have a public no arg constructor?";
			logger.error(mesg, ex);
			throw new ServletException(mesg + ": " + ex);
		}
		catch (IllegalAccessException ex) {
			String mesg = getIdentifier() + "Failed with IllegalAccess to find or instantiate config class '" + contextClass + "': does it have a public no arg constructor?";
			logger.error(mesg, ex);
			throw new ServletException(mesg + ": " + ex);
		}
		catch (Throwable t) {
			String mesg = getIdentifier() + "Unexpected error loading config: " + t;
			logger.error(mesg, t);			
			throw new ServletException(mesg, t);
		}	
	}	// init
	
	
	/** Return a description of this servlet
	 * @return a description of this servlet
	 */
	public String getServletInfo() {
		return "ContextLoaderServlet v 1.0";
	}
	
	
	//---------------------------------------------------------------------
	// Interface methods
	//---------------------------------------------------------------------
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//logger.info("Showing status at " + STATUS_URL);
		//request.setAttribute(WebApplicationContext.WEB_APPLICATION_CONTEXT_ATTRIBUTE_NAME, webApplicationContext);
		//request.getRequestDispatcher(STATUS_URL).forward(request, response);
		
		logger.info("!!!!!!!!!!!! RELOADING CONFIG");
		init();
		response.getOutputStream().println("RELOADED CONTEXT");
		
	}

	
	//---------------------------------------------------------------------
	// Implementation methods
	//---------------------------------------------------------------------
	/** Return a String identifing this Servlet
	 * @return a String identifing this Servlet
	 */
	private String getIdentifier() {
		return identifier;
	}

}	// class ContextLoaderServlet
