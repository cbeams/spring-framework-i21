package com.interface21.web.servlet.theme;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Cookie;

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
public class CookieThemeResolver implements ThemeResolver {

	public static final String THEME_ATTRIBUTE_NAME = SessionThemeResolver.class.getName() + ".THEME";

	public static final String DEFAULT_COOKIE_NAME = THEME_ATTRIBUTE_NAME;

	public static final int DEFAULT_COOKIE_MAX_AGE = Integer.MAX_VALUE;

	private String cookieName = DEFAULT_COOKIE_NAME;

	private int cookieMaxAge = DEFAULT_COOKIE_MAX_AGE;
	
	private String defaultTheme = FixedThemeResolver.DEFAULT_THEME;

	/**
	 * Use the given name for theme cookies.
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

	public String resolveTheme(HttpServletRequest request) {
		// check locale for preparsed resp. preset locale
		String theme = (String) request.getAttribute(THEME_ATTRIBUTE_NAME);
		if (theme != null)
			return theme;

		// retrieve cookie value
		Cookie cookie = WebUtils.getCookie(request, getCookieName());

		if (cookie != null) {
			return cookie.getValue();
		}

		// fallback
		return defaultTheme;
	}

	public void setTheme(HttpServletRequest request, HttpServletResponse response, String theme) {
		Cookie cookie = null;
		if (theme != null) {
			// set request attribute and add cookie
			request.setAttribute(THEME_ATTRIBUTE_NAME, theme);
			cookie = new Cookie(getCookieName(), theme);
			cookie.setMaxAge(getCookieMaxAge());
		}
		else {
			// set request attribute to fallback theme and remove cookie
			request.setAttribute(THEME_ATTRIBUTE_NAME, defaultTheme);
			cookie = new Cookie(getCookieName(), "");
			cookie.setMaxAge(0);
		}
		response.addCookie(cookie);
	}
	
	/**
	 * @return
	 */
	public String getDefaultTheme() {
		return defaultTheme;
	}

	/**
	 * @param string
	 */
	public void setDefaultTheme(String defaultTheme) {
		this.defaultTheme = defaultTheme;
	}

}
