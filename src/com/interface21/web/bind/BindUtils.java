package com.interface21.web.bind;

import com.interface21.validation.Errors;
import com.interface21.validation.BindException;

import javax.servlet.ServletRequest;

/**
 * Convenience class for retrieving binding errors.
 *
 * @author Juergen Hoeller
 * @since 17.02.2003
 */
public class BindUtils {

	/**
	 * Retrieves the Errors instance for the given bind object.
	 * @param request  the request to retrieve the instance from
	 * @param name  the name of the bind object
	 * @return  the Errors instance
	 */
	public static Errors getErrors(ServletRequest request, String name) {
		return (Errors)request.getAttribute(BindException.ERROR_KEY_PREFIX + name);
	}
}
