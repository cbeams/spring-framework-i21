
package com.interface21.beans;

/**
 * Class to hold information and value for an individual property.
 * Using an object here, rather than just storing all properties in a 
 * map keyed by property name, allows for more flexibility, and the
 * ability to handle indexed properties etc. if necessary.
 * <br/>Note that the value doesn't need to be the final required type:
 * a BeanWrapper implementation should handle any necessary conversion,
 * as this object doesn't know anything about the objects it will be
 * applied to.
 * @author  Rod Johnson
 * @since 13 May 2001
 * @version $Id$
 */
public class PropertyValue {
	
    //---------------------------------------------------------------------
	// Instance data
    //---------------------------------------------------------------------
	/** Property name */
	private String	name;
	
	/** Value of the property */
	private Object value;

    //---------------------------------------------------------------------
    // Constructors
    //---------------------------------------------------------------------
	/** 
	 * Creates new PropertyValue 
	 * @param name name of the property
	 * @param value value of the property (posibly before type conversion)
	 */
    public PropertyValue(String name, Object value) {
		this.name = name;
		this.value = value;
    }


    //---------------------------------------------------------------------
	// Public methods
    //---------------------------------------------------------------------
	/** 
	 * Return the name of the property
	 * @return the name of the property
	 */
	public String getName() {
		return name;
	}
	
	/** 
	 * Return the value of the property
	 * @return the value of the property. Type conversion
	 * will probably not have occurred. It is the responsibility
	 * of BeanWrapper implementations to perform type conversion.
	 */
	public Object getValue() {
		return value;
	}
	
	/**
	 * Diagnostic method
	 */
	public String toString() {
		return "PropertyValue: name='" + name + "'; value=[" + value + "]";
	}
	
	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object other) {
		if (!(other instanceof PropertyValue))
			return false;
		PropertyValue pvOther = (PropertyValue) other;
		return this == other || 
			(this.name == pvOther.name && this.value == pvOther.value);
	}

}	// class PropertyValue
