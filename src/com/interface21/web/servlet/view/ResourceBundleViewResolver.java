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
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.servlet.ServletException;

import com.interface21.beans.BeansException;
import com.interface21.beans.factory.BeanFactory;
import com.interface21.beans.factory.NoSuchBeanDefinitionException;
import com.interface21.beans.factory.support.ListableBeanFactoryImpl;
import com.interface21.web.servlet.View;

/**
 * Implementation of ViewResolver that uses bean definitions in a
 * ResourceBundle, specified by the bundle basename. The bundle is
 * typically defined in a properties file, located in the classpath.
 *
 * <p>This ViewResolver supports internationalization,
 * using the default support of java.util.PropertyResourceBundle.
 *
 * <p>Extends AbstractCachingViewResolver for decent performance.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see java.util.ResourceBundle#getBundle
 * @see java.util.PropertyResourceBundle
 */
public class ResourceBundleViewResolver extends AbstractCachingViewResolver {

	/** Default if no other basename is supplied */
	public final static String DEFAULT_BASENAME = "views";

	private String basename = DEFAULT_BASENAME;

	private String defaultParentView;

	/** Locale -> BeanFactory */
	private Map cachedFactories = new HashMap();

	/**
	 * Set the basename, as defined in the java.util.ResourceBundle documentation.
	 * ResourceBundle supports different suffixes. For example, a base name of
	 * "views" might map to ResourceBundle files "views", "views_en_au" and "views_de".
	 * <p>The default is "views".
	 * @param basename the ResourceBundle base name
	 * @see java.util.ResourceBundle
	 */
	public void setBasename(String basename) {
		this.basename = basename;
	}

	/**
	 * Set the default parent for views defined in the ResourceBundle.
	 * This avoids repeated "yyy1.parent=xxx", "yyy2.parent=xxx" definitions
	 * in the bundle, especially if all defined views share the same parent.
	 * The parent will typically define the view class and common attributes.
	 * Concrete views might simply consist of an URL definition then:
	 * a la "yyy1.url=/my.jsp", "yyy2.url=/your.jsp".
	 * @param defaultParentView the default parent view
	 */
	public void setDefaultParentView(String defaultParentView) {
		this.defaultParentView = defaultParentView;
	}

	protected View loadView(String viewName, Locale locale) throws ServletException {
		try {
			Object o = initFactory(locale).getBean(viewName);
			if (!(o instanceof View)) {
				throw new ServletException("Bean with name '" + viewName + "' in resource bundle with basename '" + this.basename + "' must be of type View");
			}
			return (View) o;
		}
		catch (MissingResourceException ex) {
			throw new ServletException("Cannot load resource bundle with basename '" + this.basename + "' trying to resolve view with name '" + viewName + "'", ex);
		}
		catch (NoSuchBeanDefinitionException ex) {
			// Let superclass handle this
			return null;
		}
		catch (BeansException ex) {
			throw new ServletException("Error initializing view bean with name '" + viewName + "' in resource bundle with basename '" + this.basename + "'", ex);
		}
	}

	/**
	 * Initialize the BeanFactory from the ResourceBundle, for the given locale.
	 * Synchronized because of access by parallel threads.
	 */
	protected synchronized BeanFactory initFactory(Locale locale) throws MissingResourceException, BeansException {
		BeanFactory parsedBundle = isCache() ? (BeanFactory) this.cachedFactories.get(locale) : null;
		if (parsedBundle != null) {
			return parsedBundle;
		}

		ResourceBundle bundle = ResourceBundle.getBundle(this.basename, locale,
																										 Thread.currentThread().getContextClassLoader());
		ListableBeanFactoryImpl lbf = new ListableBeanFactoryImpl();
		lbf.setDefaultParentBean(this.defaultParentView);
		lbf.registerBeanDefinitions(bundle, null);
		if (isCache()) {
			this.cachedFactories.put(locale, lbf);
		}
		return lbf;
	}

}
