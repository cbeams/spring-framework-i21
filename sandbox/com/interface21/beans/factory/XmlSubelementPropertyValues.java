package com.interface21.beans.factory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import com.interface21.beans.FatalBeanException;
import com.interface21.beans.MutablePropertyValues;
import com.interface21.beans.PropertyValue;
import com.interface21.beans.PropertyValues;

/**
 * PropertyValues implementation created from ServetConfig parameters.
 * <br/>This class is immutable once initialized. Used by
 * the XmlBeanFactory.
 * @author Rod Johnson
 */
class XmlSubelementPropertyValues implements PropertyValues {

	/** Logger instance shared by all instances */
	private static Log logger = LogFactory.getLog(XmlSubelementPropertyValues.class);

	/** PropertyValues delegate. We use delegation rather than simply subclass
	 * MutablePropertyValues as we don't want to expose MutablePropertyValues's
	 * update methods. This class is immutable once initialized.
	 */
	private MutablePropertyValues mutablePropertyValues;

	public XmlSubelementPropertyValues(Element e, String match) throws Exception {
		logger.info("Looking for property value subelements with tag name <" + match + ">");
		mutablePropertyValues = new MutablePropertyValues();
		NodeList nl = e.getElementsByTagName(match);
		for (int i = 0; i < nl.getLength(); i++) {
			Element propEle = (Element) nl.item(i);
			String propName = propEle.getAttribute("property");
			if ("".equals(propName))
				throw new Exception("'property' attribute required in element <" + propEle.getTagName() + ">");
			NodeList nl2 = propEle.getChildNodes();
			if (nl2.getLength() != 1 || !(nl2.item(0) instanceof Text))
				throw new FatalBeanException("Unexpected element or type mismatch: " + nl2.item(0), null);
			Text t = (Text) nl2.item(0);
			// This will be a String
			String propVal = t.getData();
			PropertyValue pv = new PropertyValue(propName, propVal);
			logger.debug("Found new XML subelement property value [" + pv + "]");
			mutablePropertyValues.addPropertyValue(pv);
		}
		if (logger.isDebugEnabled())
			logger.debug("Found property values: [" + mutablePropertyValues + "]");
	}


	/**
	 * @see com.interface21.beans.PropertyValues#getPropertyValues()
	 */
	public PropertyValue[] getPropertyValues() {
		// We simply let the delegate handle this
		return mutablePropertyValues.getPropertyValues();
	}


	/**
	 * @see com.interface21.beans.PropertyValues#contains(java.lang.String)
	 */
	public boolean contains(String propertyName) {
		return mutablePropertyValues.contains(propertyName);
	}

	/**
	 * @see com.interface21.beans.PropertyValues#getPropertyValue(java.lang.String)
	 */
	public PropertyValue getPropertyValue(String propertyName) {
		return mutablePropertyValues.getPropertyValue(propertyName);
	}

	/**
	 * @see com.interface21.beans.PropertyValues#changesSince(com.interface21.beans.PropertyValues)
	 */
	public PropertyValues changesSince(PropertyValues old) {
		return mutablePropertyValues.changesSince(old);
	}

}
