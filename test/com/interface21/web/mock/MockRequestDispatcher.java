package com.interface21.web.mock;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 *
 * @author Rod Johnson
 * @version $RevisionId$
 */
public class MockRequestDispatcher implements RequestDispatcher {

	private String url;

	public MockRequestDispatcher(String url) {
		this.url = url;
	}

	public void forward(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
		((MockHttpServletResponse) servletResponse).forwarded = url;
		System.out.println("RequestDispatcher -- FORWARDING to [" + url + "]");
	}

	public void include(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
		((MockHttpServletResponse) servletResponse).included = url;
		System.out.println("RequestDispatcher -- INCLUDING [" + url + "]");
	}

}
