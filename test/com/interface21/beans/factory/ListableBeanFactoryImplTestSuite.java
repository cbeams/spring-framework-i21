

package com.interface21.beans.factory;

import java.util.Properties;

import junit.framework.TestCase;

import com.interface21.beans.BeansException;
import com.interface21.beans.ITestBean;
import com.interface21.beans.TestBean;
import com.interface21.beans.factory.support.ListableBeanFactoryImpl;


/**
 * This largely tests Properties population:
 * ListableBeanFactoryTestSuite tests basic functionality
 * @author Rod Johnson
 * @version $RevisionId$
 */
public class ListableBeanFactoryImplTestSuite extends TestCase {

	public ListableBeanFactoryImplTestSuite(String name) {
		super(name);
	}
	
	public void testUnreferencedSingletonWasInstantiated() {
		KnowsIfInstantiated.clearInstantiationRecord();
		ListableBeanFactoryImpl lbf = new ListableBeanFactoryImpl();
		
		lbf = new ListableBeanFactoryImpl();
		Properties p = new Properties();
		p.setProperty("x1.class", KnowsIfInstantiated.class.getName());
		assertTrue("singleton not instantiated", !KnowsIfInstantiated.wasInstantiated());
		lbf.registerBeanDefinitions(p, null);
		lbf.preInstantiateSingletons();
		assertTrue("Singleton was instantiated", KnowsIfInstantiated.wasInstantiated());
	} 
	
	public void testEmpty() {		
		ListableBeanFactory lbf = new ListableBeanFactoryImpl();
		assertTrue("No beans defined --> array != null", lbf.getBeanDefinitionNames() != null);
		assertTrue("No beans defined after no arg constructor", lbf.getBeanDefinitionNames().length == 0);
		assertTrue("No beans defined after no arg constructor", lbf.getBeanDefinitionCount() == 0);
	}
	
	public void testEmptyPropertiesPopulation() throws BeansException {		
		ListableBeanFactoryImpl lbf = new ListableBeanFactoryImpl();
		Properties p = new Properties();
		lbf.registerBeanDefinitions(p, null);
		assertTrue("No beans defined after ignorable invalid", lbf.getBeanDefinitionCount() == 0);
	}
	
	public void testHarmlessIgnorableRubbish() throws BeansException {		
		ListableBeanFactoryImpl lbf = new ListableBeanFactoryImpl();
		Properties p = new Properties();
		p.setProperty("foo", "bar");
		p.setProperty("qwert", "er");
		lbf.registerBeanDefinitions(p, "test");
		assertTrue("No beans defined after harmless ignorable rubbish", lbf.getBeanDefinitionCount() == 0);
	}
	

	public void testPropertiesPopulationWithNullPrefix() throws Exception {		
		ListableBeanFactoryImpl lbf = new ListableBeanFactoryImpl();
		Properties p = new Properties();
		p.setProperty("test.class", "com.interface21.beans.TestBean");
		p.setProperty("test.name", "Tony");
		p.setProperty("test.age", "48");
		//p.setProperty("
		int count = lbf.registerBeanDefinitions(p, null);
		assertTrue("1 beans registered, not " + count, count == 1);
		testSingleTestBean(lbf);
	}
	
	public void testPropertiesPopulationWithPrefix() throws Exception {		
		String PREFIX = "beans.";
		ListableBeanFactoryImpl lbf = new ListableBeanFactoryImpl();
		Properties p = new Properties();
		p.setProperty(PREFIX + "test.class", "com.interface21.beans.TestBean");
		p.setProperty(PREFIX + "test.name", "Tony");
		p.setProperty(PREFIX + "test.age", "48");
		//p.setProperty("
		int count = lbf.registerBeanDefinitions(p, PREFIX);
		assertTrue("1 beans registered, not " + count, count == 1);
		testSingleTestBean(lbf);
	} 
	
	
	/** Test introduced to fix bug at end of processing similar names
	 */
	public void testSimilarNames() throws Exception {		
		ListableBeanFactoryImpl lbf = new ListableBeanFactoryImpl();
		Properties p = new Properties();
		//p.setProperty("XintResultView.class", "com.interface21.framework.web.servlet.view.XSLTView");
		//p.setProperty("XintResultView.stylesheet", "foobar");
		//p.setProperty("XintResultView2.class", "com.interface21.framework.web.servlet.view.XSLTView");

		//p.setProperty("
		//int count = lbf.registerBeanDefinitions(p, null);
		//assertTrue("2 beans registered, not " + count, count == 2);
	//	assertTrue("XintResultView is an XSLT view", lbf.getPrototype("XintResultView") instanceof XSLTView);
	//	assertTrue("XintResultView2 is an XSLT view", lbf.getPrototype("XintResultView2") instanceof XSLTView);
		
	} 
	
	public void testSimpleReference() throws Exception {		
		String PREFIX = "beans.";
		ListableBeanFactoryImpl lbf = new ListableBeanFactoryImpl();
		Properties p = new Properties();
		
		p.setProperty(PREFIX + "rod.class", "com.interface21.beans.TestBean");
		p.setProperty(PREFIX + "rod.name", "Rod");
		
		p.setProperty(PREFIX + "kerry.class", "com.interface21.beans.TestBean");
		p.setProperty(PREFIX + "kerry.class", "com.interface21.beans.TestBean");
		p.setProperty(PREFIX + "kerry.name", "Kerry");
		p.setProperty(PREFIX + "kerry.age", "35");
		p.setProperty(PREFIX + "kerry.spouse(ref)", "rod");
		//p.setProperty("
		int count = lbf.registerBeanDefinitions(p, PREFIX);
		assertTrue("2 beans registered, not " + count, count == 2);
		
		TestBean kerry = (TestBean) lbf.getBean("kerry", TestBean.class);
		assertTrue("Kerry name is Kerry", "Kerry".equals(kerry.getName()));
		ITestBean spouse = kerry.getSpouse();
		assertTrue("Kerry spouse is non null", spouse != null);
		assertTrue("Kerry spouse name is Rod", "Rod".equals(spouse.getName()));
	} 
	
