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

package com.interface21.web.context.support;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.apache.log4j.Logger;

import com.interface21.beans.BeansException;
import com.interface21.context.ApplicationContext;
import com.interface21.context.ApplicationContextAware;
import com.interface21.context.ApplicationContextException;
import com.interface21.web.context.WebApplicationContext;

/**
 * Utilities common to all WebApplicationContext implementations
 * @author Rod Johnson
 * @version $Id$
 */
public abstract class WebApplicationContextUtils {

	/** Config object prefix in bean names */
	public static final String CONFIG_OBJECT_PREFIX = "config.";

	/** Logging category for this class
	 */
	protected static Logger logger = Logger.getLogger(WebApplicationContextUtils.class.getName());


	/**
	 * Find the root WebApplicationContext for this web app
	 * @param sc ServletContext of web application to find application ontext for
	 * @return the WebApplicationContext for this web app
	 * @throws ServletException if the context object can't be found
	 */
	public static WebApplicationContext getWebApplicationContext(ServletContext sc) throws ServletException {
		return (WebApplicationContext) sc.getAttribute(WebApplicationContext.WEB_APPLICATION_CONTEXT_ATTRIBUTE_NAME);
	}
	

	/**
	 * Retrieve a config object by name. This will be sought in the
	 * ServletContext, where it must have been placed by config.
	 * Can only be called after the ServletContext is available. This means
	 * it can't be called in a subclass constructor.
	 * @param name name of the config object
	 * @param requiredClass type of the config object
	 * @throws ServletException if the object isn't found, or isn't
	 * of the required type.
	 */
	public static Object getConfigObject(ServletContext sc, String name, Class requiredClass, boolean allowNull)
		throws ServletException {
		Object o = sc.getAttribute("applicationConfig." + name);
		if (o == null) {
			String mesg = "Cannot retrieve config object with name '" + name + "'";
			if (allowNull) {
				logger.info(mesg);
				return null;
			}
			logger.error(mesg);
			throw new ServletException(mesg);
		}
		if (!requiredClass.isAssignableFrom(o.getClass())) {
			String mesg = "Config object with name '" + name + "' isn't of required type " + requiredClass.getName();
			logger.error(mesg);
			throw new ServletException(mesg);
		}
		return o;
	}
	
	
	/**
	 * Retrieve a config object by name, treating a null value as an error.
	 */
	public static Object getConfigObject(ServletContext sc, String name, Class requiredClass) throws ServletException {
		return getConfigObject(sc, name, requiredClass, false);
	}

	/** 
	 * Initialize all config objects if necessary, and publish them
	 * as ServletContext attributes
	 */
	public static void configureConfigObjects(WebApplicationContext wac) throws ApplicationContextException {
		logger.info("Configuring config objects");

		String[] beanNames = wac.getBeanDefinitionNames();

		for (int i = 0; i < beanNames.length; i++) {
			String name = beanNames[i];
			if (name.startsWith(CONFIG_OBJECT_PREFIX)) {
				// Strip prefix
				String strippedName = name.substring(CONFIG_OBJECT_PREFIX.length());
				// For each object, check if it implements the ConfigurableWebApplicationObject interface.
				// If it does, give it a reference to this object
				try {
					Object configObject = wac.getBean(name);
					configureManagedObject(configObject, wac);
					//*CONFIG_OBJECT_BASE +
					wac.getServletContext().setAttribute(strippedName, configObject);
					logger.info(
						"Config object with name ["
							+ name
							+ "] and class ["
							+ configObject.getClass().getName()
							+ "] initialized and added to ServletConfig");
				}
				catch (BeansException ex) {
					throw new ApplicationContextException("Couldn't load config object with name '" + name + "': " + ex, ex);
				}
			}
		}

	} 	// configureConfigObjects

	/** 
	 * Expose the given WebApplciationContext as an attribute of the ServletContext
	 * it references
	 */
	public static void setAsContextAttribute(WebApplicationContext wac) {
		// Set WebApplicationContext as an attribute in the ServletContext so
		// other components in this web application can access it
		ServletContext sc = wac.getServletContext();
		if (sc == null)
			throw new IllegalArgumentException("ServletContext can't be null in WebApplicationContext " + wac);

		sc.setAttribute(WebApplicationContext.WEB_APPLICATION_CONTEXT_ATTRIBUTE_NAME, wac);
		logger.info(
			"Loader initialized on server name "
				+ wac.getServletContext().getServerInfo()
				+ "; WebApplicationContext object is available in ServletContext with name '"
				+ WebApplicationContext.WEB_APPLICATION_CONTEXT_ATTRIBUTE_NAME
				+ "'");
	}

	/**
	 * If the given object implements ApplicationContextAware, invoke its
	 * setApplicationContextMethod.
	 */
	public static void configureManagedObject(Object o, ApplicationContext ac) throws ApplicationContextException {
		if (o instanceof ApplicationContextAware) {
			ApplicationContextAware aca = (ApplicationContextAware) o;
			if (aca.getApplicationContext() == null) {
				aca.setApplicationContext(ac);
			}
		}
	}

}