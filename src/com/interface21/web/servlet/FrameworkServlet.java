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
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import javax.servlet.ServletException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.interface21.context.ApplicationContext;
import com.interface21.context.ApplicationEvent;
import com.interface21.web.context.RequestHandledEvent;
import com.interface21.web.context.WebApplicationContext;
import com.interface21.web.context.support.XmlWebApplicationContext;
import com.interface21.web.context.support.WebApplicationContextUtils;

/**
 * Base servlet for servlets within the Interface21 framework. Allows
 * integration with bean factory and application context, in a
 * JavaBean based overall solution.
 * <br>This class offers the following functionality:
 * <li>Uses a WebApplicationContext to access a BeanFactory. The
 * servlet's configuration is determined by the beans in the namespace
 * 'servlet-name'-servlet.
 * <li>Debug capabilities
 * <li>Publishes events on request processing, whether or not
 * a request is successfully handled.
 * <p/>Because this extends HttpServletBean, rather than HttpServlet directly,
 * bean properties are mapped onto it.
 * <p/>Subclasses must implement two abstract methods:
 * initFrameworkServlet(), and doService(), which handles requests
 * to the servlet.
 * @author Rod Johnson
 * @version $Revision$
 */
public abstract class FrameworkServlet extends HttpServletBean {

	/**
	 * Suffix for namespace bean factory names. If a servlet of this class is
	 * given the name 'test' in a context, the namespace used by the servlet will
	 * resolve to 'test-servlet'.
	 */
	private static final String DEFAULT_NAMESPACE_SUFFIX = "-servlet";

	/**
	 * Prefix for the ServletContext attribute for the web application context.
	 * The completion is the servlet name.
	 */
	public static final String SERVLET_CONTEXT_PREFIX = FrameworkServlet.class.getName() + ".CONTEXT.";

	/**
	 * Will be added to a request before processing begins:
	 * the name of the FrameworkServlet that handled the request.
	 */
	public static final String SERVLET_NAME_REQUEST_ATTRIBUTE = FrameworkServlet.class.getName() + ".SERVLET_NAME";

	/**
	 * Will be added to a request before processing begins if we're in debug mode
	 * (a Boolean.TRUE instance).
	 * */
	public static final String DEBUG_REQUEST_ATTRIBUTE = FrameworkServlet.class.getName() + ".DEBUG";

	/**
	 * Request parameter that will enable debug mode. This isn't a security hole:
	 * it's only set when the debuggable property is true.
	 */
	public static final String DEBUG_PARAMETER = "__debug__";


	//---------------------------------------------------------------------
	// Instance data
	//---------------------------------------------------------------------

	/** Custom context class */
	private String contextClass;

	/**
	 * Should we publish the context as a ServletContext attribute
	 * to make it available to other code?
	 */
	private boolean publishContext = true;

	/** Holder for debug property */
	private boolean debug = false;

	/** Can request debugging be enabled? */
	private boolean debuggable = false;

	/** Namespace for this servlet */
	private String namespace;

	/** WebApplicationContext for this servlet */
	private WebApplicationContext webApplicationContext;

	/**
	 * Any fatal exception encountered on startup.
	 * Thrown on each request.
	 */
	private ServletException startupException;


	/**
	 * Convenient method to allow other classes to check whether
	 * this is a debug request.
	 */
	public static boolean isDebugMode(HttpServletRequest request) {
		return request.getAttribute(DEBUG_REQUEST_ATTRIBUTE) != null;
	}


	//---------------------------------------------------------------------
	// Bean properties
	//---------------------------------------------------------------------

	/**
	 * Set the a custom context class name
	 * @param classname name of custom context class to use
	 * This class be of type WebApplicationContext, and must
	 * implement a constructor taking two arguments:
	 * a parent WebApplicationContext (the root), and
	 * the current namespace
	 */
	public final void setContextClass(String classname) {
		this.contextClass = classname;
	}

	/**
	 * Set whether to publish this servlet's context as a ServletContext attribute.
	 * Default is true.
	 * @param publishContext whether we should publish this servlet's
	 * WebApplicationContext as a ServletContext attribute, available to
	 * all objects in this web container. Default is true. This is especially
	 * handy during testing, although it is debatable whether it's good practice
	 * to let other application objects access the context this way.
	 */
	public final void setPublishContext(boolean publishContext) {
		this.publishContext = publishContext;
	}

	/**
	 * If debug is enabled, a debug attribute is set on every request
	 * handled. Default is false.
	 * Cooperating classes can use this to output debug information
	 * in any way they choose.
	 */
	public final void setDebug(boolean debug) {
		this.debug = debug;
		logger.info("Debug property set by bean property to " + this.debug);
	}

	/**
	 * Return the value of the debug property.
	 */
	public final boolean getDebug() {
		return debug;
	}

