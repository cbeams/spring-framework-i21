/**
 * Generic framework code included with 
 * <a href="http://www.amazon.com/exec/obidos/tg/detail/-/1861007841/">Expert One-On-One J2EE Design and Development</a>
 * by Rod Johnson (Wrox, 2002). 
 * This code is free to use and modify. However, please
 * acknowledge the source and include the above URL in each
 * class using or derived from this code. 
 * Please contact <a href="mailto:rod.johnson@interface21.com">rod.johnson@interface21.com</a>
 * for commercial support.
 */

package com.interface21.web.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.interface21.beans.BeansException;
import com.interface21.beans.factory.NoSuchBeanDefinitionException;
import com.interface21.context.ApplicationContextException;
import com.interface21.core.OrderComparator;
import com.interface21.web.servlet.handler.BeanNameUrlHandlerMapping;
import com.interface21.web.servlet.i18n.AcceptHeaderLocaleResolver;
import com.interface21.web.servlet.mvc.SimpleControllerHandlerAdapter;
import com.interface21.web.servlet.view.InternalResourceViewResolver;
import com.interface21.web.util.WebUtils;

/**
 * Concrete front controller for use within the Interface21 MVC framework.<br>
 * This class and the MVC approach it delivers is discussed in Chapter 12 of
 * <a href="http://www.amazon.com/exec/obidos/tg/detail/-/1861007841/">Expert One-On-One J2EE Design and Development</a>
 * by Rod Johnson (Wrox, 2002).
 *
 * <p>This servlet is very flexible: It can be used with just about any workflow,
 * with the installation of the appropriate adapter classes.
 *
 * <p>It offers the following functionality that distinguishes it from other MVC frameworks:
 * <ul>
 * <li>It is based around a JavaBeans configuration mechanism.
 * <li>It can use any HandlerMapping implementation - whether standard, or provided
 * as part of an application - to control the routing of requests to handler objects.
 * Additional HandlerMapping objects can be added through defining beans in the
 * servlet's application context that implement the HandlerMapping interface in this
 * package. HandlerMappings can be given any bean name (they are tested by type).
 * <li>It can use any HandlerAdapter (additional HandlerAdapter objects can be added
 * through the application context).
 * <li>Its view resoltion strategy can be specified via a ViewResolver implementation.
 * Standard implementations support mapping URLs to bean names, and explicit mappings.
 * <li>Its locale resolution strategy is determined by a LocaleResolver implementation.
 * Standard implementations work via HTTP accept header, cookie, or session.
 * </ul>
 *
 * <p>A web application can use any number of controller servlets.
 * Each servlet will operate in its own namespace. Only the default name space,
 * and any config objects set for the application as a whole, will be shared.
 *
 * @see HandlerMapping
 * @see HandlerAdapter
 * @see ViewResolver
 * @see LocaleResolver
 * @see com.interface21.web.context.WebApplicationContext
 * @see com.interface21.web.context.ContextLoaderListener
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @version $Revision$
 */
public class DispatcherServlet extends FrameworkServlet {
	
	/**
	 * Well-known name for the LocaleResolver object in the bean factory for
	 * this namespace.
	 */
	public static final String LOCALE_RESOLVER_BEAN_NAME = "localeResolver";

	/**
	 * Well-known name for the ViewResolver object in the bean factory for
	 * this namespace.
	 */
	public static final String VIEW_RESOLVER_BEAN_NAME = "viewResolver";
	
	/**
	 * Request attribute to hold current web application context.
	 * Otherwise only the global web app context is obtainable by tags etc.
	 */
	public static final String WEB_APPLICATION_CONTEXT_ATTRIBUTE = DispatcherServlet.class.getName() + ".CONTEXT";

	/**
	 * Request attribute to hold current locale, retrievable by views.
	 * @see com.interface21.web.servlet.support.RequestContext
	 */
	public static final String LOCALE_ATTRIBUTE = DispatcherServlet.class.getName() + ".LOCALE";


	/** LocaleResolver used by this servlet */
	private LocaleResolver localeResolver;

	/** List of HandlerMappings */
	private List handlerMappings;

	/** List of HandlerAdapters */
	private List handlerAdapters;

	/** ViewResolver used by this servlet */
	private ViewResolver viewResolver;


	/**
	 * Overridden method, invoked after any bean properties have been set and the
	 * WebApplicationContext and BeanFactory for this namespace is available.
	 * <p>Loads HandlerMapping and HandlerAdapter objects, and configures a
	 * ViewResolver and a LocaleResolver.
	 */
	protected void initFrameworkServlet() throws ServletException {
		initLocaleResolver();
		initHandlerMappings();
		initHandlerAdapters();
		initViewResolver();
	}


