
package com.interface21.beans;

import java.beans.Introspector;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;


/**
 * Convenience superclass for JavaBeans VetoableChangeListeners. This class implements the
 * VetoableChangeListener interface to delegate the method call to one of any number
 * of validation methods defined in concrete subclasses. This is a typical use of
 * reflection to avoid the need for a chain of if/else statements, discussed in
 * <a href="http://www.amazon.com/exec/obidos/tg/detail/-/1861007841/">Expert One-on-One J2EE Design and Development</a>.
 * <br/>Because validation methods are cached by this class's constructor, the
 * overhead of reflection is not great.
 * <br/>The signature for validation methods must be of this form (the following
 * example validates an int property named age):
 * <code>
 * public void validateAge(int age, PropertyChangeEvent e) throws PropertyVetoException
 * </code>
 * Note that the field can be expected to have been converted to the required type,
 * simplifying validation logic.<br/>
 * Validation methods must be public or protected. The return value is not required,
 * but will be ignored.
 * <br/><b>NB:</b>Validation methods will receive a reversion event after they have
 * vetoed a change. So, if an email property is initially null and an invalid email address
 * is supplied and vetoed by the first call to validateEmail for the given validator,
 * a second event will be sent when the email field is reverted to null. This means that
 * validation methods must be able to cope with initial values. They can, however,
 * throw another PropertyVetoException, which will be ignored by the caller.<br/>
 * Subclasses should be threadsafe: nothing in this superclass will cause a problem.
 * @author  Rod Johnson
 * @version $Id$
 */
public abstract class AbstractVetoableChangeListener implements VetoableChangeListener {
	
	/** Prefix for validation methods: a typical name might be
	 * validateAge()
	 */
	protected static final String VALIDATE_METHOD_PREFIX = "validate";
	
	/** Validation methods, keyed by propertyName */
	private HashMap validationMethodHash = new HashMap();
	
	
	/** Creates new AbstractVetoableChangeListener.
	 * Caches validation methods for efficiency.
	 */
	public AbstractVetoableChangeListener() throws SecurityException {
		// Look at all methods in the subclass, trying to find
		// methods that are validators according to our criteria
		Method [] methods = getClass().getMethods();
		for (int i = 0; i < methods.length; i++) {
			// We're looking for methods with names starting with the given prefix,
			// and two parameters: the value (which may be of any type, primitive or object)
			// and a PropertyChangeEvent.
			if (methods[i].getName().startsWith(VALIDATE_METHOD_PREFIX) &&
				methods[i].getParameterTypes().length == 2 &&
				PropertyChangeEvent.class.isAssignableFrom(methods[i].getParameterTypes()[1])) {
				// We've found a potential validator: it has the right number of parameters
				// and its name begins with validate...
				//System.out.println("Found potential validator method [" + methods[i] + "]");
				Class[] exceptions = methods[i].getExceptionTypes();
				// We don't care about the return type, but we must ensure that
				// the method throws only one checked exception, PropertyVetoException
				if (exceptions.length == 1 && PropertyVetoException.class.isAssignableFrom(exceptions[0])) {
					// We have a valid validator method
					// Ensure it's accessible (for example, it might be a method on an inner class)
					methods[i].setAccessible(true);
					String propertyName = Introspector.decapitalize(methods[i].getName().substring(VALIDATE_METHOD_PREFIX.length()));
					validationMethodHash.put(propertyName, methods[i]);
					//System.out.println(methods[i] + " is validator for property " + propertyName);
				}
				else {
					//System.out.println("invalid validator");
				}
			}
			else {
				//System.out.println("method [" + methods[i] + "] is not a validator");
			}
		}
	}	// constructor
	
	
	/** Implementation of VetoableChangeListener.
	 * Will attempt to locate the appropriate validator method and
	 * invoke it. Will do nothing if there is no validation method for this
	 * property.
	 */
	public final void vetoableChange(PropertyChangeEvent e) throws PropertyVetoException {
		//System.out.println("VetoableChangeEvent: old value=[" + e.getOldValue() + "] new value=[" + e.getNewValue() + "]");
		//if (m==null)
		Method m = (Method) validationMethodHash.get(e.getPropertyName());
				
		if (m != null) {
			try {
				//System.out.println("Using validator method " + m);
				Object val = e.getNewValue();
				//String vals = (String) val;
				//if (false) 
				m.invoke(this, new Object[] { val, e });
				//System.out.println("PROPERTY VALUE NOT VETOED: OK");
			}
			catch (IllegalAccessException ex) {
				//System.out.println("WARNING: can't validate. method isn't accessible");
			}
			catch (InvocationTargetException ex) {
				// This is what we're looking for: the subclass's
				// validator method vetoed the property change event
				// We know that the exception must be of the correct type (unless
				// it's a runtime exception) as we checked the declared exceptions of the
				// validator method in this class's constructor.
				// If it IS a runtime exception, we just rethrow it, to encourage the
				// author of the subclass to write robust code...
				if (ex.getTargetException() instanceof RuntimeException)
					throw (RuntimeException) ex.getTargetException();
				PropertyVetoException pex = (PropertyVetoException) ex.getTargetException();
				throw pex;
			}
		}	// if there was a validator method for this property
		else {
			//System.out.println("no validation method for " + e.getPropertyName());
		}
	}	// vetoableChange
	
	
	/** Private convenience method used in this class */
	private static String capitalize(String propertyName) {
		return propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
	}
	
}	// class AbstractVetoableChangeListener
