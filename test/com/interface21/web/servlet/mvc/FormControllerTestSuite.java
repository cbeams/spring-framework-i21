package com.interface21.web.servlet.mvc;

import servletapi.TestHttpRequest;
import servletapi.TestHttpResponse;
import servletapi.TestHttpSession;

import java.io.IOException;
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
import com.interface21.validation.*;
import com.interface21.validation.BindException;
import com.interface21.validation.DataBinder;
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
public class FormControllerTestSuite extends TestCase {

	
	
	/**
	 * Constructor for AbstractMultiRequestHandlerTestSuite.
	 * @param arg0
	 */
	public FormControllerTestSuite(String arg0) {
		super(arg0);
	}

	public void setUp() {
	}
	
	
	public void testReferenceDataOnForm() throws Exception {
		String formView = "f";
		String successView = "s";
		
		RefController mc = new RefController();
		mc.setFormView(formView);
		mc.setBeanName("tb");
		mc.setSuccessView(successView);
		mc.refDataCount = 0;
		
		HttpServletRequest request = new TestHttpRequest(null, "GET", "/welcome.html");
		HttpServletResponse response = new TestHttpResponse();
		ModelAndView mv = mc.handleRequest(request, response);
		assertTrue("returned correct view name", mv.getViewname().equals(formView));
		
		assertTrue("refDataCount == 1", mc.refDataCount == 1);
		
		// Has bean
		TestBean person = (TestBean) mv.getModel().get(mc.getBeanName());
		int[] numbers = (int[]) mv.getModel().get(mc.NUMBERS_ATT);
		assertTrue("model is non null", person != null);
		assertTrue("numbers is non null", numbers != null);
	}
	
	public void testReferenceDataOnResubmit() throws Exception {
		String formView = "f";
		String successView = "s";
		
		RefController mc = new RefController();
		mc.setFormView(formView);
		mc.setBeanName("tb");
		mc.setSuccessView(successView);
		mc.refDataCount = 0;
		
		TestHttpRequest request = new TestHttpRequest(null, "POST", "/welcome.html");
		request.addParameter("age", "23x");
		HttpServletResponse response = new TestHttpResponse();
		ModelAndView mv = mc.handleRequest(request, response);
		assertTrue("returned correct view name", mv.getViewname().equals(formView));
		assertTrue("has errors", mv.getModel().get(BindException.EXCEPTION_KEY) != null);
		
		assertTrue("refDataCount == 1", mc.refDataCount == 1);
		
		// Has bean
		TestBean person = (TestBean) mv.getModel().get(mc.getBeanName());
		int[] numbers = (int[]) mv.getModel().get(mc.NUMBERS_ATT);
		assertTrue("model is non null", person != null);
		assertTrue("numbers is non null", numbers != null);
	}
	
	
	
	public void testForm() throws Exception {
		String formView = "f";
		String successView = "s";
		
		TestController mc = new TestController();
		mc.setFormView(formView);
		mc.setSuccessView(successView);
		
		HttpServletRequest request = new TestHttpRequest(null, "GET", "/welcome.html");
		HttpServletResponse response = new TestHttpResponse();
		ModelAndView mv = mc.handleRequest(request, response);
		assertTrue("returned correct view name", mv.getViewname().equals(formView));
		
		// Has bean
		TestBean person = (TestBean) mv.getModel().get(mc.BEAN_NAME);
		assertTrue("model is non null", person != null);
		assertTrue("Bean age default ok", person.getAge() == mc.DEFAULT_AGE);
	}
	
	
	public void testSubmitNoErrors() throws Exception {
		String formView = "f";
		String successView = "s";
		
		TestController mc = new TestController();
		mc.setFormView(formView);
		mc.setSuccessView(successView);
		
		String name = "Rod";
		int age = 32;
		
		
		TestHttpRequest request = new TestHttpRequest(null, "POST", "/welcome.html");
		request.addParameter("name", name);
		request.addParameter("age", "" + age);
		HttpServletResponse response = new TestHttpResponse();
		ModelAndView mv = mc.handleRequest(request, response);
		assertTrue("returned correct view name: expected '" + successView + "', not '" + mv.getViewname() + "'", 
			mv.getViewname().equals(successView));
		
		// Has bean
		TestBean person = (TestBean) mv.getModel().get(mc.BEAN_NAME);
		assertTrue("model is non null", person != null);
		assertTrue("bean name bound ok", person.getName().equals(name));
		assertTrue("bean age bound ok", person.getAge() == age);
	}
	
	
	/**
	 * REFACTOR TO AVOID DUP
	 */
	public void testSubmitPassedByValidator() throws Exception {
		String formView = "f";
		String successView = "s";
		
		TestController mc = new TestController();
		mc.setFormView(formView);
		mc.setSuccessView(successView);
		mc.setValidator(new TestValidator());
		
		String name = "Roderick Johnson";
		int age = 32;
		
		
		TestHttpRequest request = new TestHttpRequest(null, "POST", "/welcome.html");
		request.addParameter("name", name);
		request.addParameter("age", "" + age);
		HttpServletResponse response = new TestHttpResponse();
		ModelAndView mv = mc.handleRequest(request, response);
		assertTrue("returned correct view name: expected '" + successView + "', not '" + mv.getViewname() + "'", 
			mv.getViewname().equals(successView));
		
		// Has bean
		TestBean person = (TestBean) mv.getModel().get(mc.BEAN_NAME);
		assertTrue("model is non null", person != null);
		assertTrue("bean name bound ok", person.getName().equals(name));
		assertTrue("bean age bound ok", person.getAge() == age);
	}
	
