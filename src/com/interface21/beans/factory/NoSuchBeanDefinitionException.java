
package com.interface21.beans.factory;

import com.interface21.beans.BeansException;

/**
 * Exception thrown when a BeanFactory is asked for a bean 
 * instance name for which it cannot find a definition.
 * @author Rod Johnson
 */
public class NoSuchBeanDefinitionException extends BeansException {
	
	/** Name of the missing bean */
	private String name;

    /**
     * Creates new <code>NoSuchBeanDefinitionException</code>..
     * @param name the name of the missing bean
     */
    public NoSuchBeanDefinitionException(String name) {
        super("No bean named [" + name + "] is defined", null);
        this.name = name;
    }
	
	 /**
     * Creates new <code>NoSuchBeanDefinitionException</code>..
     * @param name the name of the missing bean
     * @param message further, detailed message describing the problem.
     */
	public NoSuchBeanDefinitionException(String name, String message) {
		super("No bean named [" + name + "] is defined {" + message + "}", null);
        this.name = name;
    }
    
    /**
     * Return the name of the missing bean
     * @return the name of the missing bean
     */
    public String getBeanName() {
        return name;
    }
    
}


