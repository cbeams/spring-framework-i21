package com.interface21.beans.propertyeditors;

import java.beans.PropertyEditorSupport;
import java.lang.reflect.InvocationTargetException;

/**
 * Property editor for any Number subclass like Integer, Long, Float, Double.
 * Supports custom allowEmpty setting, i.e. whether empty String will get
 * converted to null.
 * @author Juergen Hoeller
 * @since 06.06.2003
 */
public class CustomNumberEditor extends PropertyEditorSupport {

	private Class propertyClass;

	private final boolean allowEmpty;

	/**
	 * Create a new instance.
	 * The allowEmpty parameter states if an empty String should
	 * be allowed for parsing, i.e. get interpreted as null value.
	 * Else, an IllegalArgumentException gets thrown in that case.
	 * @param allowEmpty if empty strings should be allowed
	 */
	public CustomNumberEditor(Class propertyClass, boolean allowEmpty) {
		if (!Number.class.isAssignableFrom(propertyClass)) {
			throw new IllegalArgumentException("Property class must be a subclass of Number");
		}
		this.propertyClass = propertyClass;
		this.allowEmpty = allowEmpty;
	}

	public void setAsText(String text) throws IllegalArgumentException {
		if (this.allowEmpty && text.trim().equals("")) {
			setValue(null);
		}
		else {
			try {
				setValue(propertyClass.getConstructor(new Class[] {String.class}).newInstance(new Object[] {text}));
			}
			catch (InstantiationException ex) {
				throw new IllegalArgumentException("Could not instantiate property class [" + propertyClass + "]: " + ex);
			}
			catch (IllegalAccessException ex) {
				throw new IllegalArgumentException("Could not instantiate property class [" + propertyClass + "]: " + ex);
			}
			catch (InvocationTargetException ex) {
				throw new IllegalArgumentException("Could not instantiate property class [" + propertyClass + "]: " + ex);
			}
			catch (NoSuchMethodException ex) {
				throw new IllegalArgumentException("Could not instantiate property class [" + propertyClass + "]: " + ex);
			}
		}
	}

}
