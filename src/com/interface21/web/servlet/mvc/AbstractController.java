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

package com.interface21.web.servlet.mvc;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.interface21.context.ApplicationContextException;
import com.interface21.web.servlet.LastModified;
import com.interface21.web.servlet.ModelAndView;

/**
 * Convenient superclass for controller implementations,
 * using the Template Method design pattern.
 *
 * <p>Exposes the following bean properties:
 * <ul>
 * <li>supportedMethods: CSV of supported methods, such as "GET,POST".
 * Default allows GET and POST.
 * <li>requireSession: Whether a session should be required, meaning that subclasses
 * can call request.getSession() in the assurance that there <b>is</b> a session.
 * </ul>
 *
 * @author Rod Johnson
 */
public abstract class AbstractController extends WebContentGenerator implements Controller {
	
	/**
	 * Set of supported methods (GET/POST etc.). GET and POST by default.
	 */
	private Set	supportedMethods;
	
	private boolean requireSession;
	
	private int cacheSeconds = -1;
	
	/**
	 * Create a new Controller supporting GET and POST methods.
	 */
	public AbstractController() {
		this.supportedMethods = new HashSet();
		this.supportedMethods.add("GET");
		this.supportedMethods.add("POST");
	}
	
	/**
	 * Set supported methods as CSV.
	 * The String[] property editor will get the type right.
	 */
	public final void setSupportedMethods(String[] supportedMethodsArray) throws ApplicationContextException {
		if (supportedMethodsArray == null || supportedMethodsArray.length == 0)
			throw new ApplicationContextException("SupportedMethods must include some methods");
		this.supportedMethods.clear();
		for (int i = 0; i < supportedMethodsArray.length; i++) {
			this.supportedMethods.add(supportedMethodsArray[i]);
			logger.info("Supported request method '" + supportedMethodsArray[i] + "'");
		}
	}
	
	/**
	 * Is a session required to handle requests?
	 */
	public final void setRequireSession(boolean requireSession) {
		this.requireSession = requireSession;
		logger.info("Requires session set to " + requireSession);
	}
	
	/**
	 * If 0 disable caching, default is no caching header generation.
	 * Only if this is set to 0 (no cache) or a positive value (cache for this many
	 * seconds) will this class generate cache headers.
	 * They can be overwritten by subclasses anyway, before content is generated.
	 */
	public final void setCacheSeconds(int seconds) {
		this.cacheSeconds = seconds;
	}
	
	public final ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		
		// Check whether we should support the request method
		String method = request.getMethod();
		if (!this.supportedMethods.contains(method)) {
			logger.info("Disallowed '" + method + "' request");
			throw new ServletException("This resource does not support request method '" + method + "'");
		}
		
		// Check whether session was required
		if (this.requireSession) {
			// Don't create a session if none exists
			HttpSession session = request.getSession(false);
			if (session == null) {
				throw new ServletException("This resource requires a pre-existing HttpSession: none was found"); 
			}
		}
		
		// Do declarative cache control
		if (this.cacheSeconds == 0) {
			preventCaching(response);
		}
		else if (this.cacheSeconds > 0) {
			// Revalidate only if we understand last modification
			boolean revalidate = this instanceof LastModified;
			cacheForSeconds(response, this.cacheSeconds, revalidate);
		}
		// Leave caching to the client otherwise
		
		// If everything's OK, leave subclass to do the business
		return handleRequestInternal(request, response);
	}
	
	/**
	 * Template method. Subclasses must implement this.
	 * The contract is the same as for handleRequest.
	 * @see #handleRequest
	 */
	protected abstract ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;

}
