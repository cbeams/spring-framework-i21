
package com.interface21.web.bind;

import java.util.Properties;

import javax.servlet.ServletRequest;

import org.apache.log4j.Logger;

import com.interface21.beans.MutablePropertyValues;
import com.interface21.beans.PropertyValue;
import com.interface21.beans.PropertyValues;
import com.interface21.web.util.WebUtils;

// Could also provide URL implementation
// aliases? in superclass?

/**
 * PropertyValues implementation created from parameters in a ServletRequest.
 * Looks for all property values beginning with a certain prefix
 * and prefix separator.
 * <br/>This class is immutable once initialized.
 * @author Rod Johnson
 * @version $Id$
 */
public class ServletRequestParameterPropertyValues implements PropertyValues {

	/** Default prefix separator */
	private static final String DEFAULT_PREFIX_SEPARATOR = "_";

	/**
	* Create a logging category that is available
	* to subclasses. 
	*/
	protected final Logger logger = Logger.getLogger(getClass().getName());
	
	//---------------------------------------------------------------------
	// Instance data
	//---------------------------------------------------------------------
	/** PropertyValues delegate. We use delegation rather than simply subclass
	 * MutablePropertyValues as we don't want to expose MutablePropertyValues's
	 * update methods. This class is immutable once initialized.
	 */
	private MutablePropertyValues mutablePropertyValues;

	/** Creates new ServletRequestPropertyValues using the default
	 * prefix separator and the given prefix (the underscore character, _).
	 * @param request HTTP Request
	 * @param prefix prefix for properties
	 */
    public ServletRequestParameterPropertyValues(ServletRequest request, String prefix) {
		this(request, prefix, DEFAULT_PREFIX_SEPARATOR);
    }


	/** Creates new ServletRequestPropertyValues using no prefix 
	 * (and hence, no prefix separator)
	 * @param request HTTP Request
	 */
	public ServletRequestParameterPropertyValues(ServletRequest request) {
		this(request, null, null);
    }

	/** Creates new ServletRequestPropertyValues supplying
	 * both prefix and prefixSeparator
	 * @param request HTTP Request
	 * @param prefix prefix for properties
	 * @param prefixSeparator Separator delimiting prefix (e.g. user) from property name
	 * (e.g. age) to build a request parameter name such as user_age
	 */
    public ServletRequestParameterPropertyValues(ServletRequest request, String prefix, String prefixSeparator) {
		String base = (prefix != null) ?  prefix + prefixSeparator : null;
		Properties p = WebUtils.getParametersStartingWith(request, base);
		mutablePropertyValues = new MutablePropertyValues(p);
		if (logger.isDebugEnabled())
			logger.debug("Found PropertyValues in request: " + mutablePropertyValues);
    }

	//---------------------------------------------------------------------
	// Implementation of PropertyValues
	//---------------------------------------------------------------------
	/** Return an array of the PropertyValue objects
	 * held in this object.
	 * @return an array of the PropertyValue objects
	 * held in this object.
 	*/
	public PropertyValue[] getPropertyValues() {
		// We simply let the delegate handle this
		return mutablePropertyValues.getPropertyValues();
	}

	/** Is there a propertyValue object for this property?
	 * @param propertyName name of the property we're interested in
	 * @return whether there is a propertyValue object for this property?
	 */
	public boolean contains(String propertyName) {
		// Just pass it to the delegate...
		return mutablePropertyValues.contains(propertyName);
	}
	
	public PropertyValue getPropertyValue(String propertyName) {
		// Just pass it to the delegate...
		return mutablePropertyValues.getPropertyValue(propertyName);
	}
	
	public PropertyValues changesSince(PropertyValues old) {
		// Just pass it to the delegate...
		return mutablePropertyValues.changesSince(old);
	}

}
