
package com.interface21.beans;

import java.beans.PropertyChangeEvent;

/**
 * Superclass for exceptions related to a property access,
 * such as type mismatch or invocation target exception.
 * @author  Rod Johnson
 * @version $Revision$
 */
public abstract class PropertyAccessException extends BeansException {

	private PropertyChangeEvent propertyChangeEvent;

	public PropertyAccessException(String mesg, PropertyChangeEvent propertyChangeEvent, Throwable t) {
		super(mesg, t);
		this.propertyChangeEvent = propertyChangeEvent;
	}

	/**
	 * @return the PropertyChangeEvent that resulted in the problem
	 */
	public PropertyChangeEvent getPropertyChangeEvent() {
		return propertyChangeEvent;
	}

}


