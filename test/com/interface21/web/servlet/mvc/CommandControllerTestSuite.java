package com.interface21.web.servlet.mvc;

import servletapi.TestHttpRequest;
import servletapi.TestHttpResponse;
import servletapi.TestHttpSession;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import com.interface21.beans.FatalBeanException;
import com.interface21.beans.TestBean;
import com.interface21.validation.Errors;
import com.interface21.validation.FieldError;
import com.interface21.web.bind.WebRequestBindingException;
import com.interface21.web.context.WebApplicationContext;
import com.interface21.web.context.support.StaticWebApplicationContext;
import com.interface21.web.servlet.ModelAndView;
import com.interface21.web.servlet.View;
import com.interface21.web.servlet.mvc.multiaction.*;

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
		HttpServletRequest request = new TestHttpRequest(null, "GET", "/welcome.html");
		HttpServletResponse response = new TestHttpResponse();
		ModelAndView mv = mc.handleRequest(request, response);
		assertTrue("returned correct view name", mv.getViewname().equals(request.getServletPath()));
		TestBean person = (TestBean) mv.getModel().get("command");
		Errors errors = (Errors) mv.getModel().get("errors");
		assertTrue("command and errors non null", person != null && errors != null);
		assertTrue("no errors", !errors.hasErrors());
		
		
	}
	
	
	public void test2ArgsNoErrors() throws Exception {
		TestController mc = new TestController();
		TestHttpRequest request = new TestHttpRequest(null, "GET", "/ok.html");
		String name = "Rod";
		int age = 32;
		request.addParameter("name", name);
		request.addParameter("age", "" + age);
		HttpServletResponse response = new TestHttpResponse();
		ModelAndView mv = mc.handleRequest(request, response);
		assertTrue("returned correct view name", mv.getViewname().equals(request.getServletPath()));
		TestBean person = (TestBean) mv.getModel().get("command");
		Errors errors = (Errors) mv.getModel().get("errors");
		assertTrue("command and errors non null", person != null && errors != null);
		assertTrue("no errors", !errors.hasErrors());
		assertTrue("command name bound ok", person.getName().equals(name));
		assertTrue("command age bound ok", person.getAge() == age);
	}
	
	public void test2Args1Mismatch() throws Exception {
		TestController mc = new TestController();
		TestHttpRequest request = new TestHttpRequest(null, "GET", "/ok.html");
		String name = "Rod";
		String age = "32x";
		request.addParameter("name", name);
		request.addParameter("age", age);
		HttpServletResponse response = new TestHttpResponse();
		ModelAndView mv = mc.handleRequest(request, response);
		assertTrue("returned correct view name", mv.getViewname().equals(request.getServletPath()));
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
	
	
	public static class TestController extends AbstractCommandController {
		
		public TestController() {
			super(TestBean.class, "person");
		}
		
		/**
		 * @see AbstractCommandController#handle(HttpServletRequest, HttpServletResponse, Object, Errors)
		 */
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
 
}

