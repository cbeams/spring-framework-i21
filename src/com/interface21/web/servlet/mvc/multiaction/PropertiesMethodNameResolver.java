/*
 * Generic framework code included with 
 * <a href="http://www.amazon.com/exec/obidos/tg/detail/-/1861007841/">Expert One-On-One J2EE Design and Development</a>
 * by Rod Johnson (Wrox, 2002). 
 * This code is free to use and modify. However, please
 * acknowledge the source and include the above URL in each
 * class using or derived from this code. 
 * Please contact <a href="mailto:rod.johnson@interface21.com">rod.johnson@interface21.com</a>
 * for commercial support.
 */
 
package com.interface21.web.servlet.mvc.multiaction;

import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.interface21.beans.factory.InitializingBean;


/**
 * The most sophisticated and useful framework implementation of 
 * the MethodNameResolver interface. Uses java.util.Properties
 * defining the mapping between the URL of incoming requests and
 * method name. Such properties can be held in an XML document.
 * <br>Properties format is
 * <code>
 * /welcome.html=displayGenresPage
 * </code>
 * Note that method overloading isn't allowed, so there's no
 * need to specify arguments.
 * @author Rod Johnson
 */
public class PropertiesMethodNameResolver implements MethodNameResolver, InitializingBean {
	
	//---------------------------------------------------------------------
	// Instance data
	//---------------------------------------------------------------------
	/**
	* Create a logging category that is available
	* to subclasses. 
	*/
	protected final Logger logger = Logger.getLogger(getClass().getName());
	
	/** Properties defining the mapping */
	private Properties props;
	
	
	//---------------------------------------------------------------------
	// Constructors
	//---------------------------------------------------------------------
	/**
	 * Create a new PropertiesMethodNameResolver. The mappings
	 * property must be set before use.
	 */
	public PropertiesMethodNameResolver() {
	}
	
	/**
	 * Create a new PropertiesMethodNameResolver, fully configuring this
	 * class by passing in propertiers
	 * @param props property mapping
	 */
	public PropertiesMethodNameResolver(Properties props) {
		setMappings(props);
	}
	
	
	//---------------------------------------------------------------------
	// Bean properties and initializer
	//---------------------------------------------------------------------
	/**
	 * Set the mappings configuring this class
	 * @param props mappings configuring this class
	 */
	public void setMappings(Properties props) {
		this.props = props;
		// WHAT ABOUT /!?
	}
	
	/**
	 * @see InitializingBean#afterPropertiesSet()
	 */
	public void afterPropertiesSet() throws Exception {
		if (props == null)
			throw new Exception("Must set 'mappings' property on PropertiesMethodNameResolver");
	}
	
	
	//---------------------------------------------------------------------
	// Implementation of MethodNameResolver
	//---------------------------------------------------------------------
	/**
	 * @see MethodNameResolver#getHandlerMethodName(HttpServletRequest)
	 */
	public String getHandlerMethodName(HttpServletRequest request) throws NoSuchRequestHandlingMethodException {
		String servletPath = request.getServletPath();
		// forward slash prepend?
		String name = props.getProperty(servletPath);
		if (name == null)
			throw new NoSuchRequestHandlingMethodException(request);
			
		if (logger.isDebugEnabled())
			logger.debug("Returning MultiActionController method name '" + name + "' for servlet path '" + servletPath + "'");
		return name;
	}

}