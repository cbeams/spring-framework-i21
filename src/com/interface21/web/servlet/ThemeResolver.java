package com.interface21.web.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Interface for web-based theme resolution strategies that allows for
 * both theme resolution via the request and locale modification via
 * request and response.
 *
 * This interface allows for implementations based on request, session,
 * cookies, etc. The default implementantion is AcceptHeaderLocaleResolver,
 * simply using the default theme.
 *
 * @author Juergen Hoeller
 * @since 27.02.2003
 * @see com.interface21.web.servlet.i18n.AcceptHeaderLocaleResolver
 */
public interface ThemeResolver {

  /**
   * Resolve the current theme via the given request.
   * Should return a default theme as fallback in any case.
   * @param request  the request to be used for resolution
   * @return the current theme name
   */
	String resolveTheme(HttpServletRequest request);

  /**
   * Set the current theme to the given one.
   * @param request  the request to be used for theme modification
   * @param response  the response to be used for theme modification
   * @param theme  the new theme name
   */
	void setTheme(HttpServletRequest request, HttpServletResponse response, String theme);
}
