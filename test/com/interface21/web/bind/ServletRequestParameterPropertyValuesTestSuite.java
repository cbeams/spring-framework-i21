package com.interface21.web.bind;

import java.util.Arrays;

import com.interface21.beans.AbstractPropertyValuesTests;
import com.interface21.web.mock.MockHttpServletRequest;


/**
 *
 * @author Rod Johnson
 * @version $RevisionId$
 */
public class ServletRequestParameterPropertyValuesTestSuite extends AbstractPropertyValuesTests {

	public ServletRequestParameterPropertyValuesTestSuite(String name) {
		super(name);
	}

	public void testNoPrefix() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest(null, "GET", "/test/foobar");
		request.addParameter("forname", "Tony");
		request.addParameter("surname", "Blair");
		request.addParameter("age", "" + 50);

		ServletRequestParameterPropertyValues pvs = new ServletRequestParameterPropertyValues(request);
		testTony(pvs);
	}

	public void testPrefix() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest(null, "GET", "/test/foobar");
		request.addParameter("test_forname", "Tony");
		request.addParameter("test_surname", "Blair");
		request.addParameter("test_age", "" + 50);

		ServletRequestParameterPropertyValues pvs = new ServletRequestParameterPropertyValues(request);
		assertTrue("Didn't fidn normal when given prefix", !pvs.contains("forname"));
		assertTrue("Did treat prefix as normal when not given prefix", pvs.contains("test_forname"));

		pvs = new ServletRequestParameterPropertyValues(request, "test");
		testTony(pvs);
	}

	public void testNoParameters() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest(null, "GET", "/test/foobar");
		ServletRequestParameterPropertyValues pvs = new ServletRequestParameterPropertyValues(request);
		assertTrue("Found no parameters", pvs.getPropertyValues().length == 0);
	}

	public void testMultipleValuesForParameter() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest(null, "GET", "/test/foobar");
		String[] original = new String[] {"Tony", "Rod"};
		request.addParameter("forname", original);

		ServletRequestParameterPropertyValues pvs = new ServletRequestParameterPropertyValues(request);
		assertTrue("Found 1 parameter", pvs.getPropertyValues().length == 1);
		assertTrue("Found array value", pvs.getPropertyValue("forname").getValue() instanceof String[]);
		String[] values = (String[]) pvs.getPropertyValue("forname").getValue();
		assertEquals("Correct values", Arrays.asList(values), Arrays.asList(original));
	}

}