	/**
	 * Initialize the LocaleResolver used by this class.
	 * If no bean is defined with the given name in the BeanFactory
	 * for this namespace, we default to a AcceptHeaderLocaleResolver.
	 */
	private void initLocaleResolver() throws ServletException {
		try {
			this.localeResolver = (LocaleResolver) getWebApplicationContext().getBean(LOCALE_RESOLVER_BEAN_NAME);
			logger.info("Loaded localeResolver [" + this.localeResolver + "]");
		}
		catch (NoSuchBeanDefinitionException ex) {
			// We need to use the default
			logger.info("Unable to load locale resolver: bean with name '" + LOCALE_RESOLVER_BEAN_NAME + "': using default LocaleResolver [" + this.localeResolver + "]");
			this.localeResolver = new AcceptHeaderLocaleResolver();
		}
		catch (BeansException ex) {
			// We tried and failed to load the ViewResolver specified by a bean
			throw new ServletException("Fatal error loading locale resolver: bean with name '" + LOCALE_RESOLVER_BEAN_NAME + "' is required in servlet '" + getServletName()  + "': using default", ex);
		}
	}


	/**
	 * Initialize the HandlerMappings used by this class.
	 * If no HandlerMapping beans are defined in the BeanFactory
	 * for this namespace, we default to a BeanNameUrlHandlerMapping.
	 */
	private void initHandlerMappings() throws ServletException {
		this.handlerMappings = new ArrayList();

		// Find all HandlerMappings in the ApplicationContext
		String[] maps = getWebApplicationContext().getBeanDefinitionNames(HandlerMapping.class);
		for (int i = 0; i < maps.length; i++) {
			initHandlerMapping(maps[i]);
		}

		// Ensure we have at least one HandlerMapping, by registering
		// a default HandlerMapping if no other mappings are found.
		if (this.handlerMappings.isEmpty()) {
			logger.info("No HandlerMappings found in servlet '" + getServletName() + "': using default");
			initDefaultHandlerMapping();
		}
		else {
			// We keep HandlerMappings in sorted order
			Collections.sort(this.handlerMappings, new OrderComparator());
		}
	}


	/**
	 * Initialize the given HandlerMapping object.
	 * @param beanName bean beanName in the current web application context
	 */
	private void initHandlerMapping(String beanName) throws ServletException {
		try {
			HandlerMapping hm = (HandlerMapping) getWebApplicationContext().getBean(beanName);
			hm.setLocaleResolver(this.localeResolver);
			hm.initHandlerMapping();
			this.handlerMappings.add(hm);
		}
		catch (ApplicationContextException ex) {
			// We don't need to catch NoSuchBeanDefinitionException:
			// we got the name from the bean factory.
			throw new ServletException("Error initializing HandlerMapping bean '" + beanName + "': " + ex.getMessage(), ex);
		}
	}


	/**
	 * Initialize the default HandlerMapping object, a BeanNameUrlHandlerMapping.
	 */
	private void initDefaultHandlerMapping() throws ServletException {
		try {
			HandlerMapping hm = new BeanNameUrlHandlerMapping();
			hm.setApplicationContext(getWebApplicationContext());
			hm.setLocaleResolver(this.localeResolver);
			hm.initHandlerMapping();
			this.handlerMappings.add(hm);
		}
		catch (ApplicationContextException ex) {
			throw new ServletException("Error initializing default HandlerMapping: " + ex.getMessage(), ex);
		}
	}


	/**
	 * Initialize the HandlerAdapters used by this class.
	 * If no HandlerAdapter beans are defined in the BeanFactory
	 * for this namespace, we default to a SimpleControllerHandlerAdapter.
	 */
	private void initHandlerAdapters() throws ServletException {
		this.handlerAdapters = new ArrayList();

		String[] handlers = getWebApplicationContext().getBeanDefinitionNames(HandlerAdapter.class);
		for (int i = 0; i < handlers.length; i++) {
			initHandlerAdapter(handlers[i]);
		}

		// Ensure we have at least one HandlerAdapter, by registering
		// a default HandlerAdapter if no other adapters are found.
		if (this.handlerAdapters.isEmpty()) {
			logger.info("No HandlerAdapters found in servlet '" + getServletName() + "': using default");
			initDefaultHandlerAdapter();
		}
		else {
			// We keep HandlerAdapters in sorted order
			Collections.sort(this.handlerAdapters, new OrderComparator());
		}
	}


