
package com.interface21.context;

import com.interface21.core.NestedRuntimeException;

/**
 * Exception thrown during application context initialization.
 * @author Rod Johnson
 */
public class ApplicationContextException extends NestedRuntimeException {

    /**
	 * Constructs an <code>ApplicationContextException</code> 
	 * with the specified detail message and no root cause.
     * @param msg the detail message.
     */
    public ApplicationContextException(String msg) {
        super(msg);
    }
	
	/**
	 * @see NestedRuntimeException#NestedRuntimeException(String, Throwable)
	 */
	public ApplicationContextException(String msg, Throwable t) {
        super(msg, t);
    }
}


