package com.interface21.web.servlet.mvc;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import junit.framework.TestCase;

import com.interface21.beans.FatalBeanException;
import com.interface21.beans.TestBean;
import com.interface21.web.bind.ServletRequestBindingException;
import com.interface21.web.mock.MockHttpServletRequest;
import com.interface21.web.mock.MockHttpServletResponse;
import com.interface21.web.mock.MockHttpSession;
import com.interface21.web.servlet.ModelAndView;
import com.interface21.web.servlet.mvc.multiaction.InternalPathMethodNameResolver;
import com.interface21.web.servlet.mvc.multiaction.MultiActionController;
import com.interface21.web.servlet.mvc.multiaction.PropertiesMethodNameResolver;

/**
 *
 * @author Rod Johnson
 * @version $RevisionId$
 */
public class MultiActionControllerTestSuite extends TestCase {

	public MultiActionControllerTestSuite(String arg0) {
		super(arg0);
	}

	public void testDefaultNameExtraction() throws Exception {
		testDefaultNameExtraction("/foo.html", "foo");
		testDefaultNameExtraction("/foo/bar.html", "bar");
		testDefaultNameExtraction("/bugal.xyz", "bugal");
		testDefaultNameExtraction("/x/y/z/q/foo.html", "foo");
		testDefaultNameExtraction("qqq.q", "qqq");
	}

	public void testDefaultNameExtraction(String in, String expected) throws Exception {
		MultiActionController rc = new MultiActionController();
		rc.setMethodNameResolver(new InternalPathMethodNameResolver());
		HttpServletRequest request = new MockHttpServletRequest(null, "GET", in);
		String actual = rc.getMethodNameResolver().getHandlerMethodName(request);
		assertTrue("input [" + in + "] should have produced [" + expected + "], not [" + actual + "]",
		           actual.equals(expected));
	}

	public void testInvokesCorrectMethod() throws Exception {
		TestMaController mc = new TestMaController();
		HttpServletRequest request = new MockHttpServletRequest(null, "GET", "/welcome.html");
		HttpServletResponse response = new MockHttpServletResponse();
		Properties p = new Properties();
		p.put("/welcome.html", "welcome");
		PropertiesMethodNameResolver mn = new PropertiesMethodNameResolver();
		mn.setMappings(p);
		mc.setMethodNameResolver(mn);

		ModelAndView mv = mc.handleRequest(request, response);
		assertTrue("Invoked welcome method", mc.wasInvoked("welcome"));
		assertTrue("view name is welcome", mv.getViewName().equals("welcome"));
		assertTrue("Only one method invoked", mc.getInvokedMethods() == 1);

		mc = new TestMaController();
		request = new MockHttpServletRequest(null, "GET", "/subdir/test.html");
		response = new MockHttpServletResponse();
		mv = mc.handleRequest(request, response);
		assertTrue("Invoked test method", mc.wasInvoked("test"));
		assertTrue("view name is subdir_test", mv.getViewName().equals("test"));
		assertTrue("Only one method invoked", mc.getInvokedMethods() == 1);
	}

	public void testPathMatching() throws Exception {
		TestMaController mc = new TestMaController();
		HttpServletRequest request = new MockHttpServletRequest(null, "GET", "/welcome.html");
		HttpServletResponse response = new MockHttpServletResponse();
		Properties p = new Properties();
		p.put("/welc*.html", "welcome");
		PropertiesMethodNameResolver mn = new PropertiesMethodNameResolver();
		mn.setMappings(p);
		mc.setMethodNameResolver(mn);

		ModelAndView mv = mc.handleRequest(request, response);
		assertTrue("Invoked welcome method", mc.wasInvoked("welcome"));
		assertTrue("view name is welcome", mv.getViewName().equals("welcome"));
		assertTrue("Only one method invoked", mc.getInvokedMethods() == 1);

		mc = new TestMaController();
		mc.setMethodNameResolver(mn);
		request = new MockHttpServletRequest(null, "GET", "/nomatch");
		response = new MockHttpServletResponse();
		try {
			mv = mc.handleRequest(request, response);
		}
		catch (Exception e) {
			// expected
		}
		assertFalse("Not invoking welcome method", mc.wasInvoked("welcome"));
		assertTrue("No method invoked", mc.getInvokedMethods() == 0);
	}

	public static class TestDelegate {

		boolean invoked;

		public ModelAndView test(HttpServletRequest request, HttpServletResponse response) {
			invoked = true;
			return new ModelAndView("test");
		}
	}

