package com.interface21.web.servlet.support;

import java.util.Locale;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;

import org.apache.log4j.Logger;

import com.interface21.context.NoSuchMessageException;
import com.interface21.validation.Errors;
import com.interface21.validation.BindException;
import com.interface21.web.bind.EscapedErrors;
import com.interface21.web.context.WebApplicationContext;
import com.interface21.web.context.support.WebApplicationContextUtils;
import com.interface21.web.servlet.ControllerServlet;
import com.interface21.web.util.HtmlUtils;

/**
 * Utility class for easy access to various request-specific state.
 *
 * @author Juergen Hoeller
 * @since 03.03.2003
 */
public abstract class RequestContextUtils {

	private static final Logger logger = Logger.getLogger(RequestContextUtils.class);

	/**
	 * Look for the WebApplicationContext associated with the controller serlvet that has initiated
	 * request processing, and for the global context if none was found associated with the current request.
	 * This method is useful to allow components outside our framework proper,
	 * such as JSP tag handlers, to access the most specific application context
	 * available.
	 * @return the request-specific or global web application context if no request-specific
	 * context has been set
	 * @throws ServletException if no request-specific or global context can be found
	 */
	public static WebApplicationContext getWebApplicationContext(ServletRequest request, ServletContext sc) throws ServletException {
		WebApplicationContext waca = (WebApplicationContext) request.getAttribute(
				ControllerServlet.WEB_APPLICATION_CONTEXT_ATTRIBUTE);
		if (waca == null && sc != null) {
			waca = WebApplicationContextUtils.getWebApplicationContext(sc);
		}
		if (waca == null) {
			String msg = "No WebApplicationContext found: has ContextLoaderServlet been set to run on startup with index=1?";
			logger.error(msg);
			throw new ServletException(msg);
		}
		return waca;
	}

	public static WebApplicationContext getWebApplicationContext(ServletRequest request) throws ServletException {
		return getWebApplicationContext(request, null);
	}

	/**
	 * Retrieves the current locale from the given request.
	 */
	public static Locale getLocale(ServletRequest request) {
		return (Locale)request.getAttribute(ControllerServlet.LOCALE_ATTRIBUTE);
	}

	/**
	 * Resolves the given message code using the current WebApplicationContext.
	 * @param request  the request to retrieve the WebApplicationContext from
	 * @param code  the code of the message
	 * @param htmlEscape  HTML escape the message?
	 * @return the message
	 */
	public static String getMessage(ServletRequest request, String code, boolean htmlEscape) throws ServletException, NoSuchMessageException {
		String msg = getWebApplicationContext(request).getMessage(code, null, getLocale(request));
		return (htmlEscape ? HtmlUtils.htmlEscape(msg) : msg);
	}

	/**
	 * Resolves the given message code using the current WebApplicationContext.
	 * @param request  the request to retrieve the WebApplicationContext from
	 * @param code  the code of the message
	 * @return the message
	 */
	public static String getMessage(ServletRequest request, String code) throws ServletException, NoSuchMessageException {
		return getMessage(request, code, false);
	}

	/**
	 * Retrieves the Errors instance for the given bind object.
	 * @param request  the request to retrieve the instance from
	 * @param name  the name of the bind object
	 * @param htmlEscape  create an Errors instance with automatic HTML escaping?
	 * @return the Errors instance
	 */
	public static Errors getErrors(ServletRequest request, String name, boolean htmlEscape) {
		Errors errors = (Errors)request.getAttribute(BindException.ERROR_KEY_PREFIX + name);
		return (htmlEscape ? new EscapedErrors(errors) : errors);
	}

	/**
	 * Retrieves the Errors instance for the given bind object.
	 * @param request  the request to retrieve the instance from
	 * @param name  the name of the bind object
	 * @return the Errors instance
	 */
	public static Errors getErrors(ServletRequest request, String name) {
		return getErrors(request, name, false);
	}
}
