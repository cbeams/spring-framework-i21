package com.interface21.web.servlet.handler;

import java.util.Map;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import com.interface21.web.util.WebUtils;
import com.interface21.context.ApplicationContextException;
import com.interface21.beans.BeansException;

/**
 * Abstract base class for url-mapped HandlerMapping implementations.
 * Provides infrastructure for mapping handlers to URLs, support
 * for a default handler, and configurable URL lookup.
 * For information on the latter, see alwaysUseFullPath property.
 * @author Juergen Hoeller
 * @since 16.04.2003
 * @see #setAlwaysUseFullPath
 */
public abstract class AbstractUrlHandlerMapping extends AbstractHandlerMapping {

	private Map handlerMap = new HashMap();

	private Object defaultHandler = null;

	private boolean alwaysUseFullPath = false;

	/**
	 * Set the default handler.
	 * @param defaultHandler  default handler instance, or null.
	 */
	public void setDefaultHandler(Object defaultHandler) {
		this.defaultHandler = defaultHandler;
		logger.info("Default mapping is to controller [" + this.defaultHandler + "]");
	}

	protected Object getDefaultHandler() {
		return defaultHandler;
	}

	/**
	 * Set if url lookup should always use full path within current servlet
	 * context. Else, the path within the current servlet mapping is used
	 * if applicable (i.e. in the case of a ".../*" servlet mapping in web.xml).
	 * Default is false.
	 */
	public void setAlwaysUseFullPath(boolean alwaysUseFullPath) {
		this.alwaysUseFullPath = alwaysUseFullPath;
	}

	protected void registerHandler(String url, Object handler) {
		this.handlerMap.put(url, handler);
		logger.info("Mapped url [" + url + "] onto handler [" + handler + "]");
	}

	/**
	 * Initialize the handler object with the given name in the bean factory.
	 * This includes setting the LocaleResolver and mapped URL if aware.
	 * @param beanName  the name of the bean in the application cntext
	 * @param url  the URL the bean is mapped to
	 * @return the initialized handler instance
	 * @throws ApplicationContextException if the bean wasn't found in the context
	 */
	protected Object initHandler(String beanName, String url) throws ApplicationContextException {
		try {
			Object handler = getApplicationContext().getBean(beanName);
			logger.debug("Initializing handler [" + handler + "] for url [" + url + "]");
			initHandler(handler);
			if (handler instanceof UrlAwareHandler) {
				((UrlAwareHandler) handler).setUrlMapping(url);
			}
			return handler;
		}
		catch (BeansException ex) {
			// We don't need to worry about NoSuchBeanDefinitionException:
			// we should have got the name from the bean factory.
			throw new ApplicationContextException("Error initializing handler bean for URL mapping '" + beanName + "': " + ex.getMessage(), ex);
		}
	}

	public Object getHandler(HttpServletRequest request) {
		String lookupPath = getLookupPathForRequest(request);
		logger.debug("Looking up handler for: " + lookupPath);
		Object handler = this.handlerMap.get(lookupPath);
		return (handler != null ? handler : this.defaultHandler);
	}

	private String getLookupPathForRequest(HttpServletRequest request) {
		// always use full path within current servlet context?
		if (this.alwaysUseFullPath)
			return WebUtils.getPathWithinApplication(request);
		// else use path within current servlet mapping if applicable
		String rest = WebUtils.getPathWithinServletMapping(request);
		if (!"".equals(rest))
			return rest;
		else
			return WebUtils.getPathWithinApplication(request);
	}
}
