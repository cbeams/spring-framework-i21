package com.interface21.web.servlet.mvc;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;

import com.interface21.beans.TestBean;
import com.interface21.validation.BindException;
import com.interface21.validation.Errors;
import com.interface21.validation.FieldError;
import com.interface21.validation.Validator;
import com.interface21.web.mock.MockHttpRequest;
import com.interface21.web.mock.MockHttpResponse;
import com.interface21.web.servlet.ModelAndView;

/**
 *
 * @author Rod Johnson
 * @version $RevisionId$
 */
public class FormControllerTestSuite extends TestCase {
	
	public FormControllerTestSuite(String name) {
		super(name);
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
		
		HttpServletRequest request = new MockHttpRequest(null, "GET", "/welcome.html");
		HttpServletResponse response = new MockHttpResponse();
		ModelAndView mv = mc.handleRequest(request, response);
		assertTrue("returned correct view name", mv.getViewName().equals(formView));
		
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
		
		MockHttpRequest request = new MockHttpRequest(null, "POST", "/welcome.html");
		request.addParameter("age", "23x");
		HttpServletResponse response = new MockHttpResponse();
		ModelAndView mv = mc.handleRequest(request, response);
		assertTrue("returned correct view name", mv.getViewName().equals(formView));
		assertTrue("has errors", mv.getModel().get(BindException.ERROR_KEY_PREFIX + mc.getBeanName()) != null);
		
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
		
		HttpServletRequest request = new MockHttpRequest(null, "GET", "/welcome.html");
		HttpServletResponse response = new MockHttpResponse();
		ModelAndView mv = mc.handleRequest(request, response);
		assertTrue("returned correct view name", mv.getViewName().equals(formView));
		
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

		MockHttpRequest request = new MockHttpRequest(null, "POST", "/welcome.html");
		request.addParameter("name", name);
		request.addParameter("age", "" + age);
		HttpServletResponse response = new MockHttpResponse();
		ModelAndView mv = mc.handleRequest(request, response);
		assertTrue("returned correct view name: expected '" + successView + "', not '" + mv.getViewName() + "'",
			mv.getViewName().equals(successView));
		
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

		MockHttpRequest request = new MockHttpRequest(null, "POST", "/welcome.html");
		request.addParameter("name", name);
		request.addParameter("age", "" + age);
		HttpServletResponse response = new MockHttpResponse();
		ModelAndView mv = mc.handleRequest(request, response);
		assertTrue("returned correct view name: expected '" + successView + "', not '" + mv.getViewName() + "'",
			mv.getViewName().equals(successView));
		
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

		MockHttpRequest request = new MockHttpRequest(null, "POST", "/foo.html");
		request.addParameter("name", name);
		request.addParameter("age", "" + age);
		HttpServletResponse response = new MockHttpResponse();
		ModelAndView mv = mc.handleRequest(request, response);
		assertTrue("returned correct view name: expected '" + formView + "', not '" + mv.getViewName() + "'",
		mv.getViewName().equals(formView));
		
		// Has bean
		TestBean person = (TestBean) mv.getModel().get(mc.getBeanName());
		assertTrue("model is non null", person != null);
		assertTrue("bean name bound ok", person.getName().equals(name));
		assertTrue("bean age is default", person.getAge() == new TestBean().getAge());
		Errors errors = (Errors) mv.getModel().get(BindException.ERROR_KEY_PREFIX + mc.getBeanName());
		assertTrue("errors returned in model", errors != null);
		assertTrue("One error", errors.getErrorCount() == 1);
		FieldError fe = errors.getFieldError("age");
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
		
		MockHttpRequest request = new MockHttpRequest(null, "POST", "/foo.html");
		request.addParameter("name", name);
		request.addParameter("age", "" + age);
		HttpServletResponse response = new MockHttpResponse();
		ModelAndView mv = mc.handleRequest(request, response);
		assertTrue("returned correct view name: expected '" + formView + "', not '" + mv.getViewName() + "'",
		mv.getViewName().equals(formView));
		
		// Has bean
		TestBean person = (TestBean) mv.getModel().get(mc.BEAN_NAME);
		assertTrue("model is non null", person != null);
		
		// yes, but it was rejected after binding by the validator
		assertTrue("bean name bound ok", person.getName().equals(name));
		assertTrue("bean age is default", person.getAge() == new TestBean().getAge());
		Errors errors = (Errors) mv.getModel().get(BindException.ERROR_KEY_PREFIX + mc.getBeanName());
		assertTrue("errors returned in model", errors != null);
		assertTrue("One error", errors.getErrorCount() == 2);
		FieldError fe = errors.getFieldError("age");
		assertTrue("Saved invalid value", fe.getRejectedValue().equals(age));
		assertTrue("Correct field", fe.getField().equals("age"));
		
		// Raised by validator
		fe = errors.getFieldError("name");
		assertTrue("Saved invalid value", fe.getRejectedValue().equals(name));
		assertTrue("Correct field", fe.getField().equals("name"));
		assertTrue("Correct validation code: expected '" +TestValidator.TOOSHORT + "', not '" 
		+ fe.getCode() + "'", fe.getCode().equals(TestValidator.TOOSHORT));
	}


