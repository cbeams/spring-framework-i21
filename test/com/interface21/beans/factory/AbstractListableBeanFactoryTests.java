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
	public void testCount() {
		assertCount(8);
	}
	
	protected final void assertCount(int count) {
		try {
			String[] defnames = getListableBeanFactory().getBeanDefinitionNames();
			assertTrue("We should have " + count + " beans, not " + defnames.length, defnames.length == count);
			for (int i = 0; i < defnames.length; i++) {
			//	Object o = listableBeanFactory.getBeanInstance(defnames[i]);
				// One gets vetoed
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail("Shouldn't throw exception on getting bean from definition " + ex);
		}
	}

	public void testGetDefinitionsForClass() {
		try {
			String[] defnames = getListableBeanFactory().getBeanDefinitionNames(com.interface21.beans.TestBean.class);
			assertTrue("We should have 7 beans for class com.interface21.beans.TestBean, not " + defnames.length, defnames.length == 7);
			for (int i = 0; i < defnames.length; i++) {
			//	Object o = listableBeanFactory.getBeanInstance(defnames[i]);
				// One gets vetoed
				// CHECK CLASS OF OBJECT
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail("Shouldn't throw exception on getting bean by definition by class: " + ex);
		}
	}
	
	public void testGetDefinitionsForNoSuchClass() {
		String[] defnames = getListableBeanFactory().getBeanDefinitionNames(String.class);
		assertTrue("No string definitions", defnames.length == 0);

	}


}
