package com.interface21.web.util;

import javax.servlet.http.HttpServlet;

/**
 * Bootstrap servlet for custom Log4J initialization in a web environment.
 * Simply delegates to Log4jWebConfigurer.
 *
 * <p>Note: This servlet should have a lower load-on-startup value in web.xml
 * than ContextLoaderServlet, when using custom Log4J initialization.
 *
 * <p><i>Note that this class has been deprecated for containers implementing
 * Servlet API 2.3 in favour of Log4jConfigListener. It is part of Spring
 * to maintain compatibility for servlet containers implementing v2.2 only.
 * See Log4jConfigListener and the web.xml files of the sample web apps
 * for more information.</i>
 *
 * @author Juergen Hoeller
 * @author Darren Davison
 * @since 12.08.2003
 * @deprecated beyond Servlet 2.2 - use Log4jConfigListener in a
 * Servlet 2.3 compatible container
 * @see Log4jWebConfigurer
 * @see Log4jConfigListener
 */
public class Log4jConfigServlet extends HttpServlet {

	public void init() {
		Log4jWebConfigurer.initLogging(getServletContext());
	}

	public void destroy() {
		Log4jWebConfigurer.shutdownLogging(getServletContext());
	}

	public String getServletInfo() {
		return "Log4jConfigServlet for Servlet API 2.2 (deprecated in favour of Log4jConfigListener for Servlet API 2.3)";
	}

}
