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

package com.interface21.web.context.support;

import com.interface21.context.ApplicationEvent;

/**
 * Event raised when a request is handled by our web framework.
 * @author Rod Johnson
 * @since January 17, 2001
 */
public class RequestHandledEvent extends ApplicationEvent {

	private String url;

	private long timeMillis;

	private String ipAddress;

	/** Usually GET or POST */
	private String method;

	/** Name of the servlet that handled this request is available */
	private String servletName;

	private Throwable failureCause;

	public RequestHandledEvent(Object source, String url, long timeMillis, String ip, String method, String servletName) {
		super(source);
		this.url = url;
		this.timeMillis = timeMillis;
		this.ipAddress = ip;
		this.method = method;
		this.servletName = servletName;
	}

	public RequestHandledEvent(Object source, String url, long timeMillis, String ip, String method, String servletName, Throwable ex) {
		this(source, url, timeMillis, ip, method, servletName);
		this.failureCause = ex;
	}

	public String getURL() {
		return url;
	}

	public long getTimeMillis() {
		return timeMillis;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public String getMethod() {
		return method;
	}

	public String getServletName() {
		return servletName;
	}

	public boolean wasFailure() {
		return failureCause != null;
	}

	public Throwable getFailureCause() {
		return failureCause;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer("RequestHandledEvent: url=[" + getURL() + "] time=" + getTimeMillis() + "ms");
		sb.append(" client=" + getIpAddress() + " method='" + getMethod() + "' servlet='" + getServletName() + "'");
		sb.append(" status=" + (sb.append(!wasFailure() ? "OK" : "failed: " + getFailureCause())));
		return sb.toString();
	}

}
