package com.interface21.web.servlet.i18n;

import java.util.Locale;
import java.util.StringTokenizer;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.interface21.web.servlet.LocaleResolver;
import com.interface21.web.util.WebUtils;

/**
 * Implementation of LocaleResolver that uses a cookie sent back to the user
 * in case of a custom setting, with a fallback to the accept header locale.
 * This is especially useful for stateless applications without user sessions.
 *
 * <p>Custom controllers can thus override the user's locale by calling setLocale,
 * e.g. responding to a certain locale change request.
 *
 * @author Juergen Hoeller
 * @since 27.02.2003
 */
public class CookieLocaleResolver implements LocaleResolver {

	public static final String LOCALE_REQUEST_ATTRIBUTE_NAME = CookieLocaleResolver.class.getName() + ".LOCALE";

	public static final String DEFAULT_COOKIE_NAME = CookieLocaleResolver.class.getName() + ".LOCALE";

	public static final int DEFAULT_COOKIE_MAX_AGE = Integer.MAX_VALUE;

	private String cookieName = DEFAULT_COOKIE_NAME;

	private int cookieMaxAge = DEFAULT_COOKIE_MAX_AGE;

	/**
	 * Use the given name for locale cookies.
	 */
	public void setCookieName(String cookieName) {
		this.cookieName = cookieName;
	}

	public String getCookieName() {
		return cookieName;
	}

	/**
	 * Use the given maximum age, specified in seconds, for locale cookies.
	 * Useful special value: -1 ... not persistent, deleted when client shuts down
	 */
	public void setCookieMaxAge(int cookieMaxAge) {
		this.cookieMaxAge = cookieMaxAge;
	}

	public int getCookieMaxAge() {
		return cookieMaxAge;
	}

	public Locale resolveLocale(HttpServletRequest request) {
		// check locale for preparsed resp. preset locale
		Locale locale = (Locale) request.getAttribute(LOCALE_REQUEST_ATTRIBUTE_NAME);
		if (locale != null)
			return locale;

		// retrieve cookie value
		Cookie cookie = WebUtils.getCookie(request, getCookieName());

		if (cookie != null) {
			// parse cookie value
			String language = "";
			String country = "";
			String variant = "";

			StringTokenizer tokenizer = new StringTokenizer(cookie.getValue());
			if (tokenizer.hasMoreTokens())
				language = tokenizer.nextToken();
			if (tokenizer.hasMoreTokens())
				country = tokenizer.nextToken();
			if (tokenizer.hasMoreTokens())
				variant = tokenizer.nextToken();

			// evaluate results
			if (language != null) {
				locale = new Locale(language, country, variant);
				request.setAttribute(LOCALE_REQUEST_ATTRIBUTE_NAME, locale);
				return locale;
			}
		}

		// fallback
		return request.getLocale();
	}

	public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {
		Cookie cookie = null;
		if (locale != null) {
			// set request attribute and add cookie
			request.setAttribute(LOCALE_REQUEST_ATTRIBUTE_NAME, locale);
			cookie = new Cookie(getCookieName(), locale.getLanguage() + " " + locale.getCountry() + " " + locale.getVariant());
			cookie.setMaxAge(getCookieMaxAge());
		}
		else {
			// set request attribute to fallback locale and remove cookie
			request.setAttribute(LOCALE_REQUEST_ATTRIBUTE_NAME, request.getLocale());
			cookie = new Cookie(getCookieName(), "");
			cookie.setMaxAge(0);
		}
		response.addCookie(cookie);
	}
}
