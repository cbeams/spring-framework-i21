package com.interface21.web.context;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;

import com.interface21.context.ApplicationContextException;
import com.interface21.web.context.support.XmlWebApplicationContext;

/**
 * Performs the actual initialization work for the root application context.
 * Called by both ContextLoaderListener.
 *
 * <p>Regards a "contextClass" parameter at the servlet context resp. web.xml root level,
 * falling back to the default context class (XmlWebApplicationContext) if not found.
 *
 * @author Juergen Hoeller
 * @since 17.02.2003
 * @see ContextLoaderListener
 * @see XmlWebApplicationContext
 */
public class ContextLoader {

	/**
	 * Config param for the root WebApplicationContext implementation class to use.
	 */
	public static final String CONTEXT_CLASS_PARAM = "contextClass";

	public static final Class DEFAULT_CONTEXT_CLASS = XmlWebApplicationContext.class;

	private static final Logger logger = Logger.getLogger(ContextLoader.class);

	/**
	 * Initializes Spring's web application context for the given servlet context,
	 * solely regarding the servlet context init parameter.
	 * 
	 * @param servletContext  the current servlet context
	 * @return the new WebApplicationContext
	 */
	public static WebApplicationContext initContext(ServletContext servletContext) throws ApplicationContextException {
		String contextClass = servletContext.getInitParameter(CONTEXT_CLASS_PARAM);

		// Now we must load the WebApplicationContext.
		// It configures itself: all we need to do is construct the class with a no-arg
		// constructor, and invoke setServletContext.
		try {
			Class clazz = (contextClass != null ? Class.forName(contextClass) : DEFAULT_CONTEXT_CLASS);
			logger.info("Using context class '" + clazz.getName() + "'");

			if (!WebApplicationContext.class.isAssignableFrom(clazz)) {
				String msg = "Context class is no WebApplicationContext: " + contextClass;
				throw new ApplicationContextException(msg);
			}

			WebApplicationContext webApplicationContext = (WebApplicationContext) clazz.newInstance();
			webApplicationContext.setServletContext(servletContext);
			return webApplicationContext;

		} catch (ClassNotFoundException ex) {
			String msg = "Failed to load config class '" + contextClass + "'";
			handleException(msg, ex);

		} catch (InstantiationException ex) {
			String msg = "Failed to instantiate config class '" + contextClass + "': does it have a public no arg constructor?";
			handleException(msg, ex);

		} catch (IllegalAccessException ex) {
			String msg = "Failed with IllegalAccess to find or instantiate config class '" + contextClass + "': does it have a public no arg constructor?";
			handleException(msg, ex);

		} catch (Throwable t) {
			String msg = "Unexpected error loading config: " + t;
			handleException(msg, t);
		}

		return null;
	}

	private static void handleException(String msg, Throwable t) throws ApplicationContextException {
		logger.error(msg, t);
		throw new ApplicationContextException(msg, t);
	}

}