	public void testSubmit1Mismatch() throws Exception {
		String formView = "fred";
		String successView = "tony";
		
		TestController mc = new TestController();
		mc.setFormView(formView);
		mc.setSuccessView(successView);
		
		String name = "Rod";
		String age = "xxx";
		
		
		TestHttpRequest request = new TestHttpRequest(null, "POST", "/foo.html");
		request.addParameter("name", name);
		request.addParameter("age", "" + age);
		HttpServletResponse response = new TestHttpResponse();
		ModelAndView mv = mc.handleRequest(request, response);
		assertTrue("returned correct view name: expected '" + formView + "', not '" + mv.getViewname() + "'", 
		mv.getViewname().equals(formView));
		
		// Has bean
		TestBean person = (TestBean) mv.getModel().get(mc.BEAN_NAME);
		assertTrue("model is non null", person != null);
		assertTrue("bean name bound ok", person.getName().equals(name));
		assertTrue("bean age is default", person.getAge() == new TestBean().getAge());
		Errors errors = (Errors) mv.getModel().get(BindException.EXCEPTION_KEY);
		assertTrue("errors returned in model", errors != null);
		assertTrue("One error", errors.getErrorCount() == 1);
		FieldError fe = errors.getError(mc.BEAN_NAME, "age");
		assertTrue("Saved invalid value", fe.getRejectedValue().equals(age));
		assertTrue("Correct field", fe.getField().equals("age"));
	}
	
	
	public void testSubmit1Mismatch1Invalidated() throws Exception {
		String formView = "fred";
		String successView = "tony";
		
		TestController mc = new TestController();
		mc.setFormView(formView);
		mc.setSuccessView(successView);
		mc.setValidator(new TestValidator());
		
		String name = "Rod";
		// will be rejected
		String age = "xxx";
		
		
		TestHttpRequest request = new TestHttpRequest(null, "POST", "/foo.html");
		request.addParameter("name", name);
		request.addParameter("age", "" + age);
		HttpServletResponse response = new TestHttpResponse();
		ModelAndView mv = mc.handleRequest(request, response);
		assertTrue("returned correct view name: expected '" + formView + "', not '" + mv.getViewname() + "'", 
		mv.getViewname().equals(formView));
		
		// Has bean
		TestBean person = (TestBean) mv.getModel().get(mc.BEAN_NAME);
		assertTrue("model is non null", person != null);
		
		// yes, but it was rejected after binding by the validator
		assertTrue("bean name bound ok", person.getName().equals(name));
		assertTrue("bean age is default", person.getAge() == new TestBean().getAge());
		Errors errors = (Errors) mv.getModel().get(BindException.EXCEPTION_KEY);
		assertTrue("errors returned in model", errors != null);
		assertTrue("One error", errors.getErrorCount() == 2);
		FieldError fe = errors.getError(mc.BEAN_NAME, "age");
		assertTrue("Saved invalid value", fe.getRejectedValue().equals(age));
		assertTrue("Correct field", fe.getField().equals("age"));
		
		// Raised by validator
		fe = errors.getError(mc.BEAN_NAME, "name");
		assertTrue("Saved invalid value", fe.getRejectedValue().equals(name));
		assertTrue("Correct field", fe.getField().equals("name"));
		assertTrue("Correct validation code: expected '" +TestValidator.TOOSHORT + "', not '" 
		+ fe.getErrorCode() + "'", fe.getErrorCode().equals(TestValidator.TOOSHORT));
	}
	
	
	public static class TestValidator implements Validator {
		public static String TOOSHORT = "tooshort";
			public boolean supports(Class clazz) { return true; }
			public void validate(Object o, Errors errors) {
				// CHECK THERE ISN'T ALREADY AN ERROR!?
				TestBean tb = (TestBean) o;
				if (tb.getName() == null || "".equals(tb.getName()))
					errors.rejectValue("name", "needname", "need name");
				else if (tb.getName().length() < 5)
					errors.rejectValue("name", TOOSHORT, "need full name");
			}
		};


	// TEST VALIDATOR ALSO
	
	
	public static class TestController extends FormController {
		
		public static String BEAN_NAME = "person";
		
		public static int DEFAULT_AGE = 52;
		
		public TestController() {
			super(TestBean.class, BEAN_NAME);
		}
		

		/**
		 * @see ValidatingFormController#formBackingObject(HttpServletRequest)
		 */
		protected Object formBackingObject(HttpServletRequest request) throws ServletException {
			TestBean person = new TestBean();
			person.setAge(DEFAULT_AGE);
			return person;
		}

		/**
		 * @see ValidatingFormController#onSubmit(HttpServletRequest, HttpServletResponse, Object, DataBinder)
		 */
		protected ModelAndView onSubmit(
			HttpServletRequest request,
			HttpServletResponse response,
			Object command,
			DataBinder binder)
			throws ServletException, IOException {
			return super.onSubmit(request, response, command, binder);
		}
	}
	
	
	public static class RefController extends FormController {
		
		final String NUMBERS_ATT = "NUMBERS";
		
		static final int[] NUMBERS = { 1, 2, 3, 4 };
		
		int refDataCount;
		
		public RefController() {
			super(TestBean.class);
		}
		
		protected Map referenceData(HttpServletRequest request) {
			++refDataCount;
			Map m = new HashMap();
			m.put(NUMBERS_ATT, NUMBERS);
			return m;
		}

		/**
		 * @see FormController#onSubmit(Object)
		 */
		protected ModelAndView onSubmit(Object command) throws ServletException {
			return super.onSubmit(command);
		}
	}
 
}

