package com.interface21.context;

import java.util.Locale;

/**
 * Exception thrown when a message can't be resolved
 * @author Rod Johnson
 */
public class NoSuchMessageException extends Exception {

	/**
	 * Create a new exception.
	 * @param code code that could not be resolved for given locale.
         * @param locale locale that was used to search for the code within
	 */
	public NoSuchMessageException(String code, Locale locale) {
		super("No message found under code '" + code + "' for locale '" + locale + "'.");
	}

        /**
         * Create a new exception.
         * @param code code that could not be resolved for given locale.
         */
        public NoSuchMessageException(String code) {
                super("No message found under code '" + code + "' for locale '" + Locale.getDefault() + "'.");
        }

}

