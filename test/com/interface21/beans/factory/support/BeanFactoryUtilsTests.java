/*
 * The Spring Framework is published under the terms
 * of the Apache Software License.
 */
 
package com.interface21.beans.factory.support;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import com.interface21.beans.ITestBean;
import com.interface21.beans.TestBean;
import com.interface21.beans.factory.ListableBeanFactory;
import com.interface21.context.support.ClassPathXmlApplicationContext;
import com.interface21.web.servlet.HandlerAdapter;
import com.interface21.web.servlet.mvc.DemoController;

/**
 * 
 * @author Rod Johnson
 * @since 04-Jul-2003
 * @version $Id$
 */
public class BeanFactoryUtilsTests extends TestCase {

	private final static String BASE_PATH = "com/interface21/beans/factory/support/";
	
	private ListableBeanFactory listableFactory;
	
	/**
	 * Constructor for BeanFactoryUtilsTest.
	 * @param arg0
	 */
	public BeanFactoryUtilsTests(String arg0) throws Exception {
		super(arg0);
		// Interesting hierarchical factory to test counts
		// Slow to read so we cache it
		this.listableFactory = new ClassPathXmlApplicationContext(new String[] {BASE_PATH + "root.xml", BASE_PATH + "middle.xml", BASE_PATH + "leaf.xml"});
	}
	
	public void testNoBeansOfType() {
		StaticListableBeanFactory lbf = new StaticListableBeanFactory();
		lbf.addBean("foo", new Object());
		List l = BeanFactoryUtils.beansOfType(ITestBean.class, lbf);
		assertTrue(l.isEmpty());
	}
	
	public void testFindsBeansOfType() {
		StaticListableBeanFactory lbf = new StaticListableBeanFactory();
		lbf.addBean("t1", new TestBean());
		lbf.addBean("t2", new TestBean());
		List l = BeanFactoryUtils.beansOfType(ITestBean.class, lbf);
		assertTrue(l.size() == 2);
		assertTrue(l.get(0) instanceof ITestBean);
		assertTrue(l.get(1) instanceof ITestBean);
		// Hierarchical find should produce fine results as there's no hierarchy
		assertTrue(BeanFactoryUtils.beansOfTypeIncludingAncestors(ITestBean.class, lbf).equals(l));
	}
	
	
	
	public void testHierarchicalCountBeansWithNonHierarchicalFactory() {
		StaticListableBeanFactory lbf = new StaticListableBeanFactory();
		lbf.addBean("t1", new TestBean());
		lbf.addBean("t2", new TestBean());
		assertTrue(BeanFactoryUtils.countBeansIncludingAncestors(lbf) == 2);
	}
	
	/**
	 * Check that override doesn't count as too separate beans
	 * @throws Exception
	 */
	public void testHierarchicalCountBeansWithOverride() throws Exception {
		// Leaf count
		assertTrue(this.listableFactory.getBeanDefinitionCount() == 1);
		// Count minus duplicate
		assertTrue("Should count 5 beans, not " + BeanFactoryUtils.countBeansIncludingAncestors(this.listableFactory),
			BeanFactoryUtils.countBeansIncludingAncestors(this.listableFactory) == 5);
	}
	
	public void testHierarchicalNamesWithOverride() throws Exception {
		Collection c = BeanFactoryUtils.beanNamesIncludingAncestors(ITestBean.class, this.listableFactory);
		assertTrue(c.contains("test"));
		assertTrue(c.contains("test3"));
		assertTrue(c.size() == 2);
	}
	
	public void testHierarchicalResolutionWithOverride() throws Exception {
		Collection c = BeanFactoryUtils.beansOfTypeIncludingAncestors(ITestBean.class, this.listableFactory);
		assertTrue(c.size() == 2);
		Iterator iter = c.iterator();
		assertTrue(iter.next() instanceof ITestBean);
		assertTrue(iter.next() instanceof ITestBean);
	}
	
	public void testHierarchicalNamesWithNoMatch() throws Exception {
		Collection c = BeanFactoryUtils.beanNamesIncludingAncestors(HandlerAdapter.class, this.listableFactory);
		assertTrue("Expected 0 beans, not " + c.size(), c.size() == 0);
	}
	
	public void testHierarchicalNamesWithMatchOnlyInRoot() throws Exception {
		Collection c = BeanFactoryUtils.beanNamesIncludingAncestors(DemoController.class, this.listableFactory);
		assertTrue("Expected 0 beans, not " + c.size(), c.size() == 1);
		assertTrue(c.contains("demoController"));
		
		// Distinguish from default ListableBeanFactory behaviour
		assertTrue(listableFactory.getBeanDefinitionNames(DemoController.class).length == 0);
	}

}
