package com.interface21.web.context;

import java.util.Locale;

import javax.servlet.ServletContext;

import com.interface21.beans.ITestBean;
import com.interface21.context.AbstractApplicationContextTests;
import com.interface21.context.ApplicationContext;
import com.interface21.context.NoSuchMessageException;
import com.interface21.context.TestListener;
import com.interface21.web.context.support.XmlWebApplicationContext;
import com.interface21.web.mock.MockServletContext;

/**
 * @author Rod Johnson
 */
public class WebApplicationContextTestSuite extends AbstractApplicationContextTests {

	private ServletContext servletContext;
	
	private WebApplicationContext root;

	public WebApplicationContextTestSuite() throws Exception {
	}

	protected ApplicationContext createContext() throws Exception {
		root = new XmlWebApplicationContext();
		MockServletContext sc = new MockServletContext("", "/com/interface21/web/context/WEB-INF/web.xml");
		sc.addInitParameter(XmlWebApplicationContext.CONFIG_LOCATION_PARAM, "/com/interface21/web/context/WEB-INF/applicationContext.xml");
		sc.addInitParameter(XmlWebApplicationContext.CONFIG_LOCATION_PREFIX_PARAM, "/com/interface21/web/context/WEB-INF/");
		this.servletContext = sc;
		root.setServletContext(sc);
		WebApplicationContext wac = new XmlWebApplicationContext(root, "test-servlet");
		wac.setServletContext(sc);
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
			this.applicationContext.getBeanDefinitionCount() == 15);
	}

	public void testWithoutMessageSource() throws Exception {
		MockServletContext sc = new MockServletContext("", "/com/interface21/web/context/WEB-INF/web.xml");
		sc.addInitParameter(XmlWebApplicationContext.CONFIG_LOCATION_PREFIX_PARAM, "/com/interface21/web/context/WEB-INF/");
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

}