	/**
	 * Initialize the handler bean with the given name in the bean factory.
	 * @param beanName bean name in the current web application context
	 * @throws ServletException if there is an error trying to instantiate and initialize the handler bean
	 */
	private void initHandlerAdapter(String beanName) throws ServletException {
		try {
			HandlerAdapter ha = (HandlerAdapter) getWebApplicationContext().getBean(beanName);
			this.handlerAdapters.add(ha);
		}
		catch (BeansException ex) {
			// We don't need to catch NoSuchBeanDefinitionException:
			// we got the name from the bean factory.
			throw new ServletException("Error initializing HandlerAdapter bean '" + beanName + "': " + ex.getMessage(), ex);
		}
	}


	/**
	 * Initialize the default HandlerAdapter, a SimpleControllerHandlerAdapter.
	 */
	private void initDefaultHandlerAdapter() throws ServletException {
		try {
			logger.info("Dispatcher servlet '" + getServletName() + "': Initializing default HandlerAdapter");
			HandlerAdapter ha = new SimpleControllerHandlerAdapter();
			ha.setApplicationContext(getWebApplicationContext());
			this.handlerAdapters.add(ha);
		}
		catch (ApplicationContextException ex) {
			throw new ServletException("Error initializing default HandlerAdapter: " + ex.getMessage(), ex);
		}
	}


	/**
	 * Initialize the ViewResolver used by this class.
	 * If no bean is defined with the given name in the BeanFactory
	 * for this namespace, we default to a InternalResourceViewResolver.
	 */
	private void initViewResolver() throws ServletException {
		try {
			this.viewResolver = (ViewResolver) getWebApplicationContext().getBean(VIEW_RESOLVER_BEAN_NAME);
			logger.info("Loaded viewResolver [" + viewResolver + "]");
		}
		catch (NoSuchBeanDefinitionException ex) {
			// We need to use the default
			// NOW: now InternalResourceViewResolver without prefix or suffix -> direct resource url
			// WAS: ResourceBundleViewResolver -> need for mappings in views.properties file
			logger.info("Unable to load view resolver: bean with name '" + VIEW_RESOLVER_BEAN_NAME + "': using default ViewResolver [" + this.viewResolver + "]");
			this.viewResolver = new InternalResourceViewResolver();
			try {
				this.viewResolver.setApplicationContext(getWebApplicationContext());
			} catch (ApplicationContextException ex2) {
				throw new ServletException("Fatal error initializing default ViewResolver");
			}
		}
		catch (BeansException ex) {
			// We tried and failed to load the ViewResolver specified by a bean
			throw new ServletException("Fatal error loading view resolver: bean with name '" + VIEW_RESOLVER_BEAN_NAME + "' is required in servlet '" + getServletName()  + "': using default", ex);
		}
	}


