package com.interface21.context;

import java.util.Locale;

import com.interface21.beans.TestBean;
import com.interface21.beans.factory.BeanFactory;
import com.interface21.beans.factory.AbstractListableBeanFactoryTests;

/**
 * Classname doesn't match XXXXTestSuite pattern, so as to avoid
 * being invoked by Ant JUnit run, as it's abstract
 * @author Rod Johnson
 * @version $RevisionId$
 */
public abstract class AbstractApplicationContextTests extends AbstractListableBeanFactoryTests {

	/** Must be supplied as XML */
	public static final String TEST_NAMESPACE = "testNamespace";

	protected ApplicationContext applicationContext;

	/** Subclass must register this */
	protected TestListener listener = new TestListener();

	protected TestListener parentListener = new TestListener();

	public AbstractApplicationContextTests() throws Exception {
		this.applicationContext = createContext();
	}

	protected BeanFactory getBeanFactory() {
		return applicationContext;
	}

	/** Must register a TestListener
	 *  Must register standard beans
	 * Parent must register rod with name Roderick
	 * and father with name Albert
	 */
	protected abstract ApplicationContext createContext() throws Exception;

	protected void tearDown() {
	}

	public void testContextAwareSingletonWasCalledBack() throws Exception {
		ACATest aca = (ACATest) applicationContext.getBean("aca");
		assertTrue("has had context set", aca.getApplicationContext() == applicationContext);
		Object aca2 = applicationContext.getBean("aca");
		assertTrue("Same instance", aca == aca2);
		assertTrue("Says is singleton", applicationContext.isSingleton("aca"));
	}

	public void testContextAwarePrototypeWasCalledBack() throws Exception {
		ACATest aca = (ACATest) applicationContext.getBean("aca-prototype");
		assertTrue("has had context set", aca.getApplicationContext() == applicationContext);
		Object aca2 = applicationContext.getBean("aca-prototype");
		assertTrue("NOT Same instance", aca != aca2);
		assertTrue("Says is prototype", !applicationContext.isSingleton("aca-prototype"));
	}


	public void testParentNonNull() {
		assertTrue("parent isn't null", applicationContext.getParent() != null);
	}

	public void testGrandparentNull() {
		assertTrue("grandparent is null", applicationContext.getParent().getParent() == null);
	}

	public void testOverrideWorked() throws Exception {
		TestBean rod = (TestBean) applicationContext.getParent().getBean("rod");
		assertTrue("Parent's name differs", rod.getName().equals("Roderick"));
	}

	public void testGrandparentDefinitionFound() throws Exception {
		TestBean dad = (TestBean) applicationContext.getBean("father");
		assertTrue("Dad has correct name", dad.getName().equals("Albert"));
	}


	public void testGrandparentTypedDefinitionFound() throws Exception {
		TestBean dad = (TestBean) applicationContext.getBean("father", TestBean.class);
		assertTrue("Dad has correct name", dad.getName().equals("Albert"));
	}

	public void testContextOptions() throws Exception {
		assertTrue("contextOptions cannot be null", applicationContext.getOptions() != null);
	}

	public void testMessageSource() throws NoSuchMessageException {
		assertEquals(applicationContext.getMessage("code1", null, Locale.getDefault()), "message1");
		assertEquals(applicationContext.getMessage("code2", null, Locale.getDefault()), "message2");

		try {
			applicationContext.getMessage("code0", null, Locale.getDefault());
			fail("looking for code0 should throw a NoSuchMessageException");
		} catch (NoSuchMessageException ex) {
			// that's how it should be
		}
	}

	public void testNoSuchSharedObject() {
		Object o = applicationContext.sharedObject("foobar");
		assertTrue("No such object is null. No exception was thrown", o == null);
	}

	public void testRetrievesSharedObject() {
		Object bar = new Object();
		applicationContext.shareObject("foo", bar);
		Object o = applicationContext.sharedObject("foo");
		assertTrue("Shared object is found", o != null);
		assertTrue("==", o == bar);
	}

	public void testRemoveSharedObject() {
		Object bar = new Object();
		applicationContext.shareObject("foo", bar);
		Object o = applicationContext.sharedObject("foo");
		assertTrue("Shared object is found", o != null);
		assertTrue("==", o == bar);
		Object removed = applicationContext.removeSharedObject("foo");
		assertTrue("removed == original", removed == bar);
		assertTrue("no longer there", applicationContext.sharedObject("bar") == null);
		assertTrue("no longer there for remove", applicationContext.removeSharedObject("bar") == null);
	}


	// HAD TO COMMENT OUT AS COULDN'T GUARANTEE TO FIND LISTENERS
	public void testEvents() throws Exception {
		listener.zeroCounter();
		parentListener.zeroCounter();
		assertTrue("0 events before publication", listener.getEventCount() == 0);
		assertTrue("0 parent events before publication", parentListener.getEventCount() == 0);
		this.applicationContext.publishEvent(new MyEvent(this));
		assertTrue("1 events after publication, not " + listener.getEventCount(), listener.getEventCount() == 1);
		assertTrue("1 parent events after publication", parentListener.getEventCount() == 1);
	}


	public void testBeanAutomaticallyHearsEvents() throws Exception {
		//String[] listenerNames = ((ListableBeanFactory) applicationContext).getBeanDefinitionNames(ApplicationListener.class);
		//assertTrue("listeners include beanThatListens", Arrays.asList(listenerNames).contains("beanThatListens"));
		BeanThatListens b = (BeanThatListens) applicationContext.getBean("beanThatListens");
		b.zero();
		assertTrue("0 events before publication", b.getEventCount() == 0);
		this.applicationContext.publishEvent(new MyEvent(this));
		assertTrue("1 events after publication, not " + b.getEventCount(), b.getEventCount() == 1);
	}


	public class MyEvent extends ApplicationEvent {

		/**
		 * Constructor for MyEvent.
		 * @param source
		 */
		public MyEvent(Object source) {
			super(source);
		}

	}


}
