package com.interface21.web.context.support;

import java.util.Locale;

import javax.servlet.ServletContext;

import com.interface21.beans.BeansException;
import com.interface21.beans.factory.ListableBeanFactory;
import com.interface21.context.ApplicationContext;
import com.interface21.context.ApplicationContextException;
import com.interface21.context.MessageSourceResolvable;
import com.interface21.context.NoSuchMessageException;
import com.interface21.context.support.StaticApplicationContext;
import com.interface21.web.context.ThemeSource;
import com.interface21.web.context.WebApplicationContext;

/**
 * WebApplicationContext implementation for testing.
 * Not for use in production applications.
 */
public class StaticWebApplicationContext extends StaticApplicationContext implements WebApplicationContext {

	private String namespace;

	private ServletContext servletContext;

	/** ThemeSource */
	private ThemeSource themeSource = new ConcreteThemeSource();
	
	public StaticWebApplicationContext() {
	}

	public StaticWebApplicationContext(ApplicationContext parent, String namespace)
	    throws BeansException, ApplicationContextException {
		super(parent);
		this.namespace = namespace;
	}

	public String getNamespace() {
		return namespace;
	}

	/**
	 * Normally this would cause loading, but this class doesn't rely on loading.
	 * @see WebApplicationContext#setServletContext(ServletContext)
	 */
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
		refresh();
		WebApplicationContextUtils.publishConfigObjects(this);
		// Expose as a ServletContext object
		WebApplicationContextUtils.publishWebApplicationContext(this);
	}
	

	public ServletContext getServletContext() {
		return servletContext;
	}

	/**
	 * @see com.interface21.web.context.ThemeSource#getTheme(java.lang.String, com.interface21.context.MessageSourceResolvable, java.util.Locale)
	 */
	public String getTheme(	String theme, MessageSourceResolvable resolvable, Locale locale)
		throws NoSuchMessageException {
		return themeSource.getTheme( theme, resolvable, locale);
	}

	/**
	 * @see com.interface21.web.context.ThemeSource#getTheme(java.lang.String, java.lang.String, java.lang.Object[], java.util.Locale)
	 */
	public String getTheme(String theme, String code, Object[] args, Locale locale)
		throws NoSuchMessageException {
		return themeSource.getTheme(theme, code, args, locale);
	}

	/**
	 * @see com.interface21.web.context.ThemeSource#getTheme(java.lang.String, java.lang.String, java.lang.Object[], java.lang.String, java.util.Locale)
	 */
	public String getTheme(String theme, String code, Object[] args, String defaultMessage,	Locale locale) {
		return themeSource.getTheme(theme, code, args, defaultMessage, locale);
	}

	/**
	 * @see com.interface21.web.context.ThemeSource#refresh(com.interface21.beans.factory.ListableBeanFactory)
	 * Drop from the interface ?
	 */
	public void refresh(ListableBeanFactory beanFactory, ApplicationContext parent)
		throws ApplicationContextException {
		// Nothing
	}

	/**
	 * @see com.interface21.context.support.AbstractApplicationContext#afterRefresh()
	 */
	protected void afterRefresh() throws ApplicationContextException {
		themeSource.refresh(getBeanFactory(), getParent());
	}


}
