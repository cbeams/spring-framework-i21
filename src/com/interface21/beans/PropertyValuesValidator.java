
package com.interface21.beans;

/**
 * Interface that can be implemented by application code that needs
 * to validate PropertyValues. If an implementation of this
 * interface is supplied, it will be invoked by a BeanWrapper implementation's
 * setPropertyValues() method.
 * @author  Rod Johnson
 * @version $Revision$ 
 */
public interface PropertyValuesValidator {
	
	/** 
	 * Are these PropertyValues invalid? For example,
	 * are required properties missing? Does the presence of a particular
	 * property require others to be present? Implementations can
	 * rely on the contains() method of the PropertyValues interface, but cannot
	 * assume anything about the type of properties: type conversion is done only later,
	 * when PropertyValues are applied to a bean using a BeanWrapper object.
	 * @param pvs PropertyValues to validate
	 * @throws InvalidPropertyValuesException if the PropertyValues object is invalid
	 */
	void validatePropertyValues(PropertyValues pvs) throws InvalidPropertyValuesException;

}

