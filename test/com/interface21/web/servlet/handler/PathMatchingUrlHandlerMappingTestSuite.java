package com.interface21.web.servlet.handler;

import java.io.IOException;

import junit.framework.TestCase;

import com.interface21.context.ApplicationContext;
import com.interface21.context.support.ClassPathXmlApplicationContext;
import com.interface21.web.mock.MockHttpServletRequest;
import com.interface21.web.servlet.HandlerExecutionChain;
import com.interface21.web.servlet.HandlerMapping;

/**
 *
 * @author Alef Arendsen
 * @version $RevisionId$
 */
public class PathMatchingUrlHandlerMappingTestSuite extends TestCase {

	public static final String CONF = "/com/interface21/web/servlet/handler/map3.xml";

	private HandlerMapping hm;

	private ApplicationContext ac;

	public PathMatchingUrlHandlerMappingTestSuite() throws IOException {
		ac = new ClassPathXmlApplicationContext(CONF);
		hm = (HandlerMapping) ac.getBean("a.urlMap");
		hm.setApplicationContext(ac);
	}

	public void testRequestsWithHandlers() throws Exception {
		Object bean = ac.getBean("mainController");

		MockHttpServletRequest req = new MockHttpServletRequest(null, "GET", "/welcome.html");
		HandlerExecutionChain hec = hm.getHandler(req);
		assertTrue("Handler is correct bean", hec != null && hec.getHandler() == bean);

		req = new MockHttpServletRequest(null, "GET", "/show.html");
		hec = hm.getHandler(req);
		assertTrue("Handler is correct bean", hec != null && hec.getHandler() == bean);

		req = new MockHttpServletRequest(null, "GET", "/bookseats.html");
		hec = hm.getHandler(req);
		assertTrue("Handler is correct bean", hec != null && hec.getHandler() == bean);
	}

