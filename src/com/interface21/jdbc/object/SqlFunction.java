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

import com.interface21.dao.InvalidDataAccessApiUsageException;

/**
 * SQL function wrapper that expects an int column value in
 * the single row of results returned. Intended to use to
 * call SQL functions.
 * <br>This is a concrete class, which there is normally no need
 * to subclass. Code using this package can create an object of this
 * type, declaring SQL and parameters, and then invoke the appropriate
 * run() method repeatedly to execute the function. 
 * <br>Like all RdbmsOperation objects, SqlFunction objects are
 * threadsafe.
 * @author Rod Johnson
 */
public class SqlFunction extends ManualExtractionSqlQuery {
	
	/**
	 * Constructor to allow use as a JavaBean.
	 * A DataSource, SQL and any parameters must be supplied
	 * before invoking the compile() method and using this object.
	 */
	public SqlFunction() {
	}
	
	
	/**
	 * Create a new SQLFunction object with SQL and parameters.
	 * @param ds DataSource to obtain connections from
	 * @param sql SQL to execute
	 * @param types SQL types of the parameters, as defined
	 * in the java.sql.Types class
	 */
	public SqlFunction(DataSource ds, String sql, int[] types) {
		setDataSource(ds);
		setSql(sql);
		setTypes(types);
		setRowsExpected(1);
	}
	
	
	/**
	 * Create a new SQLFunction object with SQL, but without parameters.
	 * Must add parameters or settle with none.
	 * @param ds DataSource to obtain connections from
	 * @param sql SQL to execute
	 */
	public SqlFunction(DataSource ds, String sql) {
		setDataSource(ds);
		setSql(sql);
		setRowsExpected(1);
	}
	
	
	/**
	 * This implementation of this method extracts an int
	 * value from the single row returned by the function.
	 * If there are a different number of rows returned, this
	 * is treated as an error.
	 * @see ManualExtractionSqlQuery#extract(ResultSet, int)
	 */
	protected Object extract(ResultSet rs, int rownum) throws SQLException, InvalidDataAccessApiUsageException {
		if (rownum != 0) {
			throw new InvalidDataAccessApiUsageException("SQL function '" + getSql() + "' can't return more than one row");			
		}
		return new Integer(rs.getInt(1));
	}
	
	/**
	 * Convenient method to run the function without arguments
	 * @return the value of the function
	 */
	public int run() {
		Integer I = (Integer) super.findObject((Object[]) null);
		return I.intValue();
	}
	
	
	/**
	 * Convenient method to run the function with a single int argument
	 * @param p single int argument
	 * @return the value of the function
	 */
	public int run(int p) {
		Integer I = (Integer) super.findObject(p);
		return I.intValue();
	}
	
	/**
	 * Analogous to the SqlQuery.execute([]) method. This is a
	 * generic method to execute a query, taken a number of
	 * arguments.
	 * @param array of arguments. These will be objects or
	 * object wrapper types for primitives.
	 * @return the value of the function
	 */
	public int run(Object[] args) {
		Integer I = (Integer) super.findObject(args);
		return I.intValue();
	}

}
