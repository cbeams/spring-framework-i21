package com.interface21.web.servlet.handler;

import com.interface21.util.StringUtils;

/**
 * Implementation of the HandlerMapping interface to map from URLs
 * to beans. This is the default implementation used by the
 * ControllerServlet, but is somewhat naive. A SimpleUrlHandlerMapping
 * or a custom handler mapping should be used by preference.
 * <br/>The mapping is from URL to bean name. Thus an incoming URL
 * /foo would map to a handler named /foo.
 * @author Rod Johnson, Juergen Hoeller
 * @see SimpleUrlHandlerMapping
 */
public class BeanNameUrlHandlerMapping extends AbstractUrlHandlerMapping {
	
	/** Delimiter between multiple URLs in mappings */
	public static final String MULTI_URL_DELIMITER = " ";

	public void initHandlerMapping() {
		logger.debug("Looking for URL mappings...");
		String[] urlMaps = getApplicationContext().getBeanDefinitionNames();

		// Take anything beginning with a / in the bean name
		for (int i = 0; i < urlMaps.length; i++) {
			if (urlMaps[i].startsWith("/")) {
				logger.debug("Found URL mapping [" + urlMaps[i] + "]");
				Object handler = initHandler(urlMaps[i], urlMaps[i]);

				// Create a mapping to each part of the path
				String[] mappedUrls = StringUtils.delimitedListToStringArray(urlMaps[i], MULTI_URL_DELIMITER);
				for (int j = 0; j < mappedUrls.length; j++) {
					registerHandler(mappedUrls[j], handler);
				}
			}
			else {
				logger.debug("Rejected bean name '" + urlMaps[i] + "'");
			}
		}
	}

}
