package com.interface21.web.bind;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;

import org.apache.log4j.Logger;

import com.interface21.validation.Validator;

/**
 * Offers convenience methods for binding servlet request parameters
 * to objects, including option validation.
 *
 * @author Juergen Hoeller
 * @since 10.03.2003
 */
public abstract class BindUtils {

	private static Logger logger = Logger.getLogger(BindUtils.class);

	/**
	 * Bind the parameters from the given request to the given object.
	 * @param request  the request containing the parameters
	 * @param object  the object to bind the parameters to
	 * @param objectName  the name of the bind object
	 * @return the binder used (can be treated as DataBinder or Errors instance)
	 */
	public static ServletRequestDataBinder bind(ServletRequest request, Object object, String objectName) throws ServletException {
		ServletRequestDataBinder binder = new ServletRequestDataBinder(object, objectName);
		binder.bind(request);
		return binder;
	}

	/**
	 * Bind the parameters from the given request to the given object,
	 * invoking the given validator.
	 * @param request  the request containing the parameters
	 * @param object  the object to bind the parameters to
	 * @param objectName  the name of the bind object
	 * @param validator  the validator to be invoked, or null if no validation
	 * @return the binder used (can be treated as DataBinder or Errors instance)
	 */
	public static ServletRequestDataBinder bindAndValidate(ServletRequest request, Object object, String objectName, Validator validator) throws ServletException {
		ServletRequestDataBinder binder = bind(request, object, objectName);

		if (validator != null) {
			logger.debug("Invoking validator [" + validator + "]");
			if (!validator.supports(object.getClass()))
				throw new IllegalArgumentException("Validator " + validator.getClass() + " does not support " + object.getClass());
			validator.validate(object, binder);
			if (binder.hasErrors())
				logger.debug("Validator found " + binder.getErrorCount() + " errors");
			else
				logger.debug("Validator found no errors");
		}

		return binder;
	}
}
