package com.interface21.web.servlet.theme;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.interface21.web.servlet.ThemeResolver;

/**
 * Abstract base class for ThemeResolver implementations.
 * Provides support for a default theme name.
 * @author Juergen Hoeller
 * @author Jean-Pierre Pawlak
 * @since 17.06.2003
 */
public abstract class AbstractThemeResolver implements ThemeResolver {

	public final static String ORIGINAL_DEFAULT_THEME_NAME = "theme";

	private String defaultThemeName = ORIGINAL_DEFAULT_THEME_NAME;

	/**
	 * Set the name of the default theme.
	 * @param defaultThemeName new default theme name
	 */
	public void setDefaultThemeName(String defaultThemeName) {
		this.defaultThemeName = defaultThemeName;
	}

	/**
	 * Return the name of the default theme.
	 * @return the default theme name
	 */
	public String getDefaultThemeName() {
		return defaultThemeName;
	}

	/**
	 * Make the theme name available for the view.
	 */
	public void makeThemeNameAvailable(HttpServletRequest request, HttpServletResponse response) {
		setThemeName(request, response, resolveThemeName(request));
	}

}
