package com.interface21.util;

import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.helpers.FileWatchdog;
import org.apache.log4j.xml.DOMConfigurator;

/**
 * Convenience class that features simple methods for
 * custom Log4J configuration.
 *
 * @author Juergen Hoeller
 * @since 13.03.2003
 * @see com.interface21.web.util.Log4jConfigServlet
 */
public abstract class Log4jConfigurer {

	public static final long DEFAULT_REFRESH_INTERVAL = FileWatchdog.DEFAULT_DELAY;

	/**
	 * Initializes Log4J with the given configuration.
	 * @param location  the location to the config file
	 * @param xmlFile  if the config file is an XML file
	 * (else a property file)
	 * @param refreshInterval  the interval between
	 * config file refresh checks
	 */
	public static void initLogging(String location, boolean xmlFile, long refreshInterval) {
		if (xmlFile) {
			DOMConfigurator.configureAndWatch(location, refreshInterval);
		} else {
			PropertyConfigurator.configureAndWatch(location, refreshInterval);
		}
	}

	/**
	 * Initializes Log4J with the given configuration and the
	 * default refresh interval.
	 * @param location  the location to the config file
	 * @param xmlFile  if the config file is an XML file
	 * (else a property file)
	 */
	public static void initLogging(String location, boolean xmlFile) {
		initLogging(location, xmlFile, DEFAULT_REFRESH_INTERVAL);
	}

	/**
	 * Initializes Log4J with the given configuration and the
	 * default refresh interval (assuming a properties file).
	 * @param location  the location to the property config file
	 */
	public static void initLogging(String location) {
		initLogging(location, false, DEFAULT_REFRESH_INTERVAL);
	}
}
