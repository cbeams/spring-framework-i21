package com.interface21.web.servlet.handler;

import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.interface21.context.support.ApplicationObjectSupport;
import com.interface21.core.Ordered;
import com.interface21.web.servlet.HandlerExecutionChain;
import com.interface21.web.servlet.HandlerInterceptor;
import com.interface21.web.servlet.HandlerMapping;

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

	private Object defaultHandler = null;

	private List interceptors;

	public void setOrder(int order) {
	  this.order = order;
	}

	public int getOrder() {
	  return order;
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

	public void setInterceptors(List interceptors) {
		this.interceptors = interceptors;
	}

	/**
	 * Lookup a handler for the given request, falling back to the default
	 * handler if no specific one is found.
	 * @param request current HTTP request
	 * @return the looked up handler instance, or the default handler
	 */
	public final HandlerExecutionChain getHandler(HttpServletRequest request) throws ServletException {
		Object handler = getHandlerInternal(request);
		if (handler == null) {
			handler = this.defaultHandler;
		}
		if (handler == null) {
			return null;
		}
		HandlerInterceptor[] interceptorArray = null;
		if (this.interceptors != null) {
			interceptorArray = (HandlerInterceptor[]) this.interceptors.toArray(new HandlerInterceptor[this.interceptors.size()]);
		}
		return new HandlerExecutionChain(handler, interceptorArray);
	}

	/**
	 * Lookup a handler for the given request, returning null if no specific
	 * one is found. This method is evaluated by getHandler, a null return
	 * value will lead to the default handler, if one is set.
	 * @param request current HTTP request
	 * @return the looked up handler instance, or null
	 */
	protected abstract Object getHandlerInternal(HttpServletRequest request) throws ServletException;

}
