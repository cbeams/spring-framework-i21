package com.interface21.validation;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.AddressException;

/**
 * This class contains useful generic validation routines,
 * such as a standard way of validating emails.
 *
 * <p>These routines can be leveraged in custom Validator implementations,
 * rejecting respective fields via the Errors interface accordingly.
 *
 * @author Juergen Hoeller
 * @see Errors
 */
public abstract class ValidationUtils {

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
