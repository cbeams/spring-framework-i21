package com.interface21.web.bind;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletException;

import junit.framework.TestCase;

import com.interface21.beans.TestBean;
import com.interface21.beans.propertyeditors.StringTrimmerEditor;
import com.interface21.validation.Errors;
import com.interface21.validation.Validator;
import com.interface21.web.mock.MockHttpServletRequest;
import com.interface21.web.mock.MockServletContext;

/**
 * @author Juergen Hoeller
 * @since 23.05.2003
 */
public class BindUtilsTestSuite extends TestCase {

	public void testBind() {
		ServletContext sc = new MockServletContext();
		MockHttpServletRequest request = new MockHttpServletRequest(sc, "GET", "/test.do");
		request.addParameter("name", " myname ");
		request.addParameter("age", "myage");
		TestBean tb = new TestBean();
		assertTrue("Name not set", tb.getName() == null);
		assertTrue("Age not set", tb.getAge() == 0);
		Errors errors = BindUtils.bind(request, tb, "tb");
		assertTrue("Name set", " myname ".equals(tb.getName()));
		assertTrue("No name error", !errors.hasFieldErrors("name"));
		assertTrue("Age not set", tb.getAge() == 0);
		assertTrue("Has age error", errors.hasFieldErrors("age"));
		assertTrue("Correct age error", "typeMismatch".equals(errors.getFieldError("age").getCode()));
	}

	public void testBindAndValidate() {
		ServletContext sc = new MockServletContext();
		MockHttpServletRequest request = new MockHttpServletRequest(sc, "GET", "/test.do");
		request.addParameter("name", " myname ");
		request.addParameter("age", "myage");
		TestBean tb = new TestBean();
		assertTrue("Name not set", tb.getName() == null);
		Errors errors = BindUtils.bindAndValidate(request, tb, "tb", new Validator() {
			public boolean supports(Class clazz) {
				return TestBean.class.isAssignableFrom(clazz);
			}
			public void validate(Object obj, Errors errors) {
				TestBean vtb = (TestBean) obj;
				if (" myname ".equals(vtb.getName())) {
					errors.rejectValue("name", "notMyname", null, "Name may not be myname");
				}
			}
		});
		assertTrue("Name set", " myname ".equals(tb.getName()));
		assertTrue("Has name error", errors.hasFieldErrors("name"));
		assertTrue("Correct name error", "notMyname".equals(errors.getFieldError("name").getCode()));
		assertTrue("Age not set", tb.getAge() == 0);
		assertTrue("Has age error", errors.hasFieldErrors("age"));
		assertTrue("Correct age error", "typeMismatch".equals(errors.getFieldError("age").getCode()));
	}

	public void testBindWithInitializer() throws ServletException {
		ServletContext sc = new MockServletContext();
		MockHttpServletRequest request = new MockHttpServletRequest(sc, "GET", "/test.do");
		request.addParameter("name", " myname ");
		request.addParameter("age", "myage");
		TestBean tb = new TestBean();
		assertTrue("Name not set", tb.getName() == null);
		assertTrue("Age not set", tb.getAge() == 0);
		Errors errors = BindUtils.bind(request, tb, "tb", new BindInitializer() {
			public void initBinder(ServletRequest request, ServletRequestDataBinder binder) {
				binder.registerCustomEditor(String.class, "name", new StringTrimmerEditor(true));
			}
		});
		assertTrue("Name set", "myname".equals(tb.getName()));
		assertTrue("No name error", !errors.hasFieldErrors("name"));
		assertTrue("Age not set", tb.getAge() == 0);
		assertTrue("Has age error", errors.hasFieldErrors("age"));
		assertTrue("Correct age error", "typeMismatch".equals(errors.getFieldError("age").getCode()));
	}

	public void testBindAndValidateWithInitializer() throws ServletException {
		ServletContext sc = new MockServletContext();
		MockHttpServletRequest request = new MockHttpServletRequest(sc, "GET", "/test.do");
		request.addParameter("name", " myname ");
		request.addParameter("age", "myage");
		TestBean tb = new TestBean();
		assertTrue("Name not set", tb.getName() == null);
		Errors errors = BindUtils.bindAndValidate(request, tb, "tb",
			new Validator() {
				public boolean supports(Class clazz) {
					return TestBean.class.isAssignableFrom(clazz);
				}
				public void validate(Object obj, Errors errors) {
					TestBean vtb = (TestBean) obj;
					if ("myname".equals(vtb.getName())) {
						errors.rejectValue("name", "notMyname", null, "Name may not be myname");
					}
				}
			},
			new BindInitializer() {
				public void initBinder(ServletRequest request, ServletRequestDataBinder binder) {
					binder.registerCustomEditor(String.class, "name", new StringTrimmerEditor(true));
				}
			}
		);
		assertTrue("Name set", "myname".equals(tb.getName()));
		assertTrue("Has name error", errors.hasFieldErrors("name"));
		assertTrue("Correct name error", "notMyname".equals(errors.getFieldError("name").getCode()));
		assertTrue("Age not set", tb.getAge() == 0);
		assertTrue("Has age error", errors.hasFieldErrors("age"));
		assertTrue("Correct age error", "typeMismatch".equals(errors.getFieldError("age").getCode()));
	}

}
