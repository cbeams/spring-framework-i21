package com.interface21.ui.context;

/**
 * Interface to be implemented by objects that can resolve Themes.
 * This enables parameterization and internationalization of messages
 * for a given theme.
 * @author Jean-Pierre Pawlak
 * @author Juergen Hoeller
 * @see Theme
 */
public interface ThemeSource {

	/**
	 * Return the Theme instance for the given theme name.
	 * The returned Theme will resolve theme-specific messages, codes,
	 * file paths, etc (e.g. CSS and image files in a web environment).
	 * @param themeName name of the theme
	 * @return the respective Theme, or null if none defined
	 */
	Theme getTheme(String themeName);

}
