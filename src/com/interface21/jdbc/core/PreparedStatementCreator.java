/**
 * Generic framework code included with 
 * <a href="http://www.amazon.com/exec/obidos/tg/detail/-/1861007841/">Expert One-On-One J2EE Design and Development</a>
 * by Rod Johnson (Wrox, 2002). 
 * This code is free to use and modify. 
 * Please contact <a href="mailto:rod.johnson@interface21.com">rod.johnson@interface21.com</a>
 * for commercial support.
 */

package com.interface21.jdbc.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * One of the two central callback interfaces used by the 
 * JdbcTemplate class. This interface creates
 * a PreparedStatement given a connection, provided by the
 * JdbcTemplate class. Implementations are responsible
 * for providing SQL and any necessary parameters.
 * Implementations <i>do not</i> need to concern themselves
 * with SQLExceptions that may be thrown from operations they
 * attempt. The JdbcTemplate class will catch and handle
 * SQLExceptions appropriately.
 * @author Rod Johnson
 */
public interface PreparedStatementCreator {

	/** 
	 * Create a statement in this connection. Allows
	* implementations to use PreparedStatements. Only invoked
	* if no SQL is passed into the ResultSetHandler.
	* The ResultSetHandler will close this statement.
	* @param conn Connection to use to create statement
	* @return a prepared statement
	* @throws SQLException there is no need to catch SQLExceptions
	* that may be thrown in the implementation of this method.
	* The JdbcTemplate class will handle them.
	*/
	PreparedStatement createPreparedStatement(Connection conn) throws SQLException;
	
	String getSql();

}