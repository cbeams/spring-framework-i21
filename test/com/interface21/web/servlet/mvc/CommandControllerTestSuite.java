package com.interface21.web.servlet.mvc;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;

import com.interface21.beans.TestBean;
import com.interface21.validation.Errors;
import com.interface21.validation.FieldError;
import com.interface21.web.mock.MockHttpServletRequest;
import com.interface21.web.mock.MockHttpServletResponse;
import com.interface21.web.servlet.ModelAndView;
import com.interface21.web.servlet.LastModified;

/**
 *
 * @author Rod Johnson
 * @version $RevisionId$
 */
public class CommandControllerTestSuite extends TestCase {

	/**
	 * Constructor for AbstractMultiRequestHandlerTestSuite.
	 * @param arg0
	 */
	public CommandControllerTestSuite(String arg0) {
		super(arg0);
	}

	public void setUp() {
		
	}

	public void testNoArgsNoErrors() throws Exception {
		TestController mc = new TestController();
		HttpServletRequest request = new MockHttpServletRequest(null, "GET", "/welcome.html");
		MockHttpServletResponse response = new MockHttpServletResponse();
		ModelAndView mv = mc.handleRequest(request, response);
		assertTrue("returned correct view name", mv.getViewName().equals(request.getServletPath()));
		TestBean person = (TestBean) mv.getModel().get("command");
		Errors errors = (Errors) mv.getModel().get("errors");
		assertTrue("command and errors non null", person != null && errors != null);
		assertTrue("no errors", !errors.hasErrors());
		assertTrue("Correct caching", response.getHeader("Cache-Control") == null);
		assertTrue("Correct expires header", response.getHeader("Expires") == null);
	}

	public void test2ArgsNoErrors() throws Exception {
		TestController mc = new TestController();
		MockHttpServletRequest request = new MockHttpServletRequest(null, "GET", "/ok.html");
		String name = "Rod";
		int age = 32;
		request.addParameter("name", name);
		request.addParameter("age", "" + age);
		HttpServletResponse response = new MockHttpServletResponse();
		ModelAndView mv = mc.handleRequest(request, response);
		assertTrue("returned correct view name", mv.getViewName().equals(request.getServletPath()));
		TestBean person = (TestBean) mv.getModel().get("command");
		Errors errors = (Errors) mv.getModel().get("errors");
		assertTrue("command and errors non null", person != null && errors != null);
		assertTrue("no errors", !errors.hasErrors());
		assertTrue("command name bound ok", person.getName().equals(name));
		assertTrue("command age bound ok", person.getAge() == age);
	}
	
	public void test2Args1Mismatch() throws Exception {
		TestController mc = new TestController();
		MockHttpServletRequest request = new MockHttpServletRequest(null, "GET", "/ok.html");
		String name = "Rod";
		String age = "32x";
		request.addParameter("name", name);
		request.addParameter("age", age);
		HttpServletResponse response = new MockHttpServletResponse();
		ModelAndView mv = mc.handleRequest(request, response);
		assertTrue("returned correct view name", mv.getViewName().equals(request.getServletPath()));
		TestBean person = (TestBean) mv.getModel().get("command");
		Errors errors = (Errors) mv.getModel().get("errors");
		assertTrue("command and errors non null", person != null && errors != null);
		assertTrue("has 1 errors", errors.getErrorCount() == 1);
		assertTrue("command name bound ok", person.getName().equals(name));
		assertTrue("command age default", person.getAge() == new TestBean().getAge());
		
		assertTrue("has error on field age", errors.hasFieldErrors("age"));
		FieldError fe = errors.getFieldError("age");
		assertTrue("Saved invalid value", fe.getRejectedValue().equals(age));
		assertTrue("Correct field", fe.getField().equals("age"));
	}

	public void testSupportedMethods() throws Exception {
		TestController mc = new TestController();
		mc.setSupportedMethods(new String[] {"POST"});
		HttpServletRequest request = new MockHttpServletRequest(null, "GET", "/ok.html");
		HttpServletResponse response = new MockHttpServletResponse();
		try {
			mc.handleRequest(request, response);
			fail("Should have thrown ServletException");
		}
		catch (ServletException ex) {
			// expected
		}
	}

	public void testRequireSession() throws Exception {
		TestController mc = new TestController();
		mc.setRequireSession(true);
		HttpServletRequest request = new MockHttpServletRequest(null, "GET", "/ok.html");
		HttpServletResponse response = new MockHttpServletResponse();
		try {
			mc.handleRequest(request, response);
			fail("Should have thrown ServletException");
		}
		catch (ServletException ex) {
			// expected
		}
	}

	public void testNoCaching() throws Exception {
		TestController mc = new TestController();
		mc.setCacheSeconds(0);
		HttpServletRequest request = new MockHttpServletRequest(null, "GET", "/ok.html");
		MockHttpServletResponse response = new MockHttpServletResponse();
		mc.handleRequest(request, response);
		assertTrue("Correct caching", response.getHeader("Cache-Control").equals("no-cache"));
		assertTrue("Correct expires header", response.getHeader("Expires").equals("" + 1L));
	}

	public void testNoCachingWithoutExpires() throws Exception {
		TestController mc = new TestController();
		mc.setCacheSeconds(0);
		mc.setUseExpiresHeader(false);
		HttpServletRequest request = new MockHttpServletRequest(null, "GET", "/ok.html");
		MockHttpServletResponse response = new MockHttpServletResponse();
		mc.handleRequest(request, response);
		assertTrue("Correct caching", response.getHeader("Cache-Control").equals("no-cache"));
		assertTrue("Correct expires header", response.getHeader("Expires") == null);
	}

	public void testCaching() throws Exception {
		TestController mc = new TestController();
		mc.setCacheSeconds(10);
		HttpServletRequest request = new MockHttpServletRequest(null, "GET", "/ok.html");
		MockHttpServletResponse response = new MockHttpServletResponse();
		mc.handleRequest(request, response);
		assertTrue("Correct caching", response.getHeader("Cache-Control").equals("max-age=10"));
		assertTrue("Correct expires header", response.getHeader("Expires") != null);
	}

	public void testCachingWithoutExpires() throws Exception {
		TestController mc = new TestController();
		mc.setCacheSeconds(10);
		mc.setUseExpiresHeader(false);
		HttpServletRequest request = new MockHttpServletRequest(null, "GET", "/ok.html");
		MockHttpServletResponse response = new MockHttpServletResponse();
		mc.handleRequest(request, response);
		assertTrue("Correct expires header", response.getHeader("Expires") == null);
	}

	public void testCachingWithLastModified() throws Exception {
		TestController mc = new LastModifiedTestController();
		mc.setCacheSeconds(10);
		HttpServletRequest request = new MockHttpServletRequest(null, "GET", "/ok.html");
		MockHttpServletResponse response = new MockHttpServletResponse();
		mc.handleRequest(request, response);
		assertTrue("Correct caching", response.getHeader("Cache-Control").equals("max-age=10, must-revalidate"));
		assertTrue("Correct expires header", response.getHeader("Expires") != null);
	}


	public static class TestController extends AbstractCommandController {
		
		public TestController() {
			super(TestBean.class, "person");
		}
		
		protected ModelAndView handle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object command,
			Errors errors) {
				Map m = new HashMap();
				assertTrue("Command not null", command != null);
				assertTrue("errors not null", errors != null);
				m.put("errors", errors);
				m.put("command", command);
			return new ModelAndView(request.getServletPath(), m);
		}
	}

	public static class LastModifiedTestController extends TestController implements LastModified {

		public long getLastModified(HttpServletRequest request) {
			return 0;
		}
	}

}
