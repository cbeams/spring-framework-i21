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

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.servlet.ServletException;

import com.interface21.beans.BeansException;
import com.interface21.beans.factory.NoSuchBeanDefinitionException;
import com.interface21.beans.factory.support.ListableBeanFactoryImpl;
import com.interface21.web.servlet.View;

/**
 * Implementation of ViewResolver using bean definitions in
 * a given namespace's BeanFactory.
 * <b>This ViewResolver supports internationalization,</b>
 * using the default support of the ResourceBundle.
 * <br/>Extends AbstractCachingViewResolver for decent performance.
 * @author  Rod Johnson
 */
public class ResourceBundleViewResolver extends AbstractCachingViewResolver {

	/** Default if no other basename is supplied */
	public final static String DEFAULT_BASENAME = "views";

	//---------------------------------------------------------------------
	// Instance data
	//---------------------------------------------------------------------
	/** Resource bundle basename */
	private String basename = DEFAULT_BASENAME;


	//---------------------------------------------------------------------
	// Bean properties
	//---------------------------------------------------------------------
	/**
	 * Basename property. A basename is as defined in the java.util.ResourceBundle
	 * documentation. ResourceBundle supports different suffixes. For example,
	 * a base name of view might map to ResourceBundle files view, view_en_au
	 * and view_de
	 * @param basename new value of the ResourceBundle base name
	 */
	public void setBasename(String basename) {
		this.basename = basename;
	}


	//---------------------------------------------------------------------
	// Implementation of protected abstract methods
	//---------------------------------------------------------------------
	/** 
	 * Subclasses must implement this method. There need be no concern for efficiency,
	 * as AbstractCachingViewResolver will cache views.
	 * @param viewname name of the view to retrieve
	 * @param locale Locale to retrieve the view for. Not all subclasses may support
	 * internationalization. A subclass that doesn't can ignore this parameter.
	 * @throws ServetException if there is an error trying to resolve the view
	 * @return the View if it can be resolved; otherwise null.
	 */
	protected View loadView(String viewName, Locale locale) throws ServletException {
		ResourceBundle bundle = null;

		// Distinguish between failure to load a bundle and failure
		// to load a message
		try {
			bundle = ResourceBundle.getBundle(basename, locale);
		}
		catch (MissingResourceException ex) {
			throw new ServletException("Cannot load resource bundle with basename '" + this.basename + "' trying to resolve view with name '" + viewName + "'", ex);
		}

		// Now try to load view beans
		try {
			// We must pass a class loader context to the ListableBeanFactoryImpl
			// class, which may have been loaded by a different class Loader.
			ListableBeanFactoryImpl lbf = new ListableBeanFactoryImpl(this);
			lbf.registerBeanDefinitions(bundle, null);
			
			Object o = lbf.getBean(viewName);
			if (!(o instanceof View))
				throw new ServletException("Bean with name '" + viewName + "' in resource bundle with basename '" + this.basename + "' must be of type View");
			return (View) o;
		}
		catch (NoSuchBeanDefinitionException ex) {
			// Let superclass handle this
			return null;
		}
		catch (BeansException ex) {
			throw new ServletException("Error initializing view bean with name '" + viewName + "' in resource bundle with basename '" + this.basename + "': " + ex.getMessage(), ex);
		}
	}	// loadView

}	// class ResourceBundleViewResolver 
