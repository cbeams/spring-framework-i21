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

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import com.interface21.context.ApplicationContext;
import com.interface21.context.support.AbstractXmlApplicationContext;
import com.interface21.web.context.WebApplicationContext;
import com.interface21.web.util.WebUtils;


/**
 * WebApplicationContext implementation that takes configuration from an XML document.
 * Used in the sample application included in 
 * <a href="http://www.amazon.com/exec/obidos/tg/detail/-/1861007841/">Expert One-On-One J2EE Design and Development</a>.
 * @author  Rod Johnson
 * @version $Revision$
 */
public class XmlWebApplicationContext extends AbstractXmlApplicationContext	implements WebApplicationContext {

	public static final String CONFIG_LOCATION_PARAM = "configLocation";

	public static final String DEFAULT_CONFIG_LOCATION = "/WEB-INF/applicationContext.xml";

	//---------------------------------------------------------------------
	// Instance data
	//---------------------------------------------------------------------
	/** namespace of this context, or null if root */
	private String namespace = null;

	/** URL from which the configuration was loaded */
	private String configLocation;

	private ServletContext servletContext;

	//---------------------------------------------------------------------
	// Constructors
	//---------------------------------------------------------------------
	/**
	 * Create a new root web application context, for use in an entire
	 * web application. This context will be the parent for individual
	 * servlet contexts.
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
		this.configLocation = "/WEB-INF/" + namespace + ".xml";
		setDisplayName("WebApplicationContext for namespace '" + namespace + "'");
	}

	//---------------------------------------------------------------------
	// Implementation of WebApplicationContext
	//---------------------------------------------------------------------
	/**
	 * Initialize and attach to the given context.
	 * @param servletContext ServletContext to use to load configuration,
	 * and in which this web application context should be set as an attribute.
	 */
	public void setServletContext(ServletContext servletContext) throws ServletException {
		this.servletContext = servletContext;

		if (this.namespace == null) {
			String configLocation = servletContext.getInitParameter(CONFIG_LOCATION_PARAM);
			if (configLocation == null)
				configLocation = DEFAULT_CONFIG_LOCATION;
			this.configLocation = configLocation;
		}
		logger.info("Using config location '" + this.configLocation + "'");

		refresh();

		if (this.namespace == null) {
			// We're the root context
			WebApplicationContextUtils.configureConfigObjects(this);
			// Expose as a ServletContext object
			WebApplicationContextUtils.setAsContextAttribute(this);
		}	
	}	// setServletContext
	
	public final ServletContext getServletContext() {
		return servletContext;
	}

	/**
	 * @return the namespace of this context, or null if root
	 */
	public String getNamespace() {
		return namespace;
	}

	/**
	 * @return the URL where our configuration is held
	 */
	protected String getConfigLocation() {
		return configLocation;
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
	 * This implementation supports fully qualified URLs, absolute file paths,
	 * and relative file paths beneath the root of the web application.
	 * @see com.interface21.context.ApplicationContext#getResourceAsStream
	 */
	public InputStream getResourceAsStream(String location) throws IOException {
		return WebUtils.getResourceInputStream(location, getServletContext());
	}

	/**
	 * @return diagnostic information
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer(super.toString() + "; ");
		sb.append("config path='" + configLocation + "'; ");
		return sb.toString();
	}

	//---------------------------------------------------------------------
	// Implementation of superclass abstract methods
	//---------------------------------------------------------------------
	/**
	 * Open and return the input stream for the bean factory for this namespace. 
	 * If namespace is null, return the input stream for the default bean factory.
	 * @exception IOException if the required XML document isn't found
	 */
	protected InputStream getInputStreamForBeanFactory() throws IOException {
		return getResourceAsStream(this.configLocation);
	}

}	// class XmlWebApplicationContext
