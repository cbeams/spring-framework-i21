package com.interface21.web.context;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.interface21.web.context.support.WebApplicationContextUtils;

/**
 * Bootstrap servlet to start up Spring's root WebApplicationContext.
 * Simply delegates to ContextLoader.
 *
 * <p>This servlet should have a lower load-on-startup value in web.xml
 * than any servlets that access the root application context.
 *
 * <p><i>Note that this class has been deprecated for containers implementing
 * Servlet API 2.3 in favour of ContextLoaderListener. It is part of Spring
 * to maintain compatibility for servlet containers implementing v2.2 only.
 * See ContextLoaderListener and the web.xml files of the sample web apps
 * for more information.</i>
 *
 * @author Rod Johnson
 * @author Darren Davison
 * @deprecated beyond Servlet 2.2 - use ContextLoaderListener in a
 * Servlet 2.3 compatible container
 * @see ContextLoader
 * @see ContextLoaderListener
 */
public class ContextLoaderServlet extends HttpServlet {

	/**
	 * Initialize the root web application context.
	 */
	public void init() throws ServletException {
		ContextLoader.initContext(getServletContext());
	}

	public void destroy() {
		ContextLoader.closeContext(getServletContext());
	}

	/**
	 * This should never even be called since no mapping to this servlet should
	 * ever be created in web.xml. That's why a Servlet 2.3 listener is much more
	 * appropriate for initialization work ;-)
	 */
	public void doService(HttpServletRequest request, HttpServletResponse response) throws IOException {
		getServletContext().log("Attempt to call service method on ContextLoaderServlet as " + request.getRequestURI() + " was ignored");
		response.sendError(HttpServletResponse.SC_BAD_REQUEST);
	}

	public String getServletInfo() {
		return "ContextLoaderServlet for Servlet API 2.2 (deprecated in favour of ContextLoaderListener for Servlet API 2.3)";
	}

}
