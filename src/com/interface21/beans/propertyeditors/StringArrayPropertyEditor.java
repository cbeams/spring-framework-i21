package com.interface21.beans.propertyeditors;

import java.beans.PropertyEditorSupport;

import com.interface21.util.StringUtils;

/**
 * Properties editor for String[] type.
 * Strings must be in CSV format.
 * This property editor is registered by BeanWrapperImpl.
 * @author Rod Johnson
 */
public class StringArrayPropertyEditor extends PropertyEditorSupport {
	
	/**
	 * @see java.beans.PropertyEditor#setAsText(String)
	 */
	public void setAsText(String s) throws IllegalArgumentException {
		String[] sa = StringUtils.commaDelimitedListToStringArray(s);
		setValue(sa);
	}

}

