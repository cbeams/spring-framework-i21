

package com.interface21.web.context;

import javax.servlet.ServletContext;
import junit.framework.Test;
import junit.framework.TestSuite;

import com.interface21.context.ApplicationContext;
import com.interface21.context.AbstractApplicationContextTests;
import com.interface21.context.TestListener;
import com.interface21.web.context.support.XmlWebApplicationContext;

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
		servletapi.TestServletContext sc = new servletapi.TestServletContext(WAR_ROOT);

		// This is required for the framework
		sc.addInitParameter("configUrl", "/WEB-INF/applicationContext.xml");

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

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
		//	junit.swingui.TestRunner.main(new String[] {PrototypeFactoryTests.class.getName() } );
	}

	public static Test suite() {
		return new TestSuite(WebApplicationContextTestSuite.class);
	}

	/**
	 * @see com.interface21.beans.factory.AbstractListableBeanFactoryTests#testCount()
	 */
	public void testCount() {
		assertTrue("should have 10 beans, not"+ this.applicationContext.getBeanDefinitionCount(),
			this.applicationContext.getBeanDefinitionCount() == 10);
	}

}