	public void testUnresolvedReference() throws Exception {		
		String PREFIX = "beans.";
		ListableBeanFactoryImpl lbf = new ListableBeanFactoryImpl();
		Properties p = new Properties();
		
		//p.setProperty(PREFIX + "rod.class", "com.interface21.beans.TestBean");
		//p.setProperty(PREFIX + "rod.name", "Rod");
		
		try {
			p.setProperty(PREFIX + "kerry.class", "com.interface21.beans.TestBean");
			p.setProperty(PREFIX + "kerry.class", "com.interface21.beans.TestBean");
			p.setProperty(PREFIX + "kerry.name", "Kerry");
			p.setProperty(PREFIX + "kerry.age", "35");
			p.setProperty(PREFIX + "kerry.spouse(ref)", "rod");
			
			lbf.registerBeanDefinitions(p, PREFIX);
			
			Object kerry = lbf.getBean("kerry");
			fail ("Unresolved reference should have been detected");
		}
		catch (BeansException ex) {
			// cool
		}
	}
	
	
	public void testPrototype() throws Exception {		
		ListableBeanFactoryImpl lbf = new ListableBeanFactoryImpl();
		Properties p = new Properties();
		p.setProperty("kerry.class", "com.interface21.beans.TestBean");
		p.setProperty("kerry.age", "35");
		lbf.registerBeanDefinitions(p, null);
		TestBean kerry1 = (TestBean) lbf.getBean("kerry");
		TestBean kerry2 = (TestBean) lbf.getBean("kerry");
		assertTrue("Non null", kerry1 != null);
		assertTrue("Singletons equal", kerry1 == kerry2);
		
		lbf = new ListableBeanFactoryImpl();
		p = new Properties();
		p.setProperty("kerry.class", "com.interface21.beans.TestBean");
		p.setProperty("kerry.(singleton)", "false");
		p.setProperty("kerry.age", "35");
		lbf.registerBeanDefinitions(p, null);
		kerry1 = (TestBean) lbf.getBean("kerry");
		kerry2 = (TestBean) lbf.getBean("kerry");
		assertTrue("Non null", kerry1 != null);
		assertTrue("Prototypes NOT equal", kerry1 != kerry2);
		
		lbf = new ListableBeanFactoryImpl();
		p = new Properties();
		p.setProperty("kerry.class", "com.interface21.beans.TestBean");
		p.setProperty("kerry.(singleton)", "true");
		p.setProperty("kerry.age", "35");
		lbf.registerBeanDefinitions(p, null);
		kerry1 = (TestBean) lbf.getBean("kerry");
		kerry2 = (TestBean) lbf.getBean("kerry");
		assertTrue("Non null", kerry1 != null);
		assertTrue("Specified singletons equal", kerry1 == kerry2);
	}
	
	
	/*
	public void testInvalidBeanDefinition() throws Exception {		
		ListableBeanFactoryImpl lbf = new ListableBeanFactoryImpl();
		Properties p = new Properties();
		p.setProperty("test.class", "com.interface21.beans.TestBean");
		p.setProperty("test.name", "Tony");
		p.setProperty("test.age", "48");
		//p.setProperty("
		int count = lbf.registerBeanDefinitions(p, null);
		assertTrue("1 beans registered", count == 1);
		testSingleTestBean(lbf);
	}
	 **/
	
	private void testSingleTestBean(ListableBeanFactory lbf) throws BeansException {
		assertTrue("1 beans defined", lbf.getBeanDefinitionCount() == 1);
		String[] names = lbf.getBeanDefinitionNames();
		assertTrue("Array length == 1", names.length == 1);
		assertTrue("0th element == test", names[0].equals("test"));
		TestBean tb = (TestBean) lbf.getBean("test");
		assertTrue("Test is non null", tb != null);
		assertTrue("Test bean name is Tony", "Tony".equals(tb.getName()));
		assertTrue("Test bean age is 48", tb.getAge() == 48);
	}
	
	public void testBeanReferenceWithNewSyntax() {
		ListableBeanFactoryImpl lbf = new ListableBeanFactoryImpl();
		lbf = new ListableBeanFactoryImpl();
		Properties p = new Properties();
		p.setProperty("r.class", TestBean.class.getName());
		p.setProperty("r.name", "rod");
		p.setProperty("k.class", TestBean.class.getName());
		p.setProperty("k.name", "kerry");
		p.setProperty("k.spouse", "*r");
		lbf.registerBeanDefinitions(p, null);
		TestBean k = (TestBean) lbf.getBean("k");
		TestBean r = (TestBean) lbf.getBean("r");
		assertTrue(k.getSpouse() == r);
	} 
	
	public void testCanEscapeBeanReferenceSyntax() {
		String name = "*name";
		ListableBeanFactoryImpl lbf = new ListableBeanFactoryImpl();
		lbf = new ListableBeanFactoryImpl();
		Properties p = new Properties();
		p.setProperty("r.class", TestBean.class.getName());
		p.setProperty("r.name", "*" + name);
		lbf.registerBeanDefinitions(p, null);
		TestBean r = (TestBean) lbf.getBean("r");
		assertTrue(r.getName().equals(name));
	}


}
