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

import java.util.HashMap;
import java.util.Locale;

import javax.servlet.ServletException;

import org.apache.log4j.Logger;

import com.interface21.context.ApplicationContext;
import com.interface21.context.ApplicationContextAware;
import com.interface21.context.ApplicationContextException;
import com.interface21.web.servlet.View;
import com.interface21.web.servlet.ViewResolver;

/**
 * Convenient superclass for view resolvers.
 * Caches Views once resolved. This means that
 * view resolution won't be a performance problem, no matter how costly
 * initial view retrieval is. View retrieval is deferred to subclasses.
 * @author  Rod Johnson
 */
public abstract class AbstractCachingViewResolver implements ViewResolver {

	//---------------------------------------------------------------------
	// Instance data
	//---------------------------------------------------------------------
	/** View name --> View instance */
	private HashMap viewHash = new HashMap();

	/** Logging category for this object */
	protected final Logger logger = Logger.getLogger(getClass().getName());

	/** ApplicationContext for this ViewResolver */
	private ApplicationContext applicationContext;

	/** Whether we should cache views, once resolved */
	private boolean cache = true;

	//---------------------------------------------------------------------
	// Implementation of ApplicationContextAware
	//---------------------------------------------------------------------
	/** Set the ApplicationContext object used by this object
	 * @param applicationContext ApplicationContext object used by this object
	 * @throws ApplicationContextException if initialization attempted by this object
	 * after it has access to the WebApplicatinContext fails
	 */
	public final void setApplicationContext(ApplicationContext applicationContext)
		throws ApplicationContextException {
		this.applicationContext = applicationContext;
	}

	/**
	 * @see ApplicationContextAware#getApplicationContext()
	 */
	public final ApplicationContext getApplicationContext() {
		return this.applicationContext;
	}

	//---------------------------------------------------------------------
	// Bean properties
	//---------------------------------------------------------------------
	/**
	 * Disable caching. Do this only for debugging and development. Default is
	 * for caching to be enabled. 
	 * <br><b>Warning: disabling caching severely impacts performance.</b>
	 * Tests indicate that turning caching off
	 * reduces performance by at least 20%. Increased object churn
	 * probably eventually makes the problem even worse.
	 */
	public void setCache(boolean cache) {
		this.cache = cache;
	}

	/**
	 * @return whether caching is enabled
	 */
	public boolean getCache() {
		return this.cache;
	}

	//---------------------------------------------------------------------
	// Implementation of ViewResolver
	//---------------------------------------------------------------------
	/**
	 * @see ViewResolver#resolveViewname(String, Locale)
	 */
	public final View resolveViewname(String viewname, Locale locale) throws ServletException {
		View v = null;
		if (!cache) {
			logger.warn("View caching is SWITCHED OFF -- DEVELOPMENT SETTING ONLY: this will severely impair performance");
			v = loadAndConfigureView(viewname, locale);
		}
		else {
			// We're caching
			// Don't really need synchronization
			v = (View) viewHash.get(getCacheKey(viewname, locale));
			if (v == null) {
				// Ask the subclass to load the View
				v = loadAndConfigureView(viewname, locale);
			}
		}

		return v;
	}	// resolveViewname


	/** 
	 * Configure the given View. Only invoked once per View.
	 * Configuration means giving the View its name, and 
	 * setting the ApplicationContext on the View if necessary
	 */
	private View loadAndConfigureView(String viewname, Locale locale) throws ServletException {

		// Ask the subclass to load the view
		View v = loadView(viewname, locale);
		if (v == null)
			throw new ServletException("Cannot resolve view name '" + viewname + "'");
			
		// Configure view
		v.setName(viewname);

		// Give the view access to the ApplicationContext
		// if it needs it
		if (v instanceof ApplicationContextAware) {
			try {
				((ApplicationContextAware) v).setApplicationContext(this.applicationContext);
			}
			catch (ApplicationContextException ex) {
				throw new ServletException("Error initializing View [" + v + "]: " + ex.getMessage(), ex);
			}

			String cacheKey = getCacheKey(viewname, locale);
			logger.info("Cached view '" + cacheKey + "'");
			viewHash.put(cacheKey, v);
		}

		return v;
	} // loadAndConfigureView

	/**
	 * Returns the cache key for the given viewname and the given locale.
	 * Needs to regard the locale, as a different locale can lead to a different view!
	 */
	private String getCacheKey(String viewname, Locale locale) {
		return viewname + "_" + locale;
	}

	/** Subclasses must implement this method. There need be no concern for efficiency,
	 * as this class will cache views.
	 * @param viewName name of the view to retrieve
	 * @param locale Locale to retrieve the view for. Not all subclasses may support
	 * internationalization. A subclass that doesn't can ignore this parameter.
	 * @throws ServletException if there is an error trying to resolve the view
	 * @return the View if it can be resolved; otherwise null.
	 */
	protected abstract View loadView(String viewName, Locale locale) throws ServletException;
}
