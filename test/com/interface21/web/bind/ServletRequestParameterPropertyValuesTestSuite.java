package com.interface21.web.bind;

import com.interface21.web.mock.MockHttpRequest;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.interface21.beans.AbstractPropertyValuesTests;
import com.interface21.web.bind.*;


/**
 *
 * @author Rod Johnson
 * @version $RevisionId$
 */
public class ServletRequestParameterPropertyValuesTestSuite extends AbstractPropertyValuesTests {


	/** Creates new SeatingPlanTest */
	public ServletRequestParameterPropertyValuesTestSuite(String name) {
		super(name);
	}

	/** Run for each test */
	protected void setUp() throws Exception {
	}


	public void testNoPrefix() throws Exception {
		MockHttpRequest request = new MockHttpRequest(null, "GET", "/test/foobar");
		request.addParameter("forname", "Tony");
		request.addParameter("surname", "Blair");
		request.addParameter("age", "" + 50);

		ServletRequestParameterPropertyValues pvs = new ServletRequestParameterPropertyValues(request);
		testTony(pvs);
	}

	//public void testPrefix(String prefix p, String prefixSeparator ps) throws Exception {

	public void testPrefix() throws Exception {
		MockHttpRequest request = new MockHttpRequest(null, "GET", "/test/foobar");
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
		MockHttpRequest request = new MockHttpRequest(null, "GET", "/test/foobar");
		ServletRequestParameterPropertyValues pvs = new ServletRequestParameterPropertyValues(request);
		assertTrue("Found no parameters", pvs.getPropertyValues().length == 0);
	}



	// NULL TESTS ETC.

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
		//	junit.swingui.TestRunner.main(new String[] {PrototypeFactoryTests.class.getName() } );
	}

	public static Test suite() {
		return new TestSuite(ServletRequestParameterPropertyValuesTestSuite.class);
	}

}
