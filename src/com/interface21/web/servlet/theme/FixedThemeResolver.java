package com.interface21.web.servlet.theme;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.interface21.web.servlet.ThemeResolver;

/**
 * Implementation of LocaleResolver that simply uses the primary locale
 * specified in the "accept-language" header of the HTTP request
 * (i.e., the locale sent by the client browser, normally that of the client's OS).
 *
 * <p>Note: Does not support setLocale, because the accept header cannot be changed.
 *
 * @author Juergen Hoeller
 * @since 27.02.2003
 */
public class FixedThemeResolver implements ThemeResolver {

	public final static String DEFAULT_THEME = "theme";

	private String defaultTheme = DEFAULT_THEME;

	public String resolveTheme(HttpServletRequest request) {
		return defaultTheme;
	}

	public void setTheme(HttpServletRequest request, HttpServletResponse response, String theme) {
		throw new IllegalArgumentException("cannot change theme - use a different theme resolution strategy");
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
