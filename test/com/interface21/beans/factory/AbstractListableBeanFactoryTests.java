/*
 * BeanWrapperTestSuite.java
 *
 * Created on 1 September 2001, 19:35
 */

package com.interface21.beans.factory;


/**
 *
 * @author Rod Johnson
 * @version $RevisionId$
 */
public abstract class AbstractListableBeanFactoryTests extends AbstractBeanFactoryTests {

	/** Subclasses must initialize this */
	protected ListableBeanFactory getListableBeanFactory() {
		BeanFactory bf = getBeanFactory();
		if (!(bf instanceof ListableBeanFactory))
			throw new RuntimeException("ListableBeanFactory required");
		return (ListableBeanFactory) bf;
	}
	
	public AbstractListableBeanFactoryTests(String name) {
		super(name);
	}
	
	/**
	 * Subclasses can override
	 */
	public void testCount() throws Exception {
		assertCount(13);
	}
	
	protected final void assertCount(int count) throws Exception {
		String[] defnames = getListableBeanFactory().getBeanDefinitionNames();
		assertTrue("We should have " + count + " beans, not " + defnames.length, defnames.length == count);
		for (int i = 0; i < defnames.length; i++) {
		}
	}

	public void testGetDefinitionsForClass() throws Exception {
		String[] defnames = getListableBeanFactory().getBeanDefinitionNames(com.interface21.beans.TestBean.class);
		assertTrue("We should have 7 beans for class com.interface21.beans.TestBean, not " + defnames.length, defnames.length == 7);
		for (int i = 0; i < defnames.length; i++) {
			// CHECK CLASS OF OBJECT
		}
	}
	
	public void testGetDefinitionsForNoSuchClass() {
		String[] defnames = getListableBeanFactory().getBeanDefinitionNames(String.class);
		assertTrue("No string definitions", defnames.length == 0);
	}
	
	/**
	 * Check that count refers to factory class, not
	 * bean class (we don't know what type factories may return,
	 * and it may even change over time).
	 */
	public void testGetCountForFactoryClass() {
		assertTrue("Should have 3 factories, not " + getListableBeanFactory().getBeanDefinitionNames(FactoryBean.class).length, 
			getListableBeanFactory().getBeanDefinitionNames(FactoryBean.class).length == 3);
	}

}
