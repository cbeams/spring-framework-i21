/*
 * The Spring Framework is published under the terms
 * of the Apache Software License.
 */
 
package com.interface21.util;

import junit.framework.TestCase;

/**
 * @author Rod Johnson
 * @since 28-Apr-2003
 * @version $Revision$
 */
public class ConstantsTests extends TestCase {

	public void testA() {
		Constants c = new Constants(A.class);
		assertTrue(c.getSize() == 3);
		
		assertTrue(c.asInt("DOG") == A.DOG);
		assertTrue(c.asInt("dog") == A.DOG);
		assertTrue(c.asInt("cat") == A.CAT);
		try {
			c.asInt("bogus");
			fail("Can't get bogus field");
		}
		catch (ConstantException ex) {
		}
		assertTrue(c.asString("S1").equals(A.S1));
		try {
			c.asInt("S1");
			fail("Wrong type");
		}
		catch (ConstantException ex) {
		}
	}
	
	
	public static class A {
		
		public static final int DOG = 0;
		public static final int CAT = 66;
		public static final String S1 = "";
		
		/** ignore these */
		protected static final int P = -1;
		protected boolean f;
		static final Object o = new Object();
	}

}
