/*
 * Generic framework code included with 
 * <a href="http://www.amazon.com/exec/obidos/tg/detail/-/1861007841/">Expert One-On-One J2EE Design and Development</a>
 * by Rod Johnson (Wrox, 2002). 
 * This code is free to use and modify. However, please
 * acknowledge the source and include the above URL in each
 * class using or derived from this code. 
 * Please contact <a href="mailto:rod.johnson@interface21.com">rod.johnson@interface21.com</a>
 * for commercial support.
 */
 
package com.interface21.web.servlet.mvc.multiaction;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.interface21.context.ApplicationContextException;
import com.interface21.web.bind.ServletRequestDataBinder;
import com.interface21.web.servlet.LastModified;
import com.interface21.web.servlet.ModelAndView;
import com.interface21.web.servlet.mvc.Controller;
import com.interface21.web.servlet.mvc.SessionRequiredException;
import com.interface21.web.servlet.mvc.WebContentGenerator;


/**
 * Controller implementation that allows multiple request types
 * to be handled by the same class.<br>
 *
 * Subclasses of this class can handle several different types of request
 * with methods of the form
 * ModelAndView actionName(HttpServletRequest request, HttpServletResponse response);
 *
 * May take a third parameter HttpSession in which an existing session will be required,
 * or a third parameter of an arbitrary class that gets treated as command
 * (i.e. an instance of the class gets created, and request parameters get bound to it)
 *
 * <br/>These methods can throw any kind of exception, but should only let propagate
 * those that they consider fatal, or which their class or superclass is prepared to
 * catch by implementing an exception handler.
 *
 * <br/>This model allows for rapid coding, but loses the advantage of compile-time
 * checking.
 *
 * <p>Inherits superclass bean properties. Adds methodNameResolver bean property.
 * An implementation of the MethodNameResolver interface defined in this package
 * should return a method name for a given request, based on any aspect of the request,
 * such as its URL or an "action" or like attribute. The default behavior is URL based.
 *
 * <br/>Also supports delegation to another object.
 *
 * <p>Subclasses can implement custom exception handler methods with names such as
 * ModelAndView anyMeaningfulName(HttpServletRequest request, HttpServletResponse response, ExceptionClass exception);
 * The third parameter can be any subclass or Exception or RuntimeException.
 *
 * </br>There can also be an optional lastModified method for handlers, of signature
 * long anyMeaningfulNameLastModified(HttpServletRequest request)
 * If such a method is present, it will be invoked. Default return from getLastModified()
 * is -1, meaning that content must always be regenerated.
 *
 * <br>Like Struts 1.1 DispatchAction, but more sophisticated.
 *
 * <br>The mapping from requests to handler method names is parameterized in the MethodNameResolver
 * interface.
 *
 * <br>Note that method overloading isn't allowed.
 *
 * @author Rod Johnson
 */
