package com.interface21.web.util;

import java.io.FileNotFoundException;

import javax.servlet.ServletContext;

import com.interface21.util.Log4jConfigurer;

/**
 * Convenience class that performs custom Log4J initialization for web environments,
 * supporting 2 init parameters at the servlet context level (i.e. context-param
 * in web.xml):
 * <ul>
 * <li>"log4jLocation": name of the Log4J config file (relative to the web application
 * root directory, e.g. "WEB-INF/log4j.properties");
 * <li>"log4jRefreshInterval": interval between config file refresh* checks.
 * </ul>
 *
 * <p>Note: initLogging should be called before any other Spring activity (when using
 * Log4J), to guarantee proper initialization before any Spring logging attempts.
 *
 * <p>Note: Sets the web app root system property implicitly, for ${key} substitutions
 * within log file locations in the Log4J config file. The default system property
 * key is "webapp.root". Example, using context-param "webAppRootKey" = "demo.root":
 * log4j.appender.myfile.File=${demo.root}/WEB-INF/demo.log
 *
 * <p>WARNING: Some containers like Tomcat do NOT keep system properties separate
 * per web app. You have to use unique "webAppRootKey" context-params per web app
 * then, to avoid clashes. Other containers like Resin do isolate each web app's
 * system properties: Here you can use the default key (i.e. no "webAppRootKey"
 * context-param at all) without worrying.
 *
 * @author Juergen Hoeller
 * @since 12.08.2003
 * @see com.interface21.util.Log4jConfigurer
 */
public abstract class Log4jWebConfigurer {

	/**
	 * Parameter specifying the location of the Log4J config file.
	 */
	public static final String CONFIG_LOCATION_PARAM = "log4jConfigLocation";

	/**
	 * Parameter specifying the refresh interval for checking the Log4J config file.
	 */
	public static final String REFRESH_INTERVAL_PARAM = "log4jRefreshInterval";

	public static void initLogging(ServletContext servletContext) {
		// set the web app root system property
		WebUtils.setWebAppRootSystemProperty(servletContext);

		// only perform custom Log4J initialization in case of a config file
		String location = servletContext.getInitParameter(CONFIG_LOCATION_PARAM);
		if (location != null) {

			// interpret location as relative to the web application root directory
			if (location.charAt(0) != '/') {
				location = "/" + location;
			}
			location = servletContext.getRealPath(location);

			// use default refresh interval if not specified
			long refreshInterval = Log4jConfigurer.DEFAULT_REFRESH_INTERVAL;
			String intervalString = servletContext.getInitParameter(REFRESH_INTERVAL_PARAM);
			if (intervalString != null) {
				refreshInterval = Long.parseLong(intervalString);
			}

			// write log message to server log
			servletContext.log("Initializing Log4J from " + location);

			// perform actual Log4J initialization
			try {
				Log4jConfigurer.initLogging(location, refreshInterval);
			}
			catch (FileNotFoundException ex) {
				throw new IllegalArgumentException("Invalid log4jConfigLocation parameter: " + ex.getMessage());
			}
		}
	}

	/**
	 * Shutdown Log4J to release all file locks.
	 */
	public static void shutdownLogging(ServletContext servletContext) {
		servletContext.log("Terminating Log4J");
		Log4jConfigurer.shutdownLogging();
	}

}
