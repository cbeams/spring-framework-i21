/*
 * The Spring Framework is published under the terms
 * of the Apache Software License.
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
 * to simplify their implementation. Subclasses should be JavaBeans.
 *
 * <p>Extends ApplicationObjectSupport, which will be helpful to some views.
 * Handles static attributes, and merging static with dynamic attributes.
 * Subclasses just need to implement the actual rendering.
 *
 * <p>It's recommended that subclasses <b>don't</b> cache anything, in the
 * quest for efficiency. This class offers caching. However, it's possible
 * to disable this class's caching, which is useful during development.
 *
 * @author Rod Johnson
 * @version $Id$
 * @see #renderMergedOutputModel
 */
public abstract class AbstractView extends ApplicationObjectSupport implements View {

	/** Map of static attributes, keyed by attribute name (String) */
	private Map	staticAttributes = new HashMap();

	/** Name of request context attribute, or null if not needed */
	private String requestContextAttribute;

	/** Default content type. Overridable as bean property. */
	private String contentType = "text/html; charset=ISO-8859-1";
	
	/** The name by which this View is known */
	private String name;


	/**
	 * Set static attributes as a CSV string.
	 * Format is attname0={value1},attname1={value1}
	 */
	public final void setAttributesCSV(String propString) throws IllegalArgumentException {
		if (propString == null)
			// Leave static attributes unchanged
			return;
			
		StringTokenizer st = new StringTokenizer(propString, ",");
		while (st.hasMoreTokens()) {
			String tok = st.nextToken();
			int eqindx = tok.indexOf("=");
			if (eqindx == -1)
				throw new IllegalArgumentException("Expected = in View string '" + propString + "'");
			
			if (eqindx >= tok.length() - 2)
				throw new IllegalArgumentException("At least 2 characters ([]) required in View string '" + propString + "'");
				
			String name = tok.substring(0, eqindx);
			String val = tok.substring(eqindx + 1);
			
			// Delete first and last characters of value: { and }
			val = val.substring(1);
			val = val.substring(0, val.length() - 1);

			if (logger.isDebugEnabled()) {
				logger.info("Set static attribute with name '" + name + "' and value [" + val + "] on view");
			}
			addStaticAttribute(name, val);
		}
	}
	
	/**
	 * Set static attributes from a java.util.Properties object. This is
	 * the most convenient way to set static attributes. Note that static
	 * attributes can be overridden by dynamic attributes, if a value
	 * with the same name is included in the model.
	 * <p>Relies on registration of PropertiesEditor.
	 * @see com.interface21.beans.propertyeditors.PropertiesEditor
	 */
	public final void setAttributes(Properties prop) throws IllegalArgumentException {
		if (prop != null) {
			Iterator itr = prop.keySet().iterator();
			while (itr.hasNext()) {
				String name = (String) itr.next();
				String val = prop.getProperty(name);
				if (logger.isDebugEnabled()) {
					logger.info("Set static attribute with name '" + name + "' and value [" + val + "] on view");//with name '" + viewname + "'");
				}
				addStaticAttribute(name, val);
			}
		}
	}

	/**
	 * Add static data to this view, exposed in each view.
	 * <p>Must be invoked before any calls to render().
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
	 * Set the name of the RequestContext attribute for all views,
	 * or null if not needed.
	 * @param requestContextAttribute name of the RequestContext attribute
	 */
	public final void setRequestContextAttribute(String requestContextAttribute) {
		this.requestContextAttribute = requestContextAttribute;
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

	/**
	 * Return the content type for this view.
	 * @return content type for this view
	 */
	protected final String getContentType() {
		return this.contentType;
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
	 * Return the view's name. Should never be null,
	 * if the view was correctly configured.
	 * @return the view's name
	 */
	public final String getName() {
		return name;
	}


	/**
	 * Prepares the view given the specified model.
	 * Delegates to renderMergedOutputModel for the actual rendering.
	 * @see #renderMergedOutputModel
	 */
	public final void render(Map model, HttpServletRequest request, HttpServletResponse response)
	    throws ServletException, IOException {
		if (logger.isDebugEnabled())
			logger.debug("Rendering view with name '" + this.name + "' with model=" + model +
				" and static attributes=" + this.staticAttributes);
		
		// Consolidate static and dynamic model attributes
		Map mergedModel = new HashMap(this.staticAttributes);
		mergedModel.putAll(model);

		// expose request context?
		if (this.requestContextAttribute != null) {
			mergedModel.put(this.requestContextAttribute, new RequestContext(request, mergedModel));
		}

		renderMergedOutputModel(mergedModel, request, response);
	}

	/** 
	 * Subclasses must implement this method to render the view.
	 * <p>The first take will be preparing the request: This may include setting
	 * the model elements as attributes, e.g. in the case of a JSP view.
	 * @param model combined output Map, with dynamic values
	 * taking precedence over static attributes
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @throws ServletException if there is any other error
	 * @throws IOException if there is an IO exception trying to obtain
	 * or render the view
	 */
	protected abstract void renderMergedOutputModel(Map model, HttpServletRequest request, HttpServletResponse response)
	    throws ServletException, IOException;

}
