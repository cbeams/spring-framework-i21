package com.interface21.web.servlet.handler;

import junit.framework.TestCase;
import com.interface21.web.mock.MockHttpRequest;
import com.interface21.web.servlet.HandlerMapping;

import com.interface21.context.ApplicationContext;
import com.interface21.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author Rod Johnson
 * @version $RevisionId$
 */
public class SimpleUrlHandlerMappingTestSuite extends TestCase {

	public static final String CONF = "/com/interface21/web/servlet/handler/map2.xml";
	
	private HandlerMapping hm;
	
	private ApplicationContext ac;

	public SimpleUrlHandlerMappingTestSuite(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		ac = new ClassPathXmlApplicationContext(CONF);
		hm = (HandlerMapping) ac.getBean("a.urlMap");
		hm.setApplicationContext(ac);
		hm.initHandlerMapping();
	}
	
	public void tearDown() {
	}

	public void testRequestsWithHandlers() throws Exception {
		Object bean = ac.getBean("mainController");
		
		MockHttpRequest req = new MockHttpRequest(null, "GET", "/welcome.html");
		Object h = hm.getHandler(req);
		assertTrue("handler is null", h != null);
		assertTrue("Handler is correct bean", h == bean);
		
		req = new MockHttpRequest(null, "GET", "/show.html");
		h = hm.getHandler(req);
		assertTrue("handler isn't null", h != null);
		assertTrue("Handler is correct bean", h == bean);
		
		req = new MockHttpRequest(null, "GET", "/bookseats.html");
		h = hm.getHandler(req);
		assertTrue("handler isn't null", h != null);
		assertTrue("Handler is correct bean", h == bean);
	}
	
	public void testDefaultMapping() throws Exception {
		Object bean = ac.getBean("starController");
		
		MockHttpRequest req = new MockHttpRequest(null, "GET", "/goggog.html");
		Object h = hm.getHandler(req);
		assertTrue("handler is null", h != null);
		assertTrue("Handler is correct bean", h ==bean);
	}
	
// This test broken by default mapping
//	public void testRequestsWithoutHandlers() throws Exception {
//		MockHttpRequest req = new MockHttpRequest(null, "GET", "/nonsense.html");
//		Object h = hm.getHandler(req);
//		assertTrue("Handler is null", h == null);
//		
//		req = new MockHttpRequest(null, "GET", "/foo/bar/baz.html");
//		h = hm.getHandler(req);
//		assertTrue("Handler is null", h == null);
//	}

}
