package com.interface21.web.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Interface for web-based theme resolution strategies that allows for
 * both theme resolution via the request and theme modification via
 * request and response.
 *
 * <p>This interface allows for implementations based on session,
 * cookies, etc. The default implementation is FixedThemeResolver,
 * simply using a configured default theme.
 *
 * <p>Note that this resolver is only responsible for determining the
 * current theme name. The Theme instance for the resolved theme name
 * gets looked up by DispatcherServlet via the respective ThemeSource,
 * i.e. the current WebApplicationContext.
 *
 * @author Jean-Pierre Pawlak
 * @author Juergen Hoeller
 * @since 17.06.2003
 * @see com.interface21.web.servlet.theme.FixedThemeResolver
 * @see com.interface21.ui.context.Theme
 * @see com.interface21.ui.context.ThemeSource
 */
public interface ThemeResolver {

  /**
   * Resolve the current theme name via the given request.
   * Should return a default theme as fallback in any case.
   * @param request request to be used for resolution
   * @return the current theme name
   */
	String resolveThemeName(HttpServletRequest request);

  /**
   * Set the current theme name to the given one.
   * @param request request to be used for theme name modification
   * @param response response to be used for theme name modification
   * @param themeName the new theme name
   */
	void setThemeName(HttpServletRequest request, HttpServletResponse response, String themeName);
	
}
