package com.interface21.beans;

import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.interface21.beans.propertyeditors.CustomDateEditor;

/**
 * @author Juergen Hoeller
 * @since 19.05.2003
 */
public class BeanUtilsTests extends TestCase {

	public BeanUtilsTests(String msg) {
		super(msg);
	}

	public void testInstantiateClass() {
		// give proper class
		BeanUtils.instantiateClass(ArrayList.class);

		try {
			// give interface
			BeanUtils.instantiateClass(List.class);
			fail("Should have thrown FatalBeanException");
		}
		catch (FatalBeanException ex) {
			// expected
		}

		try {
			// give class without default constructor
			BeanUtils.instantiateClass(CustomDateEditor.class);
			fail("Should have thrown FatalBeanException");
		}
		catch (FatalBeanException ex) {
			// expected
		}
	}

	public void testCopyProperties() throws Exception {
		TestBean tb = new TestBean();
		tb.setName("rod");
		tb.setAge(32);
		tb.setTouchy("touchy");
		TestBean tb2 = new TestBean();
		assertTrue("Name empty", tb2.getName() == null);
		assertTrue("Age empty", tb2.getAge() == 0);
		assertTrue("Touchy empty", tb2.getTouchy() == null);

		try {
			BeanUtils.copyProperties(tb, "");
			fail("Should have thrown IllegalArgumentException");
		}
		catch (IllegalArgumentException ex) {
			// expected
		}

		BeanUtils.copyProperties(tb, tb2);
		assertTrue("Name copied", tb2.getName().equals(tb.getName()));
		assertTrue("Age copied", tb2.getAge() == tb.getAge());
		assertTrue("Touchy copied", tb2.getTouchy().equals(tb.getTouchy()));
	}

	public void testCopyPropertiesWithIgnore() throws IllegalAccessException, PropertyVetoException {
		TestBean tb = new TestBean();
		tb.setName("rod");
		tb.setAge(32);
		TestBean tb2 = new TestBean();
		assertTrue("Name empty", tb2.getName() == null);
		assertTrue("Age empty", tb2.getAge() == 0);
		assertTrue("Touchy empty", tb2.getTouchy() == null);

		BeanUtils.copyProperties(tb, tb2, new String[] {"spouse", "touchy", "age"});
		assertTrue("Name copied", tb2.getName().equals(tb.getName()));
		assertTrue("Age still empty", tb2.getAge() == 0);
		assertTrue("Touchy still empty", tb2.getTouchy() == null);
	}

	/*
	public void testSortByPropertyWithList() {
		TestBean tb1 = new TestBean();
		tb1.setName("eva");
		tb1.setAge(25);
		TestBean tb2 = new TestBean();
		tb2.setName("juergen");
		tb2.setAge(99);
		TestBean tb3 = new TestBean();
		tb3.setName("Rod");
		tb3.setAge(32);
		List tbs = new ArrayList();
		tbs.add(tb1);
		tbs.add(tb2);
		tbs.add(tb3);

		BeanUtils.sortByProperty(tbs, "name", true, false);
		assertTrue("Correct 1. entry", tbs.get(0) == tb3);
		assertTrue("Correct 2. entry", tbs.get(1) == tb1);
		assertTrue("Correct 3. entry", tbs.get(2) == tb2);

		BeanUtils.sortByProperty(tbs, "name", false, false);
		assertTrue("Correct 1. entry", tbs.get(0) == tb2);
		assertTrue("Correct 2. entry", tbs.get(1) == tb1);
		assertTrue("Correct 3. entry", tbs.get(2) == tb3);

		BeanUtils.sortByProperty(tbs, "name", true, true);
		assertTrue("Correct 1. entry", tbs.get(0) == tb1);
		assertTrue("Correct 2. entry", tbs.get(1) == tb2);
		assertTrue("Correct 3. entry", tbs.get(2) == tb3);

		BeanUtils.sortByProperty(tbs, "age", false, false);
		assertTrue("Correct 1. entry", tbs.get(0) == tb2);
		assertTrue("Correct 2. entry", tbs.get(1) == tb3);
		assertTrue("Correct 3. entry", tbs.get(2) == tb1);
	}

	public void testSortByPropertyWithArray() {
		TestBean tb1 = new TestBean();
		tb1.setName("eva");
		tb1.setAge(25);
		TestBean tb2 = new TestBean();
		tb2.setName("juergen");
		tb2.setAge(99);
		TestBean tb3 = new TestBean();
		tb3.setName("Rod");
		tb3.setAge(32);
		List tbs = new ArrayList();
		tbs.add(tb1);
		tbs.add(tb2);
		tbs.add(tb3);

		Object[] sorted = BeanUtils.sortByProperty(tbs.toArray(), "name", true, false);
		assertTrue("Correct 1. entry", sorted[0] == tb3);
		assertTrue("Correct 2. entry", sorted[1] == tb1);
		assertTrue("Correct 3. entry", sorted[2] == tb2);

		sorted = BeanUtils.sortByProperty(tbs.toArray(), "name", false, false);
		assertTrue("Correct 1. entry", sorted[0] == tb2);
		assertTrue("Correct 2. entry", sorted[1] == tb1);
		assertTrue("Correct 3. entry", sorted[2] == tb3);

		sorted = BeanUtils.sortByProperty(tbs.toArray(), "name", true, true);
		assertTrue("Correct 1. entry", sorted[0] == tb1);
		assertTrue("Correct 2. entry", sorted[1] == tb2);
		assertTrue("Correct 3. entry", sorted[2] == tb3);

		sorted = BeanUtils.sortByProperty(tbs.toArray(), "age", false, false);
		assertTrue("Correct 1. entry", sorted[0] == tb2);
		assertTrue("Correct 2. entry", sorted[1] == tb3);
		assertTrue("Correct 3. entry", sorted[2] == tb1);
	}
	*/

}
