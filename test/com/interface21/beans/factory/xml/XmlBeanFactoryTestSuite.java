
package com.interface21.beans.factory.xml;

import java.io.InputStream;

import junit.framework.Test;
import junit.framework.TestSuite;


import com.interface21.beans.ITestBean;
import com.interface21.beans.TestBean;
import com.interface21.beans.factory.BeanFactory;
import com.interface21.beans.factory.AbstractListableBeanFactoryTests;
import com.interface21.beans.factory.support.*;

/**
 *
 * @author Rod Johnson
 * @version $RevisionId$
 */
public class XmlBeanFactoryTestSuite extends AbstractListableBeanFactoryTests { 

	private XmlBeanFactory factory;

	public XmlBeanFactoryTestSuite(String name) {
		super(name);
	}

	/** Run for each test */
	protected void setUp() throws Exception {
		//this.listableBeanFactory = new XMLBeanFactory("d:\\book\\project\\i21-framework\\test\\com\\interface21\\beans\\factory\\xml\\test.xml");

		// Load from classpath, NOT a file path
		InputStream is = getClass().getResourceAsStream("test.xml");
		this.factory = new XmlBeanFactory(is);

	}

	/**
	 * @see BeanFactoryTests#getBeanFactory()
	 */
	protected BeanFactory getBeanFactory() {
		return factory;
	}

	/** Uses a separate factory */
	public void testRefToSeparatePrototypeInstances() throws Exception {
		InputStream is = getClass().getResourceAsStream("reftypes.xml");
		XmlBeanFactory xbf = new XmlBeanFactory(is);
		assertTrue("5 beans in reftypes, not " + xbf.getBeanDefinitionCount(), xbf.getBeanDefinitionCount() == 5);
		TestBean emma = (TestBean) xbf.getBean("emma");
		TestBean georgia = (TestBean) xbf.getBean("georgia");
		ITestBean emmasJenks = emma.getSpouse();
		ITestBean georgiasJenks = georgia.getSpouse();
		assertTrue("Emma and georgia think they have a different boyfriend",emmasJenks != georgiasJenks);
		assertTrue("Emmas jenks has right name", emmasJenks.getName().equals("Andrew"));
			assertTrue("Emmas doesn't equal new ref", emmasJenks != xbf.getBean("jenks"));
		assertTrue("Georgias jenks has right name", emmasJenks.getName().equals("Andrew"));
		assertTrue("They are object equal", emmasJenks.equals(georgiasJenks));
		assertTrue("They object equal direct ref", emmasJenks.equals(xbf.getBean("jenks")));
	}
	
	public void testRefToSingleton() throws Exception {
		InputStream is = getClass().getResourceAsStream("reftypes.xml");
		XmlBeanFactory xbf = new XmlBeanFactory(is);
		assertTrue("5 beans in reftypes, not " + xbf.getBeanDefinitionCount(), xbf.getBeanDefinitionCount() == 5);
		TestBean jen = (TestBean) xbf.getBean("jenny");
		TestBean dave = (TestBean) xbf.getBean("david");
		TestBean jenks = (TestBean) xbf.getBean("jenks");
		ITestBean davesJen = dave.getSpouse();
		ITestBean jenksJen = jenks.getSpouse();
		assertTrue("1 jen instances", davesJen == jenksJen);
	}
	
	public void testCustomDefinitionClasses() throws Exception {
		InputStream is = getClass().getResourceAsStream("custom.xml");
		XmlBeanFactory xbf = new XmlBeanFactory(is);
		assertTrue("2 beans in custom, not " + xbf.getBeanDefinitionCount(), xbf.getBeanDefinitionCount() == 2);
		TestBean bob = (TestBean) xbf.getBean("bob");
		TestBean custom = (TestBean) xbf.getBean("custom");
		assertTrue("bob name is Bob", bob.getName().equals("Bob"));
		assertTrue("custom name is Custom", custom.getName().equals("custom"));
		
	//	assertTrue("1 jen instances", davesJen == jenksJen);
	
	} 
	
	/*
	public void testCycles() throws Exception {
		InputStream is = getClass().getResourceAsStream("cycles.xml");
		XmlBeanFactory xbf = new XmlBeanFactory(is);
		assertTrue("6 beans in reftypes, not " + xbf.getBeanDefinitionCount(), xbf.getBeanDefinitionCount() == 6);
		TestBean jen = (TestBean) xbf.getPrototype("jenny");
		TestBean dave = (TestBean) xbf.getPrototype("david");
		TestBean jenks = (TestBean) xbf.getPrototype("jenks");
		TestBean davesJen = dave.getSpouse();
		TestBean jenksJen = jenks.getSpouse();
		assertTrue("1 jen instances", davesJen == jenksJen);
	}
	*/

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
		//	junit.swingui.TestRunner.main(new String[] {PrototypeFactoryTests.class.getName() } );
	}

	public static Test suite() { 
		return new TestSuite(XmlBeanFactoryTestSuite.class);
	}

}
