package com.interface21.web.servlet.support;

import java.util.Locale;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;

import com.interface21.context.MessageSourceResolvable;
import com.interface21.context.NoSuchMessageException;
import com.interface21.validation.BindException;
import com.interface21.validation.Errors;
import com.interface21.web.bind.EscapedErrors;
import com.interface21.web.context.WebApplicationContext;
import com.interface21.web.context.support.WebApplicationContextUtils;
import com.interface21.web.servlet.DispatcherServlet;
import com.interface21.web.util.HtmlUtils;

/**
 * Utility class for easy access to various request-specific state.
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
	 * Retrieves the current locale from the given request.
	 * @param request current HTTP request
	 * @return the current locale
	 */
	public static Locale getLocale(ServletRequest request) {
		return (Locale) request.getAttribute(DispatcherServlet.LOCALE_ATTRIBUTE);
	}

	/**
	 * Resolves the given message code using the current WebApplicationContext.
	 * @param request request to retrieve the WebApplicationContext from
	 * @param code code of the message
	 * @param args arguments for the message, or null if none
	 * @param htmlEscape HTML escape the message?
	 * @return the message
	 */
	public static String getMessage(ServletRequest request, String code, Object[] args, boolean htmlEscape)
	    throws ServletException, NoSuchMessageException {
		WebApplicationContext context = getWebApplicationContext(request);
		String msg = context.getMessage(code, args, getLocale(request));
		return (htmlEscape ? HtmlUtils.htmlEscape(msg) : msg);
	}

	/**
	 * Resolves the given message code using the current WebApplicationContext.
	 * @param request request to retrieve the WebApplicationContext from
	 * @param code code of the message
	 * @param args arguments for the message, or null if none
	 * @return the message
	 */
	public static String getMessage(ServletRequest request, String code, Object[] args)
	    throws ServletException, NoSuchMessageException {
		return getMessage(request, code, args, false);
	}

	/**
	 * Resolves the given message code using the current WebApplicationContext.
	 * @param request request to retrieve the WebApplicationContext from
	 * @param resolvable the MessageSourceResolvable
	 * @param htmlEscape HTML escape the message?
	 * @return the message
	 */
	public static String getMessage(ServletRequest request, MessageSourceResolvable resolvable, boolean htmlEscape)
	    throws ServletException, NoSuchMessageException {
		String msg = getWebApplicationContext(request).getMessage(resolvable, getLocale(request));
		return (htmlEscape ? HtmlUtils.htmlEscape(msg) : msg);
	}

	/**
	 * Resolves the given message code using the current WebApplicationContext.
	 * @param request request to retrieve the WebApplicationContext from
	 * @param resolvable the MessageSourceResolvable
	 * @return the message
	 */
	public static String getMessage(ServletRequest request, MessageSourceResolvable resolvable)
	    throws ServletException, NoSuchMessageException {
		return getMessage(request, resolvable, false);
	}

	/**
	 * Retrieves the Errors instance for the given bind object.
	 * @param request request to retrieve the instance from
	 * @param name name of the bind object
	 * @param htmlEscape create an Errors instance with automatic HTML escaping?
	 * @return the Errors instance
	 */
	public static Errors getErrors(ServletRequest request, String name, boolean htmlEscape) {
		Errors errors = (Errors)request.getAttribute(BindException.ERROR_KEY_PREFIX + name);
		return (htmlEscape ? new EscapedErrors(errors) : errors);
	}

	/**
	 * Retrieves the Errors instance for the given bind object.
	 * @param request request to retrieve the instance from
	 * @param name name of the bind object
	 * @return the Errors instance
	 */
	public static Errors getErrors(ServletRequest request, String name) {
		return getErrors(request, name, false);
	}

}
