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
import java.util.Map;

import javax.sql.DataSource;

/**
 * Reusable query in which concrete subclasses must implement the abstract
 * mapRow(ResultSet, int) method to convert each row of the JDBC ResultSet
 * into an object.
 *
 * <p>Simplifies MappingSqlQueryWithParameters API by dropping parameters and
 * context. Most subclasses won't care about parameters. If you don't use
 * contextual information, subclass this instead of MappingSqlQueryWithParameters.
 *
 * @author Rod Johnson
 * @author Thomas Risberg
 * @author Jean-Pierre Pawlak
 * @see MappingSqlQueryWithParameters
 */
public abstract class MappingSqlQuery extends MappingSqlQueryWithParameters {

	/**
	 * Constructor to allow use as a JavaBean.
	 */
	public MappingSqlQuery() {
	}

	/**
	 * Convenient constructor with DataSource and SQL string.
	 * @param ds DataSource to use to obtain connections
	 * @param sql SQL to run
	 */
	public MappingSqlQuery(DataSource ds, String sql) {
		super(ds, sql);
	}

	/**
	 * This method is implemented to invoke the protected abstract
	 * mapRow() method, ignoring parameters.
	 * @see MappingSqlQueryWithParameters#mapRow(ResultSet, int, Object[], Map)
	 */
	protected final Object mapRow(ResultSet rs, int rowNum, Object[] parameters, Map context) throws SQLException {
		return mapRow(rs, rowNum);
	}

	/**
	 * Subclasses must implement this method to convert each row of the
	 * ResultSet into an object of the result type. Subclasses of this class,
	 * as opposed to direct subclasses of MappingSqlQueryWithParameters,
	 * don't need to concern themselves with the parameters to the execute()
	 * method of the query object.
	 * @param rs ResultSet we're working through
	 * @param rowNum row number (from 0) we're up to
	 * @return an object of the result type
	 * @throws SQLException if there's an error extracting data.
	 * Subclasses can simply fail to catch SQLExceptions.
	 */
	protected abstract Object mapRow(ResultSet rs, int rowNum) throws SQLException;

}
