package com.interface21.web.servlet;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.interface21.beans.MutablePropertyValues;
import com.interface21.beans.PropertyValue;
import com.interface21.beans.PropertyValues;
import com.interface21.util.StringUtils;

/**
 * PropertyValues implementation created from ServetConfig parameters.
 * This class is immutable once initialized.
 * @author Rod Johnson
 */
class ServletConfigPropertyValues implements PropertyValues {

	protected final Log logger = LogFactory.getLog(getClass());
	/** PropertyValues delegate. We use delegation rather than simply subclass
	 * MutablePropertyValues as we don't want to expose MutablePropertyValues's
	 * update methods. This class is immutable once initialized.
	 */
	private MutablePropertyValues mutablePropertyValues;

	/** Creates new PropertyValues object
	 * @param config ServletConfig we'll use to take PropertyValues from
	 * @throws ServletException should never be thrown from this method
	 */
	public ServletConfigPropertyValues(ServletConfig config) throws ServletException {
		this(config, null);
	}

	/** Creates new PropertyValues object
	 * @param config ServletConfig we'll use to take PropertyValues from
	 * @param requiredProperties array of property names we need, where
	 * we can't accept default values
	 * @throws ServletException if any required properties are missing
	 */
	public ServletConfigPropertyValues(ServletConfig config, List requiredProperties) throws ServletException {
		// Ensure we have a deep copy
		List missingProps = (requiredProperties == null) ? new ArrayList(0) : new ArrayList(requiredProperties);

		mutablePropertyValues = new MutablePropertyValues();
		Enumeration enum = config.getInitParameterNames();
		while (enum.hasMoreElements()) {
			String property = (String) enum.nextElement();
			Object value = config.getInitParameter(property);
			mutablePropertyValues.addPropertyValue(new PropertyValue(property, value));
			// Check it off
			missingProps.remove(property);
		}

		// Fail if we are still missing properties
		if (missingProps.size() > 0) {
			throw new ServletException("Initialization from ServletConfig for servlet '" + config.getServletName() + "' failed: the following required properties were missing -- (" +
			                           StringUtils.collectionToDelimitedString(missingProps, ", ") + ")");
		}

		if (logger.isDebugEnabled()) {
			logger.debug("Found PropertyValues in ServletConfig: " + mutablePropertyValues);
		}
	}


	/**
	 * Return an array of the PropertyValue objects
	 * held in this object.
	 * @return an array of the PropertyValue objects
	 * held in this object.
	 */
	public PropertyValue[] getPropertyValues() {
		// We simply let the delegate handle this
		return mutablePropertyValues.getPropertyValues();
	}

	/**
	 * Is there a propertyValue object for this property?
	 * @param propertyName name of the property we're interested in
	 * @return whether there is a propertyValue object for this property?
	 */
	public boolean contains(String propertyName) {
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
