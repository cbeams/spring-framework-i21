/*
 * TestConfigBean.java
 *
 * Created on 14 December 2001, 18:14
 */

package com.interface21.web.context;

/**
 *
 * @author  rod
 * @version 
 */
public class TestConfigBean {

	/** Holds value of property name. */
	private String name;
	
	/** Holds value of property age. */
	private int age;
	
	/** Creates new TestConfigBean */
    public TestConfigBean() {
    }

	/** Getter for property name.
	 * @return Value of property name.
	 */
	public String getName() {
		return name;
	}
	
	/** Setter for property name.
	 * @param name New value of property name.
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/** Getter for property age.
	 * @return Value of property age.
	 */
	public int getAge() {
		return age;
	}
	
	/** Setter for property age.
	 * @param age New value of property age.
	 */
	public void setAge(int age) {
		this.age = age;
	}
	
}
