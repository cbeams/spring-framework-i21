
package com.interface21.web.bind;

import javax.servlet.ServletException;


/**
 * Fatal binding exception, thrown when we want to
 * treat binding exceptions as unrecoverable.
 * @author Rod Johnson
 */
public class ServletRequestBindingException extends ServletException {

	public ServletRequestBindingException(String s) {
		super(s);
	}

	public ServletRequestBindingException(String s, Throwable t) {
		super(s, t);
	}

}
