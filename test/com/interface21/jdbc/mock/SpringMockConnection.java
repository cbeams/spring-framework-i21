/*
 * SpringMockConnection.java
 *
 * Copyright (C) 2002 by Interprise Software.  All rights reserved.
 */
package com.interface21.jdbc.datasource;

import java.sql.CallableStatement;
import java.sql.SQLException;

import com.mockobjects.ExpectationCollection;
import com.mockobjects.ExpectationList;
import com.mockobjects.ReturnObjectList;
import com.mockobjects.sql.MockConnection;

/**
 * Spring subclass to assist future refactorings.
 * 
 * @author <a href="mailto:tcook@interprisesoftware.com">Trevor D. Cook</a>
 * @version $Id$
 */
public class SpringMockConnection extends MockConnection {

	private ReturnObjectList myCallableStatements =
		new ReturnObjectList("CommonMockConnection.CallableStatements");

	private ExpectationCollection myCallableStatementStrings =
		new ExpectationList("CommonMockConnection.callableStatementsString");

	private SQLException myCallableStatementException = null;

	/**
	 * Constructor for SpringMockConnection.
	 */
	public SpringMockConnection() {
		super();
	}

	/**
	 * Add an SQL string to use with a callable staement.
	 */
	public void addExpectedPreparedStatementString(String sql) {
		myCallableStatementStrings.addExpected(sql);
	}

	/**
	 * Add a CallableStatement instance for use with tests.
	 */
	public void addExpectedCallableStatement(CallableStatement callable) {
		myCallableStatements.addObjectToReturn(callable);
	}

	/**
	 * @see java.sql.Connection#prepareCall(java.lang.String)
	 */
	public CallableStatement prepareCall(String sql) throws SQLException {
		return prepareCall(sql, 0, 0, 0);
	}

	/**
	 * @see java.sql.Connection#prepareCall(java.lang.String, int, int)
	 */
	public CallableStatement prepareCall(
		String sql,
		int resultSetType,
		int resultSetConcurrency)
		throws SQLException {
		return prepareCall(sql, resultSetType, resultSetConcurrency, 0);
	}

	/**
	 * @see java.sql.Connection#prepareCall(java.lang.String, int, int, int)
	 */
	public CallableStatement prepareCall(
		String sql,
		int resultSetType,
		int resultSetConcurrency,
		int resultSetHoldability)
		throws SQLException {
			myCallableStatementStrings.addActual(sql);
			throwCallableStatementExceptionIfAny();
		return (SpringMockCallableStatement) myCallableStatements.nextReturnObject();
	}

	/**
	 * Pass the SQL exception to throw if preparedStatement or createStatement
	 * is called during a test.
	 */
	public void setupThrowExceptionOnPrepareCall(SQLException exception) {
		myCallableStatementException = exception;
	}

	/**
	 * Throw the Statement instance passed to
	 * setupThrowExceptionOnPrepareOrCreate, if any.
	 */
	private void throwCallableStatementExceptionIfAny() throws SQLException {
		if(null != myCallableStatementException) {
			throw myCallableStatementException;
		}
	}


}
