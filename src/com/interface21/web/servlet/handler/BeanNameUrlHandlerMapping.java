package com.interface21.web.servlet.handler;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.interface21.beans.BeansException;
import com.interface21.beans.factory.BeanFactory;
import com.interface21.context.ApplicationContext;
import com.interface21.context.ApplicationContextAware;
import com.interface21.context.ApplicationContextException;
import com.interface21.util.StringUtils;
import com.interface21.web.servlet.HandlerMapping;
import com.interface21.web.servlet.LocaleResolver;
import com.interface21.web.servlet.LocaleResolverAware;


/**
 * Implementation of the HandlerMap interface to map from URLs
 * to beans. This is the default implementation used by the
 * ControllerServlet, but is somewhat naive. A UrlHandlerMapping
 * or a custom handler mapping should be used by preference.
 * <br/>The mapping is from URL to bean name. Thus an incoming URL
 * /foo.html would map to a handler named foo.
 * @author Rod Johnson
 * @see com.interface21.web.servlet.handler.UrlHandlerMapping
 */
public class BeanNameUrlHandlerMapping implements HandlerMapping {
	
	/** Delimiter between multiple URLs in mappings */
	public static final String MULTI_URL_DELIMITER = ",";
	
	//---------------------------------------------------------------------
	// Instance data
	//---------------------------------------------------------------------
	protected final Logger logger = Logger.getLogger(getClass().getName());
	
	private Map handlerMap;
	
	private ApplicationContext applicationContext;

	private LocaleResolver localeResolver;


	//---------------------------------------------------------------------
	// Implementation of ApplicationContextAware
	//---------------------------------------------------------------------
	/** Set the ApplicationContext object used by this object
	 * @param ctx ApplicationContext object used by this object
	 * @throws com.interface21.context.ApplicationContextException if initialization attempted by this object
	 * after it has access to the WebApplicatinContext fails
	 */
	public void setApplicationContext(ApplicationContext ctx) throws ApplicationContextException {
		this.applicationContext = ctx;
	}
	
	/** Return the ApplicationContext used by this object.
	 * @return the ApplicationContext used by this object.
	 * Returns null if setApplicationContext() has not yet been called:
	 * this can be useful to check whether or not the object has been initialized.
	 */
	public ApplicationContext getApplicationContext() {
		return this.applicationContext;
	}


	//---------------------------------------------------------------------
	// Implementation of LocaleResolverAware
	//---------------------------------------------------------------------
	public void setLocaleResolver(LocaleResolver localeResolver) {
		this.localeResolver = localeResolver;
	}


	//---------------------------------------------------------------------
	// Implementation of HandlerMap
	//---------------------------------------------------------------------
	public void init() throws ApplicationContextException {
		this.handlerMap = new HashMap();
		logger.debug("Looking for URL mappings...");
		String[] urlmaps = applicationContext.getBeanDefinitionNames();
		
		// Take anything beginning with a / in the bean name
		for (int i = 0; i < urlmaps.length; i++) {
			if (urlmaps[i].startsWith("/")) {
				logger.debug("Found URL mapping [" + urlmaps[i] + "]"); 
				initHandler(applicationContext, urlmaps[i]);
			}
			else {
				logger.debug("Rejected bean name '" + urlmaps[i] + "'");
			}
		}
	}	// init
	
	/**
	 * Return a handler for this request
	 * @return null if no match was found
	 * @throws javax.servlet.ServletException if there is an internal error
	 */
	public Object getHandler(HttpServletRequest request) throws ServletException {
		String path = request.getServletPath();
		if (request.getPathInfo() != null)
			path += request.getPathInfo();
		return handlerMap.get(path);
	}
	
	
	//---------------------------------------------------------------------
	// Implementation methods
	//---------------------------------------------------------------------
	/** 
	 * Init the handler object with this name in the bean factory.
	 * This will include setting the Context and namespace if the CommandGenerator is context aware.
	 */
	private void initHandler(BeanFactory bf, String beanName) throws ApplicationContextException {
		try {
			
			Object handler = bf.getBean(beanName);
			//log4jCategory.info("Command servlet '" + getServletName() + "': Mapping from [" + beanName + "] to " + handler);
			
			if (handler instanceof UrlAwareHandler) {
				((UrlAwareHandler) handler).setUrlMapping(beanName);
			}
			
			if (handler instanceof ApplicationContextAware) {
				((ApplicationContextAware) handler).setApplicationContext(this.applicationContext);
				//log4jCategory.debug("Command servlet '" + getServletName() + "': CommandGenerator " + cp + " is WebApplicationContextAware: set context");
			}

			if (handler instanceof LocaleResolverAware) {
				((LocaleResolverAware) handler).setLocaleResolver(this.localeResolver);
			}

			// Create a mapping to each part of the path
			String[] mappedUrls = StringUtils.delimitedListToStringArray(beanName, MULTI_URL_DELIMITER);
			for (int i = 0; i < mappedUrls.length; i++) {
				handlerMap.put(mappedUrls[i], handler);
				logger.info("Mapped url [" + mappedUrls[i] + "] onto handler [" + handler + "]");
			}
		}
		catch (BeansException ex) {
			// We don't need to worry about NoSuchBeanDefinitionException:
			// we got the name from the bean factory.
			throw new ApplicationContextException("Error initializing handler bean for URLMapping '" + beanName + "': " + ex, ex);
		}
		catch (ApplicationContextException ex) {
			throw new ApplicationContextException("Can't set ApplicationContext on handler bean for URLMapping '" + beanName + "': " + ex, ex.getRootCause());
		}
	}	// initHandler
	
}	

