package com.interface21.web.servlet.handler;

import javax.servlet.ServletException;

import junit.framework.TestCase;
import com.interface21.web.mock.MockHttpServletRequest;

import com.interface21.context.ApplicationContext;
import com.interface21.context.support.ClassPathXmlApplicationContext;
import com.interface21.web.servlet.handler.BeanNameUrlHandlerMapping;
import com.interface21.web.servlet.HandlerMapping;

/**
 *
 * @author Rod Johnson
 * @version $RevisionId$
 */
public class BeanNameUrlHandlerMappingTestSuite extends TestCase {

	/** We use ticket WAR root for file structure.
	 * We don't attempt to read web.xml.
	 */
	//public static final String WAR_ROOT = "d:\\book\\project\\i21-framework\\test\\com\\interface21\\framework\\web";
	
	public static final String CONF = "/com/interface21/web/servlet/handler/maps.xml";
	
	private HandlerMapping hm;
	
	private ApplicationContext ac;

	//ServletConfig servletConfig;
	
	//DispatcherServlet controllerServlet;

	/** Creates new SeatingPlanTest */
	public BeanNameUrlHandlerMappingTestSuite(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		ac = new ClassPathXmlApplicationContext(CONF);
		hm = new BeanNameUrlHandlerMapping();
		hm.setApplicationContext(ac);
		hm.initHandlerMapping();
	}
	
	public void tearDown() {
	}

	public void testRequestsWithHandlers() throws Exception {
		Object bean = ac.getBean("godCtrl");

		MockHttpServletRequest req = new MockHttpServletRequest(null, "GET", "/welcome.html");
		Object h = hm.getHandler(req);
		assertTrue("Handler is correct bean", h == bean);
		
		req = new MockHttpServletRequest(null, "GET", "/show.html");
		h = hm.getHandler(req);
		assertTrue("Handler is correct bean", h == bean);
		
		req = new MockHttpServletRequest(null, "GET", "/bookseats.html");
		h = hm.getHandler(req);
		assertTrue("Handler is correct bean", h == bean);
	}
	
	public void testRequestsWithoutHandlers() throws Exception {
		MockHttpServletRequest req = new MockHttpServletRequest(null, "GET", "/nonsense.html");
		Object h = hm.getHandler(req);
		assertTrue("Handler is null", h == null);
		
		req = new MockHttpServletRequest(null, "GET", "/foo/bar/baz.html");
		h = hm.getHandler(req);
		assertTrue("Handler is null", h == null);
	}

	public void testAsteriskMatches() throws ServletException {
		Object bean = ac.getBean("godCtrl");

		MockHttpServletRequest req = new MockHttpServletRequest(null, "GET", "/test.html");
		Object h = hm.getHandler(req);
		assertTrue("Handler is correct bean", h == bean);

		req = new MockHttpServletRequest(null, "GET", "/testarossa");
		h = hm.getHandler(req);
		assertTrue("Handler is correct bean", h == bean);

		req = new MockHttpServletRequest(null, "GET", "/tes");
		h = hm.getHandler(req);
		assertTrue("Handler is null", h == null);
	}

}
