package com.interface21.ui.context.support;

import com.interface21.beans.BeansException;
import com.interface21.context.ApplicationContext;
import com.interface21.context.ApplicationContextException;
import com.interface21.context.support.StaticApplicationContext;
import com.interface21.ui.context.Theme;
import com.interface21.ui.context.ThemeSource;

/**
 * Adds theme capabilities for UI contexts.
 * @author Jean-Pierre Pawlak
 */
public class StaticUiApplicationContext extends StaticApplicationContext implements ThemeSource {

	private ThemeSource themeSource;

	/**
	 * Standard constructor.
	 */
	public StaticUiApplicationContext()	throws BeansException, ApplicationContextException {
		super();
	}

	/**
	 * Constructor with parent context.
	 */
	public StaticUiApplicationContext(ApplicationContext parent) throws BeansException, ApplicationContextException {
		super(parent);
	}

	/**
	 * Initialize the theme capability.
	 */
	protected void onRefresh() {
		this.themeSource = UiApplicationContextUtils.initThemeSource(this);
	}

	public Theme getTheme(String themeName) {
		return this.themeSource.getTheme(themeName);
	}

}
