
package com.interface21.beans;

import java.beans.PropertyChangeEvent;

/**
 * Thrown when a method getter or setter throws an exception
 * (analogous to an InvocationTargetException)
 * @author  Rod Johnson
 * @version $Revision$
 */
public class MethodInvocationException extends PropertyAccessException {

	/**
	 * @param t throwable raised by invoked method
	 * @param propertyChangeEvent PropertyChangeEvent that resulted in an exception
	 */
    public MethodInvocationException(Throwable t, PropertyChangeEvent propertyChangeEvent) {
        super("Property " + propertyChangeEvent.getPropertyName() + " threw exception (" + t + ")", t, propertyChangeEvent);
    }
	
	/**
	 * Constructor to use when an exception results from
	 * a method invocation, and there is no PropertyChangeEvent
	 * @param t throwable root cause
	 * @param methodName name of the method we were trying to invoke
	 */
	public MethodInvocationException(Throwable t, String methodName) {
		super("Method " + methodName + " threw exception (" + t + ")", t, null);
	}
}
