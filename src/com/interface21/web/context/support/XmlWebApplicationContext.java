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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletContext;

import com.interface21.context.ApplicationContext;
import com.interface21.context.ApplicationContextException;
import com.interface21.ui.context.support.AbstractXmlUiApplicationContext;
import com.interface21.web.context.WebApplicationContext;

/**
 * WebApplicationContext implementation that takes configuration from
 * an XML document.
 *
 * <p>Supports various servlet context init parameters for config file
 * lookup. By default, the lookup occurs in the web app's WEB-INF
 * directory, looking for "WEB-INF/applicationContext.xml" for a root
 * context, and "WEB-INF/test-servlet.xml" for a namespaced context
 * with the name "test-servlet" (like for a DispatcherServlet instance
 * with the web.xml servlet name "test").
 *
 * <p>Interprets (file) paths as servlet context resources, i.e. as
 * paths beneath the web application root. Thus, absolute paths, i.e.
 * files outside the web app root, should be accessed via "file:" URLs.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @version $Revision$
 * @see com.interface21.web.context.ContextLoader#initContext
 * @see com.interface21.web.servlet.FrameworkServlet#getNamespace
 */
public class XmlWebApplicationContext extends AbstractXmlUiApplicationContext	implements WebApplicationContext {

	/**
	 * Name of servlet context parameter that can specify the config location prefix
	 * for namespaced contexts, falling back to DEFAULT_CONFIG_LOCATION_PREFIX.
	 */
	public static final String CONFIG_LOCATION_PREFIX_PARAM = "contextConfigLocationPrefix";

	/**
	 * Name of servlet context parameter that can specify the config location suffix
	 * for namespaced contexts, falling back to DEFAULT_CONFIG_LOCATION_SUFFIX.
	 */
	public static final String CONFIG_LOCATION_SUFFIX_PARAM = "contextConfigLocationSuffix";

	/**
	 * Name of servlet context parameter that can specify the config location
	 * for the root context, falling back to DEFAULT_CONFIG_LOCATION.
	 */
	public static final String CONFIG_LOCATION_PARAM = "contextConfigLocation";

	/** Default prefix for config locations, followed by the namespace */
	public static final String DEFAULT_CONFIG_LOCATION_PREFIX = "/WEB-INF/";

	/** Default suffix for config locations, following the namespace */
	public static final String DEFAULT_CONFIG_LOCATION_SUFFIX = ".xml";

	/** Default config location for the root context. */
	public static final String DEFAULT_CONFIG_LOCATION =
	    DEFAULT_CONFIG_LOCATION_PREFIX + "applicationContext" + DEFAULT_CONFIG_LOCATION_SUFFIX;


	/** Namespace of this context, or null if root */
	private String namespace = null;

	/** Servlet context that this context runs in */
	private ServletContext servletContext;

	/** Path from which the configuration was loaded */
	private String configLocation;


	/**
	 * Create a new root web application context, for use in an entire web application.
	 * This context will be the parent for individual servlet contexts.
	 */
	public XmlWebApplicationContext() {
		setDisplayName("Root WebApplicationContext");
	}
	
	/** 
	 * Create a new child WebApplicationContext.
	 */
	public XmlWebApplicationContext(ApplicationContext parent, String namespace) {
		super(parent);
		this.namespace = namespace;
		setDisplayName("WebApplicationContext for namespace '" + namespace + "'");
	}

	/**
	 * Return the namespace of this context, or null if root.
	 */
	public String getNamespace() {
		return this.namespace;
	}

	/**
	 * Initialize and attach to the given context.
	 * @param servletContext ServletContext to use to load configuration,
	 * and in which this web application context should be set as an attribute.
	 */
	public void setServletContext(ServletContext servletContext) throws ApplicationContextException {
		this.servletContext = servletContext;
		this.configLocation = initConfigLocation();
		logger.info("Using config location '" + this.configLocation + "'");
		refresh();
		
		if (this.namespace == null) {
			// We're the root context
			WebApplicationContextUtils.publishConfigObjects(this);
			// Expose as a ServletContext object
			WebApplicationContextUtils.publishWebApplicationContext(this);
		}	
	}

	public ServletContext getServletContext() {
		return this.servletContext;
	}

	/**
	 * Return the URL or path of the configuration.
	 */
	protected String getConfigLocation() {
		return this.configLocation;
	}

	/**
	 * Initialize the config location for the current namespace.
	 * This can be overridden in subclasses for custom config lookup.
	 * <p>Default implementation returns the namespace with the default prefix
	 * "WEB-INF/" and suffix ".xml", if a namespace is set. For the root context,
	 * the "configLocation" servlet context parameter is used, falling back to
	 * "WEB-INF/applicationContext.xml" if no parameter is found.
	 * @return the URL or path of the configuration
	 */
	private String initConfigLocation() {
		if (getNamespace() != null) {
			String configLocationPrefix = this.servletContext.getInitParameter(CONFIG_LOCATION_PREFIX_PARAM);
			String prefix = (configLocationPrefix != null) ? configLocationPrefix : DEFAULT_CONFIG_LOCATION_PREFIX;
			String configLocationSuffix = this.servletContext.getInitParameter(CONFIG_LOCATION_SUFFIX_PARAM);
			String suffix = (configLocationSuffix != null) ? configLocationSuffix : DEFAULT_CONFIG_LOCATION_SUFFIX;
			return prefix + getNamespace() + suffix;
		}
		else {
			String configLocation = this.servletContext.getInitParameter(CONFIG_LOCATION_PARAM);
			return (configLocation != null) ? configLocation : DEFAULT_CONFIG_LOCATION;
		}
	}

	/**
	 * Open and return the input stream for the bean factory for this namespace.
	 * If namespace is null, return the input stream for the default bean factory.
	 * @exception IOException if the required XML document isn't found
	 */
	protected InputStream getInputStreamForBeanFactory() throws IOException {
		InputStream in = getResourceAsStream(this.configLocation);
		if (in == null) {
			throw new FileNotFoundException("Config location not found: " + this.configLocation);
		}
		return in;
	}

	/**
	 * This implementation supports file paths beneath the root
	 * of the web application.
	 */
	protected InputStream getResourceByPath(String path) throws IOException {
		if (path.charAt(0) != '/') {
			path = "/" + path;
		}
		return getServletContext().getResourceAsStream(path);
	}

	/**
	 * This implementation returns the real path of the root directory of the
	 * web application that this WebApplicationContext is associated with.
	 * @see com.interface21.context.ApplicationContext#getResourceBasePath
	 * @see javax.servlet.ServletContext#getRealPath
	 */
	public String getResourceBasePath() {
		return getServletContext().getRealPath("/");
	}

	/**
	 * Return diagnostic information.
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer(super.toString() + "; ");
		sb.append("config path='" + this.configLocation + "'; ");
		return sb.toString();
	}

}
