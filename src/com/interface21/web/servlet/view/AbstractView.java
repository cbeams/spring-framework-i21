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

package com.interface21.web.servlet.view;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.interface21.context.support.ApplicationObjectSupport;
import com.interface21.web.servlet.View;
import com.interface21.web.servlet.support.RequestContext;

/**
 * Abstract view superclass. Standard framework view implementations
 * and application-specific custom views can extend this class
 * to simplify their implementation.
 * <br>Subclasses should be JavaBeans.
 * <br>Implements ApplicationContextAware, which will be helpful
 * to some views. This means that the ApplicationContext
 * will be set by the framework during initialization.
 * <br/>Handles static attributes, and merging static with
 * dynamic attributes.
 * <br/>It's recommended that subclasses <b>don't</b> cache
 * anything, in the quest for efficiency. This class offers
 * caching. However, it's possible
 * to disable this class's caching, which is useful during development.
 * <br/>Also provides a logging category.
 * @author  Rod Johnson
 */
public abstract class AbstractView extends ApplicationObjectSupport implements View {

	//---------------------------------------------------------------------
	// Instance data
	//---------------------------------------------------------------------
	/** Map of static attributes, keyed by attribute name (String) */
	private Map	staticAttributes = new HashMap();

	/** Default content type. Overridable as bean property. */
	private String contentType = "text/html; charset=ISO-8859-1";
	
	/** Name of request context attribute, or null if not needed */
	private String requestContextAttribute;

	/** The name by which this View is known */
	private String name;


	//---------------------------------------------------------------------
	// Bean properties
	//---------------------------------------------------------------------
	/** 
	 * Set static attributes as a CSV string.
	 * <br>Format is attname0={value1},attname1={value1}
	 */
	public final void setAttributesCSV(String s) throws IllegalArgumentException {
		StringTokenizer st = new StringTokenizer(s, ",");
		while (st.hasMoreTokens()) {
			String tok = st.nextToken();
			int eqindx = tok.indexOf("=");
			if (eqindx == -1)
				throw new IllegalArgumentException("Expected = in View string '" + s + "'");
			// LENGTH CHECKS
			String name = tok.substring(0, eqindx);
			String val = tok.substring(eqindx + 1);
			
			// Delete first and last characters of value: { and }
			val = val.substring(1);
			val = val.substring(0, val.length() - 1);

			if (logger.isDebugEnabled()) {
				logger.debug("Set static attribute with name '" + name + "' and value [" + val + "] on view");//with name '" + viewname + "'");
			}
			addStaticAttribute(name, val);
		}
	}
	
	/**
	 * Set static attributes from a java.util.Properties object. This is
	 * the most convenient way to set static attributes. Note that static
	 * attributes can be overriden by dynamic attributes, if a value
	 * with the same name is included in the model.
	 * <br>Relies on registration of properties PropertyEditor
	 */
	public final void setAttributes(Properties p) throws IllegalArgumentException {
		if (p != null) {
			Iterator itr = p.keySet().iterator();
			while (itr.hasNext()) {
				String name = (String) itr.next();
				String val = p.getProperty(name);
				if (logger.isDebugEnabled()) {
					logger.info("Set static attribute with name '" + name + "' and value [" + val + "] on view");//with name '" + viewname + "'");
				}
				addStaticAttribute(name, val);
			}
		}
	}

	/**
	 * Set the content type for this view.
	 * May be ignored by subclasses if the view itself is assumed
	 * to set the content type, e.g. in case of JSPs.
	 * @param contentType content type for this view
	 */
	public final void setContentType(String contentType) {
		this.contentType = contentType;
	}
	
	protected final String getContentType() {
		return this.contentType;
	}

	/**
	 * Set the name of the RequestContext attribute for all views,
	 * or null if not needed.
	 * @param requestContextAttribute name of the RequestContext attribute
	 */
	public void setRequestContextAttribute(String requestContextAttribute) {
		this.requestContextAttribute = requestContextAttribute;
	}


	//---------------------------------------------------------------------
	// Implementation of View
	//---------------------------------------------------------------------
	/** 
	 * Add static data to this view, exposed in each view.
	 * <br/>Must be invoked before any calls to render().
	 * @param name name of attribute to expose
	 * @param o object to expose
	 */
	public final void addStaticAttribute(String name, Object o) {
		this.staticAttributes.put(name, o);
	}

	/** 
	 * Handy for testing. Return the static attributes
	 * held in this view.
	 * @return the static attributes in this view
	 */
	public final Map getStaticAttributes() {
		return Collections.unmodifiableMap(this.staticAttributes);
	}

	/**
	 * Set the view's name. Helpful for traceability.
	 * Framework code must call this when constructing views.
	 * @param name the view's name. May not be null.
	 * Views should use this for log messages.
	 */
	public final void setName(String name) {
		this.name = name;
	}
	
	/** 
	 * Return the view's name. Should
	 * never be null, if the view was correctly configured.
	 * @return the view's name
	 */
	public final String getName() {
		return name;
	}

	/**
	 * Renders the view given the specified model.  There can be many types of
	 * view.<br/>
	 * The first take will be preparing the request: this may include setting the model
	 * as an attribute, in the case of a JSP view.
	 */
	public final void render(Map pModel, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (logger.isDebugEnabled())
			logger.debug("Rendering view with name '" + this.name + " with model={" + pModel + 
				"} and static attributes={" + this.staticAttributes + "}");
		
		// Consolidate static and dynamic model attributes
		Map model = new HashMap(this.staticAttributes);
		model.putAll(pModel);

		// expose request context?
		if (this.requestContextAttribute != null)
			model.put(this.requestContextAttribute, new RequestContext(request));

		renderMergedOutputModel(model, request, response);
	}

	/** 
	 * Subclasses must implement this method. 
	 * Render the view given the model to output.
	 * @param model combined output Map, with dynamic values
	 * taking precedence over static attributes
	 * @param request HttpServetRequest
	 * @param response HttpServletResponse
	 * @throws IOException if there is an IO exception trying to obtain
	 * or render the view
	 * @throws ServletException if there is any other error
	 */
	protected abstract void renderMergedOutputModel(Map model, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;

}
