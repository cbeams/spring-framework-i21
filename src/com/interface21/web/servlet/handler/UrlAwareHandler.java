package com.interface21.web.servlet.handler;

/**
 * Optional interface to be implemented by request handlers in our
 * MVC approach that are interested in the URL they are mapped to.
 * @author Rod Johnson
 */
public interface UrlAwareHandler {
	
	/** 
	 * Set the URL this handler is mapped to.
	 * This interface will only be implemented by some handlers:
	 * others may be mapped to many URLs, and so cannot
	 * benefit from knowledge of individual mappings.
	 * Of course a handler can find out the request URL when it handles
	 * a request: implementations of this interface want to know
	 * about a single URL mapping <b>before</b> they begin to handle
	 * requests.
	 * @param url the URL this handler is mapped to
	 */
	void setUrlMapping(String url);

}

