
package com.interface21.beans;

import java.beans.PropertyVetoException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import com.interface21.util.StringUtils;

/**
 * Combined exception, composed of individual binding exceptions.
 * An object of this class is created at the beginning of the binding process,
 * and errors added to it as necessary.
 * The binding process continues when it encounters application-level
 * exceptions, applying those changes that can be applied and storing
 * rejected changes in an object of this class.
 * @author  Rod Johnson
 * @since 18 April 2001
 * @version $Id$
 */
public class PropertyVetoExceptionsException extends BeansException {

    //---------------------------------------------------------------------
	// Instance data
    //---------------------------------------------------------------------
	/** List of ErrorCodedPropertyVetoException objects */
    private List    exceptions = new LinkedList();
    
	/** BeanWrapper wrapping the target object for binding */
    private BeanWrapper beanWrapper;

    //---------------------------------------------------------------------
    // Constructors
    //---------------------------------------------------------------------
    /** 
     * Creates new empty PropertyVetoExceptionsException
     * We'll add errors to it as we attempt to bind properties.
     */
    PropertyVetoExceptionsException(BeanWrapper beanWrapper) {
        super("PropertyVetoExceptionsException", null);
        this.beanWrapper = beanWrapper;
    }


    //---------------------------------------------------------------------
	// Public methods
    //---------------------------------------------------------------------
    /**
     * Return the BeanWrapper that generated this exception
     * @return the BeanWrapper that generated this exception
     */
    public BeanWrapper getBeanWrapper() {
        return beanWrapper;
    }
	
	/** Return the object we're binding to
	 */
	public Object getBindObject() {
        return beanWrapper.getWrappedInstance();
    }
	
	/** Messages keyed by field name */
	public Map getMessages() {
		HashMap m = new HashMap();
		for (int i = 0; i < exceptions.size(); i++) {
			ErrorCodedPropertyVetoException ex = (ErrorCodedPropertyVetoException) exceptions.get(i);
			m.put(ex.getPropertyChangeEvent().getPropertyName(), ex.getErrorCode() + ": " + ex.getMessage());
		}
		return m;
	}
	
	/** 
	 * Errors iterator 
	 */
	public Iterator iterator() {
		return new Iterator() {
			int count;
			public boolean hasNext() {
				return count < getExceptionCount();
			}
			public Object next() {
				if (!hasNext())
					throw new NoSuchElementException("PropertyVetoExceptionExceptions iterator has only " + getExceptionCount() + " elements");
				return exceptions.get(count++);
			}
			public void remove() {
				throw new UnsupportedOperationException("Cannot remove elements from PropertyVetoExceptionExceptions iterator");
			}
		};
	}	// iterator
    
	/** 
	 * If this returns 0, no errors were encountered
	 * during binding 
	 */
    public int getExceptionCount() {
        return exceptions.size();
    }
    
	/**
	 * @return an array of the exceptions stored in this object.
	 * Will return the empty array (not null) if there were no errors. 
	 */
    public ErrorCodedPropertyVetoException[] getPropertyVetoExceptions() {
        return (ErrorCodedPropertyVetoException[]) exceptions.toArray(new ErrorCodedPropertyVetoException[0]);
    }
    
    /** 
	 * @return the exception for this field, or null if there isn't one 
     */
    public ErrorCodedPropertyVetoException getPropertyVetoException(String propertyName) {
        for (Iterator itr = exceptions.iterator(); itr.hasNext(); ) {
            ErrorCodedPropertyVetoException pve = (ErrorCodedPropertyVetoException) itr.next();
            if (propertyName.equals(pve.getPropertyChangeEvent().getPropertyName()))
                return pve;
        }
        return null;
    }
    
	//---------------------------------------------------------------------
	// Package methods allowing errors to be added
    //---------------------------------------------------------------------
    void addPropertyVetoException(PropertyVetoException ex) {
		if (ex instanceof ErrorCodedPropertyVetoException)
			addErrorCodedPropertyVetoException((ErrorCodedPropertyVetoException) ex);
		else
        	exceptions.add(new ErrorCodedPropertyVetoException(ex));
    }
	
	void addErrorCodedPropertyVetoException(ErrorCodedPropertyVetoException ex) {
        exceptions.add(ex);
    }
	
	void addTypeMismatchException(TypeMismatchException ex) {
		// Need proper stack trace!?
        addPropertyVetoException(new ErrorCodedPropertyVetoException(ex));
    }
	
	void addMissingFields(InvalidPropertyValuesException ex) {
		// Need proper stack trace!?
		for (int i = 0; i < ex.getMissingFields().size(); i++) {
			addPropertyVetoException(new ErrorCodedPropertyVetoException(getBindObject(), (InvalidPropertyValuesException.MissingFieldException) ex.getMissingFields().get(i)));
		}
    }
	
	void addMethodInvocationException(MethodInvocationException ex) {
		// Need proper stack trace!?
        addPropertyVetoException(new ErrorCodedPropertyVetoException(ex));
    }
    
    public String toString() {
        String s = "PropertyVetoExceptionsException: " + getExceptionCount() + " errors:-- ";
		// Stack traces for each!?
        s += StringUtils.collectionToDelimitedString(exceptions, ";");
        return s;
    }

}	// class PropertyVetoExceptionsException
