package com.interface21.context.support;

import java.util.HashMap;
import java.util.Locale;

/**
 * Simple implementation of NestingMessageSource that
 * allows messages to be held in a Java object, and added
 * programmatically.
 * <br/>Does not support internationalization.
 * <br/>Intended for testing, rather than use production
 * systems.
 * @author Rod Johnson
 */
public class StaticMessageSource extends AbstractNestingMessageSource {
	
	private HashMap	messages = new HashMap();
	

	/**
	 * @see MessageSource#getMessage(String, Locale, String)
	 */
	protected String resolve(String code, Locale locale) {
		return (String) messages.get(code);
	}
	
	
	/**
	 * Associate the given message with the given code.
	 * @param code lookup code
	 * @param message message associated with this lookup code
	 */
	public void addMessage(String code, String message) {
		messages.put(code, message);
	}

}

