
package com.interface21.beans;

/**
 * Exception thrown when navigation of a valid nested property
 * path encounters a null pointer exception. For example,
 * navigating spouse.age fails because the spouse property of the
 * target object has a null value.
 * @author Rod Johnson
 */
public class NullValueInNestedPathException extends FatalBeanException {

	private String property;

	private Class clazz;

	/**
	 * Constructor for NullValueInNestedPathException.
	 * @param clazz
	 * @param propertyName
	 */
	public NullValueInNestedPathException(Class clazz, String propertyName) {
		super("Value of nested property '" + propertyName + "' is null in " + clazz, null);
		this.property = propertyName;
		this.clazz = clazz;
	}

	/**
	 * @return the name of the offending property
	 */
	public String getPropertyName() {
		return property;
	}

	public Class getBeanClass() {
		return clazz;
	}

}
