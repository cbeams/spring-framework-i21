package com.interface21.web.bind;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;

import com.interface21.validation.BindException;
import com.interface21.validation.ValidationUtils;
import com.interface21.validation.Validator;

/**
 * Offers convenience methods for binding servlet request parameters
 * to objects, including optional validation.
 * @author Juergen Hoeller
 * @author Jean-Pierre Pawlak
 * @since 10.03.2003
 */
public abstract class BindUtils {

	/**
	 * Bind the parameters from the given request to the given object.
	 * @param request request containing the parameters
	 * @param object object to bind the parameters to
	 * @param objectName name of the bind object
	 * @return the binder used (can be treated as DataBinder or Errors instance)
	 */
	public static BindException bind(ServletRequest request, Object object, String objectName) {
		ServletRequestDataBinder binder = new ServletRequestDataBinder(object, objectName);
		binder.bind(request);
		return binder;
	}

	/**
	 * Bind the parameters from the given request to the given object,
	 * allowing for optional custom editors set in an bind initializer.
	 * @param request request containing the parameters
	 * @param object object to bind the parameters to
	 * @param objectName name of the bind object
	 * @param initializer implementation of the BindInitializer interface
	 * which will be able to set custom editors
	 * @return the binder used (can be treated as DataBinder or Errors instance)
	 * @throws ServletException if thrown by the BindInitializer
	 */
	public static BindException bind(ServletRequest request, Object object, String objectName,
																	 BindInitializer initializer) throws ServletException  {
		ServletRequestDataBinder binder = new ServletRequestDataBinder(object, objectName);
		if (initializer != null) {
			initializer.initBinder(request, binder);
		}
		binder.bind(request);
		return binder;
	}

	/**
	 * Bind the parameters from the given request to the given object,
	 * invoking the given validator.
	 * @param request request containing the parameters
	 * @param object object to bind the parameters to
	 * @param objectName name of the bind object
	 * @param validator validator to be invoked, or null if no validation
	 * @return the binder used (can be treated as Errors instance)
	 */
	public static BindException bindAndValidate(ServletRequest request, Object object, String objectName,
																							Validator validator) {
		BindException binder = bind(request, object, objectName);
		ValidationUtils.invokeValidator(validator, object, binder);
		return binder;
	}

	/**
	 * Bind the parameters from the given request to the given object,
	 * invoking the given validator, and allowing for optional custom editors
	 * set in an bind initializer.
	 * @param request request containing the parameters
	 * @param object object to bind the parameters to
	 * @param objectName name of the bind object
	 * @param validator validator to be invoked, or null if no validation
	 * @param initializer Implementation of the BindInitializer interface which will be able to set custom editors
	 * @return the binder used (can be treated as Errors instance)
	 * @throws ServletException if thrown by the BindInitializer
	 */
	public static BindException bindAndValidate(ServletRequest request,	Object object, String objectName,
																							Validator validator, BindInitializer initializer) throws ServletException  {
		BindException binder = bind(request, object, objectName, initializer);
		ValidationUtils.invokeValidator(validator, object, binder);
		return binder;
	}

}
