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
 */
public class InternalResourceViewResolver extends AbstractCachingViewResolver {

	private String prefix = "";

	private String suffix = "";

	private String requestContextAttribute = null;

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public void setRequestContextAttribute(String requestContextAttribute) {
		this.requestContextAttribute = requestContextAttribute;
	}

	protected View loadView(String viewname, Locale locale) throws ServletException {
		InternalResourceView view = new InternalResourceView();
		view.setUrl(this.prefix + viewname + this.suffix);
		view.setRequestContextAttribute(this.requestContextAttribute);
		return view;
	}
}
