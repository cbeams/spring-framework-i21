package com.interface21.web.util;

import javax.servlet.http.HttpServlet;

import com.interface21.util.Log4jConfigurer;
import com.interface21.web.util.WebUtils;

/**
 * Servlet that performs custom Log4J initialization, supporting
 * 3 init parameters:
 * <ul>
 * <li>"location": name of the Log4J config file (relative to the
 * web application root directory, e.g. "WEB-INF/log4j.properties");
 * <li>"xmlFile": boolean that indicates whether the specified file
 * is an XML file, or a properties file else;
 * <li>"refreshInterval": interval between config file refresh* checks.
 * </ul>
 *
 * <p>Note: For correct initialization order, this servlet should not be
 * used with ContextLoaderListener but rather with ContextLoaderServlet,
 * and it needs a lower load-on-startup number than the latter.
 *
 * <p>Note: Sets the web app root system property implicitly, for ${key}
 * substitutions within log file locations in the Log4J config file.
 * The default system property key is "webapp.root".
 * Example, using context-param "webAppRootKey" = "demo.root":
 * log4j.appender.myfile.File=${demo.root}/WEB-INF/demo.log
 *
 * @author Juergen Hoeller
 * @since 13.03.2003
 * @see com.interface21.util.Log4jConfigurer
 * @see WebUtils#setWebAppRootSystemProperty
 * @see WebAppRootListener
 */
public class Log4jConfigServlet extends HttpServlet {

	public void init() {
		// set the web app root system property
		WebUtils.setWebAppRootSystemProperty(getServletContext());

		// only perform custom Log4J initialization in case of a config file
		String location = getInitParameter("location");
		if (location != null) {

			// interpret location as relative to the web application root directory
			location = getServletContext().getRealPath(location);

			// XML file or properties file?
			boolean xmlFile = Boolean.valueOf(getInitParameter("xmlFile")).booleanValue();

			// use default refresh interval if not specified
			long refreshInterval = Log4jConfigurer.DEFAULT_REFRESH_INTERVAL;
			String intervalString = getInitParameter("refreshInterval");
			if (intervalString != null) {
				refreshInterval = Long.parseLong(intervalString);
			}

			// write log message to server log
			getServletContext().log("Initializing Log4J from " + location);

			// perform actual Log4J initialization
			Log4jConfigurer.initLogging(location, xmlFile, refreshInterval);
		}
	}

}
