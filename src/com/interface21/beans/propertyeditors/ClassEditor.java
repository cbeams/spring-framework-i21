package com.interface21.beans.propertyeditors;

import java.beans.PropertyEditorSupport;

/**
 * Editor for java.lang.Class, to directly feed a Class property
 * instead of needing an additional class name property.
 * @author Juergen Hoeller
 * @since 13.05.2003
 */
public class ClassEditor extends PropertyEditorSupport {

	public void setAsText(String text) throws IllegalArgumentException {
		Class clazz = null;
		try {
			clazz = Class.forName(text);
		}
		catch (ClassNotFoundException ex) {
			throw new IllegalArgumentException("Invalid class name [" + text + "]: " + ex.getMessage());
		}
		setValue(clazz);
	}

}
