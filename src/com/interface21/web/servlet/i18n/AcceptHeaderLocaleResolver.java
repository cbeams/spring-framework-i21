package com.interface21.web.servlet.i18n;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.interface21.web.servlet.LocaleResolver;

/**
 * Implementation of LocaleResolver that simply uses the primary locale
 * specified in the "accept-language" header of the HTTP request
 * (i.e., the locale sent by the client browser, normally that of the client's OS).
 *
 * <p>Note: Does not support setLocale, because the accept header cannot be changed.
 *
 * @author Juergen Hoeller
 * @since 27.02.2003
 */
public class AcceptHeaderLocaleResolver implements LocaleResolver {

	public Locale resolveLocale(HttpServletRequest request) {
		return request.getLocale();
	}

	public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {
		throw new IllegalArgumentException("Cannot change HTTP accept header - use a different locale resolution strategy");
	}
	
}
