package com.interface21.web.servlet;

/**
 * Handler execution chain, consisting of handler object and any
 * preprocessing interceptors. Returned by HandlerMapping's
 * getHandler method.
 * @author Juergen Hoeller
 * @since 20.06.2003
 * @see HandlerMapping#getHandler
 * @see HandlerInterceptor
 */
public class HandlerExecutionChain {

	private Object handler;

	private HandlerInterceptor[] interceptors;

	/**
	 * Create new HandlerExecutionChain.
	 * @param handler the handler object to execute
	 */
	public HandlerExecutionChain(Object handler) {
		this.handler = handler;
	}

	/**
	 * Create new HandlerExecutionChain.
	 * @param handler the handler object to execute
	 * @param interceptors the array of interceptors to apply
	 * (in the given order) before the handler itself executes
	 */
	public HandlerExecutionChain(Object handler, HandlerInterceptor[] interceptors) {
		this.handler = handler;
		this.interceptors = interceptors;
	}

	/**
	 * Return the handler object to execute.
	 * @return the handler object (should not be null)
	 */
	public Object getHandler() {
		return handler;
	}

	/**
	 * Return the array of interceptors to apply (in the given order)
	 * before the handler itself executes.
	 * @return the array of HandlerInterceptors instances (may be null)
	 */
	public HandlerInterceptor[] getInterceptors() {
		return interceptors;
	}

}
