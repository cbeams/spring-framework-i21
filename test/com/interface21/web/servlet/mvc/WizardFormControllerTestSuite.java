package com.interface21.web.servlet.mvc;

import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import junit.framework.TestCase;

import com.interface21.beans.TestBean;
import com.interface21.validation.BindException;
import com.interface21.validation.Errors;
import com.interface21.web.mock.MockHttpServletRequest;
import com.interface21.web.mock.MockHttpServletResponse;
import com.interface21.web.servlet.ModelAndView;
import com.interface21.web.util.WebUtils;

/**
 * @author Juergen Hoeller
 * @since 29.04.2003
 */
public class WizardFormControllerTestSuite extends TestCase {

	public WizardFormControllerTestSuite(String location) {
		super(location);
	}

	public void testNoDirtyPageChange() {
		AbstractWizardFormController wizard = createWizard();
		wizard.setAllowDirtyBack(false);
		wizard.setAllowDirtyForward(false);
		wizard.setPageAttribute("currentPage");
		HttpSession session = performRequest(wizard, null, null, 0, null, 0, "currentPage");

		Properties params = new Properties();
		params.setProperty(AbstractWizardFormController.PARAM_TARGET + "1", "value");
		performRequest(wizard, session, null, 0, null, 0, "currentPage");
		// not allowed to go to 1

		params.clear();
		params.setProperty("name", "myname");
		params.setProperty(AbstractWizardFormController.PARAM_TARGET + "1", "value");
		performRequest(wizard, session, params, 1, "myname", 0, "currentPage");
		// name set -> now allowed to go to 1

		params.clear();
		params.setProperty(AbstractWizardFormController.PARAM_TARGET + "0", "value");
		performRequest(wizard, session, params, 1, "myname", 0, "currentPage");
		// not allowed to go to 0

		params.clear();
		params.setProperty("age", "32");
		params.setProperty(AbstractWizardFormController.PARAM_TARGET + "0", "value");
		performRequest(wizard, session, params, 0, "myname", 32, "currentPage");
		// age set -> now allowed to go to 0

		params.clear();
		params.setProperty(AbstractWizardFormController.PARAM_FINISH, "value");
		performRequest(wizard, session, params, -1, "myname", 32, null);
	}

	public void testDirtyBack() {
		AbstractWizardFormController wizard = createWizard();
		wizard.setAllowDirtyBack(true);
		wizard.setAllowDirtyForward(false);
		HttpSession session = performRequest(wizard, null, null, 0, null, 0, null);

		Properties params = new Properties();
		params.setProperty(AbstractWizardFormController.PARAM_TARGET + "1", "value");
		performRequest(wizard, session, params, 0, null, 0, null);
		// not allowed to go to 1

		params.clear();
		params.setProperty("name", "myname");
		params.setProperty(AbstractWizardFormController.PARAM_TARGET + "1", "value");
		performRequest(wizard, session, params, 1, "myname", 0, null);
		// name set -> now allowed to go to 1

		params.clear();
		params.setProperty(AbstractWizardFormController.PARAM_TARGET + "0", "value");
		performRequest(wizard, session, params, 0, "myname", 0, null);
		// dirty back -> allowed to go to 0

		params.clear();
		params.setProperty(AbstractWizardFormController.PARAM_FINISH, "value");
		performRequest(wizard, session, params, 1, "myname", 0, null);
		// finish while dirty -> show dirty page (1)

		params.clear();
		params.setProperty("age", "32");
		params.setProperty(AbstractWizardFormController.PARAM_FINISH, "value");
		performRequest(wizard, session, params, -1, "myname", 32, null);
		// age set -> now allowed to finish
	}

	public void testDirtyForward() {
		AbstractWizardFormController wizard = createWizard();
		wizard.setAllowDirtyBack(false);
		wizard.setAllowDirtyForward(true);
		HttpSession session = performRequest(wizard, null, null, 0, null, 0, null);

		Properties params = new Properties();
		params.setProperty(AbstractWizardFormController.PARAM_TARGET + "1", "value");
		performRequest(wizard, session, params, 1, null, 0, null);
		// dirty forward -> allowed to go to 1

		params.clear();
		params.setProperty(AbstractWizardFormController.PARAM_TARGET + "0", "value");
		performRequest(wizard, session, params, 1, null, 0, null);
		// not allowed to go to 0

		params.clear();
		params.setProperty("age", "32");
		params.setProperty(AbstractWizardFormController.PARAM_TARGET + "0", "value");
		performRequest(wizard, session, params, 0, null, 32, null);
		// age set -> now allowed to go to 0

		params.clear();
		params.setProperty(AbstractWizardFormController.PARAM_FINISH, "value");
		performRequest(wizard, session, params, 0, null, 32, null);
		// finish while dirty -> show dirty page (0)

		params.clear();
		params.setProperty("name", "myname");
		params.setProperty(AbstractWizardFormController.PARAM_FINISH + WebUtils.SUBMIT_IMAGE_SUFFIX, "value");
		performRequest(wizard, session, params, -1, "myname", 32, null);
		// name set -> now allowed to finish
	}

