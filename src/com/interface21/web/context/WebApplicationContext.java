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

package com.interface21.web.context;

import javax.servlet.ServletContext;

import com.interface21.context.ApplicationContext;
import com.interface21.context.ApplicationContextException;
import com.interface21.ui.context.*;

/** 
 * Interface to provide configuration for a web application. This is read-only while
 * the application is running, but may be reloaded if the implementation supports this.
 *
 * <p>This interface adds ServletContext methods to the generic ApplicationContext
 * interface, and defines a well-known application attribute name that the root
 * context must be bound to in the bootstrap process.
 *
 * <p>Like generic application contexts, web application contexts are hierarchical.
 * There is a single root context per application, while each servlet in the application
 * (including controller servlets in the MVC framework) has its own child context.
 *
 * @author Rod Johnson
 * @since January 19, 2001
 * @version $Revision$
 */
public interface WebApplicationContext extends ApplicationContext, ThemeSource {

	/** 
	 * Context attribute to bind root WebApplicationContext to on successful startup.
	 */
	String WEB_APPLICATION_CONTEXT_ATTRIBUTE_NAME = WebApplicationContext.class + ".ROOT";
	
	/** 
	 * Give this context access to the standard Servlet API ServletContext for this
	 * web application. This method amounts to an init method: implementations are
	 * responsible for loading their URL or other config, and reloading.
	 */
	void setServletContext(ServletContext servletContext) throws ApplicationContextException;
	
	/** 
	 * Return the standard Servlet API ServletContext for this application.
	 */
	ServletContext getServletContext();
	
}
