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
import java.io.InputStream;
import java.util.Iterator;
import java.util.Properties;

import javax.servlet.ServletException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.app.Velocity;

import com.interface21.context.ApplicationContextException;
import com.interface21.context.support.ApplicationObjectSupport;
import com.interface21.util.StringUtils;
import com.interface21.web.util.WebUtils;

/**
 * JavaBean to configure Velocity, by setting the configLocation of the Velocity
 * properties file. This bean must be included in the application context of any
 * application using Velocity with the Spring Framework.
 *
 * <p>The optional "configLocation" property sets the configLocation within the WAR
 * of the Velocity properties file. By default it will be sought in the WEB-INF
 * directory, with the name "velocity.properties".
 *
 * <p>Velocity properties can be overridden via "velocityProperties", or even
 * completely specified locally, avoiding the need for an external properties file.
 *
 * <p>When using Velocity's FileResourceLoader, the "webAppRootMarker" mechanism can
 * be used to refer to the web app resource base within a Velocity property value.
 * By default, the marker "${webapp.root}" gets replaced with the web app root
 * directory. Note that this will only work with expanded WAR files.
 *
 * <p>This bean exists purely to configure Velocity. It exposes no methods other than
 * initialization methods, and is not meant to be referenced by application components.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see VelocityView
 */
public class VelocityConfigurer extends ApplicationObjectSupport {

	public static final String DEFAULT_CONFIG_LOCATION = "WEB-INF/velocity.properties";

	public static final String DEFAULT_WEB_APP_ROOT_MARKER = "${" + WebUtils.DEFAULT_WEB_APP_ROOT_KEY + "}";

	protected final Log logger = LogFactory.getLog(getClass());

	private String configLocation;

	private Properties velocityProperties;

	private String webAppRootMarker = DEFAULT_WEB_APP_ROOT_MARKER;

	private boolean overrideLogging = true;

	/**
	 * Set location of the Velocity config file. Default value is
	 * "WEB-INF/velocity.properties", which will be applied
	 * <i>only</i> if "velocityProperties" is not set.
	 * @see #setVelocityProperties
	 */
	public void setConfigLocation(String configLocation) {
		this.configLocation = configLocation;
	}

	/**
	 * Set Velocity properties, like "resource loader".
	 * <p>Can be used to override values in a Velocity config file,
	 * or to specify all necessary properties locally.
	 * @see #setConfigLocation
	 */
	public void setVelocityProperties(Properties velocityProperties) {
		this.velocityProperties = velocityProperties;
	}

	/**
	 * If Velocity should log via Commons Logging, i.e. if logging
	 * should be overridden with CommonsLoggingLogSystem.
	 * Default value is true.
	 */
	public void setOverrideLogging(boolean overrideLogging) {
		this.overrideLogging = overrideLogging;
	}

	/**
	 * Set the marker that gets replaced with the resource base,
	 * i.e. root directory of the web application.
	 * Default value is "${webapp.root}".
	 */
	public void setWebAppRootMarker(String webAppRootMarker) {
		this.webAppRootMarker = webAppRootMarker;
	}

	/**
	 * Initializes the Velocity runtime.
	 */
	protected void initApplicationContext() throws ApplicationContextException {
		try {
			Properties prop = new Properties();
			// try default config location as fallback
			String actualLocation = this.configLocation;
			if (this.configLocation == null && this.velocityProperties == null) {
				actualLocation = DEFAULT_CONFIG_LOCATION;
			}
			// load config file if set
			if (actualLocation != null) {
				logger.info("Loading Velocity config from [" + actualLocation + "]");
				InputStream is = getApplicationContext().getResourceAsStream(actualLocation);
				prop.load(is);
			}
			// merge local properties if set
			if (this.velocityProperties != null) {
				prop.putAll(this.velocityProperties);
			}
			// determine the root directory of the web app
			String resourceBase = getApplicationContext().getResourceBasePath();
			if (resourceBase == null) {
				logger.warn("Cannot replace marker [" + this.webAppRootMarker + "] with resource base because the WAR file is not expanded");
			}
			// set properties
			for (Iterator it = prop.keySet().iterator(); it.hasNext();) {
				String key = (String) it.next();
				String value = prop.getProperty(key);
				if (resourceBase != null) {
					value = StringUtils.replace(value, this.webAppRootMarker, resourceBase);
				}
				Velocity.setProperty(key, value);
			}
			// log via Commons Logging?
			if (this.overrideLogging) {
				Velocity.setProperty(Velocity.RUNTIME_LOG_LOGSYSTEM, new CommonsLoggingLogSystem());
			}
			// perform initialization
			Velocity.init();
		}
		catch (ServletException ex) {
			throw new ApplicationContextException("Error loading Velocity config from [" + this.configLocation + "]", ex);
		}
		catch (IOException ex) {
			throw new ApplicationContextException("Error loading Velocity config from [" + this.configLocation + "]", ex);
		}
		catch (Exception ex) {
			throw new ApplicationContextException(
				"Error initializing Velocity from properties file [" + this.configLocation + "] (loaded OK)",
				ex);
		}
	}

}
