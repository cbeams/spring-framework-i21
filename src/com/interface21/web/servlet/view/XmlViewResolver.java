package com.interface21.web.servlet.view;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.ServletException;

import com.interface21.beans.BeansException;
import com.interface21.beans.factory.BeanFactory;
import com.interface21.beans.factory.NoSuchBeanDefinitionException;
import com.interface21.beans.factory.xml.XmlBeanFactory;
import com.interface21.web.servlet.View;
import com.interface21.context.ApplicationContextException;

/**
 * Implementation of ViewResolver that uses bean definitions in an XML
 * file, specified by location (URL or relative path, according to the
 * ApplicationContext implementation).
 * The file will typically be located in the WEB-INF directory.
 *
 * <p>This ViewResolver does not support internationalization.
 * Consider ResourceBundleViewResolver if you need to apply
 * different view resources per locale.
 *
 * <p>Extends AbstractCachingViewResolver for decent performance.
 *
 * @author Juergen Hoeller
 * @since 18.06.2003
 * @see com.interface21.context.ApplicationContext#getResourceAsStream
 * @see ResourceBundleViewResolver
 */
public class XmlViewResolver extends AbstractCachingViewResolver {

	/** Default if no other location is supplied */
	public final static String DEFAULT_LOCATION = "/WEB-INF/views.xml";

	private String location = DEFAULT_LOCATION;

	private BeanFactory cachedFactory;

	/**
	 * Set the location of the XML file that defines the view beans.
	 * <p>The default is "/WEB-INF/views.xml".
	 * @param location the location of the XML file.
	 */
	public void setLocation(String location) {
		this.location = location;
	}

	/**
	 * Pre-initialize the factory from the XML file.
	 * Only effective if caching is enabled.
	 */
	protected void initApplicationContext() throws ApplicationContextException {
		if (isCache()) {
			try {
				initFactory();
			}
			catch (IOException ex) {
				throw new ApplicationContextException("Cannot initialize XML file '" + this.location + "'" , ex);
			}
			catch (BeansException ex) {
				throw new ApplicationContextException("Cannot initialize XML file '" + this.location + "'" , ex);
			}
		}
	}

	protected View loadView(String viewName, Locale locale) throws ServletException {
		try {
			Object o = initFactory().getBean(viewName);
			if (!(o instanceof View)) {
				throw new ServletException("Bean with name '" + viewName + "' in XML file '" + this.location + "' must be of type View");
			}
			return (View) o;
		}
		catch (IOException ex) {
			throw new ServletException("Cannot load  XML file '" + this.location + "' trying to resolve view with name '" + viewName + "'", ex);
		}
		catch (NoSuchBeanDefinitionException ex) {
			// Let superclass handle this
			return null;
		}
		catch (BeansException ex) {
			throw new ServletException("Error initializing view bean with name '" + viewName + "' in XML file '" + this.location + "'", ex);
		}
	}

	/**
	 * Initialize the BeanFactory from the XML file.
	 * Synchronized because of access by parallel threads.
	 */
	protected synchronized BeanFactory initFactory() throws IOException, BeansException {
		if (this.cachedFactory != null) {
			return this.cachedFactory;
		}

		BeanFactory xbf = new XmlBeanFactory(getApplicationContext().getResourceAsStream(this.location));
		if (isCache()) {
			this.cachedFactory = xbf;
		}
		return xbf;
	}

}
