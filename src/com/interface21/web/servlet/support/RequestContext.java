package com.interface21.web.servlet.support;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;

import com.interface21.context.MessageSourceResolvable;
import com.interface21.context.NoSuchMessageException;
import com.interface21.validation.Errors;
import com.interface21.web.bind.EscapedErrors;
import com.interface21.web.context.WebApplicationContext;
import com.interface21.web.util.HtmlUtils;

/**
 * Context holder for request-specific state, like current web application context,
 * current locale, and possible binding errors.
 *
 * Suitable for exposition to views, and usage within <jsp:useBean>, JSP scriptlets,
 * JSTL EL, Velocity templates, etc. Especially well-suited for views that do not
 * have access to the servlet request, like Velocity templates.
 *
 * @author Juergen Hoeller
 * @since 03.03.2003
 * @see com.interface21.web.servlet.view.AbstractView
 */
public class RequestContext {

	private ServletRequest request = null;

	private WebApplicationContext webApplicationContext = null;

	private Locale locale = null;

	private boolean defaultHtmlEscape = false;

	private Map errorsMap = null;

	/**
	 * Creates a new RequestContext for the given request.
	 */
	public RequestContext(ServletRequest request) throws ServletException {
		this.request = request;
		this.webApplicationContext = RequestContextUtils.getWebApplicationContext(request);
		this.locale = RequestContextUtils.getLocale(request);
	}

	/**
	 * Returns the current WebApplicationContext.
	 */
	public WebApplicationContext getWebApplicationContext() {
		return webApplicationContext;
	}

	/**
	 * Returns the current Locale.
	 */
	public Locale getLocale() {
		return locale;
	}

	/**
	 * (De)actives default HTML escaping for messages and errors.
	 */
	public void setDefaultHtmlEscape(boolean defaultHtmlEscape) {
		this.defaultHtmlEscape = defaultHtmlEscape;
	}

	/**
	 * Default HTML escaping?
	 */
	public boolean isDefaultHtmlEscape() {
		return defaultHtmlEscape;
	}

	/**
	 * Retrieves the message for the given code.
	 * @param code code of the message
	 * @param args arguments for the message, or null if none
	 * @param htmlEscape HTML escape the message?
	 * @return the message
	 */
	public String getMessage(String code, Object[] args, boolean htmlEscape) throws NoSuchMessageException {
		String msg = this.webApplicationContext.getMessage(code, args, locale);
		return (htmlEscape ? HtmlUtils.htmlEscape(msg) : msg);
	}

	/**
	 * Retrieves the message for the given code, using the defaultHtmlEscape setting.
	 * @param code code of the message
	 * @param args arguments for the message, or null if none
	 * @return the message
	 */
	public String getMessage(String code, Object[] args) throws NoSuchMessageException {
		return getMessage(code, args, this.defaultHtmlEscape);
	}

	/**
	 * Retrieves the given MessageSourceResolvable (e.g. an ObjectError instance).
	 * @param resolvable the MessageSourceResolvable
	 * @param htmlEscape HTML escape the message?
	 * @return the message
	 */
	public String getMessage(MessageSourceResolvable resolvable, boolean htmlEscape) throws NoSuchMessageException {
		String msg = this.webApplicationContext.getMessage(resolvable, this.locale);
		return (htmlEscape ? HtmlUtils.htmlEscape(msg) : msg);
	}

	/**
	 * Retrieves the given MessageSourceResolvable (e.g. an ObjectError instance),
	 * using the defaultHtmlEscape setting.
	 * @param resolvable the MessageSourceResolvable
	 * @return the message
	 */
	public String getMessage(MessageSourceResolvable resolvable) throws NoSuchMessageException {
		return getMessage(resolvable, this.defaultHtmlEscape);
	}

	/**
	 * Retrieves the Errors instance for the given bind object.
	 * @param name name of the bind object
	 * @param htmlEscape create an Errors instance with automatic HTML escaping?
	 * @return the Errors instance
	 */
	public Errors getErrors(String name, boolean htmlEscape) {
		if (this.errorsMap == null) {
			this.errorsMap = new HashMap();
		}
		Errors errors = (Errors) this.errorsMap.get(name);
		boolean put = false;
		if (errors == null) {
			errors = RequestContextUtils.getErrors(this.request, name);
			put = true;
		}
		if (htmlEscape && !(errors instanceof EscapedErrors)) {
			errors = new EscapedErrors(errors);
			put = true;
		}
		else if (!htmlEscape && errors instanceof EscapedErrors) {
			errors = ((EscapedErrors) errors).getSource();
			put = true;
		}
		if (put) {
			this.errorsMap.put(name, errors);
		}
		return errors;
	}

	/**
	 * Retrieves the Errors instance for the given bind object,
	 * using the defaultHtmlEscape setting.
	 * @param name name of the bind object
	 * @return the Errors instance
	 */
	public Errors getErrors(String name) {
		return getErrors(name, this.defaultHtmlEscape);
	}

}
