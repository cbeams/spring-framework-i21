/*
 * The Spring Framework is published under the terms
 * of the Apache Software License.
 */
 
package com.interface21.jdbc.datasource;

import java.io.PrintWriter;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

/**
 * Abstract base class for Spring's DataSource implementations,
 * taking care of the "uninteresting" glue.
 * @author Juergen Hoeller
 * @since 07.05.2003
 * @see DriverManagerDataSource
 * @version $Id$
 */
public abstract class AbstractDataSource implements DataSource {

	protected final Logger logger = Logger.getLogger(getClass());

	/**
	 * Returns 0: means use default system timeout.
	 */
	public int getLoginTimeout() throws SQLException {
		return 0;
	}

	public void setLoginTimeout(int timeout) throws SQLException {
		throw new UnsupportedOperationException("setLoginTimeout");
	}

	/**
	 * LogWriter methods are unsupported.
	 */
	public PrintWriter getLogWriter() {
		throw new UnsupportedOperationException("getLogWriter");
	}

	/**
	 * LogWriter methods are unsupported.
	 */
	public void setLogWriter(PrintWriter pw) throws SQLException {
		throw new UnsupportedOperationException("setLogWriter");
	}

}
