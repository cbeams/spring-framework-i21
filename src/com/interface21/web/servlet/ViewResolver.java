/**
 * Generic framework code included with 
 * <a href="http://www.amazon.com/exec/obidos/tg/detail/-/1861007841/">Expert One-On-One J2EE Design and Development</a>
 * by Rod Johnson (Wrox, 2002). 
 * This code is free to use and modify. However, please
 * acknowledge the source and include the above URL in each
 * class using or derived from this code. 
 * Please contact <a href="mailto:rod.johnson@interface21.com">rod.johnson@interface21.com</a>
 * for commercial support.
 */

package com.interface21.web.servlet;

import java.util.Locale;

import javax.servlet.ServletException;

import com.interface21.context.ApplicationContextAware;

/*
 * Interface to be implemented by objects that can resolve views by name.
 * View state doesn't change during the running of the application, 
 * so implementations are free to cache views.
 * <br>Implementations are encouraged to support internationalization.
 * @author  Rod Johnson
 * @version $Revision$
 */
public interface ViewResolver extends ApplicationContextAware {
		
	/** 
	 * Resolve the given view by name.
	 * @param viewName name of the view to resolve
	 * @param locale Locale in which to resolve the view. ViewResolvers
	 * that support internationalization should respect this.
	 * @throws ServletException if the view cannot be resolved.
	 */
	View resolveViewName(String viewName, Locale locale) throws ServletException;

}
