package com.interface21.web.servlet.handler;

import org.apache.log4j.Logger;

import com.interface21.beans.BeansException;
import com.interface21.context.ApplicationContext;
import com.interface21.context.ApplicationContextException;
import com.interface21.core.Ordered;
import com.interface21.web.servlet.HandlerMapping;
import com.interface21.web.servlet.LocaleResolver;
import com.interface21.web.servlet.LocaleResolverAware;

/**
 * Abstract base class for url-based HandlerMapping implementations.
 * Provides handler initialization method that cares about
 * LocaleResolver, UrlAwareHandler, etc.
 * @author Juergen Hoeller
 * @since 07.04.2003
 */
public abstract class AbstractUrlHandlerMapping implements HandlerMapping, Ordered {

	protected final Logger logger = Logger.getLogger(getClass());

	private ApplicationContext applicationContext;

	private LocaleResolver localeResolver;

	private int order = Integer.MAX_VALUE;  // default: same as non-Ordered

	public void setApplicationContext(ApplicationContext ctx) {
		this.applicationContext = ctx;
	}

	public ApplicationContext getApplicationContext() {
		return this.applicationContext;
	}

	public void setLocaleResolver(LocaleResolver localeResolver) {
		this.localeResolver = localeResolver;
	}

	public LocaleResolver getLocaleResolver() {
		return localeResolver;
	}

	public int getOrder() {
	  return order;
	}

	public void setOrder(int order) {
	  this.order = order;
	}

	/**
	 * Initialize the handler object with this name in the bean factory,
	 * and map it to the given url.
	 * This will include setting the LocaleResolver and url mapping if aware.
	 * @param beanName  the name of the bean in the application cntext
	 * @param url  the url the bean should be mapped to
	 * @return the initialized handler instance
	 * @throws ApplicationContextException in case of bean initialization errors
	 */
	protected Object initHandler(String beanName, String url) throws ApplicationContextException {
		try {
			Object handler = getApplicationContext().getBean(beanName);

			if (handler instanceof LocaleResolverAware) {
				((LocaleResolverAware) handler).setLocaleResolver(getLocaleResolver());
			}

			if (handler instanceof UrlAwareHandler) {
				((UrlAwareHandler) handler).setUrlMapping(url);
			}

			registerHandler(handler, url);

			return handler;
		}
		catch (BeansException ex) {
			// We don't need to worry about NoSuchBeanDefinitionException:
			// we got the name from the bean factory.
			throw new ApplicationContextException("Error initializing handler bean for URL mapping '" + beanName + "': " + ex, ex);
		}
		catch (ApplicationContextException ex) {
			throw new ApplicationContextException("Can't set ApplicationContext on handler bean for URL mapping '" + beanName + "': " + ex, ex.getRootCause());
		}
	}	// initHandler

	/**
	 * Register the given handler instance for the given url.
	 * Gets called by initHandler after initializing the bean instance.
	 * @param handler  the initialized handler instance
	 * @param url  the url the handler should be mapped to
	 */
	protected abstract void registerHandler(Object handler, String url);
}
