package com.interface21.web.context;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServlet;

import junit.framework.TestCase;

import com.interface21.beans.TestBean;
import com.interface21.beans.factory.LifecycleBean;
import com.interface21.context.ApplicationContext;
import com.interface21.context.ApplicationContextException;
import com.interface21.context.support.ClassPathXmlApplicationContext;
import com.interface21.web.context.support.StaticWebApplicationContext;
import com.interface21.web.context.support.XmlWebApplicationContext;
import com.interface21.web.mock.MockServletContext;
import com.interface21.web.mock.MockServletConfig;

/**
 * @author Juergen Hoeller
 * @since 12.08.2003
 */
public class ContextLoaderTestSuite extends TestCase {

	public void testContextLoaderListenerWithDefaultContext() throws Exception {
		MockServletContext sc = new MockServletContext("", "/com/interface21/web/context/WEB-INF/web.xml");
		sc.addInitParameter(XmlWebApplicationContext.CONFIG_LOCATION_PARAM, "/com/interface21/web/context/WEB-INF/applicationContext.xml");
		ServletContextListener listener = new ContextLoaderListener();
		ServletContextEvent event = new ServletContextEvent(sc);
		listener.contextInitialized(event);
		WebApplicationContext wc = (WebApplicationContext)sc.getAttribute(WebApplicationContext.WEB_APPLICATION_CONTEXT_ATTRIBUTE_NAME);
		assertTrue("Correct WebApplicationContext exposed in ServletContext", wc instanceof XmlWebApplicationContext);
		LifecycleBean lb = (LifecycleBean) wc.getBean("lifecycle");
		assertTrue("Not destroyed", !lb.isDestroyed());
		listener.contextDestroyed(event);
		assertTrue("Destroyed", lb.isDestroyed());
	}

	public void testContextLoaderServletWithDefaultContext() throws Exception {
		MockServletContext sc = new MockServletContext("", "/com/interface21/web/context/WEB-INF/web.xml");
		sc.addInitParameter(XmlWebApplicationContext.CONFIG_LOCATION_PARAM, "/com/interface21/web/context/WEB-INF/applicationContext.xml");
		HttpServlet servlet = new ContextLoaderServlet();
		ServletConfig config = new MockServletConfig(sc, "test");
		servlet.init(config);
		WebApplicationContext wc = (WebApplicationContext)sc.getAttribute(WebApplicationContext.WEB_APPLICATION_CONTEXT_ATTRIBUTE_NAME);
		assertTrue("Correct WebApplicationContext exposed in ServletContext", wc instanceof XmlWebApplicationContext);
		LifecycleBean lb = (LifecycleBean) wc.getBean("lifecycle");
		assertTrue("Not destroyed", !lb.isDestroyed());
		servlet.destroy();
		assertTrue("Destroyed", lb.isDestroyed());
	}

	public void testContextLoaderWithCustomContext() throws Exception {
		MockServletContext sc = new MockServletContext("", "/com/interface21/web/context/WEB-INF/web.xml");
		sc.addInitParameter(ContextLoader.CONTEXT_CLASS_PARAM, "com.interface21.web.context.support.StaticWebApplicationContext");
		ServletContextListener listener = new ContextLoaderListener();
		ServletContextEvent event = new ServletContextEvent(sc);
		listener.contextInitialized(event);
		WebApplicationContext wc = (WebApplicationContext) sc.getAttribute(WebApplicationContext.WEB_APPLICATION_CONTEXT_ATTRIBUTE_NAME);
		assertTrue("Correct WebApplicationContext exposed in ServletContext", wc instanceof StaticWebApplicationContext);
	}

	public void testContextLoaderWithInvalidLocation() throws Exception {
		MockServletContext sc = new MockServletContext("", "/com/interface21/web/context/WEB-INF/web.xml");
		sc.addInitParameter(XmlWebApplicationContext.CONFIG_LOCATION_PARAM, "/WEB-INF/myContext.xml");
		ServletContextListener listener = new ContextLoaderListener();
		ServletContextEvent event = new ServletContextEvent(sc);
		try {
			listener.contextInitialized(event);
			fail("Should have thrown ApplicationContextException");
		}
		catch (ApplicationContextException ex) {
			// expected
			assertTrue(ex.getRootCause() instanceof FileNotFoundException);
		}
	}

	public void testContextLoaderWithInvalidContext() throws Exception {
		MockServletContext sc = new MockServletContext("", "/com/interface21/web/context/WEB-INF/web.xml");
		sc.addInitParameter(ContextLoader.CONTEXT_CLASS_PARAM, "com.interface21.web.context.support.InvalidWebApplicationContext");
		ServletContextListener listener = new ContextLoaderListener();
		ServletContextEvent event = new ServletContextEvent(sc);
		try {
			listener.contextInitialized(event);
			fail("Should have thrown ApplicationContextException");
		}
		catch (ApplicationContextException ex) {
			// expected
			assertTrue(ex.getRootCause() instanceof ClassNotFoundException);
		}
	}

	public void testClassPathXmlApplicationContext() throws IOException {
		ApplicationContext context = new ClassPathXmlApplicationContext("/com/interface21/web/context/WEB-INF/applicationContext.xml");
		assertTrue("Has father", context.getBean("father") != null);
		assertTrue("Has father", context.getBean("rod") != null);
		assertTrue("Doesn't have spouse", ((TestBean) context.getBean("rod")).getSpouse() == null);
		assertTrue("myinit not evaluated", "Roderick".equals(((TestBean) context.getBean("rod")).getName()));

		context = new ClassPathXmlApplicationContext(new String[] {"/com/interface21/web/context/WEB-INF/applicationContext.xml",
		                                                           "/com/interface21/web/context/WEB-INF/test-servlet.xml"});
		assertTrue("Has father", context.getBean("father") != null);
		assertTrue("Has father", context.getBean("rod") != null);
		assertTrue("Has spouse", ((TestBean) context.getBean("rod")).getSpouse() != null);
	}

}