	public void testAbort() {
		AbstractWizardFormController wizard = createWizard();
		HttpSession session = performRequest(wizard, null, null, 0, null, 0, null);
		Properties params = new Properties();
		params.setProperty(AbstractWizardFormController.PARAM_CANCEL, "value");
		performRequest(wizard, session, params, -2, null, 0, null);

		session = performRequest(wizard, null, null, 0, null, 0, null);
		params = new Properties();
		params.setProperty(AbstractWizardFormController.PARAM_CANCEL + WebUtils.SUBMIT_IMAGE_SUFFIX, "value");
		performRequest(wizard, session, params, -2, null, 0, null);
	}


	private AbstractWizardFormController createWizard() {
		AbstractWizardFormController wizard = new TestWizardController(TestBean.class, "tb");
		wizard.setPages(new String[] {"page0", "page1"});
		return wizard;
	}

	private HttpSession performRequest(Controller wizard, HttpSession session, Properties params,
	                                   int target, String name, int age, String pageAttr) {
		MockHttpServletRequest request = new MockHttpServletRequest(null, (params != null ? "POST" : "GET"), "/wizard");
		if (params != null) {
			for (Iterator it = params.keySet().iterator(); it.hasNext();) {
				String param = (String) it.next();
				request.addParameter(param, params.getProperty(param));
			}
		}
		request.setSession(session);
		MockHttpServletResponse response = new MockHttpServletResponse();
		try {
			ModelAndView mv = wizard.handleRequest(request, response);
			if (target >= 0) {
				assertTrue("Page " + target + " returned", ("page" + target).equals(mv.getViewName()));
				if (pageAttr != null) {
					assertTrue("Page attribute set", (new Integer(target)).equals(mv.getModel().get(pageAttr)));
					assertTrue("Correct model size", mv.getModel().size() == 3);
				} else {
					assertTrue("Correct model size", mv.getModel().size() == 2);
				}
			}
			else if (target == -1) {
				assertTrue("Success target returned", "success".equals(mv.getViewName()));
				assertTrue("Correct model size", mv.getModel().size() == 1);
			}
			else if (target == -2) {
				assertTrue("Abort view returned", "abort".equals(mv.getViewName()));
				assertTrue("Correct model size", mv.getModel().size() == 1);
			}
			TestBean tb = (TestBean) mv.getModel().get("tb");
			assertTrue("Has model", tb != null);
			assertTrue("Name is " + name, (tb.getName() == name || tb.getName().equals(name)));
			assertTrue("Age is " + age, tb.getAge() == age);
			System.out.println("Model:" + mv.getModel().size());
		}
		catch (ServletException ex) {
			fail("Should not throw ServletException: " + ex.getMessage());
		}
		catch (IOException ex) {
			fail("Should not throw IOException: " + ex.getMessage());
		}
		return request.getSession(false);
	}


	private static class TestWizardController extends AbstractWizardFormController {

		public TestWizardController(Class commandClass, String beanName) {
			setCommandClass(commandClass);
			setBeanName(beanName);
		}

		protected void validatePage(Object command, Errors errors, int page) {
			TestBean tb = (TestBean) command;
			switch (page) {
				case 0:
					if (tb.getName() == null) {
						errors.rejectValue("name", "NAME_REQUIRED", null, "Name is required");
					}
					break;
				case 1:
					if (tb.getAge() == 0) {
						errors.rejectValue("age", "AGE_REQUIRED", null, "Age is required");
					}
					break;
			  default:
					throw new IllegalArgumentException("Invalid page number");
			}
		}

		protected ModelAndView processFinish(HttpServletRequest request, HttpServletResponse response,
		                                     Object command, BindException errors)
		    throws ServletException, IOException {
			return new ModelAndView("success", getBeanName(), command);
		}

		protected ModelAndView processCancel(HttpServletRequest request, HttpServletResponse response,
		                                    Object command, BindException errors)
		    throws ServletException, IOException {
			return new ModelAndView("abort", getBeanName(), command);
		}
	}

}
