package com.interface21.jdbc.core;

import java.sql.SQLException;

import junit.framework.TestCase;

/**
 * 
 * @author Rod Johnson
 * @since 13-Jan-03
 */
public class SqlStateExceptionTranslaterTestSuite extends TestCase {
	
	private SQLStateSQLExceptionTranslater trans = new SQLStateSQLExceptionTranslater();


	/**
	 * Constructor for SqlStateExceptionTranslaterTestSuite.
	 * @param arg0
	 */
	public SqlStateExceptionTranslaterTestSuite(String arg0) {
		super(arg0);
	}

	// ALSO CHECK CHAIN of SQLExceptions!?
	
	// also allow chain of translaters? default if can't do specific?
	
	
	public void testBadSqlGrammar() {
		String sql = "SELECT FOO FROM BAR";
		SQLException sex = new SQLException("Message", "42001", 1);
		try {
			throw this.trans.translate("task", sql, sex);
		}
		catch (BadSqlGrammarException ex) {
			// OK
			assertTrue("SQL is correct", sql.equals(ex.getSql()));
			assertTrue("Exception matches", sex.equals(ex.getSQLException()));
		}
	}
	
	
	public void testInvalidSqlStateCode() {
		String sql = "SELECT FOO FROM BAR";
		SQLException sex = new SQLException("Message", "NO SUCH CODE", 1);
		try {
			throw this.trans.translate("task", sql, sex);
		}
		catch (UncategorizedSQLException ex) {
			// OK
			assertTrue("SQL is correct", sql.equals(ex.getSql()));
			assertTrue("Exception matches", sex.equals(ex.getSQLException()));
		}
	}
	
	/**
	 * PostgreSQL can return null
	 * SAP DB can apparently return empty SQL code
	 * Bug 729170 
	 */
	public void testMalformedSqlStateCodes() {
		String sql = "SELECT FOO FROM BAR";
		SQLException sex = new SQLException("Message", null, 1);
		testMalformedSqlStateCode(sex);
		
		sex = new SQLException("Message", "", 1);
		testMalformedSqlStateCode(sex);
				
		// One char's not allowed
		sex = new SQLException("Message", "I", 1);
		testMalformedSqlStateCode(sex);
	}
	
	
	private void testMalformedSqlStateCode(SQLException sex) {
		String sql = "SELECT FOO FROM BAR";
		try {
			throw this.trans.translate("task", sql, sex);
		}
		catch (UncategorizedSQLException ex) {
			// OK
			assertTrue("SQL is correct", sql.equals(ex.getSql()));
			assertTrue("Exception matches", sex.equals(ex.getSQLException()));
		}
	}

}