	public void testInvokesCorrectMethodOnDelegate() throws Exception {
		MultiActionController mac = new MultiActionController();
		TestDelegate d = new TestDelegate();
		mac.setDelegate(d);
		HttpServletRequest request = new MockHttpServletRequest(null, "GET", "/test.html");
		HttpServletResponse response = new MockHttpServletResponse();
		ModelAndView mv = mac.handleRequest(request, response);
		assertTrue("view name is test", mv.getViewName().equals("test"));
		assertTrue("Delegate was invoked", d.invoked);
	}

	public void testInvokesCorrectMethodWithSession() throws Exception {
		TestMaController mc = new TestMaController();
		MockHttpServletRequest request = new MockHttpServletRequest(null, "GET", "/inSession.html");
		request.setSession(new MockHttpSession());
		HttpServletResponse response = new MockHttpServletResponse();
		ModelAndView mv = mc.handleRequest(request, response);
		assertTrue("Invoked inSession method", mc.wasInvoked("inSession"));
		assertTrue("view name is welcome", mv.getViewName().equals("inSession"));
		assertTrue("Only one method invoked", mc.getInvokedMethods() == 1);

		request = new MockHttpServletRequest(null, "GET", "/inSession.html");
		response = new MockHttpServletResponse();
		try {

			mc.handleRequest(request, response);
			fail("Should have rejected request without session");
		}
		catch (ServletException ex) {
			//OK
		}
	}

	public void testInvokesCommandMethodNoSession() throws Exception {
		TestMaController mc = new TestMaController();
		MockHttpServletRequest request = new MockHttpServletRequest(null, "GET", "/commandNoSession.html");
		request.addParameter("name", "rod");
		request.addParameter("age", "32");
		HttpServletResponse response = new MockHttpServletResponse();
		ModelAndView mv = mc.handleRequest(request, response);
		assertTrue("Invoked commandNoSession method", mc.wasInvoked("commandNoSession"));
		assertTrue("view name is commandNoSession", mv.getViewName().equals("commandNoSession"));
		assertTrue("Only one method invoked", mc.getInvokedMethods() == 1);

		//		mc = new TestMaController();
		//		request = new MockHttpServletRequest(null, "GET", "/subdir/test.html");
		//		response = new MockHttpServletResponse();
		//		mv = mc.handleRequest(request, response);
		//		assertTrue("Invoked subdir_test method", mc.wasInvoked("subdir_test"));
		//		assertTrue("view name is subdir_test", mv.getViewName().equals("subdir_test"));
		//		assertTrue("Only one method invoked", mc.getInvokedMethods() == 1);
	}


	public void testInvokesCommandMethodWithSession() throws Exception {
		TestMaController mc = new TestMaController();
		MockHttpServletRequest request = new MockHttpServletRequest(null, "GET", "/commandInSession.html");
		request.addParameter("name", "rod");
		request.addParameter("age", "32");

		request.setSession(new MockHttpSession());
		HttpServletResponse response = new MockHttpServletResponse();
		ModelAndView mv = mc.handleRequest(request, response);
		assertTrue("Invoked commandInSession method", mc.wasInvoked("commandInSession"));
		assertTrue("view name is commandInSession", mv.getViewName().equals("commandInSession"));
		assertTrue("Only one method invoked", mc.getInvokedMethods() == 1);

		request = new MockHttpServletRequest(null, "GET", "/commandInSession.html");
		response = new MockHttpServletResponse();
		try {

			mc.handleRequest(request, response);
			fail("Should have rejected request without session");
		}
		catch (ServletException ex) {
			//OK
		}
	}


	public void testSessionRequiredCatchable() throws Exception {
		HttpServletRequest request = new MockHttpServletRequest(null, "GET", "/testSession.html");
		HttpServletResponse response = new MockHttpServletResponse();
		TestMaController contr = new TestSessionRequiredController();
		try {
			contr.handleRequest(request, response);
			fail("Should have thrown exception");
		}
		catch (SessionRequiredException ex) {
			//assertTrue("session required", ex.equals(t));
		}
		request = new MockHttpServletRequest(null, "GET", "/testSession.html");
		response = new MockHttpServletResponse();
		contr = new TestSessionRequiredExceptionHandler();
		ModelAndView mv = contr.handleRequest(request, response);
		assertTrue("Name is ok", mv.getViewName().equals("handle(SRE)"));
	}

