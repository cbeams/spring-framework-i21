package com.interface21.beans;

/**
 * Definition for sorting bean instances by a property.
 * @author Juergen Hoeller
 * @since 26.05.2003
 */
public interface SortDefinition {

	/**
	 * Return the name of the property to sort by.
	 */
	String getProperty();

	/**
	 * Return whether upper and lower case in String values should be ignored.
	 */
	boolean isIgnoreCase();

	/**
	 * Return if ascending or descending.
	 */
	boolean isAscending();

}
