package com.interface21.web.servlet.theme;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.interface21.web.util.WebUtils;

/**
 * Implementation of ThemeResolver that uses a cookie sent back to the user
 * in case of a custom setting, with a fallback to the fixed locale.
 * This is especially useful for stateless applications without user sessions.
 *
 * <p>Custom controllers can thus override the user's theme by calling setTheme,
 * e.g. responding to a certain theme change request.
 *
 * @author Jean-Pierre Pawlak
 * @author Juergen Hoeller
 * @since 17.06.2003
 */
public class CookieThemeResolver extends AbstractThemeResolver {

	public static final String THEME_REQUEST_ATTRIBUTE_NAME = CookieThemeResolver.class.getName() + ".THEME";

	public static final String DEFAULT_COOKIE_NAME = CookieThemeResolver.class.getName() + ".THEME";

	public static final int DEFAULT_COOKIE_MAX_AGE = Integer.MAX_VALUE;

	private String cookieName = DEFAULT_COOKIE_NAME;

	private int cookieMaxAge = DEFAULT_COOKIE_MAX_AGE;
	
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

	public String resolveThemeName(HttpServletRequest request) {
		// check theme for preparsed resp. preset theme
		String theme = (String) request.getAttribute(THEME_REQUEST_ATTRIBUTE_NAME);
		if (theme != null)
			return theme;

		// retrieve cookie value
		Cookie cookie = WebUtils.getCookie(request, getCookieName());

		if (cookie != null) {
			return cookie.getValue();
		}

		// fallback
		return getDefaultThemeName();
	}

	public void setThemeName(HttpServletRequest request, HttpServletResponse response, String themeName) {
		Cookie cookie = null;
		if (themeName != null) {
			// set request attribute and add cookie
			request.setAttribute(THEME_REQUEST_ATTRIBUTE_NAME, themeName);
			cookie = new Cookie(getCookieName(), themeName);
			cookie.setMaxAge(getCookieMaxAge());
		}
		else {
			// set request attribute to fallback theme and remove cookie
			request.setAttribute(THEME_REQUEST_ATTRIBUTE_NAME, getDefaultThemeName());
			cookie = new Cookie(getCookieName(), "");
			cookie.setMaxAge(0);
		}
		response.addCookie(cookie);
	}

}
