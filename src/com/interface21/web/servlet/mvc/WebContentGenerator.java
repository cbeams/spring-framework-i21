package com.interface21.web.servlet.mvc;

import javax.servlet.http.HttpServletResponse;

import com.interface21.context.support.ApplicationObjectSupport;

/**
 * Convenient superclass for any kind of web content generator,
 * like AbstractController and MultiActionController.
 * Supports HTTP cache control options.
 *
 * @author Rod Johnson
 */
public class WebContentGenerator extends ApplicationObjectSupport {
	
	/** Use HTTP 1.0 expires header? */
	private boolean useExpiresHeader = true;

	public WebContentGenerator() {
	}

	public final void setUseExpiresHeader(boolean useExpiresHeader) {
		this.useExpiresHeader = useExpiresHeader;
	}
	
	/**
	 * Prevent the response being cached.
	 * See www.mnot.net.cache docs.
	 */
	protected final void preventCaching(HttpServletResponse response) {
		response.setHeader("Pragma", "No-cache");
		// HTTP 1.1 header
		response.setHeader("Cache-Control", "no-cache");
		// Http 1.0 is Expires
		if (this.useExpiresHeader) {
			response.setDateHeader("Expires", 1L);
		}
	}

	/**
	 * Set HTTP headers to allow caching for the given number of seconds.
	 * @param response HTTP response
	 * @param seconds number of seconds into the future that the response should
	 * be cacheable for
	 */
	protected final void cacheForSeconds(HttpServletResponse response, int seconds, boolean mustRevalidate) {
		String hval = "max-age=" + seconds;
		if (mustRevalidate) {
			hval += ", must-revalidate";
		}
		// HTTP 1.1 header
		response.setHeader("Cache-Control", hval);
		// Http 1.0 is Expires
		if (this.useExpiresHeader) {
			response.setDateHeader("Expires", System.currentTimeMillis() + seconds * 1000L);
		}
	}

}
