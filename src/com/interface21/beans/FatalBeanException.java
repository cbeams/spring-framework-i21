
package com.interface21.beans;

/**
 * Thrown on an unrecoverable problem encountered in the
 * beans packages or sub-packages: e.g. bad class or field.
 * @author  Rod Johnson
 * @version $Revision$
 */
public class FatalBeanException extends BeansException {

    /**
 	* Constructs an <code>FatalBeanException</code> 
 	* with the specified message and root cause.
     * @param msg the detail message.
     * @param t root cause
     */
    public FatalBeanException(String msg, Throwable t) {
        super(msg, t);
    }
}


