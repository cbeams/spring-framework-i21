package com.interface21.web.servlet.handler;

import java.io.IOException;

import javax.servlet.ServletException;

import junit.framework.TestCase;

import com.interface21.context.ApplicationContext;
import com.interface21.context.support.ClassPathXmlApplicationContext;
import com.interface21.web.mock.MockHttpServletRequest;
import com.interface21.web.servlet.HandlerExecutionChain;
import com.interface21.web.servlet.HandlerMapping;

/**
 * @author Rod Johnson
 */
public class BeanNameUrlHandlerMappingTestSuite extends TestCase {

	public static final String CONF = "/com/interface21/web/servlet/handler/map1.xml";
	
	private HandlerMapping hm;
	
	private ApplicationContext ac;

	public BeanNameUrlHandlerMappingTestSuite() throws IOException {
		ac = new ClassPathXmlApplicationContext(CONF);
		hm = new BeanNameUrlHandlerMapping();
		hm.setApplicationContext(ac);
	}

	public void testRequestsWithHandlers() throws Exception {
		Object bean = ac.getBean("godCtrl");

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
		HandlerExecutionChain hec = hm.getHandler(req);
		assertTrue("Handler is correct bean", hec != null && hec.getHandler() == bean);

		req = new MockHttpServletRequest(null, "GET", "/testarossa");
		hec = hm.getHandler(req);
		assertTrue("Handler is correct bean", hec != null && hec.getHandler() == bean);

		req = new MockHttpServletRequest(null, "GET", "/tes");
		hec = hm.getHandler(req);
		assertTrue("Handler is correct bean", hec == null);
	}

}
