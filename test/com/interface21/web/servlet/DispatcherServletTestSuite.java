package com.interface21.web.servlet;

import java.util.Locale;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;

import com.interface21.context.support.MessageSourceResolvableImpl;
import com.interface21.web.bind.EscapedErrors;
import com.interface21.web.context.WebApplicationContext;
import com.interface21.web.context.support.StaticWebApplicationContext;
import com.interface21.web.mock.MockHttpServletRequest;
import com.interface21.web.mock.MockHttpServletResponse;
import com.interface21.web.mock.MockServletConfig;
import com.interface21.web.mock.MockServletContext;
import com.interface21.web.servlet.mvc.BaseCommandController;
import com.interface21.web.servlet.support.RequestContext;
import com.interface21.web.servlet.support.RequestContextUtils;
import com.interface21.web.servlet.theme.AbstractThemeResolver;

/**
 *
 * @author Rod Johnson
 * @version $RevisionId$
 */
public class DispatcherServletTestSuite extends TestCase {

	private ServletConfig servletConfig;
	
	private DispatcherServlet simpleControllerServlet;

	private DispatcherServlet complexControllerServlet;

	public DispatcherServletTestSuite(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		servletConfig = new MockServletConfig(new MockServletContext(), "simple");

		simpleControllerServlet = new DispatcherServlet();
		simpleControllerServlet.setContextClass("com.interface21.web.servlet.SimpleWebApplicationContext");
		simpleControllerServlet.init(servletConfig);

		complexControllerServlet = new DispatcherServlet();
		complexControllerServlet.setContextClass("com.interface21.web.servlet.ComplexWebApplicationContext");
		complexControllerServlet.setNamespace("test");
		complexControllerServlet.setPublishContext(false);
		complexControllerServlet.init(new MockServletConfig(servletConfig.getServletContext(), "complex"));
	}

	public void testControllerServlets() {
		assertTrue("Correct namespace", ("simple" + FrameworkServlet.DEFAULT_NAMESPACE_SUFFIX).equals(simpleControllerServlet.getNamespace()));
		assertTrue("Correct attribute", (FrameworkServlet.SERVLET_CONTEXT_PREFIX + "simple").equals(simpleControllerServlet.getServletContextAttributeName()));
		assertTrue("Context published", simpleControllerServlet.getWebApplicationContext() == servletConfig.getServletContext().getAttribute(FrameworkServlet.SERVLET_CONTEXT_PREFIX + "simple"));

		assertTrue("Correct namespace", "test".equals(complexControllerServlet.getNamespace()));
		assertTrue("Correct attribute", (FrameworkServlet.SERVLET_CONTEXT_PREFIX + "complex").equals(complexControllerServlet.getServletContextAttributeName()));
		assertTrue("Context not published", servletConfig.getServletContext().getAttribute(FrameworkServlet.SERVLET_CONTEXT_PREFIX + "complex") == null);
	}

