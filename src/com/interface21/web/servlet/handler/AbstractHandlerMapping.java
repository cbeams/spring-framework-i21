package com.interface21.web.servlet.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.interface21.context.support.ApplicationObjectSupport;
import com.interface21.core.Ordered;
import com.interface21.web.servlet.HandlerMapping;
import com.interface21.web.servlet.LocaleResolver;
import com.interface21.web.servlet.LocaleResolverAware;

/**
 * Abstract base class for HandlerMapping implementations.
 * Provides the basic infrastructure and a handler initialization
 * method that cares about LocaleResolver.
 * @author Juergen Hoeller
 * @since 07.04.2003
 */
public abstract class AbstractHandlerMapping extends ApplicationObjectSupport implements HandlerMapping, Ordered {

	protected final Log logger = LogFactory.getLog(getClass());

	private LocaleResolver localeResolver;

	private int order = Integer.MAX_VALUE;  // default: same as non-Ordered

	public void setLocaleResolver(LocaleResolver localeResolver) {
		this.localeResolver = localeResolver;
	}

	public void setOrder(int order) {
	  this.order = order;
	}

	public int getOrder() {
	  return order;
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
	}

}
