package com.interface21.validation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class offers a convenient validate method for invoking a validator.
 * Used by BindUtils' bindAndValidate method.
 *
 * @author Juergen Hoeller
 * @since 06.05.2003
 * @see Validator
 * @see Errors
 * @see com.interface21.web.bind.BindUtils#bindAndValidate
 */
public abstract class ValidationUtils {

	private static Log logger = LogFactory.getLog(ValidationUtils.class);

	/**
	 * Invoke the given validator for the given object and Errors instance.
	 * @param validator validator to be invoked, or null if no validation
	 * @param object object to bind the parameters to
	 * @param errors Errors instance that should store the errors
	 */
	public static void invokeValidator(Validator validator, Object object, Errors errors) {
		if (validator != null) {
			logger.debug("Invoking validator [" + validator + "]");
			if (!validator.supports(object.getClass()))
				throw new IllegalArgumentException("Validator " + validator.getClass() + " does not support " + object.getClass());
			validator.validate(object, errors);
			if (errors.hasErrors())
				logger.debug("Validator found " + errors.getErrorCount() + " errors");
			else
				logger.debug("Validator found no errors");
		}
	}

}
