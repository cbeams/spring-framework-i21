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

import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;

import com.interface21.beans.BeanWrapper;
import com.interface21.beans.BeanWrapperImpl;
import com.interface21.beans.BeansException;
import com.interface21.beans.PropertyValues;

/**
 * Simple extension of javax.servlet.http.HttpServlet that treats its config
 * parameters as bean properties. A very handy superclass for any type of servlet.
  * Type conversion is automatic. It is also
 * possible for subclasses to specify required properties. This servlet leaves
 * request handling to subclasses, inheriting the default behaviour of HttpServlet.
 * <p/>This servlet superclass has no dependency on the application context.
 * However, it does use Java 1.4 logging emulation, which must have been
 * configured by another component.
 * @author Rod Johnson
 * @version $Revision$
 */
public class HttpServletBean extends HttpServlet {
	
	//---------------------------------------------------------------------
	// Instance data
	//---------------------------------------------------------------------
	/** 
	 * Log category.
	 * Protected to avoid the need for method calls. Final to avoid tampering
	 * by subclasses.
	 */
	protected final Logger logger = Logger.getLogger(getClass().getName());
	
	/** 
	 * May be null. List of required properties (Strings) that must
	 * be supplied as config parameters to this servlet.
	 */
	private List requiredProperties = new LinkedList();
	
	/** Holds name info: useful for logging */
	private String identifier;
	
	
	//---------------------------------------------------------------------
	// Constructors
	//---------------------------------------------------------------------
	/** 
	 * Construct a new HttpServletBean
	 */
	public HttpServletBean() {
	}
	
	/** 
	 * Subclasses can invoke this method to specify that this property
	 * (which must match a JavaBean property they expose) is mandatory,
	 * and must be supplied as a config parameter.
	 * @param property name of the required property
	 */
	protected final void addRequiredProperty(String property) {
		requiredProperties.add(property);
	}
	
	//---------------------------------------------------------------------
	// Overridden methods
	//---------------------------------------------------------------------
	/** 
	 * Map config parameters onto bean properties of this servlet, and
	 * invoke subclass initialization.
	 * @throws ServletException if bean properties are invalid (or required properties
	 * are missing), or if subclass initialization fails.
	 */
	public final void init() throws ServletException {
		this.identifier = "Servlet with name '" + getServletConfig().getServletName() + "' ";
		 
		logger.info(getIdentifier() + "entering init...");
		
		// Set bean properties
		try {
			PropertyValues pvs = new ServletConfigPropertyValues(getServletConfig(), requiredProperties);
			BeanWrapper bw = new BeanWrapperImpl(this);
			bw.setPropertyValues(pvs);
			logger.debug(getIdentifier() + "properties bound OK");
			
			// Let subclasses do whatever initialization they like
			initServletBean();
			logger.info(getIdentifier() + "configured successfully");
		}
		catch (BeansException ex) {
			String mesg = getIdentifier() + ": error setting properties from ServletConfig";
			logger.error(mesg, ex);
			throw new ServletException(mesg, ex);
		}
		catch (Throwable t) {
			// Let subclasses throw unchecked exceptions
			String mesg = getIdentifier() + ": initialization error";
			logger.error(mesg, t);
			throw new ServletException(mesg, t);
		}
	}	// init
	
	
	/** 
	 * Subclasses may override this to perform custom initialization.
	 *  All bean properties of this servlet will have been set before this
	 * method is invoked. This default implementation does nothing.
	 * @throws ServletException if subclass initialization fails
	 */
	protected void initServletBean() throws ServletException {
		logger.debug(getIdentifier() + "NOP default implementation of initServletBean");
	}
	
	/** 
	 * Return the name of this servlet:
	 * handy to include in log messages. Subclasses may override it if
	 * necessary to include additional information. Use like this: 
	 * <code>
	 * Category.getInstance(getClass()).debug(getIdentifier() + "body of message");
	 * </code>
	 * @return the name of this servlet
	 */
	protected String getIdentifier() {
		return this.identifier;
	}
	
}	// class HttpServletBean