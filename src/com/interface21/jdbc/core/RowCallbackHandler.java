/**
 * Generic framework code included with 
 * <a href="http://www.amazon.com/exec/obidos/tg/detail/-/1861007841/">Expert One-On-One J2EE Design and Development</a>
 * by Rod Johnson (Wrox, 2002). 
 * This code is free to use and modify. 
 * Please contact <a href="mailto:rod.johnson@interface21.com">rod.johnson@interface21.com</a>
 * for commercial support.
 */

package com.interface21.jdbc.core;

import java.sql.ResultSet;
import java.sql.SQLException;

/** 
* One of the two central callback interfaces used by the JdbcTemplate class.
* Implementations of this interface
* perform the actual work of extracting results,
* but don't need to worry about exception handling. SQLexceptions
* will be caught and handled correctly by the JdbcTemplate class.
* @author Rod Johnson
*/
public interface RowCallbackHandler {
	
	/** 
	* Implementations must implement this method to process
	* each row of data in the ResultSet. This method should not call
	* next() on the ResultSet, but extract the current values.
	* Exactly what the implementation chooses to do is up to it;
	* a trivial implementation might simply count rows, while another
	* implementation might build an XML document.
	* @param ResultSet
	* @throws SQLException if a SQLException is encountered getting
	* column values (that is, there's no need to catch SQLException)
	*/
	void processRow(ResultSet rs) throws SQLException; 

}
