package com.interface21.web.context;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.interface21.beans.ITestBean;
import com.interface21.beans.TestBean;
import com.interface21.context.AbstractApplicationContextTests;
import com.interface21.context.ApplicationContext;
import com.interface21.context.ApplicationContextException;
import com.interface21.context.NoSuchMessageException;
import com.interface21.context.TestListener;
import com.interface21.context.support.ClassPathXmlApplicationContext;
import com.interface21.web.context.support.StaticWebApplicationContext;
import com.interface21.web.context.support.XmlWebApplicationContext;
import com.interface21.web.mock.MockServletContext;

/**
 *
 * @author  rod  
 * @version
 */
public class WebApplicationContextTestSuite extends AbstractApplicationContextTests {

	/** We use ticket WAR root for file structure.
	 * We don't attempt to read web.xml.
	 */
	public static final String WAR_ROOT = "/com/interface21/web/context";

	ServletContext servletContext;
	
	private WebApplicationContext root;

	/** Creates new SeatingPlanTest */
	public WebApplicationContextTestSuite(String name) {
		super(name);
	}

	protected ApplicationContext createContext() throws Exception {
		root = new XmlWebApplicationContext();
		MockServletContext sc = new MockServletContext(WAR_ROOT);

		this.servletContext = sc;

		root.setServletContext(sc);
		
		WebApplicationContext wac = new XmlWebApplicationContext(root, "test-servlet");

		wac.setServletContext(sc);
		
		// Add listeners expected by parent test case
		//wac.(this.listener);

		return wac;
	}

	/**
	 * Overridden as we can't trust superclass method
	 * @see com.interface21.context.AbstractApplicationContextTests#testEvents()
	 */
	public void testEvents() throws Exception {
		TestListener listener = (TestListener) this.applicationContext.getBean("testListener");
		listener.zeroCounter();
		TestListener parentListener = (TestListener) this.applicationContext.getParent().getBean("parentListener");
		parentListener.zeroCounter();
		
		parentListener.zeroCounter();
		assertTrue("0 events before publication", listener.getEventCount() == 0);
		assertTrue("0 parent events before publication", parentListener.getEventCount() == 0);
		this.applicationContext.publishEvent(new MyEvent(this));
		assertTrue("1 events after publication, not " + listener.getEventCount(), listener.getEventCount() == 1);
		assertTrue("1 parent events after publication", parentListener.getEventCount() == 1);
	}


	public void testWebApplicationContextExposedAsServletContextAttribute() throws Exception {
		WebApplicationContext wc = (WebApplicationContext) this.servletContext.getAttribute(WebApplicationContext.WEB_APPLICATION_CONTEXT_ATTRIBUTE_NAME);
		assertTrue("WebApplicationContext exposed in ServletContext as attribute", wc != null);
		assertTrue("WebApplicationContext exposed in ServletContext as attribute == root", wc == this.root);
	}

	/** Assumes web.xml defines testConfigObject of type TestConfigBean */
	public void testConfigObjects() throws Exception {
		assertTrue("has 'testConfigObject' attribute", servletContext.getAttribute("testConfigObject") != null);
		Object o = servletContext.getAttribute("testConfigObject");
		assertTrue("testConfigObject attribute is of type TestConfigBean", o instanceof TestConfigBean);
		TestConfigBean tcb = (TestConfigBean) o;
		assertTrue("tcb name=Tony", tcb.getName().equals("Tony"));
		assertTrue("tcb age=48", tcb.getAge() == 48);

		// Now test context aware config object
		assertTrue("has 'testConfigObject2' attribute", servletContext.getAttribute("testConfigObject2") != null);
		o = servletContext.getAttribute("testConfigObject2");
		assertTrue("testConfigObject2 attribute is of type ContextAwareTestConfigBean", o instanceof ContextAwareTestConfigBean);
		ContextAwareTestConfigBean ctcb = (ContextAwareTestConfigBean) o;
		assertTrue("ctcb name=Gordon", ctcb.getName().equals("Gordon"));
		assertTrue("ctcb age=49", ctcb.getAge() == 49);
		assertTrue("ctcb context is non null", ctcb.getApplicationContext() != null);
		assertTrue("ctcb context is context", ctcb.getApplicationContext() == root);
	}

	public void testCount() {
		assertTrue("should have 17 beans, not"+ this.applicationContext.getBeanDefinitionCount(),
			this.applicationContext.getBeanDefinitionCount() == 17);
	}

	public void testWithoutMessageSource() throws Exception {
		MockServletContext sc = new MockServletContext(WAR_ROOT);
		WebApplicationContext wac = new XmlWebApplicationContext(null, "testNamespace");
		wac.setServletContext(sc);
		try {
			wac.getMessage("someMessage", null, Locale.getDefault());
			fail("Should have thrown NoSuchMessageException");
		}
		catch (NoSuchMessageException ex) {
			// expected;
		}
		String msg = wac.getMessage("someMessage", null, "default", Locale.getDefault());
		assertTrue("Default message returned", "default".equals(msg));
	}

	public void testContextNesting() {
		ITestBean father = (ITestBean) this.applicationContext.getBean("father");
		assertTrue("Bean from root context", father != null);

		ITestBean rod = (ITestBean) this.applicationContext.getBean("rod");
		assertTrue("Bean from child context", "Rod".equals(rod.getName()));
		assertTrue("Bean has external reference", rod.getSpouse() == father);

		rod = (ITestBean) this.root.getBean("rod");
		assertTrue("Bean from root context", "Roderick".equals(rod.getName()));
	}

	public void testContextLoaderWithDefaultContext() throws Exception {
		ServletContext sc = new MockServletContext(WAR_ROOT);
		ServletContextListener listener = new ContextLoaderListener();
		ServletContextEvent event = new ServletContextEvent(sc);
		listener.contextInitialized(event);
		WebApplicationContext wc = (WebApplicationContext)sc.getAttribute(WebApplicationContext.WEB_APPLICATION_CONTEXT_ATTRIBUTE_NAME);
		assertTrue("Correct WebApplicationContext exposed in ServletContext", wc instanceof XmlWebApplicationContext);
	}

	public void testContextLoaderWithCustomContext() throws Exception {
		MockServletContext sc = new MockServletContext(WAR_ROOT);
		sc.addInitParameter(ContextLoader.CONTEXT_CLASS_PARAM, "com.interface21.web.context.support.StaticWebApplicationContext");
		ServletContextListener listener = new ContextLoaderListener();
		ServletContextEvent event = new ServletContextEvent(sc);
		listener.contextInitialized(event);
		WebApplicationContext wc = (WebApplicationContext) sc.getAttribute(WebApplicationContext.WEB_APPLICATION_CONTEXT_ATTRIBUTE_NAME);
		assertTrue("Correct WebApplicationContext exposed in ServletContext", wc instanceof StaticWebApplicationContext);
	}

	public void testContextLoaderWithInvalidLocation() throws Exception {
		MockServletContext sc = new MockServletContext(WAR_ROOT);
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
		MockServletContext sc = new MockServletContext(WAR_ROOT);
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

		context = new ClassPathXmlApplicationContext(new String[] {"/com/interface21/web/context/WEB-INF/applicationContext.xml",
		                                                           "/com/interface21/web/context/WEB-INF/test-servlet.xml"});
		assertTrue("Has father", context.getBean("father") != null);
		assertTrue("Has father", context.getBean("rod") != null);
		assertTrue("Has spouse", ((TestBean) context.getBean("rod")).getSpouse() != null);
	}

}
