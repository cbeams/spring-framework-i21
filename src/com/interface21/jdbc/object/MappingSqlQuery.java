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

import javax.sql.DataSource;

/**
 * Reusable query in which concrete subclasses must
 * implement the abstract mapRow(ResultSet, int) method to convert 
 * each row of the JDBC ResultSet into an object.
 * <p/>
 * Simplifies MappingSqlQueryWithParameters API by dropping parameters.
 * Most subclasses won't care about parameters.
 * @author Rod Johnson
 * @author Thomas Risberg
 * @see MappingSqlQueryWithParameters
 */
public abstract class MappingSqlQuery extends MappingSqlQueryWithParameters {

	//-------------------------------------------------------------------------
	// Constructors
	//-------------------------------------------------------------------------
	/**
	 * Constructor to allow use as a JavaBean
	 */
	public MappingSqlQuery() {
	}
	
	/** 
	 * Convenient constructor
	 * @param ds DataSource to use to obtain connections
	 * @param sql SQL to run
	 */
	public MappingSqlQuery(DataSource ds, String sql) {
		super(ds, sql); 
	}
	

	//-------------------------------------------------------------------------
	// Implementation of protected abstract method
	//-------------------------------------------------------------------------

	/**
	 * This method is implemented to invoke the protected abstract
	 * mapRow() method, ignoring parameters.
	 * @see MappingSqlQueryWithParameters#extract(ResultSet, int, Object[])
	 */
	protected final Object mapRow(ResultSet rs, int rownum, Object[] parameters) throws SQLException {
		return mapRow(rs, rownum);
	}
	
	/**
	 * Subclasses must implement this method to convert
	 * each row of the ResultSet into an object of the result type.
	 * Subclasses of this class, as opposed to direct subclasses
	 * of MappingSqlQueryWithParameters, don't need to concern
	 * themselves with the parameters to the execute() method of
	 * the query object.
	 * @param rs RowSet we're working through
	 * @param rownum row number (from 0) we're up to
	 * @return an object of the result type
	 * @throws SQLException if there's an error extracting data.
	 * Subclasses can simply fail to catch SQLExceptions.
	 * @param parameters subclasses are rarely interested in this.
	 * It can be null
	 */
	protected abstract Object mapRow(ResultSet rs, int rownum) throws SQLException;
	
}