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

package com.interface21.web.servlet.handler;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.interface21.context.ApplicationContextException;

/**
 * Implementation of the HandlerMapping interface to map from URLs
 * to request handler beans.
 * <br>Objects of this class are JavaBeans.
 * <br/>Mappings are in a form accepted by the java.util.Properties class, as follows:
 * <code>
 * /welcome.html=ticketController
 * /show.html=ticketController
 * </code>
 * The syntax is<br>
 * PATH=HANDLER_BEAN_NAME
 * <br>If the path doesn't begin with a /, one is prepended.
 * <br>Mappings are set via the "mappings" property.
 * @see com.interface21.web.servlet.ControllerServlet
 * @author Rod Johnson, Juergen Hoeller
 */
public class SimpleUrlHandlerMapping extends AbstractUrlHandlerMapping {
	
	private Properties mappingProperties;
	
	private Map handlerMap;

	/** Default handler. May be null. */
	private Object defaultHandler;

	/**
	 * Set mappings from a properties object. This property must
	 * be set to configure this object.
	 * @param mappingProperties properties
	 * @see java.util.Properties
	 */
	public void setMappings(Properties mappingProperties) {
		logger.info("Set properties to [" + mappingProperties + "]");
		this.mappingProperties = mappingProperties;
	}

	public void initHandlerMapping() {
		this.handlerMap = new HashMap();
		
		if (!this.mappingProperties.isEmpty()) {
			Iterator itr = mappingProperties.keySet().iterator();
			while (itr.hasNext()) {
				String url = (String) itr.next();
				String beanName = this.mappingProperties.getProperty(url);
				logger.info("Controller mapping from URL '" + url + "' to '" +  beanName + "'");
				if ("*".equals(url)) {
					this.defaultHandler = initHandler(beanName, url);
					logger.warn("Default mapping is to controller [" + this.defaultHandler + "]");
				}
				else {
					// Prepend with / if it's not present
					if (!url.startsWith("/"))
						url = "/" + url;					
					initHandler(beanName, url);
				}
			}
		}
		else {
			logger.warn("No mappings in " + getClass().getName());
		}
	}	// init
	
	public Object getHandler(HttpServletRequest request) {
		Object handler = handlerMap.get(request.getServletPath());
		if (handler == null)
			handler = this.defaultHandler;
		return handler;
	}

	protected void registerHandler(Object handler, String url) {
		handlerMap.put(url, handler);
		logger.info("Mapped url [" + url + "] onto handler [" + handler + "]");
	}
	
}	// class SimpleUrlHandlerMapping
