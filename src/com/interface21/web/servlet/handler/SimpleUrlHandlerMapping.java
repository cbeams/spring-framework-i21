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

import java.util.Iterator;
import java.util.Properties;

/**
 * Implementation of the HandlerMapping interface to map from URLs
 * to request handler beans.
 *
 * <p>Mappings are set via the "mappings" property, in a form accepted
 * by the java.util.Properties class, like as follows:<br>
 * <code>
 * /welcome.html=ticketController
 * /show.html=ticketController
 * </code><br>
 * The syntax is PATH=HANDLER_BEAN_NAME.
 * If the path doesn't begin with a /, one is prepended.
 *
 * <p>Supports direct matches (given "/test" -> registered "/test")
 * and "*" matches (given "/test" -> registered "/t*").
 *
 * @see com.interface21.web.servlet.DispatcherServlet
 * @author Rod Johnson
 * @author Juergen Hoeller
 */
public class SimpleUrlHandlerMapping extends AbstractUrlHandlerMapping {
	
	private Properties mappings;
	
	/**
	 * Set mappings from a properties object. This property must
	 * be set to configure this object.
	 * @param mappings properties
	 * @see java.util.Properties
	 */
	public void setMappings(Properties mappings) {
		this.mappings = mappings;
		logger.debug("Set properties to [" + mappings + "]");
	}

	public void initApplicationContext() {
		if (!this.mappings.isEmpty()) {
			Iterator itr = mappings.keySet().iterator();
			while (itr.hasNext()) {
				String url = (String) itr.next();
				String beanName = this.mappings.getProperty(url);
				logger.info("Controller mapping from URL '" + url + "' to '" + beanName + "'");
				if ("*".equals(url)) {
					setDefaultHandler(initHandler(beanName, url));
				}
				else {
					// Prepend with / if it's not present
					if (!url.startsWith("/"))
						url = "/" + url;					
					Object handler = initHandler(beanName, url);
					registerHandler(url, handler);
				}
			}
		}
		else {
			logger.warn("No mappings in " + getClass().getName());
		}
	}

}
