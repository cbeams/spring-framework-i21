package com.interface21.web.servlet.mvc;

import javax.servlet.http.HttpServletResponse;

import com.interface21.web.servlet.ModelAndView;

import junit.framework.TestCase;
import com.interface21.web.mock.MockHttpRequest;
import com.interface21.web.mock.MockHttpResponse;

/**
 *
 * @author Rod Johnson
 * @version $RevisionId$
 */
public class DemoControllerTestSuite extends TestCase {

	Controller testController;
	
	/**
	 * Constructor for AbstractMultiRequestHandlerTestSuite.
	 * @param arg0
	 */
	public DemoControllerTestSuite(String arg0) {
		super(arg0);
	}

	public void setUp() {
		testController = new DemoController();
		// No bean properties to configure
	}
	
	public void testNoName() throws Exception {
		MockHttpRequest request = new MockHttpRequest(null, "GET", "test.html");
		HttpServletResponse response = new MockHttpResponse();
		ModelAndView mv = this.testController.handleRequest(request, response);
		assertTrue("View is correct", mv.getViewname().equals(DemoController.ENTER_NAME_VIEW));
		assertTrue("no name parameter", request.getParameter("name") == null);
	}
	
	public void testValidName() throws Exception {
		String name = "Tony";
		MockHttpRequest request = new MockHttpRequest(null, "GET", "test.html");
		request.addParameter("name", name);
		HttpServletResponse response = new MockHttpResponse();
		ModelAndView mv = this.testController.handleRequest(request, response);
		assertTrue("View is correct", mv.getViewname().equals(DemoController.VALID_NAME_VIEW));
		assertTrue("name parameter matches", request.getParameter("name").equals(name));
	}
	
	public void testInvalidName() throws Exception {
		String name = "Tony--";
		MockHttpRequest request = new MockHttpRequest(null, "GET", "test.html");
		request.addParameter("name", name);
		HttpServletResponse response = new MockHttpResponse();
		ModelAndView mv = this.testController.handleRequest(request, response);
		assertTrue("View is correct: expected '" + DemoController.INVALID_NAME_VIEW + "' not '" + mv.getViewname() + "'", 
			mv.getViewname().equals(DemoController.INVALID_NAME_VIEW));
		assertTrue("name parameter matches", request.getParameter("name").equals(name));
	}
	
	
}

