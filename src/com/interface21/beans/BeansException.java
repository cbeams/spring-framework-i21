
package com.interface21.beans;

import com.interface21.core.NestedRuntimeException;

/**
 * Abstract superclass for all exceptions thrown in the beans package
 * and subpackages. Note that this is a runtime (unchecked) exception.
 * Beans exceptions are usually fatal; there is no reason for them to be
 * checked.
 * @author  Rod Johnson
 */
public abstract class BeansException extends NestedRuntimeException {

    /**
     * Constructs an <code>BeansException</code> with the specified message
     * and root cause.
     * @param msg the detail message.
     * @param t the root cause
     */
    public BeansException(String msg, Throwable t) {
        super(msg, t);
    }
    
    /**
     * Constructs an <code>BeansException</code> with the specified message
     * and no root cause.
     * @param msg the detail message.
     */
    public BeansException(String msg) {
        super(msg);
    }
    
}


