package com.interface21.validation;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.log4j.Logger;

/**
 * This class offers a convenient validate method for invoking a validator,
 * and useful generic validation routines, like for validating emails.
 *
 * <p>The validation routines can be leveraged in custom Validator implementations,
 * rejecting respective fields via the Errors interface accordingly.
 *
 * @author Juergen Hoeller
 * @see Validator
 * @see Errors
 */
public abstract class ValidationUtils {

	private static Logger logger = Logger.getLogger(ValidationUtils.class);

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

	/**
	 * Validate the given email address syntactically.
	 * <p>Note: Does not check if the email address actually exists.
	 * @param emailAddress the email address to validate
	 * @return if the address is syntactically valid
	 */
	public static boolean validateEmailAddress(String emailAddress) {
		try {
			InternetAddress address = new InternetAddress(emailAddress);
			address.validate();
			// parsing succeeded -> valid
			return true;
		}
		catch (AddressException ex) {
			// parsing error -> invalid
			return false;
		}
	}

}
