package com.interface21.web.servlet.support;

import java.util.Locale;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import com.interface21.ui.context.Theme;
import com.interface21.web.context.WebApplicationContext;
import com.interface21.web.context.support.WebApplicationContextUtils;
import com.interface21.web.servlet.DispatcherServlet;
import com.interface21.web.servlet.LocaleResolver;
import com.interface21.web.servlet.ThemeResolver;

/**
 * Utility class for easy access to various request-specific state,
 * set by the DispatcherServlet.
 *
 * @author Juergen Hoeller
 * @since 03.03.2003
 */
public abstract class RequestContextUtils {

	/**
	 * Look for the WebApplicationContext associated with the controller servlet that has
	 * initiated request processing.
	 * @param request current HTTP request
	 * @return the request-specific web application context
	 */
	public static WebApplicationContext getWebApplicationContext(ServletRequest request) throws ServletException {
		return getWebApplicationContext(request, null);
	}

	/**
	 * Look for the WebApplicationContext associated with the controller servlet that has
	 * initiated request processing, and for the global context if none was found associated
	 * with the current request. This method is useful to allow components outside our framework,
	 * such as JSP tag handlers, to access the most specific application context available.
	 * @param request current HTTP request
	 * @param servletContext current servlet context
	 * @return the request-specific or global web application context if no request-specific
	 * context has been set
	 */
	public static WebApplicationContext getWebApplicationContext(ServletRequest request, ServletContext servletContext)
	    throws ServletException {
		WebApplicationContext webApplicationContext = (WebApplicationContext) request.getAttribute(
				DispatcherServlet.WEB_APPLICATION_CONTEXT_ATTRIBUTE);
		if (webApplicationContext == null) {
			if (servletContext == null) {
				throw new ServletException("No WebApplicationContext found: not in a DispatcherServlet request?");
			}
			webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
			if (webApplicationContext == null) {
				throw new ServletException("No WebApplicationContext found: no ContextLoaderListener registered?");
			}
		}
		return webApplicationContext;
	}

	/**
	 * Retrieves the current locale from the given request,
	 * using the LocaleResolver bound to the request by the DispatcherServlet.
	 * @param request current HTTP request
	 * @return the current locale
	 */
	public static Locale getLocale(HttpServletRequest request) {
		LocaleResolver localeResolver = (LocaleResolver) request.getAttribute(DispatcherServlet.LOCALE_RESOLVER_ATTRIBUTE);
		return localeResolver.resolveLocale(request);
	}

	/**
	 * Retrieves the current theme from the given request,
	 * using the ThemeResolver bound to the request by the DispatcherServlet,
	 * and the current WebApplicationContext.
	 * @param request current HTTP request
	 * @return the current theme
	 */
	public static Theme getTheme(HttpServletRequest request) throws ServletException {
		WebApplicationContext context = getWebApplicationContext(request);
		ThemeResolver themeResolver = (ThemeResolver) request.getAttribute(DispatcherServlet.THEME_RESOLVER_ATTRIBUTE);
		String themeName = themeResolver.resolveThemeName(request);
		return context.getTheme(themeName);
	}

}
