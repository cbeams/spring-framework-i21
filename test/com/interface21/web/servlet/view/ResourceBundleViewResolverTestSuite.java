package com.interface21.web.servlet.view;

import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.interface21.web.context.WebApplicationContext;
import com.interface21.web.context.support.StaticWebApplicationContext;
import com.interface21.web.servlet.View;
import com.interface21.web.mock.MockServletContext;

/**
 *
 * @author Rod Johnson
 * @version $RevisionId$
 */
public class ResourceBundleViewResolverTestSuite extends TestCase {

	/** Comes from this package */
	public static String PROPS_FILE = "com.interface21.web.servlet.view.testviews";

	ResourceBundleViewResolver rb;
	
	WebApplicationContext wac;
	
	/** Not a constant: allows overrides.
	 * Controls whether to cache views.
	 */
	protected boolean getCache() {
		return true;
	}

	/** Creates new SeatingPlanTest */
	public ResourceBundleViewResolverTestSuite(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		rb = new ResourceBundleViewResolver();
		rb.setBasename(PROPS_FILE);
		rb.setCache(getCache());
		wac = new StaticWebApplicationContext();
		wac.setServletContext(new MockServletContext());

		// This will be propagated to views, so we need it
		rb.setApplicationContext(wac);
	}

	public void tearDown() {
	}

	public void testDebugViewEnglish() throws Exception {
		View v = rb.resolveViewname("debugView", Locale.ENGLISH);
		assertTrue("debugView must be of type InternalResourceView", v instanceof InternalResourceView);
		InternalResourceView jv = (InternalResourceView) v;
		assertTrue("debugView must have correct URL", "jsp/debug/debug.jsp".equals(jv.getUrl()));

		Map m = jv.getStaticAttributes();
		assertTrue("Must have 2 static attributes, not " + m.size(), m.size() == 2);
		assertTrue("attribute foo = bar, not '" + m.get("foo") + "'", m.get("foo").equals("bar"));
		assertTrue("attribute postcode = SE10 9JY", m.get("postcode").equals("SE10 9JY"));

		// Test default content type
		assertTrue("Correct default content type", jv.getContentType().equals("text/html; charset=ISO-8859-1"));
		
		// Test default content type
		assertTrue("WebAppContext was set on view", jv.getApplicationContext() != null);
		assertTrue("WebAppContext was sticky", jv.getApplicationContext().equals(wac));
	}

	public void testDebugViewFrench() throws Exception {
		View v = rb.resolveViewname("debugView", Locale.FRENCH);
		assertTrue("French debugView must be of type InternalResourceView", v instanceof InternalResourceView);
		InternalResourceView jv = (InternalResourceView) v;
		assertTrue("French debugView must have correct URL", "jsp/debug/deboug.jsp".equals(jv.getUrl()));
		assertTrue(
			"Correct overridden (XML) content type, not '" + jv.getContentType() + "'",
			jv.getContentType().equals("text/xml; charset=ISO-8859-1"));
	}

	public void testNoSuchViewEnglish() throws Exception {
		try {
			View v = rb.resolveViewname("xxxxxxweorqiwuopeir", Locale.ENGLISH);
			fail("No such view should fail with servlet exception");
		} catch (ServletException ex) {
			// OK
		} catch (Exception ex) {
			fail("No such view should fail with servlet exception, not " + ex);
		}
	} 
	
	
	public static class TestView extends AbstractView {
		
		public int initCount;
		
		protected void renderMergedOutputModel(Map model, HttpServletRequest request, HttpServletResponse response) throws ServletException {
		}
		
		protected void initApplicationContext() {
			++initCount;
		}
		
	}
	
	
	public void testOnSetContextCalledOnce() throws Exception {
		TestView tv = (TestView) rb.resolveViewname("test", Locale.ENGLISH);
		tv = (TestView) rb.resolveViewname("test", Locale.ENGLISH);
		tv = (TestView) rb.resolveViewname("test", Locale.ENGLISH);
		assertTrue("test should have been initialized once, not " + tv.initCount + " times", tv.initCount == 1);
			
	} 
	
	public void testNameSet() throws Exception {
		TestView tv = (TestView) rb.resolveViewname("test", Locale.ENGLISH);
		assertTrue("test has correct name", "test".equals(tv.getName()));
			
	} 
	
	public void testCopyXSLTViewOnNull() throws Exception {
		View v = rb.resolveViewname("XintResultView", Locale.GERMAN);
		//assertTrue("test was inited", tv.inited());
			
	} 
	
	public void testCopyXSLTViewOnEmptyString() throws Exception {
		View v = rb.resolveViewname("XintResultView3", Locale.GERMAN);
		//assertTrue("test was inited", tv.inited());
			
	} 
	
//	public void testStylesheetXSLTView() throws Exception {
//		try {
//			View v = rb.resolveViewname("XintResultView2", Locale.GERMAN);
//			//assertTrue("test was inited", tv.inited());
//			fail("Should have thrown ServletException when it couldn't load XSLT");
//		}
//		catch (ServletException ex) {
//		}
//		catch (Throwable t) {
//			t.printStackTrace();
//			fail("Should have thrown ServletException when it couldn't load XSLT, not " + t.getClass());
//			
//		}
//			
//	} 
	

	//public void testNotNull() throws Exception {
	//	View v = rb.resolveViewname("VintResultView", Locale.ENGLISH);
	//	assertTrue("VintResultView isn't null", v != null);
//
//	}

	public void testNoSuchBasename() throws Exception {
		try {
			ResourceBundleViewResolver rb2 = new ResourceBundleViewResolver();
			rb2.setBasename("weoriwoierqupowiuer");
			View v = rb2.resolveViewname("debugView", Locale.ENGLISH);
			fail("No such basename: all requests should fail with servlet exception");
		} catch (ServletException ex) {
			// OK
		} catch (Exception ex) {
			fail("No such basename: all requests should fail with servlet exception, not " + ex);
		}
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
		//	junit.swingui.TestRunner.main(new String[] {PrototypeFactoryTests.class.getName() } );
	}

	public static Test suite() {
		TestSuite ts = new TestSuite(ResourceBundleViewResolverTestSuite.class);
		
		// This is how to run additional tests...
		ts.addTestSuite(ResourceBundleViewResolverTestSuiteNoCache.class);
		return ts;
	}
 
}

