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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.interface21.beans.factory.InitializingBean;
import com.interface21.web.util.WebUtils;

/**
 * The most sophisticated and useful framework implementation of 
 * the MethodNameResolver interface. Uses java.util.Properties
 * defining the mapping between the URL of incoming requests and
 * method name. Such properties can be held in an XML document.
 *
 * <p>Properties format is
 * <code>
 * /welcome.html=displayGenresPage
 * </code>
 * Note that method overloading isn't allowed, so there's no
 * need to specify arguments.
 *
 * @author Rod Johnson
 */
public class PropertiesMethodNameResolver implements MethodNameResolver, InitializingBean {
	
	protected final Log logger = LogFactory.getLog(getClass());

	private boolean alwaysUseFullPath = false;

	/** Properties defining the mappings */
	private Properties mappings;

	/**
	 * Create a new PropertiesMethodNameResolver. The mappings
	 * property must be set before use.
	 */
	public PropertiesMethodNameResolver() {
	}
	
	/**
	 * Create a new PropertiesMethodNameResolver, fully configuring this
	 * class by passing in properties.
	 * @param mappings property mapping
	 */
	public PropertiesMethodNameResolver(Properties mappings) {
		this.mappings = mappings;
		afterPropertiesSet();
	}

	/**
	 * Set if URL lookup should always use full path within current servlet
	 * context. Else, the path within the current servlet mapping is used
	 * if applicable (i.e. in the case of a ".../*" servlet mapping in web.xml).
	 * Default is false.
	 */
	public void setAlwaysUseFullPath(boolean alwaysUseFullPath) {
		this.alwaysUseFullPath = alwaysUseFullPath;
	}

	/**
	 * Set the mapping properties configuring this class.
	 */
	public void setMappings(Properties mappings) {
		this.mappings = mappings;
	}
	
	public void afterPropertiesSet() {
		if (this.mappings == null) {
			throw new IllegalArgumentException("'mappings' property is required");
		}
	}

	public String getHandlerMethodName(HttpServletRequest request) throws NoSuchRequestHandlingMethodException {
		String lookupPath = WebUtils.getLookupPathForRequest(request, this.alwaysUseFullPath);
		String name = this.mappings.getProperty(lookupPath);
		if (name == null) {
			throw new NoSuchRequestHandlingMethodException(request);
		}
		logger.debug("Returning MultiActionController method name '" + name + "' for lookup path '" + lookupPath + "'");
		return name;
	}

}
