package com.interface21.web.servlet.i18n;

import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.interface21.beans.propertyeditors.LocaleEditor;
import com.interface21.web.servlet.HandlerInterceptor;
import com.interface21.web.servlet.LocaleResolver;
import com.interface21.web.servlet.support.RequestContextUtils;

/**
 * Interceptor that allows for changing the current locale on every request,
 * via a configurable request parameter.
 * @author Juergen Hoeller
 * @since 20.06.2003
 * @see com.interface21.web.servlet.LocaleResolver
 */
public class LocaleChangeInterceptor implements HandlerInterceptor {

	public static final String DEFAULT_PARAM_NAME = "locale";

	private String paramName = DEFAULT_PARAM_NAME;

	/**
	 * Set the name of the parameter that contains a locale specification
	 * in a locale change request. Default is "locale".
	 */
	public void setParamName(String paramName) {
		this.paramName = paramName;
	}

	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws ServletException {
		LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
		String newLocale = request.getParameter(this.paramName);
		if (newLocale != null) {
			LocaleEditor localeEditor = new LocaleEditor();
			localeEditor.setAsText(newLocale);
			localeResolver.setLocale(request, response, (Locale) localeEditor.getValue());
		}
		// proceed in any case
		return true;
	}

}
