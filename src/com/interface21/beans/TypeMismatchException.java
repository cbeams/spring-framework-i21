
package com.interface21.beans;

import java.beans.PropertyChangeEvent;

/**
 * Exception thrown on a type mismatch when trying to set a property
 * @author  Rod Johnson
 * @version $Revision$
 */
public class TypeMismatchException extends PropertyAccessException {

	public TypeMismatchException(PropertyChangeEvent propertyChangeEvent, Class requiredType) {
		super("Failed to convert property value of type [" + propertyChangeEvent.getNewValue().getClass().getName() + "] to required type [" + requiredType.getName() + "]", propertyChangeEvent);
	}

	public TypeMismatchException(PropertyChangeEvent propertyChangeEvent, Class requiredType, Throwable t) {
		super("Failed to convert property value of type [" + propertyChangeEvent.getNewValue().getClass().getName() + "] to required type [" + requiredType.getName() + "]", t, propertyChangeEvent);
	}

}