	/**
	 * Obtain and use the handler for this method.
	 * The handler will be obtained by applying the servlet's HandlerMappings in order.
	 * The HandlerAdapter will be obtained by querying the servlet's
	 * installed HandlerAdapters to find the first that supports the handler class.
	 * Both doGet() and doPost() are handled by this method.
	 * It's up to HandlerAdapters to decide which methods are acceptable.
	 */
	protected void doService(HttpServletRequest request, HttpServletResponse response)
	    throws ServletException, IOException {

		logger.debug("DispatcherServlet with name '" + getServletName() + "' received request for [" + request.getRequestURI() + "]");

		// Make web application context available
		request.setAttribute(WEB_APPLICATION_CONTEXT_ATTRIBUTE, getWebApplicationContext());

		// Make locale available
		Locale locale = this.localeResolver.resolveLocale(request);
		request.setAttribute(LOCALE_ATTRIBUTE, locale);
		response.setLocale(locale);

		Object mappedHandler = getHandler(request);

		if (mappedHandler == null) {
			// If we didn't find a handler
			logger.error("No mapping for [" + request.getRequestURI() + "] in DispatcherServlet with name '" + getServletName() + "'");
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		// This will throw an exception if no handler is found
		HandlerAdapter ha = getHandlerAdapter(mappedHandler);

		if (wasRevalidated(request, response, ha, mappedHandler)) {
			return;
		}

		ModelAndView mv = ha.handle(request, response, mappedHandler);
		if (mv != null) {
			logger.debug("Will render model in DispatcherServlet with name '" + getServletName() + "'");
			render(mv, request, response, locale);
		}
		else {
			logger.debug("Null ModelAndView returned to DispatcherServlet with name '" + getServletName() + "': assuming HandlerAdapter completed request handling");
		}
	}
	
	
	/**
	 * Implementation method to support HTTP cache control.
	 * Was the request successfully revalidated?
	 * In this case we can return.
	 */
	private boolean wasRevalidated(HttpServletRequest request, HttpServletResponse response,
	                               HandlerAdapter ha, Object mappedHandler) {
		// Based on code from javax.servlet.HttpServlet from Apache Servlet API
		// HttpServlet checks getLastModified() before calling doGet(), so we need to
		// leave the default implementation of getLastModified() to return -1, before
		// handling unmodified requests here.
		if (!"GET".equals(request.getMethod())) 
			return false;
			
		// Apply last modified rule
		long ifModifiedSince = request.getDateHeader(WebUtils.HEADER_IFMODSINCE);
		if (ifModifiedSince == -1)
			return false;

		// We have an if modified since header (value is -1 if we don't)
		logger.debug("GET request: " + WebUtils.HEADER_IFMODSINCE + " request header present");
		long lastModified = ha.getLastModified(request, mappedHandler);

		if (lastModified == -1 || ifModifiedSince < lastModified) {
			// lastModified was -1, indicating it wasn't understood,
			// or was earlier than the servlet mod date.
			// We need to regenerate content.
			logger.debug("GET request: " + WebUtils.HEADER_IFMODSINCE + " request header contains an earlier date than lastModified date. Will regenerate content and reset lastModified header." );
			setLastModifiedIfNecessary(response, lastModified);
			return false;
		}
		else {
			// If mod header present and shows a later date than last modified date on the resource
			logger.debug("GET request: " + WebUtils.HEADER_IFMODSINCE + " request header contains a later date than lastModified date, or revalidation isn't supported. Indicating unmodified.");
			response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
			return true;
		}
	}	// wasRevalidated
	

	/*
	 * Sets the Last-Modified entity header field, if it has not
	 * already been set and if the value is meaningful.  Called before
	 * doGet, to ensure that headers are set before response data is
	 * written.  A subclass might have set this header already, so we
	 * check.
	 */
	private void setLastModifiedIfNecessary(HttpServletResponse response, long lastModified) {
		// Based on code from javax.servlet.HttpServlet
		if (!response.containsHeader(WebUtils.HEADER_LASTMOD) && lastModified >= 0)
			response.setDateHeader(WebUtils.HEADER_LASTMOD, lastModified);
	}


	/**
	 * Return the handler for this request.
	 * Try all handler mappings in order.
	 * @return null if no handler could be found
	 * @throws ServletException
	 */
	private Object getHandler(HttpServletRequest request) throws ServletException {
		Iterator itr = this.handlerMappings.iterator();
		while (itr.hasNext()) {
			HandlerMapping hm = (HandlerMapping) itr.next();
			logger.debug("Testing handler map [" + hm  + "] in DispatcherServlet with name '" + getServletName() + "'");
			Object handler = hm.getHandler(request);
			if (handler != null)
				return handler;
		}
		return null;
	}


	/**
	 * Return the HandlerAdapter for this handler class.
	 * @throws ServletException if no HandlerAdapter can be found for the handler.
	 * This is a fatal error.
	 */
	private HandlerAdapter getHandlerAdapter(Object handler) throws ServletException {
		Iterator itr = this.handlerAdapters.iterator();
		while (itr.hasNext()) {
			HandlerAdapter ha = (HandlerAdapter) itr.next();
			logger.debug("Testing handler adapter [" + ha + "]");
			if (ha.supports(handler)) {
				return ha;
			}
		}
		throw new ServletException("No adapter for handler " + handler);
	}


	/**
	 * Render the given ModelAndView. This is the last stage in handling a request.
	 * It may involve resolving the view by name.
	 * @throws IOException if there's a problem rendering the view
	 * @throws ServletException if the view cannot be resolved.
	 */
	private void render(ModelAndView mv, HttpServletRequest request, HttpServletResponse response, Locale locale)
	    throws ServletException, IOException {
		View v = null;
		if (mv.isReference()) {
			// We need to resolve this viewname
			v = this.viewResolver.resolveViewname(mv.getViewName(), locale);
		}
		else {
			// No need to lookup: the ModelAndView object contains the actual view
			v = mv.getView();
		}
		if (v == null) {
			throw new ServletException("Error in ModelAndView object or View resolution encountered by servlet with name '" + getServletName() + "'. View cannot be null in render with ModelAndView=[" + mv + "]");
		}
		v.render(mv.getModel(), request, response);
	}

}
