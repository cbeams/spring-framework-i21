package com.interface21.web.servlet.handler;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.interface21.context.ApplicationContextException;
import com.interface21.util.StringUtils;


/**
 * Implementation of the HandlerMapping interface to map from URLs
 * to beans. This is the default implementation used by the
 * ControllerServlet, but is somewhat naive. A SimpleUrlHandlerMapping
 * or a custom handler mapping should be used by preference.
 * <br/>The mapping is from URL to bean name. Thus an incoming URL
 * /foo.html would map to a handler named foo.
 * @author Rod Johnson, Juergen Hoeller
 * @see com.interface21.web.servlet.handler.SimpleUrlHandlerMapping
 */
public class BeanNameUrlHandlerMapping extends AbstractUrlHandlerMapping {
	
	/** Delimiter between multiple URLs in mappings */
	public static final String MULTI_URL_DELIMITER = " ";
	
	private Map handlerMap;
	
	public void initHandlerMapping() {
		this.handlerMap = new HashMap();
		logger.debug("Looking for URL mappings...");
		String[] urlMaps = getApplicationContext().getBeanDefinitionNames();
		
		// Take anything beginning with a / in the bean name
		for (int i = 0; i < urlMaps.length; i++) {
			if (urlMaps[i].startsWith("/")) {
				logger.debug("Found URL mapping [" + urlMaps[i] + "]");
				initHandler(urlMaps[i], urlMaps[i]);
			}
			else {
				logger.debug("Rejected bean name '" + urlMaps[i] + "'");
			}
		}
	}	// init

	public Object getHandler(HttpServletRequest request) {
		return handlerMap.get(request.getServletPath());
	}

	protected void registerHandler(Object handler, String url) {
		// Create a mapping to each part of the path
		String[] mappedUrls = StringUtils.delimitedListToStringArray(url, MULTI_URL_DELIMITER);
		for (int i = 0; i < mappedUrls.length; i++) {
			handlerMap.put(mappedUrls[i], handler);
			logger.info("Mapped url [" + mappedUrls[i] + "] onto handler [" + handler + "]");
		}
	}	// initHandler

}	

