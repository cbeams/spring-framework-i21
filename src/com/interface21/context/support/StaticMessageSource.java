package com.interface21.context.support;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * Simple implementation of NestingMessageSource that allows messages
 * to be held in a Java object, and added programmatically.
 * This class now supports internationalization.
 *
 * <p>Intended for testing, rather than use production systems.
 *
 * @author Rod Johnson
 */
public class StaticMessageSource extends AbstractNestingMessageSource {

	private final Logger logger = Logger.getLogger(getClass());

	private Map messages = new HashMap();

	/**
	 * @see AbstractNestingMessageSource#messageKey(Locale, String)
	 */
	protected String resolve(String code, Locale locale) {
		return (String) this.messages.get(messageKey(locale, code));
	}

	/**
	 * Associate the given message with the given code.
	 * @param code lookup code
   * @param locale locale message should be found within
	 * @param message message associated with this lookup code
	 */
	public void addMessage(String code, Locale locale, String message) {
		this.messages.put(messageKey(locale, code), message);
		logger.info("Added message [" + message + " for code [" + code + "] and Locale [" + locale + "]");
	}

}

