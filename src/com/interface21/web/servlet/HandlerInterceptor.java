package com.interface21.web.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Workflow interface that allows for customized handler execution chains.
 * Applications can register any number of existing or custom interceptors
 * for certain groups of handlers, to add common preprocessing behavior
 * without needing to modify each handler implementation.
 *
 * <p>A HandlerInterceptor gets called before the appropriate HandlerAdapter
 * triggers the execution of the handler itself. This mechanism can be used
 * for a large field of preprocessing aspects, e.g. for authorization checks,
 * or common handler behavior like locale or theme changes. Its main purpose
 * is to allow for factoring out repetitive handler code.
 *
 * <p>Typically an interceptor chain is defined per HandlerMapping bean,
 * sharing its granularity. To be able to apply a certain interceptor chain
 * to a group of handlers, one needs to map the desired handlers via one
 * HandlerMapping bean. The interceptors themselves are defined as beans
 * in the application context, referenced by the mapping bean definition
 * via its "interceptors" property (in XML: a <list> of <ref>s).
 *
 * <p>HandlerInterceptor is basically similar to a Servlet 2.3 Filter, but in
 * contrast to the latter it just allows custom preprocessing with the option
 * prohibiting the execution of the handler itself. Filters are more powerful,
 * for example they allow for exchanging the request and response objects that
 * are handed down the chain, and for custom postprocessing. Note that a filter
 * gets configured in web.xml, a HandlerInterceptor in the application context. 
 *
 * <p>As a basic guideline, fine-grained handler-related preprocessing tasks are
 * candidates for HandlerInterceptor implementations, especially factored-out
 * common handler code and authorization checks. On the other hand, a Filter
 * is well-suited for request content and view content handling, like multipart
 * forms and GZIP compression. This typically shows when one needs to map the
 * filter to certain content types (e.g. images), or to all requests.
 *
 * @author Juergen Hoeller
 * @since 20.06.2003
 * @see HandlerExecutionChain#getInterceptors
 * @see com.interface21.web.servlet.handler.AbstractHandlerMapping#setInterceptors
 * @see com.interface21.web.servlet.support.UserRoleAuthorizationInterceptor
 * @see com.interface21.web.servlet.i18n.LocaleChangeInterceptor
 * @see com.interface21.web.servlet.theme.ThemeChangeInterceptor
 * @see javax.servlet.Filter
 */
public interface HandlerInterceptor {

	/**
	 * Intercept the execution of a handler. Called after HandlerMapping determined
	 * an appropriate handler object, but before HandlerAdapter invokes the handler.
	 * <p>DispatcherServlet processes a handler in an execution chain, consisting
	 * of any number of interceptors, with the handler itself at the end.
	 * Each interceptor can decide to abort the execution chain, typically sending
	 * a HTTP error or writing a custom response.
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @param handler chosen handler to execute, for type and/or instance evaluation
	 * @return if the execution chain should proceed with the next interceptor resp.
	 * the handler itself, else DispatcherServlet assumes that this interceptor has
	 * already dealed with the response
	 * @throws ServletException if there is an internal error
	 * @throws IOException in case of an I/O error when writing the response
	 */
	boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
	    throws ServletException, IOException;

}
