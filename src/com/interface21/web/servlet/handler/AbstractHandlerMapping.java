package com.interface21.web.servlet.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.interface21.context.support.ApplicationObjectSupport;
import com.interface21.core.Ordered;
import com.interface21.web.servlet.HandlerMapping;
import com.interface21.web.servlet.LocaleResolver;
import com.interface21.web.servlet.LocaleResolverAware;
import com.interface21.web.servlet.ThemeResolver;
import com.interface21.web.servlet.ThemeResolverAware;

/**
 * Abstract base class for HandlerMapping implementations.
 * Provides the basic infrastructure and a handler initialization method that
 * cares about LocaleResolver. Supports a default handler.
 * @author Juergen Hoeller
 * @since 07.04.2003
 * @see #getHandlerInternal
 */
public abstract class AbstractHandlerMapping extends ApplicationObjectSupport implements HandlerMapping, Ordered {

	protected final Log logger = LogFactory.getLog(getClass());

	private int order = Integer.MAX_VALUE;  // default: same as non-Ordered

	private LocaleResolver localeResolver;

	private ThemeResolver themeResolver;

	private Object defaultHandler = null;

	public void setOrder(int order) {
	  this.order = order;
	}

	public int getOrder() {
	  return order;
	}

	public void setLocaleResolver(LocaleResolver localeResolver) {
		this.localeResolver = localeResolver;
	}

	public void setThemeResolver(ThemeResolver themeResolver) {
		this.themeResolver = themeResolver;
	}

	/**
	 * Set the default handler.
	 * @param defaultHandler default handler instance, or null if none
	 */
	public void setDefaultHandler(Object defaultHandler) {
		this.defaultHandler = defaultHandler;
		logger.info("Default mapping is to controller [" + this.defaultHandler + "]");
	}

	/**
	 * Return the default handler.
	 * @return the default handler instance, or null if none
	 */
	public Object getDefaultHandler() {
		return defaultHandler;
	}

	/**
	 * Initialize the given handler instance, i.e. set the
	 * LocaleResolver if aware. To be used by subclasses.
	 * @param handler the handler instance
	 */
	protected void initHandler(Object handler) {
		if (handler instanceof LocaleResolverAware) {
			((LocaleResolverAware) handler).setLocaleResolver(this.localeResolver);
		}
		if (handler instanceof ThemeResolverAware) {
			((ThemeResolverAware) handler).setThemeResolver(this.themeResolver);
		}
	}

	/**
	 * Lookup a handler for the given request, falling back to the default
	 * handler if no specific one is found.
	 * @param request current HTTP request
	 * @return the looked up handler instance, or the default handler
	 */
	public final Object getHandler(HttpServletRequest request) throws ServletException {
		Object handler = getHandlerInternal(request);
		return (handler != null ? handler : this.defaultHandler);
	}

	/**
	 * Lookup a handler for the given request, returning null if no specific
	 * one is found. This method is evaluated by getHandler, a null return
	 * value will lead to the default handler, if one is set.
	 * @param request current HTTP request
	 * @return the looked up handler instance, or null
	 */
	protected abstract Object getHandlerInternal(HttpServletRequest request);

}
