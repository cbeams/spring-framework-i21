/*
 * Créé le 17 juin 2003
 */
package com.interface21.ui.context.support;

import java.util.Locale;

import com.interface21.beans.BeansException;
import com.interface21.beans.factory.ListableBeanFactory;
import com.interface21.context.ApplicationContext;
import com.interface21.context.ApplicationContextException;
import com.interface21.context.MessageSourceResolvable;
import com.interface21.context.NoSuchMessageException;
import com.interface21.context.support.StaticApplicationContext;
import com.interface21.ui.context.ThemeSource;

/**
 * @author jpp
 */
public class StaticUiApplicationContext extends StaticApplicationContext implements ThemeSource {

	/** ThemeSource */
	private ThemeSource themeSource = new ConcreteThemeSource();

	/**
	 * @throws BeansException
	 * @throws ApplicationContextException
	 */
	public StaticUiApplicationContext()
		throws BeansException, ApplicationContextException {
		super();
	}

	/**
	 * @param parent
	 * @throws BeansException
	 * @throws ApplicationContextException
	 */
	public StaticUiApplicationContext(ApplicationContext parent)
		throws BeansException, ApplicationContextException {
		super(parent);
	}

	/**
	 * @see com.interface21.context.support.AbstractApplicationContext#afterRefresh()
	 */
	protected void afterRefresh() throws ApplicationContextException {
		themeSource.refresh(getBeanFactory(), getParent());
	}

	/**
	 * @see com.interface21.ui.context.ThemeSource#getTheme(java.lang.String, com.interface21.context.MessageSourceResolvable, java.util.Locale)
	 */
	public String getTheme(String theme, MessageSourceResolvable resolvable, Locale locale)
		throws NoSuchMessageException {
		return themeSource.getTheme( theme, resolvable, locale);
	}

	/**
	 * @see com.interface21.ui.context.ThemeSource#getTheme(java.lang.String, java.lang.String, java.lang.Object[], java.util.Locale)
	 */
	public String getTheme(String theme, String code, Object[] args, Locale locale)
		throws NoSuchMessageException {
			return themeSource.getTheme(theme, code, args, locale);
	}

	/**
	 * @see com.interface21.ui.context.ThemeSource#getTheme(java.lang.String, java.lang.String, java.lang.Object[], java.lang.String, java.util.Locale)
	 */
	public String getTheme(String theme, String code, Object[] args, String defaultMessage, Locale locale) {
		return themeSource.getTheme(theme, code, args, defaultMessage, locale);
	}

	/**
	 * @see com.interface21.ui.context.ThemeSource#refresh(com.interface21.beans.factory.ListableBeanFactory, com.interface21.context.ApplicationContext)
	 */
	public void refresh(ListableBeanFactory beanFactory, ApplicationContext parent)
		throws ApplicationContextException {
		// Nothing
	}

}
