package com.interface21.ui.context.support;

import com.interface21.ui.context.Theme;
import com.interface21.context.MessageSource;

/**
 * Default Theme implementation, wrapping a name and an
 * underlying MessageSource.
 * @author Juergen Hoeller
 * @since 17.06.2003
 */
public class SimpleTheme implements Theme {

	private String name;

	private MessageSource messageSource;

	public SimpleTheme(String name, MessageSource messageSource) {
		this.name = name;
		this.messageSource = messageSource;
	}

	public String getName() {
		return name;
	}

	public MessageSource getMessageSource() {
		return messageSource;
	}

}