	public void testSessionController() throws Exception {
		String formView = "f";
		String successView = "s";

		TestSessionController mc = new TestSessionController();
		mc.setFormView(formView);
		mc.setSuccessView(successView);

		// first request: GET form
		HttpServletRequest request1 = new MockHttpRequest(null, "GET", "/welcome.html");
		HttpServletResponse response1 = new MockHttpResponse();
		ModelAndView mv1 = mc.handleRequest(request1, response1);
		assertTrue("returned correct view name", mv1.getViewName().equals(formView));
		TestBean person = (TestBean) mv1.getModel().get(mc.BEAN_NAME);
		assertTrue("model is non null", person != null);
		assertTrue("Bean age default ok", person.getAge() == mc.DEFAULT_AGE);

		// second request, using same session: POST submit
		MockHttpRequest request2 = new MockHttpRequest(null, "POST", "/welcome.html");
		request2.setSession(request1.getSession(false));
		HttpServletResponse response2 = new MockHttpResponse();
		ModelAndView mv2 = mc.handleRequest(request2, response2);
		assertTrue("returned correct view name", mv2.getViewName().equals(successView));
		TestBean person2 = (TestBean) mv2.getModel().get(mc.BEAN_NAME);
		assertTrue("model is same object", person == person2);
	}


	private static class TestValidator implements Validator {

		public static String TOOSHORT = "tooshort";

		public boolean supports(Class clazz) { return true; }

		public void validate(Object o, Errors errors) {
			// CHECK THERE ISN'T ALREADY AN ERROR!?
			TestBean tb = (TestBean) o;
			if (tb.getName() == null || "".equals(tb.getName()))
				errors.rejectValue("name", "needname", null, "need name");
			else if (tb.getName().length() < 5)
				errors.rejectValue("name", TOOSHORT, null, "need full name");
		}
	};

	
	private static class TestController extends FormController {
		
		public static String BEAN_NAME = "person";
		
		public static int DEFAULT_AGE = 52;
		
		public TestController() {
			super(TestBean.class, BEAN_NAME);
		}
		
		protected Object formBackingObject(HttpServletRequest request) throws ServletException {
			TestBean person = new TestBean();
			person.setAge(DEFAULT_AGE);
			return person;
		}
	}
	
	
	private static class RefController extends FormController {
		
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
	}


  private static class TestSessionController extends SessionFormController {

	  public static String BEAN_NAME = "person";

	  public static int DEFAULT_AGE = 52;

	  public TestSessionController() {
		  super(TestBean.class, BEAN_NAME);
	  }

	  protected Object formBackingObject(HttpServletRequest request) throws ServletException {
		  TestBean person = new TestBean();
		  person.setAge(DEFAULT_AGE);
		  return person;
	  }
  }

}

