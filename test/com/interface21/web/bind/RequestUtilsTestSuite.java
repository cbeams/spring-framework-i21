package com.interface21.web.bind;

import junit.framework.TestCase;

import com.interface21.web.mock.MockHttpServletRequest;

/**
 * @author Juergen Hoeller
 * @since 06.08.2003
 */
public class RequestUtilsTestSuite extends TestCase {

	public void testIntParameter() throws ServletRequestBindingException {
		MockHttpServletRequest request = new MockHttpServletRequest(null, "GET", "/test");
		request.addParameter("param1", "5");
		request.addParameter("param2", "e");

		assertEquals(RequestUtils.getIntParameter(request, "param1", 6), 5);
		assertEquals(RequestUtils.getRequiredIntParameter(request, "param1"), 5);

		assertEquals(RequestUtils.getIntParameter(request, "param2", 6), 6);
		try {
			RequestUtils.getRequiredIntParameter(request, "param2");
			fail("Should have thrown ServletRequestBindingException");
		}
		catch (ServletRequestBindingException ex) {
			// expected
		}

		assertEquals(RequestUtils.getIntParameter(request, "param3", 6), 6);
		try {
			RequestUtils.getRequiredIntParameter(request, "param3");
			fail("Should have thrown ServletRequestBindingException");
		}
		catch (ServletRequestBindingException ex) {
			// expected
		}
	}

	public void testDoubleParameter() throws ServletRequestBindingException {
		MockHttpServletRequest request = new MockHttpServletRequest(null, "GET", "/test");
		request.addParameter("param1", "5.5");
		request.addParameter("param2", "e");

		assertTrue(RequestUtils.getDoubleParameter(request, "param1", 6.5) == 5.5);
		assertTrue(RequestUtils.getRequiredDoubleParameter(request, "param1") == 5.5);

		assertTrue(RequestUtils.getDoubleParameter(request, "param2", 6.5) == 6.5);
		try {
			RequestUtils.getRequiredDoubleParameter(request, "param2");
			fail("Should have thrown ServletRequestBindingException");
		}
		catch (ServletRequestBindingException ex) {
			// expected
		}

		assertTrue(RequestUtils.getDoubleParameter(request, "param3", 6.5) == 6.5);
		try {
			RequestUtils.getRequiredDoubleParameter(request, "param3");
			fail("Should have thrown ServletRequestBindingException");
		}
		catch (ServletRequestBindingException ex) {
			// expected
		}
	}

	public void testBooleanParameter() throws ServletRequestBindingException {
		MockHttpServletRequest request = new MockHttpServletRequest(null, "GET", "/test");
		request.addParameter("param1", "true");
		request.addParameter("param2", "e");
		request.addParameter("param4", "yes");
		request.addParameter("param5", "1");

		assertTrue(RequestUtils.getBooleanParameter(request, "param1", false));
		assertTrue(RequestUtils.getRequiredBooleanParameter(request, "param1"));

		assertFalse(RequestUtils.getBooleanParameter(request, "param2", true));
		assertFalse(RequestUtils.getRequiredBooleanParameter(request, "param2"));

		assertTrue(RequestUtils.getBooleanParameter(request, "param3", true));
		try {
			RequestUtils.getRequiredBooleanParameter(request, "param3");
			fail("Should have thrown ServletRequestBindingException");
		}
		catch (ServletRequestBindingException ex) {
			// expected
		}

		assertTrue(RequestUtils.getBooleanParameter(request, "param4", false));
		assertTrue(RequestUtils.getRequiredBooleanParameter(request, "param4"));

		assertTrue(RequestUtils.getBooleanParameter(request, "param5", false));
		assertTrue(RequestUtils.getRequiredBooleanParameter(request, "param5"));
	}

	public void testStringParameter() throws ServletRequestBindingException {
		MockHttpServletRequest request = new MockHttpServletRequest(null, "GET", "/test");
		request.addParameter("param1", "str");

		assertEquals(RequestUtils.getStringParameter(request, "param1", "string"), "str");
		assertEquals(RequestUtils.getRequiredStringParameter(request, "param1"), "str");

		assertEquals(RequestUtils.getStringParameter(request, "param3", "string"), "string");
		try {
			RequestUtils.getRequiredIntParameter(request, "param3");
			fail("Should have thrown ServletRequestBindingException");
		}
		catch (ServletRequestBindingException ex) {
			// expected
		}
	}

}
