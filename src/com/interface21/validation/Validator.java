/*
 * Generic framework code included with 
 * <a href="http://www.amazon.com/exec/obidos/tg/detail/-/1861007841/">Expert One-On-One J2EE Design and Development</a>
 * by Rod Johnson (Wrox, 2002). 
 * This code is free to use and modify. However, please
 * acknowledge the source and include the above URL in each
 * class using or derived from this code. 
 * Please contact <a href="mailto:rod.johnson@interface21.com">rod.johnson@interface21.com</a>
 * for commercial support.
 */
 
 
package com.interface21.validation;

/**
 * Interface to be implemented by objects that can validate
 * application-specific objects. This enables validation to
 * be decoupled from the interface and placed in business objects.
 * @author Rod Johnson
 */
public interface Validator {
	
	/**
	 * Return whether or not this object can validate objects
	 * of the given class.
	 */
	boolean supports(Class clazz);
	
	/**
	 * Validate an object, which must be of a class for which
	 * the supports() method returned true.
	 * @param obj  Populated object to validate
	 * @param errors  Errors object we're building. May contain
	 * errors for this field relating to types.
	 */
	void validate(Object obj, Errors errors);

}
