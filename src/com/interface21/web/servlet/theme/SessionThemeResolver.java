package com.interface21.web.servlet.theme;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.interface21.web.util.WebUtils;

/**
 * Implementation of ThemeResolver that uses a theme attribute in the user's
 * session in case of a custom setting, with a fallback to the fixed default theme.
 * This is most appropriate if the application needs user sessions anyway.
 *
 * <p>Custom controllers can override the user's theme by calling setTheme,
 * e.g. responding to a theme change request.
 *
 * @author Jean-Pierre Pawlak
 * @author Juergen Hoeller
 * @since 17.06.2003
 */
public class SessionThemeResolver implements ThemeResolver {

	private String defaultTheme = FixedThemeResolver.DEFAULT_THEME;
	
	public static final String THEME_ATTRIBUTE_NAME = SessionThemeResolver.class.getName() + ".THEME";

	/**
	 * Gets the theme used in this request.
	 * @see com.interface21.web.servlet.theme.ThemeResolver#resolveTheme(javax.servlet.http.HttpServletRequest)
	 */
	public String resolveTheme(HttpServletRequest request) {
		String theme = (String) WebUtils.getSessionAttribute(request, THEME_ATTRIBUTE_NAME);
		// specific theme, or fallback to default?
		return (theme != null ? theme : defaultTheme);
	}

	/**
	 * Sets the theme to use with this user.
	 * @see com.interface21.web.servlet.theme.ThemeResolver#setTheme(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.String)
	 */
	public void setTheme(HttpServletRequest request, HttpServletResponse response, String theme) {
		WebUtils.setSessionAttribute(request, THEME_ATTRIBUTE_NAME, theme);
	}

	/**
	 * @return the default theme name
	 */
	public String getDefaultTheme() {
		return defaultTheme;
	}

	/**
	 * Sets the default theme name
	 * @param defaultTheme The new default theme name
	 */
	public void setDefaultTheme(String defaultTheme) {
		this.defaultTheme = defaultTheme;
	}

}
