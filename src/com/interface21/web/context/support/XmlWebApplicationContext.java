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
 * WebApplicationContext implementation that takes configuration from
 * an XML document.
 * Used in the sample application included in 
 * <a href="http://www.amazon.com/exec/obidos/tg/detail/-/1861007841/">Expert One-On-One J2EE Design and Development</a>.
 * @author  Rod Johnson
 * @version $Revision$
 */
public class XmlWebApplicationContext 
					extends AbstractXmlApplicationContext 
					implements WebApplicationContext {

	public static final String CONFIG_URL = "configUrl";

	//---------------------------------------------------------------------
	// Instance data
	//---------------------------------------------------------------------
	/** URL from which the configuration was loaded */
	private String url;

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
	 * Create a new child WebApplicationContext
	 */
	public XmlWebApplicationContext(ApplicationContext parent, String namespace) {
		super(parent);
		this.url = namespace;
		setDisplayName("WebApplicationContext for namespace '" + namespace + "'");
	}

	//---------------------------------------------------------------------
	// Implementation of WebApplicationContext
	//---------------------------------------------------------------------
	/**
	 * Initialize and attach to the given context
	 * @param servletContext ServletContext to use to load configuration,
	 * and in which this web application context should be set as an attribute.
	 */
	public void setServletContext(ServletContext servletContext) throws ServletException {
		this.servletContext = servletContext;
		if (this.getParent() == null) {
			String configURL = servletContext.getInitParameter(CONFIG_URL);
			if (configURL == null) {
				throw new ServletException("Cannot initialize context of " + getClass() + ": missing required context param with name '" + CONFIG_URL + "'");
			}
			this.url = configURL;
		}
		else {
			this.url =  "/WEB-INF/" + url + ".xml";
		}

		refresh();
		if (this.getParent() == null) {
			// We're the root context
			WebApplicationContextUtils.configureConfigObjects(this);
			// Expose as a ServletContext object
			WebApplicationContextUtils.setAsContextAttribute(this);
		}	
	}	// setServletContext
	


	/**
	 * @see WebApplicationContext#getServletContext()
	 */
	public final ServletContext getServletContext() {
		return servletContext;
	}

	/**
	 * @return the URL where our configuration is held
	 */
	protected String getURL() {
		return url;
	}


	/**
	 * @return diagnostic information
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer( super.toString() + "; ");
		sb.append("config URL='" + url + "'; ");
		return sb.toString();
	}


	//---------------------------------------------------------------------
	// Implementation of superclass abstract methods
	//---------------------------------------------------------------------
	/**
	 * Open and return the input stream for the bean factory for this namespace. 
	 * If namespace is null, return the input stream for the default bean factory.
	 * @throw IOException if the required XML document isn't found
	 */
	protected InputStream getInputStreamForBeanFactory() throws IOException {
		String xmlFile = getURL();
		return WebUtils.getResourceInputStream(xmlFile, getServletContext());
	}

}	// class XmlWebApplicationContext
