package com.interface21.util;

import java.io.File;
import java.io.FileNotFoundException;

import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.helpers.FileWatchdog;
import org.apache.log4j.xml.DOMConfigurator;

/**
 * Convenience class that features simple methods for custom Log4J configuration.
 *
 * @author Juergen Hoeller
 * @since 13.03.2003
 * @see com.interface21.web.util.Log4jConfigListener
 */
public abstract class Log4jConfigurer {

	public static final long DEFAULT_REFRESH_INTERVAL = FileWatchdog.DEFAULT_DELAY;

	public static final String XML_FILE_EXTENSION = ".xml";

	/**
	 * Initialize Log4J with the given configuration.
	 * Assumes an XML file in case of a ".xml" file extension.
	 * @param location location of the config file
	 * @param refreshInterval interval between config file refresh checks
	 * @throws FileNotFoundException if the location specifies an invalid file path
	 */
	public static void initLogging(String location, long refreshInterval) throws FileNotFoundException {
		if (!(new File(location)).exists()) {
			throw new FileNotFoundException("Log4j config file [" + location + "] not found");
		}
		if (location.toLowerCase().endsWith(XML_FILE_EXTENSION)) {
			DOMConfigurator.configureAndWatch(location, refreshInterval);
		} else {
			PropertyConfigurator.configureAndWatch(location, refreshInterval);
		}
	}

	/**
	 * Initialize Log4J with the given configuration and the default refresh interval.
	 * @param location location of the property config file
	 * @throws FileNotFoundException if the location specifies an invalid file path
	 */
	public static void initLogging(String location) throws FileNotFoundException {
		initLogging(location, DEFAULT_REFRESH_INTERVAL);
	}

	/**
	 * Set the specified system property to the current working directory.
	 * This can be used e.g. for test environments, for applications that leverage
	 * Log4jConfigListener's "webAppRootKey" support in a web environment.
	 * @param key system property key to use
	 * @see com.interface21.web.util.Log4jConfigListener
	 */
	public static void setWorkingDirSystemProperty(String key) {
		System.setProperty(key, new File("").getAbsolutePath());
	}

}