	public void testActualPathMatching() throws Exception {
		// there a couple of mappings defined with which we can test the
		// path matching, let's do that...

		Object bean = ac.getBean("mainController");
		Object defaultBean = ac.getBean("starController");

		// testing some normal behavior
		MockHttpServletRequest req = new MockHttpServletRequest(null, "GET", "/pathmatchingTest.html");
		HandlerExecutionChain hec = hm.getHandler(req);
		assertTrue("Handler is null", hec != null);
		assertTrue("Handler is correct bean", hec.getHandler() == bean);

		// no match, no forward slash included
		req = new MockHttpServletRequest(null, "GET", "welcome.html");
		hec = hm.getHandler(req);
		assertTrue("Handler is correct bean", hec != null && hec.getHandler() == defaultBean);


		// testing some ????? behavior
		req = new MockHttpServletRequest(null, "GET", "/pathmatchingAA.html");
		hec = hm.getHandler(req);
		assertTrue("Handler is correct bean", hec != null && hec.getHandler() == bean);

		// testing some ????? behavior
		req = new MockHttpServletRequest(null, "GET", "/pathmatchingA.html");
		hec = hm.getHandler(req);
		assertTrue("Handler is correct bean", hec != null && hec.getHandler() == defaultBean);

		// testing some ????? behavior
		req = new MockHttpServletRequest(null, "GET", "/administrator/pathmatching.html");
		hec = hm.getHandler(req);
		assertTrue("Handler is correct bean", hec != null && hec.getHandler() == bean);

		// testing simple /**/ behavior
		req = new MockHttpServletRequest(null, "GET", "/administrator/test/pathmatching.html");
		hec = hm.getHandler(req);
		assertTrue("Handler is correct bean", hec != null && hec.getHandler() == bean);

		// this should not match because of the administratorT
		req = new MockHttpServletRequest(null, "GET", "/administratort/pathmatching.html");
		hec = hm.getHandler(req);
		assertTrue("Handler is correct bean", hec != null && hec.getHandler() == defaultBean);

		// this should match because of *.jsp
		req = new MockHttpServletRequest(null, "GET", "/bla.jsp");
		hec = hm.getHandler(req);
		assertTrue("Handler is correct bean", hec != null && hec.getHandler() == bean);

		// this as well, because there's a **/ in there as well
		req = new MockHttpServletRequest(null, "GET", "/testing/bla.jsp");
		hec = hm.getHandler(req);
		assertTrue("Handler is correct bean", hec != null && hec.getHandler() == bean);

		// should match because because exact pattern is there
		req = new MockHttpServletRequest(null, "GET", "/administrator/another/bla.xml");
		hec = hm.getHandler(req);
		assertTrue("Handler is correct bean", hec != null && hec.getHandler() == bean);

		// should not match, because there's not .gif extension in there
		req = new MockHttpServletRequest(null, "GET", "/administrator/another/bla.gif");
		hec = hm.getHandler(req);
		assertTrue("Handler is correct bean", hec != null && hec.getHandler() == defaultBean);

		// should match because there testlast* in there
		req = new MockHttpServletRequest(null, "GET", "/administrator/test/testlastbit");
		hec = hm.getHandler(req);
		assertTrue("Handler is correct bean", hec != null && hec.getHandler() == bean);

		// but this not, because it's testlast and not testla
		req = new MockHttpServletRequest(null, "GET", "/administrator/test/testla");
		hec = hm.getHandler(req);
		assertTrue("Handler is correct bean", hec != null && hec.getHandler() == defaultBean);


		req = new MockHttpServletRequest(null, "GET", "/administrator/testing/longer/bla");
		hec = hm.getHandler(req);
		assertTrue("Handler is correct bean", hec != null && hec.getHandler() == bean);

		req = new MockHttpServletRequest(null, "GET", "/administrator/testing/longer/test.jsp");
		hec = hm.getHandler(req);
		assertTrue("Handler is correct bean", hec != null && hec.getHandler() == bean);

		req = new MockHttpServletRequest(null, "GET", "/administrator/testing/longer2/notmatching/notmatching");
		hec = hm.getHandler(req);
		assertTrue("Handler is correct bean", hec != null && hec.getHandler() == defaultBean);

		req = new MockHttpServletRequest(null, "GET", "/shortpattern/testing/toolong");
		hec = hm.getHandler(req);
		assertTrue("Handler is correct bean", hec != null && hec.getHandler() == defaultBean);

		req = new MockHttpServletRequest(null, "GET", "/XXpathXXmatching.html");
		hec = hm.getHandler(req);
		assertTrue("Handler is correct bean", hec != null && hec.getHandler() == bean);

		req = new MockHttpServletRequest(null, "GET", "/pathXXmatching.html");
		hec = hm.getHandler(req);
		assertTrue("Handler is correct bean", hec != null && hec.getHandler() == bean);

		req = new MockHttpServletRequest(null, "GET", "/XpathXXmatching.html");
		hec = hm.getHandler(req);
		assertTrue("Handler is correct bean", hec != null && hec.getHandler() == defaultBean);

		req = new MockHttpServletRequest(null, "GET", "/XXpathmatching.html");
		hec = hm.getHandler(req);
		assertTrue("Handler is correct bean", hec != null && hec.getHandler() == defaultBean);

		req = new MockHttpServletRequest(null, "GET", "/show12.html");
		hec = hm.getHandler(req);
		assertTrue("Handler is correct bean", hec != null && hec.getHandler() == bean);

		req = new MockHttpServletRequest(null, "GET", "/show123.html");
		hec = hm.getHandler(req);
		assertTrue("Handler is correct bean", hec != null && hec.getHandler() == bean);

		req = new MockHttpServletRequest(null, "GET", "/show1.html");
		hec = hm.getHandler(req);
		assertTrue("Handler is correct bean", hec != null && hec.getHandler() == bean);

		req = new MockHttpServletRequest(null, "GET", "/reallyGood-test-is-this.jpeg");
		hec = hm.getHandler(req);
		assertTrue("Handler is correct bean", hec != null && hec.getHandler() == bean);

		req = new MockHttpServletRequest(null, "GET", "/reallyGood-tst-is-this.jpeg");
		hec = hm.getHandler(req);
		assertTrue("Handler is correct bean", hec != null && hec.getHandler() == defaultBean);

		req = new MockHttpServletRequest(null, "GET", "/testing/test.jpeg");
		hec = hm.getHandler(req);
		assertTrue("Handler is correct bean", hec != null && hec.getHandler() == bean);

		req = new MockHttpServletRequest(null, "GET", "/testing/test.jpg");
		hec = hm.getHandler(req);
		assertTrue("Handler is correct bean", hec != null && hec.getHandler() == defaultBean);

		req = new MockHttpServletRequest(null, "GET", "/anotherTest");
		hec = hm.getHandler(req);
		assertTrue("Handler is correct bean", hec != null && hec.getHandler() == bean);

		req = new MockHttpServletRequest(null, "GET", "/stillAnotherTest");
		hec = hm.getHandler(req);
		assertTrue("Handler is correct bean", hec != null && hec.getHandler() == defaultBean);

		// there outofpattern*yeah in the pattern, so this should fail
		req = new MockHttpServletRequest(null, "GET", "/outofpattern*ye");
		hec = hm.getHandler(req);
		assertTrue("Handler is correct bean", hec != null && hec.getHandler() == defaultBean);


	}

	public void testDefaultMapping() throws Exception {
		Object bean = ac.getBean("starController");
		MockHttpServletRequest req = new MockHttpServletRequest(null, "GET", "/goggog.html");
		HandlerExecutionChain hec = hm.getHandler(req);
		assertTrue("Handler is correct bean", hec != null && hec.getHandler() == bean);
	}

}
