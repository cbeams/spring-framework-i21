package com.interface21.web.servlet.theme;

import com.interface21.web.servlet.ThemeResolver;

/**
 * Abstract base class for ThemeResolver implementation.
 * Provides support for a default theme.
 * @author Juergen Hoeller
 * @since 17.06.2003
 */
public abstract class AbstractThemeResolver implements ThemeResolver {

	public final static String DEFAULT_THEME = "theme";

	private String defaultThemeName = DEFAULT_THEME;

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

}
