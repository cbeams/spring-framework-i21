package com.interface21.web.servlet.theme;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.interface21.web.servlet.ThemeResolver;
import com.interface21.web.util.WebUtils;

/**
 * Implementation of ThemeResolver that uses a locale attribute in the user's
 * session in case of a custom setting, with a fallback to the fixed default theme.
 * This is most appropriate if the application needs user sessions anyway.
 *
 * <p>Custom controllers can override the user's theme by calling setTheme,
 * e.g. responding to a theme change request.
 *
 * @author Juergen Hoeller
 * @since 27.02.2003
 */
public class SessionThemeResolver implements ThemeResolver {

	private String defaultTheme = FixedThemeResolver.DEFAULT_THEME;
	
	public static final String THEME_ATTRIBUTE_NAME = SessionThemeResolver.class.getName() + ".THEME";

	public String resolveTheme(HttpServletRequest request) {
		String theme = (String) WebUtils.getSessionAttribute(request, THEME_ATTRIBUTE_NAME);
		// specific theme, or fallback to default?
		return (theme != null ? theme : defaultTheme);
	}

	public void setTheme(HttpServletRequest request, HttpServletResponse response, String theme) {
		WebUtils.setSessionAttribute(request, THEME_ATTRIBUTE_NAME, theme);
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
