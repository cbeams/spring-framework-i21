package com.interface21.beans.factory.support;

import com.interface21.beans.FatalBeanException;

/**
 * 
 * @author Rod Johnson
 * @since 02-Dec-02
 */
public class BootstrapException extends FatalBeanException {

	/**
	 * Constructor for BootstrapException.
	 * @param msg
	 * @param t
	 */
	public BootstrapException(String msg, Throwable t) {
		super(msg, t);
	}

}
