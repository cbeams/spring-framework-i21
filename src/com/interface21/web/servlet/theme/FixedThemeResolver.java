package com.interface21.web.servlet.theme;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Implementation of ThemeResolver that simply uses a fixed theme which name is done by
 * default by <code>DEFAULT_THEME>/code>. the fixed name can be defined in the configuration file.
 *
 * <p>Note: Does not support setTheme, because no theme change code is provided.
 *
 * @author Jean-Pierre Pawlak
 * @author Juergen Hoeller
 * @since 17.06.2003
 */
public class FixedThemeResolver implements ThemeResolver {

	public final static String DEFAULT_THEME = "theme";

	private String defaultTheme = DEFAULT_THEME;

	/**
	 * Gets the theme used in this request.
	 * @see com.interface21.web.servlet.theme.ThemeResolver#resolveTheme(javax.servlet.http.HttpServletRequest)
	 */
	public String resolveTheme(HttpServletRequest request) {
		return defaultTheme;
	}

	/**
	 * Sets the theme to use with this user. NOT AVAILABLE IN THIS IMPMLEMENTATION.
	 * @see com.interface21.web.servlet.theme.ThemeResolver#setTheme(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.String)
	 */
	public void setTheme(HttpServletRequest request, HttpServletResponse response, String theme) {
		throw new IllegalArgumentException("cannot change theme - use a different theme resolution strategy");
	}

	/**
	 * @return the default theme name
	 */
	public String getDefaultTheme() {
		return defaultTheme;
	}

	/**
	 * Sets the default theme name.
	 * @param defaultTheme The new default theme name
	 */
	public void setDefaultTheme(String defaultTheme) {
		this.defaultTheme = defaultTheme;
	}

}
