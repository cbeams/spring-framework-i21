package com.interface21.web.servlet.theme;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.interface21.web.servlet.HandlerInterceptor;
import com.interface21.web.servlet.ThemeResolver;
import com.interface21.web.servlet.support.RequestContextUtils;

/**
 * Interceptor that allows for changing the current theme on every request,
 * via a configurable request parameter.
 * @author Juergen Hoeller
 * @since 20.06.2003
 * @see com.interface21.web.servlet.ThemeResolver
 */
public class ThemeChangeInterceptor implements HandlerInterceptor {

	public static final String DEFAULT_PARAMETER_NAME = "theme";

	private String parameterName = DEFAULT_PARAMETER_NAME;

	/**
	 * Set the name of the parameter that contains a theme specification
	 * in a locale change request.
	 * Default is "theme".
	 */
	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}

	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws ServletException {
		ThemeResolver themeResolver = RequestContextUtils.getThemeResolver(request);
		String newTheme = request.getParameter(this.parameterName);
		if (newTheme != null) {
			themeResolver.setThemeName(request, response, newTheme);
		}
		return true;
	}

}
