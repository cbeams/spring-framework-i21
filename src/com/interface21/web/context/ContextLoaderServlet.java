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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import com.interface21.context.ApplicationContextException;

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
 * Spring initialization should work if you regard the following:
 * - ContextLoaderServlet needs a lower load-on-startup number than any FrameworkServlets;
 * - ContextLoaderListener normally does not mind FrameworkServlet load-on-startup values;
 * - or you can stick to the implicit context initialization provided by FrameworkServlet.
 * (Obviously, the latter is not applicable if you do not use any FrameworkServlets.)
 *
 * @author Rod Johnson, Juergen Hoeller
 * @version $Id$
 */
public class ContextLoaderServlet extends HttpServlet {

	public void init() throws ServletException {
		initContext();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		log("Reloading config");
		initContext();
		response.getOutputStream().println("Reloaded config");
	}

	/**
	 * Initialize the root WebApplicationContext.
	 * @throws ServletException if startup fails
	 */
	protected void initContext() throws ServletException {
		try {
			ContextLoader.initContext(getServletContext(), getInitParameter(ContextLoader.CONTEXT_CLASS_PARAM));
		} catch (ApplicationContextException ex) {
			throw new ServletException(ex);
		}
	}

}
