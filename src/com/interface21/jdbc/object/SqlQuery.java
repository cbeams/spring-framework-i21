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

import java.util.List;

import javax.sql.DataSource;

import com.interface21.dao.DataAccessException;
import com.interface21.dao.InvalidDataAccessApiUsageException;
import com.interface21.jdbc.core.PreparedStatementCreatorFactory;
import com.interface21.jdbc.core.ResultReader;

/**
 * Reusable threadsafe object to represent a SQL query.
 * Subclasses must implement the newResultReader() method to
 * provide an object that can save the results of iterating
 * over the ResultSet.
 * <br>This class provides a number of public final execute() methods that are
 * analogous to the different convenient JDO query execute() methods. Subclasses
 * can either rely on one of these inherited methods, or can add their own
 * custom execution methods, with meaningful names and typed parameters. Each
 * custom query method will invoke one of this class's untype query methods.
 * @author Rod Johnson
   @version $Id$
 */
public abstract class SqlQuery extends SqlOperation {
	
	//-------------------------------------------------------------------------
	// Instance data
	//-------------------------------------------------------------------------
	/** 
	 * Number of rows to expect. If 0, unknown.
	*/
 	private int rowsExpected;
 	
 	/** 
 	 * Object used to create PreparedStatementCreators each time this query is executed
 	 * given SQL and declared parameters
 	 */
 	private PreparedStatementCreatorFactory preparedStatementFactory;

	//-------------------------------------------------------------------------
	// Constructors
	//-------------------------------------------------------------------------
	/**
	 * Allow use as a bean
	 */
	public SqlQuery() {
	}
	
	/**
	 * Convenient constructor.
	 * @param ds DataSource to use to get connections
	 * @param SQL to execute. SQL can also be supplied at runtime by overriding
	 * the getSql() method.
	 */
	public SqlQuery(DataSource ds, String sql) {
		setDataSource(ds);
		setSql(sql);
	}


	//-------------------------------------------------------------------------
	// Bean properties
	//-------------------------------------------------------------------------
	/**
	 * Gets the number of rows expected. This can be used to ensure
	 * efficient storage of results. The default behavior
	 * is not to expect any specific number of rows.
	 * @return Returns a int
	 */
	public int getRowsExpected() {
		return rowsExpected;
	}

	/**
	 * Sets the number of rows expected.
	 * @param rowsExpected The rowsExpected to set
	 */
	public void setRowsExpected(int rowsExpected) {
		this.rowsExpected = rowsExpected;
	}
	
	//-------------------------------------------------------------------------
	// Execute methods
	//-------------------------------------------------------------------------
	/**
	 * Subclasses must implement this method to save a List of objects
	 * returned by the execute() method.
	 * @param rowsExpected. If 0, we don't know how many rows to expect.
	 * This parameter can be ignored, but may help some implementations
	 * choose the most efficient Collection type: e.g. ArrayList
	 * instead of LinkedList for large result sets.
	 * @param parameters parameters to the execute() method, in case subclass is interested. 
	 * May be null if there were no parameters. 
	 */
	protected abstract ResultReader newResultReader(int rowsExpected, Object[] parameters);

	/** 
	 * All execution goes through this method
	 * @param parameters parameters, as to JDO queries. Primitive parameters must
	 * be represented by their Object wrapper type. The ordering of parameters is
	 * significant.
	 * @return a list of objects, one per row of the ResultSet. Normally all these 
	 * will be of the same class, although it is possible to use different types.
	 */
	public final List execute(final Object[] parameters) throws DataAccessException {
		validateParameters(parameters);

		ResultReader rr = newResultReader(this.rowsExpected, parameters);
		getJdbcTemplate().query(newPreparedStatementCreator(parameters), rr);
		return rr.getResults();
	}
	
	/** 
	 * Convenient method to execute without parameters
	 */
	public final List execute() throws DataAccessException {
		return execute( (Object[]) null);
	}
	
	/** 
	 * Convenient method to execute with a single int parameter
	 * @param p1 single int parameter
	 */
	public final List execute(int p1) throws DataAccessException {
		return execute(new Object[] { new Integer(p1) });
	}
	
	/** 
	 * Convenient method to execute with two int parameters
	 */
	public final List execute(int p1, int p2) throws DataAccessException {
		return execute(new Object[] { new Integer(p1), new Integer(p2) });
	}
	
	/** 
	 * Convenient method to execute with a single String parameter
	 */
	public final List execute(String p1) throws DataAccessException {
		return execute(new Object[] { p1 });
	}

	/** 
	 * Generic findObject method, used by all other findObject() methods. 
	 * findObject() methods are like EJB entity bean finders, in that it is
	 * considered an error if they return more than one result.
	 * @return null if not found. Subclasses may choose to treat this
	 * as an error and throw an exception.
	 */
	public final Object findObject(Object[] parameters) throws DataAccessException {
		List l = execute(parameters);
		if (l.size() == 0)
			return null;
		if (l.size() > 1)
			throw new InvalidDataAccessApiUsageException("Result is not unique. Found " + l.size());
		return l.get(0);
	}
	
	/** 
	 * Convenience method to find a single object given a single int parameter
	 */
	public final Object findObject(int p1) throws DataAccessException {
		return findObject(new Object[] { new Integer(p1) });
	}
	
	/** 
	 * Convenience method to find a single object given two int parameters
	 */
	public final Object findObject(int p1, int p2) throws DataAccessException {
		return findObject(new Object[] { new Integer(p1), new Integer(p2) });
	}
	
	/** 
	 * Convenience method to find a single object given a single String parameter
	 */
	public final Object findObject(String p1) throws DataAccessException {
		return findObject(new Object[] { p1 });
	}	
	
	
	/** 
	 * Convenience method to find a single object given a single long parameter
	 */
	public final Object findObject(long p1) throws DataAccessException {
		return findObject(new Object[] { new Long(p1) });
	}

	
	/**
	 * Subclasses can override this method to implement custom behavior on 
	 * compilation. This implementation does nothing.
	 */
	protected void onCompileInternal() {
		logger.debug("NOP onCompileInternal");
	}

}
