
package com.interface21.util;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Random;

import junit.framework.TestCase;

/**
 * Example of a test case using introspection to test a large number
 * of similar methods.
 * Checks with quasi-random argument values, and checks that null values
 * can be accepted for non-primitive arguments.
 * @author Rod Johnson
 * @version $Id$
 */
public class ObjectArrayUtilsTests extends TestCase {
	
	private static Random rand = new Random();

	/**
	 * Constructor for ObjectArrayUtilsTests.
	 * @param arg0
	 */
	public ObjectArrayUtilsTests(String arg0) {
		super(arg0);
	}
	
	/**
	 * Use introspection to find what to test/
	 * These will be a public static methods returning Object[].
	 * We look at the types, construct parameters, invoke the method
	 * and check that the return values match.
	 */
	public void testAll() throws Throwable {
		Method[] methods = ObjectArrayUtils.class.getMethods();
		int count = 0;
		for (int i = 0; i < methods.length; i++) {
			if (	Modifier.isPublic(methods[i].getModifiers()) &&
					Modifier.isStatic(methods[i].getModifiers()) ) {
				// Found a target method
				
				try {
					// Try without nulls
					testMethod(methods[i], false);
					// Try with nulls
					testMethod(methods[i], true);
				}
				catch (Throwable t) {
					// Ensure the failure is comprehensible from test report
					System.out.println("Failure in method " + methods[i]);
					throw t;
				}
				++count;
			}
		}
		
		System.out.println(getClass().getName() + " tested " + count + " methods");
	}
	
	// TODO null tests

	/**
	 * Test that this method works. It should return an object array from
	 * primitive parameters. 
	 * @param method public static method from ObjectArrayUtils class
	 * @param try null values for non-primitive objects
	 */
	private void testMethod(Method m, boolean tryNulls) throws Exception {
		//System.out.println(m);
		Object[] args = new Object[m.getParameterTypes().length];
		// Generate random parameter values
		for (int i = 0; i < args.length; i++) {
			args[i] = (tryNulls && !m.getParameterTypes()[i].isPrimitive()) ?
				null :
				randomInstance(m.getParameterTypes()[i]);
		}
		
		// Invoke the method
		Object[] retval = (Object[]) m.invoke(null, args);
		
		// Check that the return values match
		assertTrue(Arrays.equals(args, retval));
	}
	
	/**
	 * Return an object wrapper for random value of the given class
	 * @param clazz
	 * @return
	 */
	public static Object randomInstance(Class clazz) {
		if (clazz.equals(String.class)) {
			return "foo";
		}
		else if (clazz.equals(Integer.class) || clazz.equals(int.class)) {
			return new Integer(rand.nextInt());
		}
		else if (clazz.equals(Short.class) || clazz.equals(short.class)) {
			return new Short((short) rand.nextInt());
		}
		else if (clazz.equals(Long.class) || clazz.equals(long.class)) {
			return new Long(rand.nextLong());
		}
		else if (clazz.equals(Float.class) || clazz.equals(float.class)) {
			return new Float(rand.nextFloat());
		}
		else if (clazz.equals(Double.class) || clazz.equals(double.class)) {
			return new Double(rand.nextDouble());
		}
		else if (clazz.equals(Boolean.class) || clazz.equals(boolean.class)) {
			return new Boolean(rand.nextBoolean());
		}
		else if (clazz.equals(Character.class) || clazz.equals(char.class)) {
			return new Character(';');
		}
		else {
			return new Object();
		}
	}

}
