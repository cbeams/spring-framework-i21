package com.interface21.web.servlet.i18n;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.interface21.web.servlet.LocaleResolver;
import com.interface21.web.util.WebUtils;

/**
 * Implementation of LocaleResolver that uses a locale attribute in the user's
 * session in case of a custom setting, with a fallback to the accept header locale.
 * This is most appropriate if the application needs user sessions anyway.
 *
 * <p>Custom controllers can override the user's locale by calling setLocale,
 * e.g. responding to a locale change request.
 *
 * @author Juergen Hoeller
 * @since 27.02.2003
 */
public class SessionLocaleResolver implements LocaleResolver {

	public static final String LOCALE_SESSION_ATTRIBUTE_NAME = SessionLocaleResolver.class.getName() + ".LOCALE";

	public Locale resolveLocale(HttpServletRequest request) {
		Locale locale = (Locale) WebUtils.getSessionAttribute(request, LOCALE_SESSION_ATTRIBUTE_NAME);
		// specific locale, or fallback to request locale?
		return (locale != null ? locale : request.getLocale());
	}

	public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {
		WebUtils.setSessionAttribute(request, LOCALE_SESSION_ATTRIBUTE_NAME, locale);
	}

}
