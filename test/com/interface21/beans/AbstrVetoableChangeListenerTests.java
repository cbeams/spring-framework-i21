
package com.interface21.beans;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;

import junit.framework.TestCase;

/**
 * Must not bein with Abstract or wildcard will exclude
 * @author Rod Johnson
 */
public class AbstrVetoableChangeListenerTests extends TestCase {
	
	private static final int MAX_AGE = 65;

	/**
	 * Constructor for AbstractVetoableChangeListenerTests.
	 * @param arg0
	 */
	public AbstrVetoableChangeListenerTests(String arg0) {
		super(arg0);
	}
	
	public static class MyListener extends AbstractVetoableChangeListener {
		public void validateAge(int age, PropertyChangeEvent e) throws PropertyVetoException {
			assertTrue(e != null);
			assertTrue(e.getPropertyName().equals("age"));
			if (age > MAX_AGE)
				throw new PropertyVetoException("too old", e);
		}
		
		public void validateName(String name, PropertyChangeEvent e) throws PropertyVetoException {
			assertTrue(e.getPropertyName().equals("name"));
			if (name == null)
				throw new PropertyVetoException("must provide name", e);
		}
		
		// Wrong sig, not invoked
		public void validateName(String name) {
			throw new IllegalStateException();
		}
	}
	
	public void testDirectValidation() throws Exception {
		MyListener l = new MyListener();
		TestBean tb = new TestBean();
		PropertyChangeEvent e = new PropertyChangeEvent(tb, "age", new Integer(tb.getAge()), new Integer(MAX_AGE - 1));
		// Ok
		l.vetoableChange(e);
		e = new PropertyChangeEvent(tb, "age", new Integer(tb.getAge()), new Integer(MAX_AGE + 1));
		try {
			l.vetoableChange(e);
			fail();
		}
		catch (PropertyVetoException ex) {
			// Ok
		}
	}
	
	public void testValidationThroughBeanWrapper() throws Exception {
		MyListener l = new MyListener();
		TestBean tb = new TestBean();
		BeanWrapper bw = new BeanWrapperImpl(tb);
		// Will work fine
		bw.setPropertyValue("age", new Integer(MAX_AGE + 1));
		// Old value must be different or attempt to set to same will be ignored
		tb.setAge(-1);
		
		bw.setEventPropagationEnabled(true);
		bw.addVetoableChangeListener(l);
		try {
			bw.setPropertyValue("age", new Integer(MAX_AGE + 1));
			fail();
		}
		catch (PropertyVetoException ex) {
			// Ok
		}
		
		// Ok
		bw.setPropertyValue("age", new Integer(MAX_AGE - 1));
		bw.setPropertyValue("name", "tony");
		try {
			bw.setPropertyValue("name", null);
			fail();
		}
		catch (PropertyVetoException ex) {
			// Ok
		}
	}

}
