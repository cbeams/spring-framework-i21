/**
 * Generic framework code included with 
 * <a href="http://www.amazon.com/exec/obidos/tg/detail/-/1861007841/">Expert One-On-One J2EE Design and Development</a>
 * by Rod Johnson (Wrox, 2002). 
 * This code is free to use and modify. However, please
 * acknowledge the source and include the above URL in each
 * class using or derived from this code. 
 * Please contact <a href="mailto:rod.johnson@interface21.com">rod.johnson@interface21.com</a>
 * for commercial support.
 */

package com.interface21.jdbc.object;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.sql.DataSource;

import com.interface21.jdbc.core.ResultReader;

/**
 * Reusable RDBMS query in which concrete subclasses must
 * implement the abstract mapRow(ResultSet, int) method to map each row
 * of the JDBC ResultSet into an object.
 * <p/>
 * Such manual mapping is usually preferable to "automatic"
 * mapping using reflection, which can become complex in non-trivial
 * cases. For example, the present class allows different objects
 * to be used for different rows (for example, if a subclass is indicated).
 * It allows computed fields to be set. And there's no need for 
 * ResultSet columns to have the same names as bean properties.
 * The Pareto Principle in action: going the extra mile to automate
 * the extraction process makes the framework much more complex
 * and delivers little real benefit.
 * <p/>
 * Subclasses can be constructed providing SQL, parameter types
 * and a DataSource. SQL will often vary between subclasses.
 * @author Rod Johnson
 * @author Thomas Risberg
 * @see com.interface21.jdbc.object.MappingSqlQuery
 * @see com.interface21.jdbc.object.SqlQuery
 */
public abstract class MappingSqlQueryWithParameters extends SqlQuery {

	//-------------------------------------------------------------------------
	// Constructors
	//-------------------------------------------------------------------------
	/**
	 * Constructor to allow use as a JavaBean
	 */
	public MappingSqlQueryWithParameters() {
	}
	
	/** Convenient constructor
	 * @param ds DataSource to use to get connections
	 * @param sql SQL to run
	 */
	public MappingSqlQueryWithParameters(DataSource ds, String sql) {
		super(ds, sql); 
	}
	

	//-------------------------------------------------------------------------
	// Implementation of protected abstract method
	//-------------------------------------------------------------------------
	/** 
	 * Implementation of protected abstract method. This invokes the subclass's
	 * implementation of the mapRow() method.
	 */
	protected final ResultReader newResultReader(int rowsExpected, Object[] parameters) {
		return new ResultReaderImpl(rowsExpected, parameters);
	}

	/**
	 * Subclasses must implement this method to convert
	 * each row of the ResultSet into an object of the result type.
	 * @param rs RowSet we're working through
	 * @param rownum row number (from 0) we're up to
	 * @return an object of the result type
	 * @throws SQLException if there's an error extracting data.
	 * Subclasses can simply not catch SQLExceptions, relying on the
	 * framework to clean up.
	 * @param parameters to the query (passed to the execute() method). 
	 * Subclasses are rarely interested in these.
	 * It can be null if there are no parameters.
	 */
	protected abstract Object mapRow(ResultSet rs, int rownum, Object[] parameters) throws SQLException;


	//-------------------------------------------------------------------------
	// Inner classes
	//-------------------------------------------------------------------------
	/**
	 * Implementation of ResultReader that calls the enclosing
	 * class's mapRow() method for each row.
	 */
	private class ResultReaderImpl implements ResultReader {
		
		/** List to save results in */
		private List l; 
		
		private Object[] params;
		
		private int rowNum = 0;
		
		/** Use an array list. More efficient if we know how many
		 * results to expect
		 */
		public ResultReaderImpl(int rowsExpected, Object[] parameters) {
			// Use the more efficient collection if we know how many rows to expect
			this.l = (rowsExpected > 0) ? (List) new ArrayList(rowsExpected) : (List) new LinkedList();
			this.params = parameters;
		}
 
		public void processRow(ResultSet rs) throws SQLException {
			l.add(mapRow(rs, rowNum++, params));
		}

		/**
		 * @see ResultReader#getResults()
		 */
		public List getResults() {
			return l;
		}
	}	// inner class ResultReaderImpl
	
}	// class MappingSqlQueryWithParameters