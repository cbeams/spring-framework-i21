package com.interface21.jdbc.datasource;

import java.io.PrintWriter;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

/**
 * Abstract base class for Spring's DataSource implementations,
 * caring for the "uninteresting" glue.
 *
 * @author Juergen Hoeller
 * @since 07.05.2003
 * @see DriverManagerDataSource
 */
public abstract class AbstractDataSource implements DataSource {

	protected final Logger logger = Logger.getLogger(getClass());

	private PrintWriter pw = new PrintWriter(System.out);

	/*
	 * Returns 0: means use default system timeout.
	 */
	public int getLoginTimeout() throws SQLException {
		return 0;
	}

	public void setLoginTimeout(int timeout) throws SQLException {
		throw new UnsupportedOperationException("setLoginTimeout");
	}

	public PrintWriter getLogWriter() {
		return pw;
	}

	public void setLogWriter(PrintWriter pw) throws SQLException {
		this.pw = pw;
	}

}
