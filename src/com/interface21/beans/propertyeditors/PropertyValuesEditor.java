package com.interface21.beans.propertyeditors;

import java.beans.PropertyEditorSupport;
import java.util.Properties;

import com.interface21.beans.MutablePropertyValues;

/**
 * Editor for PropertyValues objects. Not
 * a GUI editor.
 * <br>NB: this editor must be registered with the JavaBeans API before it
 * will be available. Editors in this package are
 * registered by BeanWrapperImpl.
 * <br>The required format is defined in java.util.Properties documentation.
 * Each property must be on a new line.
 * <br>
 * The present implementation relies on a PropertiesEditor.
 * @author Rod Johnson
 */
public class PropertyValuesEditor extends PropertyEditorSupport {
	
	
	/**
	 * @see java.beans.PropertyEditor#setAsText(String)
	 */
	public void setAsText(String s) throws IllegalArgumentException {
		PropertiesEditor pe = new PropertiesEditor();
		pe.setAsText(s);
		Properties props = (Properties) pe.getValue();
		setValue(new MutablePropertyValues(props));
	}

}

