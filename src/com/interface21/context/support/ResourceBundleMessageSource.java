package com.interface21.context.support;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.MissingResourceException;

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

	/** Holder for basename bean property. 
	 */
	private String basename;
	

	/**
	 * Set the basename property. The basename
	 * follows java.util.ResourceBundle conventions.
	 * It is a fully-qualified classname. If it doesn't
	 * contain a package qualifier (such as com.interface21.mypackage)
	 * it will be resolved from the default package. 
	 * Messages will normally be held in the /lib or /classes
	 * directory of a WAR. They can also be held in Jars on the classpath.
	 * For example, a Jar in an application's manifest classpath could
	 * contain messages for the application.
	 * @param basename basename, following java.util.ResourceBundle
	 * conventions.
	 */
	public void setBasename(String basename)  {
		this.basename = basename;
		//bundle = ResourceBundle.getBundle(basenamez);
	}

	
	/**
	 * @see AbstractNestingMessageSource#resolve(String, Locale)
	 */
	protected String resolve(String code, Locale locale) {
		ResourceBundle bundle  = ResourceBundle.getBundle(basename, locale);
		try {
			return bundle.getString(code);
		} catch (MissingResourceException ex) {
			// just key not found
			// -> do NOT throw the exception to allow for checking parent message source
			return null;
		}
	}
	
	/** Show the state of this object
	 */
	public String toString() {
		return getClass().getName() + ": basename='" + basename + "'";
	}

}	// ResourceBundleMessageSource