	/**
	 * If debuggable is enabled, a debug attribute will be set on every request
	 * that contains a special parameter, regardless of whether debug mode is
	 * permanently enabled. Default is false.
	 * <p>Cooperating classes can use this to output debug information in any
	 * way they choose.
	 */
	public final void setDebuggable(boolean debuggable) {
		this.debuggable = debuggable;
		logger.info("Debuggable property set by bean property to " + this.debuggable);
	}

	/**
	 * Return the value of the debuggable property.
	 */
	public final boolean getDebuggable() {
		return debuggable;
	}

	/**
	 * Set a custom namespace for this servlet.
	 */
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	/**
	 * Return the namespace for this servlet, falling back to default scheme if
	 * no custom namespace was set: e.g. "test-servlet" for a servlet named "test".
	 */
	public String getNamespace() {
		return (namespace != null) ? namespace : getServletName() + FrameworkServlet.DEFAULT_NAMESPACE_SUFFIX;
	}

	/**
	 * Return the ServletContext attribute name for this servlet.
	 */
	public String getServletContextAttributeName() {
		return SERVLET_CONTEXT_PREFIX + getServletName();
	}

	/**
	 * Return the WebApplicationContext in which this servlet runs
	 */
	public final WebApplicationContext getWebApplicationContext() {
		return webApplicationContext;
	}


	//---------------------------------------------------------------------
	// Overridden methods of HttpServletBean
	//---------------------------------------------------------------------

	/**
	 * Overridden method of HttpServletBean, invoked after any bean properties
	 * have been set.
	 * <br/>Locate the WebApplicationContext,
	 * load URL mappings and find the CommandProcessor (if any)
	 */
	protected final void initServletBean() throws ServletException {

		long startTime = System.currentTimeMillis();
		logger.info("Framework servlet '" + getServletName() + "' init");

		this.webApplicationContext = createWebApplicationContext();

		try {
			// invoke subclass init
			initFrameworkServlet();
		}
		catch (Exception ex) {
			String msg = "Servlet with name '" + getServletName() + "' failed to initialize";
			logger.error(msg, ex);
			this.startupException = new ServletException(msg + ", with exception " + ex, ex);
			throw startupException;
		}

		long elapsedTime = System.currentTimeMillis() - startTime;
		logger.info("Framework servlet '" + getServletName() + "' init completed in " + elapsedTime + " ms");
	}

	/**
	 * Create the WebApplicationContext for this web app
	 * Go with default if can't find child
	 * @return the WebApplicationContext for this web app
	 * @throws ServletException if the context object can't be found
	 */
	private WebApplicationContext createWebApplicationContext() throws ServletException {
		ServletContext sc = getServletConfig().getServletContext();
		WebApplicationContext parent = WebApplicationContextUtils.getWebApplicationContext(sc);

		String namespace = getNamespace();
		try {
			WebApplicationContext waca = (this.contextClass != null) ?
					instantiateCustomWebApplicationContext(this.contextClass, parent, namespace) :
				 	new XmlWebApplicationContext(parent, namespace);
			waca.setServletContext(sc);
			logger.info("Servlet with name '" + getServletName() + "' loaded child context " + waca);
			if (this.publishContext) {
				// Publish the context as a servlet context attribute
				String attName = getServletContextAttributeName();
				sc.setAttribute(attName, waca);
				logger.info("Bound servlet's context in global ServletContext with name '" + attName + "'");
			}
			return waca;
		}
		catch (ServletException ex) {
			String mesg = "Can't load namespace '" + namespace + "': " + ex;
			this.startupException = new ServletException(mesg, ex);
			logger.error(mesg, ex);
			throw startupException;
		}
	}

	/**
	 * Try to instantiate a custom web application context, allowing parameterization
	 * of the classname.
	 */
	private WebApplicationContext instantiateCustomWebApplicationContext(String classname, WebApplicationContext parent, String namespace) throws ServletException {
		logger.info("Servlet with name '" + getServletName() + "' will try to create custom WebApplicationContext context of class '" + classname + "'");
		try {
			Class clazz = Class.forName(classname);
			if (!WebApplicationContext.class.isAssignableFrom(clazz))
				throw new ServletException("Fatal initialization error in servlet with name '" + getServletName() + "': custom WebApplicationContext class '" + classname + "' must implement WebApplicationContext");
			Constructor constructor = clazz.getConstructor( new Class[] { ApplicationContext.class, String.class} );
			return (WebApplicationContext) constructor.newInstance(new Object[] { parent, namespace} );
		}
		catch (ClassNotFoundException ex) {
			throw new ServletException("Fatal initialization error in servlet with name '" + getServletName() + "': can't load custom WebApplicationContext class '" + classname + "'", ex);
		}
		catch (NoSuchMethodException ex) {
			throw new ServletException("Fatal initialization error in servlet with name '" + getServletName() + "': custom WebApplicationContext class '" + classname + "' must define a constructor taking ApplicationContext (parent) and String (namespace)", ex);
		}
		catch (InvocationTargetException ex) {
			throw new ServletException("Fatal initialization error in servlet with name '" + getServletName() + "': custom WebApplicationContext class '" + classname + "' : constructor threw exception", ex);
		}
		catch (IllegalAccessException ex) {
			throw new ServletException("Fatal initialization error in servlet with name '" + getServletName() + "': custom WebApplicationContext class '" + classname + "' : no permission to invoke constructor", ex);
		}
		catch (InstantiationException ex) {
			throw new ServletException("Fatal initialization error in servlet with name '" + getServletName() + "': custom WebApplicationContext class '" + classname + "' : failed to instantiate", ex);
		}
	}

