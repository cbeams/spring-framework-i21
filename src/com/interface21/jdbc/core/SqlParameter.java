/**
 * Generic framework code included with 
 * <a href="http://www.amazon.com/exec/obidos/tg/detail/-/1861007841/">Expert One-On-One J2EE Design and Development</a>
 * by Rod Johnson (Wrox, 2002). 
 * This code is free to use and modify. 
 * Please contact <a href="mailto:rod.johnson@interface21.com">rod.johnson@interface21.com</a>
 * for commercial support.
 */

package com.interface21.jdbc.core;

/**
 * Object to represent a SQL parameter definition.
 * Parameters may be anonymous, in which case name is null.
 * However all parameters must define a SQL type constant
 * from java.sql.Types.
 * @author Rod Johnson
 */
public class SqlParameter {

	private String name;
	
	/** SQL type constant from java.sql.Types */
	private int type;

	
	/**
	 * Add a new anonymous parameter
	 */
	public SqlParameter(int type) {
		this(null, type);
	}

	public SqlParameter(String name, int type) {
		this.name = name;
		this.type = type;
	}
	
	public String getName() {
		return name;
	}
	
	public int getSqlType() {
		return type;
	}

}