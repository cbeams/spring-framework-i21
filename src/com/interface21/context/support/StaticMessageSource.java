package com.interface21.context.support;

import java.util.HashMap;
import java.util.Locale;

/**
 * Simple implementation of NestingMessageSource that
 * allows messages to be held in a Java object, and added
 * programmatically.
 * <br/>This class now supports internationalization.
 * <br/>Intended for testing, rather than use production
 * systems.
 * @author Rod Johnson
 */
public class StaticMessageSource extends AbstractNestingMessageSource {

	private HashMap	messages = new HashMap();


	/**
	 * @see AbstractNestingMessageSource#getMessage(String, Locale, String)
	 */
	protected String resolve(String code, Locale locale) {
          if (locale == null)
             locale = getDefaultLocale();
           return (String) messages.get(messageKey(locale, code));
	}


	/**
	 * Associate the given message with the given code.
	 * @param code lookup code
         * @param locale locale message should be found within
	 * @param message message associated with this lookup code
	 */
	public void addMessage(String code, Locale locale, String defaultMessage) {
          if (locale == null)
            locale = getDefaultLocale();
          messages.put(messageKey(locale, code), defaultMessage);
	}

}

