package com.interface21.web.context;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.interface21.context.ApplicationContextException;
import com.interface21.web.context.support.XmlWebApplicationContext;

/**
 * Performs the actual initialization work for the root application context.
 * Called by ContextLoaderListener.
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

	private static final Log logger = LogFactory.getLog(ContextLoader.class);

	/**
	 * Initializes Spring's web application context for the given servlet context,
	 * solely regarding the servlet context init parameter.
	 * @param servletContext current servlet context
	 * @return the new WebApplicationContext
	 */
	public static WebApplicationContext initContext(ServletContext servletContext) throws ApplicationContextException {
		String contextClass = servletContext.getInitParameter(CONTEXT_CLASS_PARAM);

		// Now we must load the WebApplicationContext.
		// It configures itself: all we need to do is construct the class with a no-arg
		// constructor, and invoke setServletContext.
		try {
			Class clazz = (contextClass != null ? Class.forName(contextClass) : DEFAULT_CONTEXT_CLASS);
			logger.info("Loading root WebApplicationContext: using context class '" + clazz.getName() + "'");

			if (!WebApplicationContext.class.isAssignableFrom(clazz)) {
				String msg = "Context class is no WebApplicationContext: " + contextClass;
				throw new ApplicationContextException(msg);
			}

			WebApplicationContext webApplicationContext = (WebApplicationContext) clazz.newInstance();
			webApplicationContext.setServletContext(servletContext);
			return webApplicationContext;

		} catch (ApplicationContextException ex) {
			throw ex;

		} catch (ClassNotFoundException ex) {
			handleException("Failed to load config class '" + contextClass + "'", ex);

		} catch (InstantiationException ex) {
			handleException("Failed to instantiate config class '" + contextClass + "': does it have a public no arg constructor?", ex);

		} catch (IllegalAccessException ex) {
			handleException("Illegal access while finding or instantiating config class '" + contextClass + "': does it have a public no arg constructor?", ex);

		} catch (RuntimeException ex) {
			handleException("Unexpected error loading config: " + ex, ex);
		}

		return null;
	}

	private static void handleException(String msg, Exception ex) throws ApplicationContextException {
		logger.error(msg, ex);
		throw new ApplicationContextException(msg, ex);
	}

}