	/**
	 * Subclasses must implement this method to perform any initialization they require.
	 * The implementation may be empty.
	 * This method will be invoked after any bean properties
	 * have been set and WebApplicationContext and BeanFactory have been loaded
	 * @throws Exception any exception
	 */
	protected void initFrameworkServlet() throws Exception {
	}


	/** It's up to each subclass to decide whether or not it supports a request method.
	 * It should throw a Servlet exception if it doesn't support a particular request type.
	 * This might commonly be done with GET for forms, for example
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		serviceWrapper(request, response);
	}

	/**
	 * It's up to each subclass to decide whether or not it supports a request method.
	 * It should throw a Servlet exception if it doesn't support a particular request type.
	 * This might commonly be done with GET for forms, for example
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		serviceWrapper(request, response);
	}

	/**
	 * Handle this request, publishing an event regardless of the outcome.
	 * The actually event handling is performed by the abstract doService() method.
	 * Both doGet() and doPost() are handled by this method
	 */
	private void serviceWrapper(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// Throw an exception if we failed to start up with a fatal error
		if (this.startupException != null)
			throw startupException;

		boolean debugMode = this.debug || (this.debuggable && request.getParameter(DEBUG_PARAMETER) != null);

		if (debugMode) {
			// Set the debug attribute in the request, which will be used
			// by other components to render content
			request.setAttribute(DEBUG_REQUEST_ATTRIBUTE, Boolean.TRUE);

			// Show the name of this servlet
			request.setAttribute(SERVLET_NAME_REQUEST_ATTRIBUTE, getServletName());
		}

		long startTime = System.currentTimeMillis();
		Throwable failureCause = null;

		try {
			// Invoke the subclass's service method
			doService(request, response, debugMode);
		}
		catch (ServletException ex) {
			failureCause = ex;
			logger.error("Servlet failed to handle request: ServletException", ex);
			throw ex;
		}
		catch (IOException ex) {
			failureCause = ex;
			logger.error("Servlet failed to handle request: IOException", ex);
			throw ex;
		}
		catch (RuntimeException ex) {
			failureCause = ex;
			String mesg = "Unexpected runtime exception";
			logger.error(mesg, ex);
			throw new ServletException(mesg, ex);
		}
		finally {
			long processingTime = System.currentTimeMillis() - startTime;

			/*
			if (debugMode && failureCause != null) {
				// Output verbose information about failure, showing
				// how far we got (some of the variables output will be null
				logger.error("url='" + request.getServletPath() + "': Failure (" + failureCause + "); " +
				"handler=[" + mappedHandler + "]; handlerAdapter=[" + ha + "]", failureCause);
			}
			*/

			// Whether or not we succeeded, publish an event
			ApplicationEvent e = (failureCause == null) ?
			new RequestHandledEvent(this, request.getRequestURI(), processingTime, request.getRemoteAddr(), request.getMethod(), getServletConfig().getServletName()) :
				new RequestHandledEvent(this, request.getRequestURI(), processingTime, request.getRemoteAddr(), request.getMethod(), getServletConfig().getServletName(), failureCause);
				webApplicationContext.publishEvent(e);
		}
	}

	/**
	 * Subclasses must implement this method to do the work of request handling.
	 * The contract is the same as that for the doGet() or doPost() method of HttpServlet.
	 * This class intercepts calls to ensure that event publication takes place.
	 * @param request HttpServletRequest
	 * @param response HttpServletResponse
	 * @param debugMode whether or not we are in debug mode. Subclasses may emit
	 * additional log output in debug mode, or add debug attributes to the request being processed.
	 * If this parameter is true, this class will already have added a debug attribute to
	 * the request.
	 */
	protected abstract void doService(HttpServletRequest request, HttpServletResponse response, boolean debugMode) throws ServletException, IOException;

}
