
package com.interface21.beans.factory;

import com.interface21.beans.BeansException;

/**
 * Exception thrown when a BeanFactory encounters an internal error, and
 * its definitions are invalid: for example, if an XML document containing
 * bean definitions isn't well-formed.
 * @author Rod Johnson
 */
public class BeanDefinitionStoreException extends BeansException {

    /**
    * Constructs a <code>BeanDefinitionStoreException</code> with the specified detail message.
     * @param msg the detail message.
     * @param t the root cause of the problem with the factory.
     */
    public BeanDefinitionStoreException(String msg,Throwable t) {
        super(msg, t);
    }
}


