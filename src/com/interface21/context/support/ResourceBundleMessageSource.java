package com.interface21.context.support;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.MissingResourceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * MessageSource that 
 * <br/>This class is a JavaBean, exposing a 'basename'
 * property.
 * <br/>This class relies on the caching of the underlying
 * core library ResourceBundle implementation.
 * @author Rod Johnson
 * @version $RevisionId$
 */
public class ResourceBundleMessageSource extends AbstractNestingMessageSource {

	private final Log logger = LogFactory.getLog(getClass());

	private String basename;

	/**
	 * Set the basename property. The basename follows ResourceBundle conventions.
	 * It is a fully-qualified classname. If it doesn't contain a package qualifier
	 * (such as com.interface21.mypackage), it will be resolved from the default package.
	 * Messages will normally be held in the /lib or /classes directory of a WAR.
	 * They can also be held in Jars on the classpath. For example, a Jar in an
	 * application's manifest classpath could contain messages for the application.
	 * @param basename basename, following ResourceBundle conventions
	 * @see java.util.ResourceBundle
	 */
	public void setBasename(String basename)  {
		this.basename = basename;
	}

	/**
	 * @see AbstractNestingMessageSource#resolve(String, Locale)
	 */
	protected String resolve(String code, Locale locale) throws MissingResourceException {
		ResourceBundle bundle = null;
		try {
			bundle = ResourceBundle.getBundle(this.basename, locale, Thread.currentThread().getContextClassLoader());
		} catch (MissingResourceException ex) {
			logger.warn("No ResourceBundle found for MessageSource: " + ex.getMessage());
			// assume bundle not found
			// -> do NOT throw the exception to allow for checking parent message source
			return null;
		}
		try {
			return bundle.getString(code);
		} catch (MissingResourceException ex) {
			// assume key not found
			// -> do NOT throw the exception to allow for checking parent message source
			return null;
		}
	}
	
	/**
	 * Show the state of this object.
	 */
	public String toString() {
		return getClass().getName() + ": basename='" + this.basename + "'";
	}

}
