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
public class FixedThemeResolver extends AbstractThemeResolver {

	/**
	 * Gets the theme used in this request.
	 */
	public String resolveTheme(HttpServletRequest request) {
		return getDefaultThemeName();
	}

	/**
	 * Sets the theme to use with this user. NOT AVAILABLE IN THIS IMPMLEMENTATION.
	 */
	public void setTheme(HttpServletRequest request, HttpServletResponse response, String theme) {
		throw new IllegalArgumentException("Cannot change theme - use a different theme resolution strategy");
	}

}
