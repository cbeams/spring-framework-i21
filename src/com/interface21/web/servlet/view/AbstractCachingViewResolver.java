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
import java.util.Map;

import javax.servlet.ServletException;

import com.interface21.context.ApplicationContextAware;
import com.interface21.context.ApplicationContextException;
import com.interface21.context.support.ApplicationObjectSupport;
import com.interface21.web.servlet.View;
import com.interface21.web.servlet.ViewResolver;

/**
 * Convenient superclass for view resolvers. Caches views once resolved.
 * This means that view resolution won't be a performance problem,
 * no matter how costly initial view retrieval is.
 * View retrieval is deferred to subclasses.
 * @author Rod Johnson
 */
public abstract class AbstractCachingViewResolver extends ApplicationObjectSupport implements ViewResolver {

	/** View name --> View instance */
	private Map viewHash = new HashMap();

	/** Whether we should cache views, once resolved */
	private boolean cache = true;

	/**
	 * Enable caching. Disable this only for debugging and development.
	 * Default is for caching to be enabled.
	 * <p><b>Warning: disabling caching severely impacts performance.</b>
	 * Tests indicate that turning caching off reduces performance by at
	 * least 20%. Increased object churn probably eventually makes the
	 * problem even worse.
	 */
	public void setCache(boolean cache) {
		this.cache = cache;
	}

	/**
	 * If caching is enabled.
	 */
	public boolean isCache() {
		return cache;
	}

	public final View resolveViewName(String viewName, Locale locale) throws ServletException {
		View v = null;
		if (!cache) {
			logger.warn("View caching is SWITCHED OFF -- DEVELOPMENT SETTING ONLY: this will severely impair performance");
			v = loadAndConfigureView(viewName, locale);
		}
		else {
			// We're caching - don't really need synchronization
			v = (View) this.viewHash.get(getCacheKey(viewName, locale));
			if (v == null) {
				// Ask the subclass to load the View
				v = loadAndConfigureView(viewName, locale);
			}
		}
		return v;
	}

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
				((ApplicationContextAware) v).setApplicationContext(getApplicationContext());
			}
			catch (ApplicationContextException ex) {
				throw new ServletException("Error initializing View [" + v + "]: " + ex.getMessage(), ex);
			}

			String cacheKey = getCacheKey(viewname, locale);
			logger.info("Cached view '" + cacheKey + "'");
			this.viewHash.put(cacheKey, v);
		}

		return v;
	}

	/**
	 * Returns the cache key for the given viewname and the given locale.
	 * Needs to regard the locale, as a different locale can lead to a different view!
	 */
	private String getCacheKey(String viewname, Locale locale) {
		return viewname + "_" + locale;
	}

	/**
	 * Subclasses must implement this method. There need be no concern for efficiency,
	 * as this class will cache views. Not all subclasses may support internationalization:
	 * A subclass that doesn't can ignore the locale parameter.
	 * @param viewName name of the view to retrieve
	 * @param locale Locale to retrieve the view for
	 * @throws ServletException if there is an error trying to resolve the view
	 * @return the View if it can be resolved, or null
	 */
	protected abstract View loadView(String viewName, Locale locale) throws ServletException;

}
