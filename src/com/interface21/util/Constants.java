/*
 * The Spring Framework is published under the terms
 * of the Apache Software License.
 */

package com.interface21.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 * This class can be used to parse other classes containing constant definitions
 * in public static final members. The asXXXX() methods of this class allow these
 * constant values to be accessed via their string names.
 *
 * <p>Consider class Foo containing public final static int CONSTANT1 = 66;
 * An instance of this class wrapping Foo.class will return the 
 * constant value of 66 from its asInt() method given the argument "CONSTANT1". 
 *
 * <p>This class is ideal for use in PropertyEditors, enabling them to recognize
 * the same names as the constants themselves, and freeing them from
 * maintaining their own mapping.
 *
 * <p>TODO: add asBoolean, asDouble methods, keys method
 *
 * @version $Id$
 * @author Rod Johnson
 * @since 16-Mar-2003
 */
public class Constants {

	/** Map from String field name to object value */
	private Map map = new HashMap();

	/** Class analyzed */
	private final Class clazz;

	/**
	 * Create a new Constants converter class wrapping the given class.
	 * All public static final variables will be exposed, whatever their type.
	 * @param clazz class to analyze.
	 */
	public Constants(Class clazz) {
		this.clazz = clazz;
		Field[] fields = clazz.getFields();
		for (int i = 0; i < fields.length; i++) {
			Field f = fields[i];
			if (Modifier.isFinal(f.getModifiers())
				&& Modifier.isStatic(f.getModifiers())
				&& Modifier.isPublic(f.getModifiers())) {
				String name = f.getName();
				try {
					Object value = f.get(null);
					map.put(name, value);
				}
				catch (IllegalAccessException ex) {
					// Just leave this field and continue
				}
			}
		}
	} // constructor

	/**
	 * Return the number of constants exposed
	 * @return int the number of constants exposed
	 */
	public int getSize() {
		return this.map.size();
	}


	//public String getKeys() {
	//	throw new UnsupportedOperationException();
	//}

	/**
	 * Return a constant value cast to an int
	 * @param code name of the field
	 * @return int int value if successfuly
	 * @see #asObject
	 * @throws ConstantException if the field name wasn't found or
	 * if the type wasn't compatible with int
	 */
	public int asInt(String code) throws ConstantException {
		Object o = asObject(code);
		if (!(o instanceof Integer))
			throw new ConstantException(code, this.clazz, "not an int");
		return ((Integer) o).intValue();
	}

	/**
	 * Return a constant value as a String
	 * @param code name of the field
	 * @return String string value if successful.
	 * Works even if it's not a string (invokes toString()).
	 * @see #asObject
	 * @throws ConstantException if the field name wasn't found
	 */
	public String asString(String code) throws ConstantException {
		Object o = asObject(code);
		return o.toString();
	}

	/**
	 * Parse the given string (upper or lower case accepted) and return 
	 * the appropriate value if it's the name of a constant field in the
	 * class we're analysing.
	 * @throws ConstantException if there's no such field
	 */
	public Object asObject(String code) throws ConstantException {
		code = code.toUpperCase();
		Object val = this.map.get(code);
		if (val == null)
			throw new ConstantException(code, this.clazz, "not found");
		return val;
	}

}
