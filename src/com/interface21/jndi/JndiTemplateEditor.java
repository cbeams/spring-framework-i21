/*
 * The Spring Framework is published under the terms
 * of the Apache Software License.
 */
 
package com.interface21.jndi;

import java.beans.PropertyEditorSupport;
import java.util.Properties;

import com.interface21.beans.propertyeditors.PropertiesEditor;

/**
 * Properties editor for JndiTemplate objects. Allows properties-format strings to
 * be used to represent properties editors.
 * @author Rod Johnson
 * @since 09-May-2003
 * @version $Id$
 */
public class JndiTemplateEditor extends PropertyEditorSupport {
	
	/**
	 * @see java.beans.PropertyEditor#setAsText(java.lang.String)
	 */
	public void setAsText(String text) throws IllegalArgumentException {
		if (text == null)
			throw new IllegalArgumentException("JndiTemplate cannot be created from null string");
		if ("".equals(text)) {
			// Empty environment
			setValue(new JndiTemplate());
		}
		else {
			// We have a non-empty properties string
			PropertiesEditor pe = new PropertiesEditor();
			pe.setAsText(text);
			Properties props = (Properties) pe.getValue();
			setValue(new JndiTemplate(props));
		}
	}

}
