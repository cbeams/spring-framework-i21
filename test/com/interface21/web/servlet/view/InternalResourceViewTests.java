
package com.interface21.web.servlet.view;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import junit.framework.TestCase;
import org.easymock.EasyMock;
import org.easymock.MockControl;

import com.interface21.context.ApplicationContextException;
import com.interface21.web.context.WebApplicationContext;
import com.interface21.web.mock.MockHttpServletResponse;
import com.interface21.web.mock.MockRequestDispatcher;

/**
 * @author Rod Johnson
 */
public class InternalResourceViewTests extends TestCase {

	/**
	 * Constructor for InternalResourceViewTests.
	 * @param arg0
	 */
	public InternalResourceViewTests(String arg0) {
		super(arg0);
	}
	
	/**
	 * Test that if the url property isn't supplied, view initialization fails.
	 */
	public void testRejectsNullUrl() throws Exception {
		MockControl mc = EasyMock.controlFor(WebApplicationContext.class);
		WebApplicationContext wac = (WebApplicationContext) mc.getMock();
		mc.activate();
	
		InternalResourceView v = new InternalResourceView();
		try {
			v.setApplicationContext(wac);
			fail("Should be forced to set URL");
		}
		catch (ApplicationContextException ex) {
		}
	}
	
	public void testExposesModelViaRequestAttributes() throws Exception {
		HashMap model = new HashMap();
		Object obj = new Integer(1);
		model.put("foo", "bar");
		model.put("I", obj);
		
		MockControl mc = EasyMock.controlFor(WebApplicationContext.class);
		WebApplicationContext wac = (WebApplicationContext) mc.getMock();
		mc.activate();
		
		String url = "forward-to";
		
		MockControl reqControl = EasyMock.controlFor(HttpServletRequest.class);
		HttpServletRequest request = (HttpServletRequest) reqControl.getMock();
		Set keys = model.keySet();
		for (Iterator iter = keys.iterator(); iter.hasNext();) {
			String key = (String) iter.next();
			request.setAttribute(key, model.get(key));
			reqControl.setVoidCallable(1);
		}
		
		request.getRequestDispatcher(url);
		reqControl.setReturnValue(new MockRequestDispatcher(url));
		reqControl.activate();
		
		// unused
		MockHttpServletResponse response = new MockHttpServletResponse();
		
		InternalResourceView v = new InternalResourceView();
		v.setUrl(url);
		v.setApplicationContext(wac);
		
		// Can now try multiple tests
		v.render(model, request, response);
		
		mc.verify();
		reqControl.verify();
	}
	
	// TODO IO exception
	
	// TODO return null RequestDispatcher
	
	/*
	public void testRequestDispatcherThrowsIOException() throws Exception {
		HashMap model = new HashMap();
		model.put("foo", "bar");
			
		MockControl mc = EasyMock.controlFor(WebApplicationContext.class);
		WebApplicationContext wac = (WebApplicationContext) mc.getMock();
		mc.activate();
			
		String url = "forward-to";
			
		MockHttpServletRequest request = new MockHttpServletRequest(new MockServletContext(), "GET", "some-url");
			
			
		// unused
		MockHttpServletResponse response = new MockHttpServletResponse();
			
		InternalResourceView v = new InternalResourceView();
		v.setUrl(url);
		v.setApplicationContext(wac);
			
		// Can now try multiple tests
		v.render(model, request, response);
		
		assertTrue(request.getAttribute("foo").equals(model.get("foo")));
			
		mc.verify();
	}
	*/

}