public class MultiActionController 
					extends WebContentGenerator 
					implements Controller, LastModified  {
		
	/** Prefix for last modified methods */
	public static final String LAST_MODIFIED_METHOD_SUFFIX = "LastModified";
	
	//---------------------------------------------------------------------
	// Instance data
	//---------------------------------------------------------------------

	/** Methods, keyed by name */
	private Map methodHash;
	
	/** LastModified methods, keyed by handler method name (without LAST_MODIFIED_SUFFIX) */
	private Map lastModifiedMethodHash;
	
	/** Methods, keyed by exception class */
	private Map exceptionHandlerHash;

	/** 
	 * Helper object that knows how to return method names from incoming requests.
	 * Can be overridden via the methodNameResolver bean property
	 */
	private MethodNameResolver methodNameResolver = new InternalPathMethodNameResolver();
	
	/** Object we'll invoke methods on. Defaults to this. */
	private Object delegate;
	
	
	//---------------------------------------------------------------------
	// Constructors
	//---------------------------------------------------------------------

	/**
	 * Constructor for MultiActionController that looks for handler methods
	 * in the present subclass.
	 * Caches methods for quick invocation later. This class's
	 * use of reflection will impose little overhead at runtime
	 * @throws ApplicationContextException if the class doesn't contain any
	 * action handler methods (and so could never handle any requests).
	 */
	public MultiActionController() throws ApplicationContextException {
		setDelegate(this);
	}
	
	/**
	 * Constructor for MultiActionController that looks for handler
	 * methods in delegate, rather than a subclass of this class.
	 * Caches methds
	 * @param delegate handler class. This doesn't need to
	 * implement any particular interface, as everything is done
	 * using reflection.
	 * @throws ApplicationContextException if the class doesn't
	 * contain any handler methods
	 */
	public MultiActionController(Object delegate) throws ApplicationContextException {
		setDelegate(delegate);
	}
	
	
	//---------------------------------------------------------------------
	// Bean properties
	//---------------------------------------------------------------------

	/**
	 * Sets the method name resolver used by this class.
	 * Allows parameterization of mappings.
	 * @param methodNameResolver the method name resolver used by this class
	 */
	public final void setMethodNameResolver(MethodNameResolver methodNameResolver) {
		this.methodNameResolver = methodNameResolver;
	}
	
	/**
	 * Get the MethodNameResolver used by this class
	 * @return MethodNameResolver the method name resolver used by this class
	 */
	public final MethodNameResolver getMethodNameResolver() {
		return this.methodNameResolver;
	}
	
	/**
	 * Set the delegate used by this class. The default is
	 * "this", assuming that handler methods have been added
	 * by a subclass. This method is rarely invoked once
	 * the class is configured.
	 * @param delegate class containing methods, which may
	 * be the present class, the handler methods being in a subclass
	 * @throws ApplicationContextException if there aren't
	 * any valid request handling methods in the subclass.
	 */
	public final void setDelegate(Object delegate) throws ApplicationContextException {
		this.delegate = delegate;
		if (delegate == null)
			throw new ApplicationContextException("Delegate cannot be null in MultiActionController");

		this.methodHash = new HashMap();
		this.lastModifiedMethodHash = new HashMap();
		
		// Look at all methods in the subclass, trying to find
		// methods that are validators according to our criteria
		Method[] methods = delegate.getClass().getMethods();
		for (int i = 0; i < methods.length; i++) {
			// We're looking for methods with given parameters
			if (methods[i].getReturnType().equals(ModelAndView.class)) {
				// We have a potential handler method, with the correct return type
				Class[] params = methods[i].getParameterTypes();
				
				// Check that the number and types of methods is correct.
				// We don't care about the declared exceptions
				if (params.length >= 2 && params[0].equals(HttpServletRequest.class) && params[1].equals(HttpServletResponse.class)) {
					// We're in business
					logger.info("Found action method [" + methods[i] + "]");
					methodHash.put(methods[i].getName(), methods[i]);
					
					// Look for corresponding LastModified method
					try {
						Method lastModifiedMethod = delegate.getClass().getMethod(methods[i].getName() + LAST_MODIFIED_METHOD_SUFFIX, new Class[] { HttpServletRequest.class } );
						// Put in cache, keyed by handler method name
						lastModifiedMethodHash.put(methods[i].getName(), lastModifiedMethod);
						logger.info("Found last modified method for action method [" + methods[i] + "]");
					}
					catch (NoSuchMethodException ex) {
						// No last modified method. That's ok
					}
				}
			}	// for each method with the correct return type
		} 	// for each method in the class
		
		// There must be SOME handler methods
		
		// WHAT IF SETTING DELEGATE LATER!?
		if (methodHash.isEmpty()) {
			String mesg = "No handler methods in class " + getClass().getName();
			logger.error(mesg);
			throw new ApplicationContextException(mesg);
		}
		
		// Now look for exception handlers
		exceptionHandlerHash = new HashMap();
		for (int i = 0; i < methods.length; i++) {
			if (methods[i].getReturnType().equals(ModelAndView.class) &&
					methods[i].getParameterTypes().length == 3) {
				Class[] params = methods[i].getParameterTypes();
				if (params[0].equals(HttpServletRequest.class) && 
					params[1].equals(HttpServletResponse.class) &&
					Throwable.class.isAssignableFrom(params[2])
				) {
					// Have an exception handler
					exceptionHandlerHash.put(params[2], methods[i]);
					logger.info("Found exception handler method [" + methods[i] + "]");
				}
			}
		}
	}	// setDelegate
	
	
	//---------------------------------------------------------------------
	// Implementation of LastModified
	//---------------------------------------------------------------------

	/**
	 * Try to find an XXXXLastModified method, where XXXX is the name of a handler.
	 * Return -1, indicating that content must be updated, if there's no such handler.
	 * @see LastModified#getLastModified(HttpServletRequest)
	 */
	public final long getLastModified(HttpServletRequest request) {
		try {
			String handlerMethodName = methodNameResolver.getHandlerMethodName(request);
			Method lastModifiedMethod = (Method) this.lastModifiedMethodHash.get(handlerMethodName);
			if (lastModifiedMethod != null) {
				try {
					// Invoke the LastModified method
					Long wrappedLong = (Long) lastModifiedMethod.invoke(this.delegate, new Object[] { request });
					return wrappedLong.longValue();
				}
				catch (Exception ex) {
					// We encountered an error invoking the lastModified method
					// We can't do anything useful except log this, as we can't throw an exception
					logger.error("Failed to invoke lastModified method", ex);
				}
			}	// if we had a lastModified method for this request
		}
		catch (NoSuchRequestHandlingMethodException ex) {
			// No handler method for this request. This shouldn't
			// happen, as this method shouldn't be called unless a previous invocation
			// of this class has generated content.
			// Do nothing, that's ok: we'll return default
		}
		// The default if we didn't find a method
		return -1L;
	}

	//---------------------------------------------------------------------
	// Implementation of Controller
	//---------------------------------------------------------------------

	/**
	 * @see com.interface21.web.servlet.mvc.AbstractController#handleRequestInternal(HttpServletRequest, HttpServletResponse)
	 */
	public final ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {

		try {
			String name = methodNameResolver.getHandlerMethodName(request);
			return invokeNamedMethod(name, request, response);
		}
		catch (NoSuchRequestHandlingMethodException ex) {
			throw new ServletException("No handler", ex);
		}
	}
	
	
	/**
	 * Invoke the named method.
	 * Use a custom exception handler if possible;
	 * otherwise, throw an unchecked exception;
	 * wrap a checked exception or Throwable
	 */
	protected final ModelAndView invokeNamedMethod(String method, HttpServletRequest request, HttpServletResponse response)
		throws NoSuchRequestHandlingMethodException, ServletException, IOException {
		Method m = null;
		
		try {
			m = (Method) methodHash.get(method);
			if (m == null)
				throw new NoSuchRequestHandlingMethodException(method, this);

			// A real generic Collection! Parameters to method
			List params = new LinkedList();
			params.add(request);
			params.add(response);
				
			if (m.getParameterTypes().length >= 3 && m.getParameterTypes()[2].equals(HttpSession.class) ){
				// Require a session
				HttpSession session = request.getSession(false);
				if (session == null)
					//throw new SessionRequiredException("Session was required for method '" + method + "'");
					return handleException(request, response, new SessionRequiredException("Session was required for method '" + method + "'"));
				params.add(session);
			}
			
			// If last parameter isn't of HttpSession type it's a command
			if (m.getParameterTypes().length >= 3 && !m.getParameterTypes()[m.getParameterTypes().length - 1].equals(HttpSession.class)) {
				Object command = newCommandObject(m.getParameterTypes()[m.getParameterTypes().length - 1]);
				params.add(command);
				bind(request, command);
			}
			
			Object[] parray = params.toArray(new Object[params.size()]);
			return (ModelAndView) m.invoke(this.delegate, parray);
		}
		catch (IllegalAccessException ex) {
			throw new ServletException("Cannot invoke request handler method [" + m + "]: not accessible", ex);
		}
		catch (InvocationTargetException ex) {
			// This is what we're looking for: the handler method threw an exception
			Throwable t = ex.getTargetException();
			return handleException(request, response, t);
		}
	}	// invokeNamedMethod


	/**
	 * We've encountered an exception which may be recoverable
	 * (InvocationTargetException or SessionRequiredException).
	 * Allow the subclass a chance to handle it.
	 * @param request request
	 * @param response response
	 * @param t problem
	 * @return a ModelAndView to render the response
	 */
	private ModelAndView handleException(HttpServletRequest request, HttpServletResponse response, Throwable t) throws ServletException, IOException, Error {
		Method handler = getExceptionHandler(t);
		if (handler != null) {
			return invokeExceptionHandler(handler, request, response, t);
		}
		
		// If we get here, there was no custom handler
		if (t instanceof RuntimeException) {
			throw (RuntimeException) t;
		}
		if (t instanceof Error) {
			throw (Error) t;
		}
		if (t instanceof ServletException) {
			throw (ServletException) t;
		}
		if (t instanceof IOException) {
			throw (IOException) t;
		}
		
		// Must be a checked application exception
		throw new ServletException("Uncaught exception", t);
	}	// invokeNamedMethod
	
	
	/**
	 * Create a new command object of the given class.
	 * <br>Subclasses can override this implementation if they want.
	 * This implementation uses class.newInstance(), so commands need to have
	 * public no arg constructors.
	 */
	protected Object newCommandObject(Class clazz) throws ServletException {
		logger.info("Must create new command of " + clazz);
		try {
			Object command = clazz.newInstance();
			return command;
		}
		catch (Exception ex) {
			throw new ServletException("Cannot instantiate command " + clazz + "; does it have a public no arg constructor?", ex);
		}
	}
	
	/**
	 * Bind request parameters onto the given command bean
	 * @param request request from which parameters will be bound
	 * @param command command object, that must be a JavaBean
	 */
	protected void bind(ServletRequest request, Object command) throws ServletException {
		logger.info("Binding request parameters onto command");
		ServletRequestDataBinder binder = new ServletRequestDataBinder(command, "command");
		binder.bind(request);
		binder.closeNoCatch();
	}
	
	/**
	 * Can return null if not found
	 * @return a handler for the given exception type
	 * @param exception Won't be a ServletException or IOException
	 */
	protected Method getExceptionHandler(Throwable exception) {
		Class exceptionClass = exception.getClass();
		logger.info("Trying to find handler for exception of " + exceptionClass);
		Method handler = (Method) exceptionHandlerHash.get(exceptionClass);
		while (handler == null && !exceptionClass.equals(Throwable.class)) {
			logger.info("Looking at superclass " + exceptionClass);
			exceptionClass = exceptionClass.getSuperclass();
			handler = (Method) exceptionHandlerHash.get(exceptionClass);
		}
		return handler;
	}
	
	
	/**
	 * Invoke the selected exception handler
	 * @param handler handler method to invoke
	 */
	private ModelAndView invokeExceptionHandler(Method handler, HttpServletRequest request, HttpServletResponse response, Throwable exception) throws ServletException, IOException {
		if (handler == null)
			throw new ServletException("No handler for exception", exception);
			
		// If we get here, we have a handler
		logger.info("Invoking exception handler [" + handler + "] for exception [" + exception + "]");
		try {
			ModelAndView mv = (ModelAndView) handler.invoke(this.delegate, new Object[] { request, response, exception }); 
			return mv;
		}
		catch (IllegalAccessException ex) {
			throw new ServletException("Cannot invoke request exception handler method [" + handler + "]: not accessible", ex);
		}
		catch (InvocationTargetException ex) {
			Throwable t = ex.getTargetException();
			if (t instanceof ServletException) {
				throw (ServletException) t;
			}
			if (t instanceof IOException) {
				throw (IOException) t;
			}
			// Shouldn't happen
			throw new ServletException("Unexpected exception thrown from exception handler method: ", t);
		}  
	}	// invokeExceptionHandler
	
}