	public void testInvalidRequest() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest(servletConfig.getServletContext(), "GET", "/invalid.do");
		MockHttpServletResponse response = new MockHttpServletResponse();
		try {
			simpleControllerServlet.doGet(request, response);
			assertTrue("not forwarded", response.forwarded == null);
			assertTrue("correct error code", response.getStatusCode() == HttpServletResponse.SC_NOT_FOUND);
		}
		catch (ServletException ex) {
			fail("Should not have thrown ServletException: " + ex.getMessage());
		}
	}

	public void testFormRequest() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest(servletConfig.getServletContext(), "GET", "/form.do");
		request.addPreferredLocale(Locale.CANADA);
		MockHttpServletResponse response = new MockHttpServletResponse();
		try {
			simpleControllerServlet.doGet(request, response);
			assertTrue("forwarded to form", "form".equals(response.forwarded));
			MessageSourceResolvableImpl resolvable = new MessageSourceResolvableImpl(new String[] {"test"}, null);
			RequestContext rc = new RequestContext(request);

			assertTrue("hasn't RequestContext attribute", request.getAttribute("rc") == null);
			assertTrue("Correct WebApplicationContext", RequestContextUtils.getWebApplicationContext(request) instanceof SimpleWebApplicationContext);
			assertTrue("Correct Locale", Locale.CANADA.equals(RequestContextUtils.getLocale(request)));
			assertTrue("Correct Theme", AbstractThemeResolver.ORIGINAL_DEFAULT_THEME_NAME.equals(RequestContextUtils.getTheme(request).getName()));
			assertTrue("Correct message", "Canadian & test message".equals(rc.getMessage("test", null)));

			assertTrue("Correct Errors", !(rc.getErrors(BaseCommandController.DEFAULT_BEAN_NAME) instanceof EscapedErrors));
			assertTrue("Correct Errors", !(rc.getErrors(BaseCommandController.DEFAULT_BEAN_NAME, false) instanceof EscapedErrors));
			assertTrue("Correct Errors", rc.getErrors(BaseCommandController.DEFAULT_BEAN_NAME, true) instanceof EscapedErrors);
			assertTrue("Correct message", "Canadian & test message".equals(rc.getMessage("test", null)));
			assertTrue("Correct message", "Canadian & test message".equals(rc.getMessage("test", null, false)));
			assertTrue("Correct message", "Canadian &amp; test message".equals(rc.getMessage("test", null, true)));
			assertTrue("Correct message", "Canadian & test message".equals(rc.getMessage(resolvable)));
			assertTrue("Correct message", "Canadian & test message".equals(rc.getMessage(resolvable, false)));
			assertTrue("Correct message", "Canadian &amp; test message".equals(rc.getMessage(resolvable, true)));
		}
		catch (ServletException ex) {
			fail("Should not have thrown ServletException: " + ex.getMessage());
		}
	}

	public void testLocaleRequest() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest(servletConfig.getServletContext(), "GET", "/locale.do");
		request.addPreferredLocale(Locale.CANADA);
		MockHttpServletResponse response = new MockHttpServletResponse();
		try {
			simpleControllerServlet.doGet(request, response);
			assertTrue("not forwarded", response.forwarded == null);
		}
		catch (ServletException ex) {
			fail("Should not have thrown ServletException: " + ex.getMessage());
		}
	}

	public void testUnknownRequest() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest(servletConfig.getServletContext(), "GET", "/unknown.do");
		MockHttpServletResponse response = new MockHttpServletResponse();
		try {
			complexControllerServlet.doGet(request, response);
			fail("Should have thrown ServletException");
		}
		catch (ServletException ex) {
			// expected
		}
	}

	public void testAnotherFormRequest() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest(servletConfig.getServletContext(), "GET", "/form.do");
		request.addPreferredLocale(Locale.CANADA);
		MockHttpServletResponse response = new MockHttpServletResponse();
		try {
			complexControllerServlet.doGet(request, response);
			assertTrue("forwarded to form", "myform.jsp".equals(response.forwarded));
			assertTrue("has RequestContext attribute", request.getAttribute("rc") != null);
			MessageSourceResolvableImpl resolvable = new MessageSourceResolvableImpl(new String[] {"test"}, null);

			RequestContext rc = (RequestContext) request.getAttribute("rc");
			assertTrue("Not in HTML escaping mode", !rc.isDefaultHtmlEscape());
			assertTrue("Correct WebApplicationContext", rc.getWebApplicationContext() instanceof ComplexWebApplicationContext);
			assertTrue("Correct Locale", Locale.CANADA.equals(rc.getLocale()));
			assertTrue("Correct Errors", !(rc.getErrors(BaseCommandController.DEFAULT_BEAN_NAME) instanceof EscapedErrors));
			assertTrue("Correct Errors", !(rc.getErrors(BaseCommandController.DEFAULT_BEAN_NAME, false) instanceof EscapedErrors));
			assertTrue("Correct Errors", rc.getErrors(BaseCommandController.DEFAULT_BEAN_NAME, true) instanceof EscapedErrors);
			assertTrue("Correct message", "Canadian & test message".equals(rc.getMessage("test", null)));
			assertTrue("Correct message", "Canadian & test message".equals(rc.getMessage("test", null, false)));
			assertTrue("Correct message", "Canadian &amp; test message".equals(rc.getMessage("test", null, true)));
			assertTrue("Correct message", "Canadian & test message".equals(rc.getMessage(resolvable)));
			assertTrue("Correct message", "Canadian & test message".equals(rc.getMessage(resolvable, false)));
			assertTrue("Correct message", "Canadian &amp; test message".equals(rc.getMessage(resolvable, true)));

			rc.setDefaultHtmlEscape(true);
			assertTrue("Is in HTML escaping mode", rc.isDefaultHtmlEscape());
			assertTrue("Correct Errors", rc.getErrors(BaseCommandController.DEFAULT_BEAN_NAME) instanceof EscapedErrors);
			assertTrue("Correct Errors", !(rc.getErrors(BaseCommandController.DEFAULT_BEAN_NAME, false) instanceof EscapedErrors));
			assertTrue("Correct Errors", rc.getErrors(BaseCommandController.DEFAULT_BEAN_NAME, true) instanceof EscapedErrors);
			assertTrue("Correct message", "Canadian &amp; test message".equals(rc.getMessage("test", null)));
			assertTrue("Correct message", "Canadian & test message".equals(rc.getMessage("test", null, false)));
			assertTrue("Correct message", "Canadian &amp; test message".equals(rc.getMessage("test", null, true)));
			assertTrue("Correct message", "Canadian &amp; test message".equals(rc.getMessage(resolvable)));
			assertTrue("Correct message", "Canadian & test message".equals(rc.getMessage(resolvable, false)));
			assertTrue("Correct message", "Canadian &amp; test message".equals(rc.getMessage(resolvable, true)));
		}
		catch (ServletException ex) {
			fail("Should not have thrown ServletException: " + ex.getMessage());
		}
	}

	public void testAnotherLocaleRequest() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest(servletConfig.getServletContext(), "GET", "/locale.do");
		request.addPreferredLocale(Locale.CANADA);
		request.addRole("role1");
		MockHttpServletResponse response = new MockHttpServletResponse();
		try {
			complexControllerServlet.doGet(request, response);
			assertTrue("not forwarded", response.forwarded == null);
		}
		catch (ServletException ex) {
			fail("Should not have thrown ServletException: " + ex.getMessage());
		}
	}

	public void testLocaleChangeInterceptor1() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest(servletConfig.getServletContext(), "GET", "/locale.do");
		request.addPreferredLocale(Locale.GERMAN);
		request.addRole("role2");
		request.addParameter("locale", "en");
		MockHttpServletResponse response = new MockHttpServletResponse();
		try {
			complexControllerServlet.doGet(request, response);
			assertTrue("not forwarded", response.forwarded == null);
			fail("Should have thrown ServletException");
		}
		catch (ServletException ex) {
			// expected
		}
	}

	public void testLocaleChangeInterceptor2() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest(servletConfig.getServletContext(), "GET", "/locale.do");
		request.addPreferredLocale(Locale.GERMAN);
		request.addRole("role2");
		request.addParameter("locale", "en");
		request.addParameter("locale2", "en_CA");
		MockHttpServletResponse response = new MockHttpServletResponse();
		try {
			complexControllerServlet.doGet(request, response);
			assertTrue("not forwarded", response.forwarded == null);
		}
		catch (ServletException ex) {
			fail("Should not have thrown ServletException: " + ex.getMessage());
		}
	}

	public void testThemeChangeInterceptor1() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest(servletConfig.getServletContext(), "GET", "/locale.do");
		request.addPreferredLocale(Locale.CANADA);
		request.addRole("role1");
		request.addParameter("theme", "mytheme");
		MockHttpServletResponse response = new MockHttpServletResponse();
		try {
			complexControllerServlet.doGet(request, response);
			assertTrue("not forwarded", response.forwarded == null);
			fail("Should have thrown ServletException");
		}
		catch (ServletException ex) {
			// expected
		}
	}

	public void testThemeChangeInterceptor2() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest(servletConfig.getServletContext(), "GET", "/locale.do");
		request.addPreferredLocale(Locale.CANADA);
		request.addRole("role3");
		request.addParameter("theme", "mytheme");
		request.addParameter("theme2", "theme");
		MockHttpServletResponse response = new MockHttpServletResponse();
		try {
			complexControllerServlet.doGet(request, response);
			assertTrue("not forwarded", response.forwarded == null);
		}
		catch (ServletException ex) {
			fail("Should not have thrown ServletException: " + ex.getMessage());
		}
	}

	public void testNotAuthorized() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest(servletConfig.getServletContext(), "GET", "/locale.do");
		request.addPreferredLocale(Locale.CANADA);
		MockHttpServletResponse response = new MockHttpServletResponse();
		try {
			complexControllerServlet.doGet(request, response);
			assertTrue("Correct response", response.getStatusCode() == HttpServletResponse.SC_FORBIDDEN);
		}
		catch (ServletException ex) {
			fail("Should not have thrown ServletException: " + ex.getMessage());
		}
	}

	public void testWebApplicationContextLookup() {
		MockServletContext servletContext = new MockServletContext();
		MockHttpServletRequest request = new MockHttpServletRequest(servletContext, "GET", "/invalid.do");

		try {
			RequestContextUtils.getWebApplicationContext(request);
			fail("Should have thrown ServletException");
		}
		catch (ServletException ex) {
			// expected
		}

		try {
			RequestContextUtils.getWebApplicationContext(request, servletContext);
			fail("Should have thrown ServletException");
		}
		catch (ServletException ex) {
			// expected
		}

		servletContext.setAttribute(WebApplicationContext.WEB_APPLICATION_CONTEXT_ATTRIBUTE_NAME, new StaticWebApplicationContext());
		try {
			RequestContextUtils.getWebApplicationContext(request, servletContext);
		}
		catch (ServletException ex) {
			fail("Should not have thrown ServletException: " + ex.getMessage());
		}
	}

}
