package com.interface21.web.context;

import javax.servlet.ServletContextListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletException;

/**
 * Bootstrap listener to start up Spring's WebApplicationContext,
 * an alternative to ContextLoaderServlet.
 * Simply delegates to ContextLoader, just like ContextLoaderServlet.
 *
 * Note: ContextLoaderServlet and ContextLoaderListener both support a contextClass
 * init parameter at the servlet context resp. web.xml root level, if not overridden
 * by the servlet init parameter.
 *
 * Note: Due to container incompatibilities concerning the order of servlet and listener
 * initialization, Spring initialization only works in all cases if you regard the following:
 * - ContextLoaderServlet needs a lower load-on-startup number than the controller servlets;
 * - ContextLoaderListener needs controller servlets WITHOUT load-on-startup.
 *
 * @author Juergen Hoeller
 * @since 17.02.2003
 */
public class ContextLoaderListener implements ServletContextListener {

	public void contextInitialized(ServletContextEvent event) {
		try {
			ContextLoader.initContext(event.getServletContext());
		} catch (ServletException ex) {
			throw new RuntimeException(ex.getMessage());
		}
	}

	public void contextDestroyed(ServletContextEvent event) {
	}
}
