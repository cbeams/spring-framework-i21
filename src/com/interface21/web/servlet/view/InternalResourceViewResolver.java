package com.interface21.web.servlet.view;

import java.util.Locale;

import javax.servlet.ServletException;

import com.interface21.web.servlet.View;

/**
 * Simple implementation of ViewResolver that allows for direct resolution of
 * symbolic view names to URLs, without explicit mapping definition.
 * View names can either be resource URLs themselves, or get augmented by a
 * specified prefix and/or suffix.
 *
 * Example: prefix="/jsp/", suffix=".jsp", viewname="test" -> "/jsp/test.jsp"
 *
 * Note: This class does not supported localized resolution, i.e. resolving a
 * symbolic view name to different resources depending on the current locale.
 *
 * @author Juergen Hoeller
 * @since 17.02.2003
 * @see InternalResourceView
 */
public class InternalResourceViewResolver extends AbstractCachingViewResolver {

	private Class viewClass = InternalResourceView.class;

	private String prefix = "";

	private String suffix = "";

	private String requestContextAttribute = null;

	/**
	 * Set the view class that should be used to create views in loadView.
	 * @param viewClass class that is assignable to InternalResourceView
	 */
	public void setViewClass(Class viewClass) {
		if (viewClass == null || !InternalResourceView.class.isAssignableFrom(viewClass))
			throw new IllegalArgumentException("View class must be an InternalResourceView: " + viewClass);
		this.viewClass = viewClass;
	}

	/**
	 * Set the prefix that gets applied to view names when building a URL.
	 * @param prefix view name prefix
	 */
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	/**
	 * Set the suffix that gets applied to view names when building a URL.
	 * @param suffix view name suffix
	 */
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	/**
	 * Set the name of the RequestContext attribute for all views,
	 * or null if not needed.
	 * @param requestContextAttribute name of the RequestContext attribute
	 */
	public void setRequestContextAttribute(String requestContextAttribute) {
		this.requestContextAttribute = requestContextAttribute;
	}

	protected View loadView(String viewName, Locale locale) throws ServletException {
		try {
			InternalResourceView view = (InternalResourceView) viewClass.newInstance();
			view.setUrl(this.prefix + viewName + this.suffix);
			view.setRequestContextAttribute(this.requestContextAttribute);
			return view;
		}
		catch (InstantiationException ex) {
			throw new ServletException("could not instantiate view class", ex);
		}
		catch (IllegalAccessException ex) {
			throw new ServletException("could not access view class", ex);
		}
	}
}
