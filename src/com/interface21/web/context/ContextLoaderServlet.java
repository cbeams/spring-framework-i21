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

import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

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
 *
 * Note: ContextLoaderServlet and ContextLoaderListener both support a contextClass
 * init parameter at the ServletContext resp. web.xml root level, if not overridden
 * by the servlet init parameter.
 *
 * Note: Due to container incompatibilities concerning the order of servlet and listener
 * initialization, Spring initialization only works in all cases if you regard the following:
 * - ContextLoaderServlet needs a lower load-on-startup number than the controller servlets;
 * - ContextLoaderListener needs controller servlets WITHOUT load-on-startup.
 *
 * @author Rod Johnson
 * @version $Id$
 */
public class ContextLoaderServlet extends HttpServlet {

	private final Logger logger = Logger.getLogger(getClass());

	//---------------------------------------------------------------------
	// Overridden methods
	//---------------------------------------------------------------------
	/**
	 * Bind the WebApplicationContext
	 * implementation as a ServletContext attribute
	 * @throws ServletException if startup fails
	 */
	public void init() throws ServletException {
		ContextLoader.initContext(getServletContext(), getInitParameter(ContextLoader.CONTEXT_CLASS_PARAM));
	}	// init

	//---------------------------------------------------------------------
	// Interface methods
	//---------------------------------------------------------------------
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//logger.info("Showing status at " + STATUS_URL);
		//request.setAttribute(WebApplicationContext.WEB_APPLICATION_CONTEXT_ATTRIBUTE_NAME, webApplicationContext);
		//request.getRequestDispatcher(STATUS_URL).forward(request, response);
		logger.info("RELOADING CONFIG");
		init();
		response.getOutputStream().println("RELOADED CONTEXT");
	}
}	// class ContextLoaderServlet
