package com.interface21.web.context;

import com.interface21.web.context.support.XmlWebApplicationContext;

import org.apache.log4j.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/**
 * Performs the actual initialization work.
 * Called by both ContextLoaderServlet and ContextLoaderListener.
 *
 * Regards a "contextClass" parameter at the servlet context resp. web.xml root level,
 * falling back to the default context class (XmlWebApplicationContext) if not found.
 *
 * @author Juergen Hoeller
 * @since 17.02.2003
 */
public class ContextLoader {

	/** Config param to this servlet for the WebApplicationContext
	 * implementation class to use
	 */
	public static final String CONTEXT_CLASS_PARAM = "contextClass";

	public static final Class DEFAULT_CONTEXT_CLASS = XmlWebApplicationContext.class;

	private static final Logger logger = Logger.getLogger(ContextLoader.class);

	/**
	 * Initializes Spring's web application context for the given servlet context,
	 * solely regarding the servlet context init parameter.
	 */
	public static void initContext(ServletContext servletContext) throws ServletException {
		initContext(servletContext, null);
	}

	/**
	 * Initializes Spring's web application context for the given servlet context,
	 * using the given context class, or the servlet context init parameter as a fallback.
	 *
	 * @param servletContext  the current servlet context
	 * @param contextClass  the context class to use, or null for default initialization
	 */
	public static void initContext(ServletContext servletContext, String contextClass) throws ServletException {
		if (contextClass == null)
			contextClass = servletContext.getInitParameter(CONTEXT_CLASS_PARAM);

		// Now we must load the WebApplicationContext.
		// It configures itself: all we need to do is construct the class with a no-arg
		// constructor, and invoke setServletContext
		try {
			Class clazz = (contextClass != null ? Class.forName(contextClass) : DEFAULT_CONTEXT_CLASS);
			logger.info("Using context class '" + clazz.getName() + "'");
			WebApplicationContext webApplicationContext = (WebApplicationContext)clazz.newInstance();
			webApplicationContext.setServletContext(servletContext);
		} catch (ClassNotFoundException ex) {
			String mesg = "Failed to load config class '" + contextClass + "'";
			logger.error(mesg, ex);
			throw new ServletException(mesg + ": " + ex);
		} catch (InstantiationException ex) {
			String mesg = "Failed to instantiate config class '" + contextClass + "': does it have a public no arg constructor?";
			logger.error(mesg, ex);
			throw new ServletException(mesg + ": " + ex);
		} catch (IllegalAccessException ex) {
			String mesg = "Failed with IllegalAccess to find or instantiate config class '" + contextClass + "': does it have a public no arg constructor?";
			logger.error(mesg, ex);
			throw new ServletException(mesg + ": " + ex);
		} catch (Throwable t) {
			String mesg = "Unexpected error loading config: " + t;
			logger.error(mesg, t);
			throw new ServletException(mesg, t);
		}
	}
}
