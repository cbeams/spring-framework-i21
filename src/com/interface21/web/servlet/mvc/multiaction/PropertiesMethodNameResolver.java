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
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.interface21.beans.factory.InitializingBean;
import com.interface21.web.util.WebUtils;
import com.interface21.util.PathMatcher;

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
 * <p>Supports direct matches, e.g. a registered "/test" matches "/test",
 * and a various Ant-style pattern matches, e.g. a registered "/t*" matches
 * both "/test" and "/team". For details, see the PathMatcher class.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see com.interface21.util.PathMatcher
 */
public class PropertiesMethodNameResolver implements MethodNameResolver, InitializingBean {
	
	protected final Log logger = LogFactory.getLog(getClass());

	private boolean alwaysUseFullPath = false;

	private Properties mappings;

	/**
	 * Set if URL lookup should always use full path within current servlet
	 * context. Else, the path within the current servlet mapping is used
	 * if applicable (i.e. in the case of a ".../*" servlet mapping in web.xml).
	 * Default is false.
	 */
	public final void setAlwaysUseFullPath(boolean alwaysUseFullPath) {
		this.alwaysUseFullPath = alwaysUseFullPath;
	}

	/**
	 * Set the mapping properties for this resolver.
	 */
	public final void setMappings(Properties mappings) {
		this.mappings = mappings;
	}
	
	public void afterPropertiesSet() {
		if (this.mappings == null) {
			throw new IllegalArgumentException("'mappings' property is required");
		}
	}

	public String getHandlerMethodName(HttpServletRequest request) throws NoSuchRequestHandlingMethodException {
		String urlPath = WebUtils.getLookupPathForRequest(request, this.alwaysUseFullPath);
		String name = this.mappings.getProperty(urlPath);
		if (name == null) {
			for (Iterator it = this.mappings.keySet().iterator(); it.hasNext();) {
				String registeredPath = (String) it.next();
				if (PathMatcher.match(registeredPath, urlPath)) {
					return (String) this.mappings.get(registeredPath);
				}
			}
			throw new NoSuchRequestHandlingMethodException(request);
		}
		logger.debug("Returning MultiActionController method name '" + name + "' for lookup path '" + urlPath + "'");
		return name;
	}

}