	private void testExceptionNoHandler(TestMaController mc, Throwable t) throws Exception {
		HttpServletRequest request = new MockHttpServletRequest(null, "GET", "/testException.html");
		request.setAttribute(mc.THROWABLE_ATT, t);
		HttpServletResponse response = new MockHttpServletResponse();
		try {
			mc.handleRequest(request, response);
			fail("Should have thrown exception");
		}
		catch (RuntimeException ex) {
			assertTrue("RTE only thrown if it went in", ex.equals(t));
		}
		catch (Error ex) {
			assertTrue("Error only thrown if it went in: in was [" + t + "] out was [" + ex + "]", ex.equals(t));
		}
		catch (ServletException ex) {
			if (t instanceof ServletException) {
				assertTrue(ex.equals(t));
			}
			else {
				// Should have been wrapped
				// Correct, unhandled
				assertTrue("Nested exception is correct: throwable=[" + t + "], got [" + ex + "]", ex.getRootCause().equals(t));
			}
		}
	}

	private void testExceptionNoHandler(Throwable t) throws Exception {
		testExceptionNoHandler(new TestMaController(), t);
	}

	public void testExceptionNoHandler() throws Exception {
		testExceptionNoHandler(new Exception());
		testExceptionNoHandler(new Throwable());

		// Should go straight through
		testExceptionNoHandler(new ServletException());

		// subclass of servlet exception
		testExceptionNoHandler(new ServletRequestBindingException("foo"));
		testExceptionNoHandler(new RuntimeException());
		testExceptionNoHandler(new Error());
	}


	public void testLastModifiedDefault() throws Exception {
		TestMaController mc = new TestMaController();
		MockHttpServletRequest request = new MockHttpServletRequest(null, "GET", "/welcome.html");
		long lastMod = mc.getLastModified(request);
		assertTrue("default last modified is -1", lastMod == -1L);
	}

	public void testLastModifiedWithMethod() throws Exception {
		LastModController mc = new LastModController();
		MockHttpServletRequest request = new MockHttpServletRequest(null, "GET", "/welcome.html");
		long lastMod = mc.getLastModified(request);
		assertTrue("last modified with method is > -1", lastMod == mc.getLastModified(request));
	}

	private ModelAndView testHandlerCaughtException(TestMaController mc, Throwable t) throws Exception {
		HttpServletRequest request = new MockHttpServletRequest(null, "GET", "/testException.html");
		request.setAttribute(mc.THROWABLE_ATT, t);
		HttpServletResponse response = new MockHttpServletResponse();
		ModelAndView mv = mc.handleRequest(request, response);
		return mv;
	}

	public void testHandlerCaughtException() throws Exception {
		TestMaController mc = new TestExceptionHandler();
		ModelAndView mv = testHandlerCaughtException(mc, new Exception());
		assertTrue("mv name is handle(Exception)", mv.getViewName().equals("handle(Exception)"));
		assertTrue("Invoked correct method", mc.wasInvoked("handle(Exception)"));

		// Check it doesn't affect unknown exceptions
		testExceptionNoHandler(mc, new Throwable());
		// WILL GET RUNTIM EEXCEPTIONS TOO
		testExceptionNoHandler(mc, new Error());

		mc = new TestServletExceptionHandler();
		mv = testHandlerCaughtException(mc, new ServletException());
		assertTrue(mv.getViewName().equals("handle(ServletException)"));
		assertTrue("Invoke correct method", mc.wasInvoked("handle(ServletException)"));

		mv = testHandlerCaughtException(mc, new ServletRequestBindingException("foo"));
		assertTrue(mv.getViewName().equals("handle(ServletException)"));
		assertTrue("Invoke correct method", mc.wasInvoked("handle(ServletException)"));

		// Check it doesn't affect unknown exceptions
		testExceptionNoHandler(mc, new Throwable());
		testExceptionNoHandler(mc, new RuntimeException());
		testExceptionNoHandler(mc, new Error());
		testExceptionNoHandler(mc, new SQLException());
		testExceptionNoHandler(mc, new Exception());


		mc = new TestRTEHandler();
		mv = testHandlerCaughtException(mc, new RuntimeException());
		assertTrue(mv.getViewName().equals("handle(RTE)"));
		assertTrue("Invoke correct method", mc.wasInvoked("handle(RTE)"));
		mv = testHandlerCaughtException(mc, new FatalBeanException(null, null));
		assertTrue(mv.getViewName().equals("handle(RTE)"));
		assertTrue("Invoke correct method", mc.wasInvoked("handle(RTE)"));

		testExceptionNoHandler(mc, new SQLException());
		testExceptionNoHandler(mc, new Exception());
	}

