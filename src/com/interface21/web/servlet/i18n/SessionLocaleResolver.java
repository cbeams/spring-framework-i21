package com.interface21.web.servlet.i18n;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.interface21.web.servlet.LocaleResolver;

/**
 * Implementation of LocaleResolver that uses a locale attribute in the user's
 * session in case of a custom setting, with a fallback to the accept header locale.
 * This is most appropriate if the application needs user sessions anyway.
 *
 * Custom controllers can thus override the user's locale by calling setLocale,
 * e.g. responding to a certain locale change request.
 *
 * @author Juergen Hoeller
 * @since 27.02.2003
 */
public class SessionLocaleResolver implements LocaleResolver {

	public static final String LOCALE_ATTRIBUTE_NAME = SessionLocaleResolver.class.getName() + ".LOCALE";

	public Locale resolveLocale(HttpServletRequest request) {
		// check session attribute
		Locale locale = null;
		if (request.getSession(false) != null)
			locale = (Locale) request.getSession().getAttribute(LOCALE_ATTRIBUTE_NAME);

		// fallback?
		return (locale != null ? locale : request.getLocale());
	}

	public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {
		if (locale != null) {
			// set locale attribute
			request.getSession().setAttribute(LOCALE_ATTRIBUTE_NAME, locale);
		}
		else {
			// remove locale attribute
			if (request.getSession(false) != null)
				request.getSession().removeAttribute(LOCALE_ATTRIBUTE_NAME);
		}
	}
}
