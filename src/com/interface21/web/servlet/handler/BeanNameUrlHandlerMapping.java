package com.interface21.web.servlet.handler;

import com.interface21.util.StringUtils;

/**
 * Implementation of the HandlerMapping interface to map from URLs
 * to beans. This is the default implementation used by the
 * DispatcherServlet, but is somewhat naive. A SimpleUrlHandlerMapping
 * or a custom handler mapping should be used by preference.
 *
 * <p>The mapping is from URL to bean name. Thus an incoming URL
 * "/foo" would map to a handler named "/foo", or to "/foo /foo2"
 * in case of multiple mappings to a single handler.
 * Note: In XML definitions, you'll need to use an alias "name=/foo"
 * in the bean definition, as the XML id may not contain slashes.
 *
 * <p>Supports direct matches (given "/test" -> registered "/test")
 * and "*" matches (given "/test" -> registered "/t*").
 *
 @author Rod Johnson
 * @author Juergen Hoeller
 * @see SimpleUrlHandlerMapping
 */
public class BeanNameUrlHandlerMapping extends AbstractUrlHandlerMapping {
	
	/** Delimiter between multiple URLs in mappings */
	public static final String MULTI_URL_DELIMITER = " ";

	public void initApplicationContext() {
		logger.debug("Looking for URL mappings...");
		String[] urlMaps = getApplicationContext().getBeanDefinitionNames();

		// Take anything beginning with a / in the bean name
		for (int i = 0; i < urlMaps.length; i++) {
			String url = checkForUrl(urlMaps[i]);
			if (url != null) {
				logger.debug("Found URL mapping [" + urlMaps[i] + "]");
				Object handler = initHandler(urlMaps[i], url);

				// Create a mapping to each part of the path
				String[] mappedUrls = StringUtils.delimitedListToStringArray(url, MULTI_URL_DELIMITER);
				for (int j = 0; j < mappedUrls.length; j++) {
					registerHandler(mappedUrls[j], handler);
				}
			}
			else {
				logger.debug("Rejected bean name '" + urlMaps[i] + "'");
			}
		}
	}

	private String checkForUrl(String beanName) {
		if (beanName.startsWith("/")) {
			return beanName;
		}
		String[] aliases = getApplicationContext().getAliases(beanName);
		for (int j = 0; j < aliases.length; j++) {
			if (aliases[j].startsWith("/")) {
				return aliases[j];
			}
		}
		return null;
	}

}