	/** No error handlers */
	public static class TestMaController extends MultiActionController {

		public static final String THROWABLE_ATT = "throwable";

		/** Method name -> object */
		protected Map invoked = new HashMap();

		public void clear() {
			invoked.clear();
		}

		public ModelAndView welcome(HttpServletRequest request, HttpServletResponse response) {
			invoked.put("welcome", Boolean.TRUE);
			return new ModelAndView("welcome");
		}

		public ModelAndView commandNoSession(HttpServletRequest request, HttpServletResponse response, TestBean command) {
			invoked.put("commandNoSession", Boolean.TRUE);

			String pname = request.getParameter("name");
			String page = request.getParameter("age");
			// ALLOW FOR NULL
			if (pname == null)
				assertTrue("name null", command.getName() == null);
			else
				assertTrue("name param set", pname.equals(command.getName()));
			//			if (page == null)
			//				assertTrue("age default", command.getAge() == 0);
			//			else
			//				assertTrue("age set", command.getName().equals(pname));
			//assertTrue("a", command.getAge().equals(request.getParameter("name")));
			return new ModelAndView("commandNoSession");
		}

		public ModelAndView inSession(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
			invoked.put("inSession", Boolean.TRUE);
			assertTrue("session non null", session != null);
			return new ModelAndView("inSession");
		}

		public ModelAndView commandInSession(HttpServletRequest request, HttpServletResponse response, HttpSession session, TestBean command) {
			invoked.put("commandInSession", Boolean.TRUE);
			assertTrue("session non null", session != null);
			return new ModelAndView("commandInSession");
		}

		public ModelAndView test(HttpServletRequest request, HttpServletResponse response) {
			invoked.put("test", Boolean.TRUE);
			return new ModelAndView("test");
		}

		public ModelAndView testException(HttpServletRequest request, HttpServletResponse response) throws Throwable {
			invoked.put("testException", Boolean.TRUE);
			Throwable t = (Throwable) request.getAttribute(THROWABLE_ATT);
			if (t != null)
				throw t;
			else
				return new ModelAndView("no throwable");
		}

		public boolean wasInvoked(String method) {
			return invoked.get(method) != null;
		}

		public int getInvokedMethods() {
			return invoked.size();
		}

	}

	public static class TestExceptionHandler extends TestMaController {

		public ModelAndView handleAnyException(HttpServletRequest request, HttpServletResponse response, Exception ex) {
			invoked.put("handle(Exception)", Boolean.TRUE);
			return new ModelAndView("handle(Exception)");
		}
	}

	public static class TestRTEHandler extends TestMaController {

		public ModelAndView handleRuntimeProblem(HttpServletRequest request, HttpServletResponse response, RuntimeException ex) {
			invoked.put("handle(RTE)", Boolean.TRUE);
			return new ModelAndView("handle(RTE)");
		}
	}

	public static class TestSessionRequiredController extends TestMaController {

		public ModelAndView testSession(HttpServletRequest request, HttpServletResponse response, HttpSession sess) {
			return null;
		}
	}

	/** Extends previous to handle exception */
	public static class TestSessionRequiredExceptionHandler extends TestSessionRequiredController {

		public ModelAndView handleServletException(HttpServletRequest request, HttpServletResponse response, SessionRequiredException ex) {
			invoked.put("handle(SRE)", Boolean.TRUE);
			return new ModelAndView("handle(SRE)");
		}
	}

	public static class TestServletExceptionHandler extends TestMaController {

		public ModelAndView handleServletException(HttpServletRequest request, HttpServletResponse response, ServletException ex) {
			invoked.put("handle(ServletException)", Boolean.TRUE);
			return new ModelAndView("handle(ServletException)");
		}
	}

	public static class LastModController extends MultiActionController {

		public static final String THROWABLE_ATT = "throwable";

		/** Method name -> object */
		protected HashMap invoked = new HashMap();

		public void clear() {
			invoked.clear();
		}

		public ModelAndView welcome(HttpServletRequest request, HttpServletResponse response) {
			invoked.put("welcome", Boolean.TRUE);
			return new ModelAndView("welcome");
		}

		/** Always says content is up to date */
		public long welcomeLastModified(HttpServletRequest request) {
			return 1111L;
		}
	}

}

