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
 
package com.interface21.web.servlet.view.velocity;

import java.io.IOException;
import java.util.Properties;

import javax.servlet.ServletException;

import org.apache.log4j.Logger;
import org.apache.velocity.app.Velocity;

import com.interface21.context.ApplicationContextException;
import com.interface21.web.context.support.WebApplicationObjectSupport;
import com.interface21.web.util.WebUtils;

/**
 * JavaBean to configure Velocity, by setting the location of the Velocity properties
 * file. This bean must be included in the application context of any application using
 * Velocity with the Interface21 framework.
 * <br>The optional path property sets the location within the WAR of the Velocity properties file.
 * By default it will be sought in the /WEB-INF directory, with the name velocity.properties.
 * <br>This bean exists purely to configure Velocity. It exposes no methods other than
 * initialization methods, and is not meant to be referenced by application components.
 * @author Rod Johnson
 */
public class VelocityConfigurer extends WebApplicationObjectSupport {

	//---------------------------------------------------------------------
	// Instance data
	//---------------------------------------------------------------------
	/**
	* Create a logging category that is available
	* to subclasses. 
	*/
	protected final Logger logger = Logger.getLogger(getClass().getName());

	public static final String DEFAULT_VELOCITY_PROPERTIES = "/WEB-INF/velocity.properties";

	private String path = DEFAULT_VELOCITY_PROPERTIES;

	/**
	 * Optionally override location of the Velocity properties file.
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 *  Initializes the Velocity runtime, first calling 
	 *  loadConfiguration(ServletConfig) to get a
	 *  java.util.Properties of configuration information
	 *  and then calling Velocity.init().  Override this
	 *  to do anything to the environment before the 
	 *  initialization of the singelton takes place, or to 
	 *  initialize the singleton in other ways.
	 */
	protected void init() throws ApplicationContextException {
		try {
			Properties p = new Properties();
			logger.info("Loading Velocity properties from [" + this.path + "]");
			p.load(getWebApplicationContext().getResourceAsStream(this.path));

			Velocity.init(p);
		}
		catch (ServletException e) {
			throw new ApplicationContextException("Error loading Velocity config from [" + this.path + "]", e);
		}
		catch (IOException e) {
			throw new ApplicationContextException("Error loading Velocity config from [" + this.path + "]", e);
		}
		catch (Exception e) {
			throw new ApplicationContextException(
				"Error initializing Velocity from properties file (loaded OK) @[" + this.path + "]",
				e);
		}
	}

}