package com.interface21.jdbc.core.support;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.interface21.beans.factory.InitializingBean;
import com.interface21.core.InternalErrorException;
import com.interface21.dao.DataAccessResourceFailureException;
import com.interface21.jdbc.datasource.DataSourceUtils;

/**
 * Class to increment maximum value of a given HSQL table with the equivalent of an auto-increment column
 * (note : if you use this class, your HSQL key column should NOT be auto-increment, as the sequence table
 * does the job)
 * <br>The sequence is kept in a table; there should be one sequence table per table that needs an auto-generated key.  
 * <p>
 * Example:<br/>
 * <code>
 * &nbsp;&nbsp;create table tab (id int not null primary key, text varchar(100));<br/>
 * &nbsp;&nbsp;create table tab_sequence (value identity);<br/>
 * &nbsp;&nbsp;insert into tab_sequence values(0);<br/>
 * </code>
 * </p>
 * <p>If cacheSize is set, the intermediate values are served without querying the
 * database. If the server or your application is stopped or crashes or a transaction 
 * is rolled back, the unused values will never be served. The maximum hole size in 
 * numbering is consequently the value of cacheSize.
 * </p>
 * @author <a href="mailto:isabelle@meta-logix.com">Isabelle Muszynski</a>
 * @author <a href="mailto:jp.pawlak@tiscali.fr">Jean-Pierre Pawlak</a>
 * @author Thomas Risberg
 * @version $Id$
 */

public class HsqlMaxValueIncrementer
    extends AbstractDataFieldMaxValueIncrementer
    implements InitializingBean {

	protected final Log logger = LogFactory.getLog(getClass());

	private DataSource ds;

	/** The name of the table containing the sequence */
	private String tableName;

	/** The number of keys buffered in a cache, and the cache itself. */
	private int cacheSize = 1;
	private long[] valueCache = null;

	/** Flag if dirty definition */
	private boolean dirty = true;

	private NextMaxValueProvider nextMaxValueProvider;

	/**
	 * Default constructor
	 **/
	public HsqlMaxValueIncrementer() {
		this.nextMaxValueProvider = new NextMaxValueProvider();
	}

	/**
	 * Constructor
	 * @param ds the datasource to use
	 * @param tableName the name of the sequence table to use
	 * @param columnName the name of the column in the sequence table to use
	 **/
	public HsqlMaxValueIncrementer(DataSource ds, String tableName) {
		this.ds = ds;
		this.tableName = tableName;
		this.nextMaxValueProvider = new NextMaxValueProvider();
	}

	/**
	 * Constructor
	 * @param ds the datasource to use
	 * @param tableName the name of the sequence table to use
	 * @param columnName the name of the column in the sequence table to use
	 * @param cacheSize the number of buffered keys
	 **/
	public HsqlMaxValueIncrementer(DataSource ds, String tableName, int cacheSize) {
		this.ds = ds;
		this.tableName = tableName;
		this.cacheSize = cacheSize;
		this.nextMaxValueProvider = new NextMaxValueProvider();
	}

	/**
	 * Constructor
	 * @param ds the datasource to use
	 * @param tableName the name of the sequence table to use
	 * @param columnName the name of the column in the sequence table to use
	 * @param prefixWithZero in case of a String return value, should the string be prefixed with zeroes
	 * @param padding the length to which the string return value should be padded with zeroes
	 **/
	public HsqlMaxValueIncrementer(DataSource ds, String tableName, boolean prefixWithZero, int padding) {
		this.ds = ds;
		this.tableName = tableName;
		this.nextMaxValueProvider = new NextMaxValueProvider();
		this.nextMaxValueProvider.setPrefixWithZero(prefixWithZero, padding);
	}

	/**
	 * Constructor
	 * @param ds the datasource to use
	 * @param tableName the name of the sequence table to use
	 * @param columnName the name of the column in the sequence table to use
	 * @param prefixWithZero in case of a String return value, should the string be prefixed with zeroes
	 * @param padding the length to which the string return value should be padded with zeroes
	 * @param cacheSize the number of buffered keys
	 **/
	public HsqlMaxValueIncrementer(DataSource ds, String tableName, boolean prefixWithZero, int padding, int cacheSize) {
		this.ds = ds;
		this.tableName = tableName;
		this.cacheSize = cacheSize;
		this.nextMaxValueProvider = new NextMaxValueProvider();
		this.nextMaxValueProvider.setPrefixWithZero(prefixWithZero, padding);
	}

	/**
	 * @see com.interface21.jdbc.core.support.AbstractDataFieldMaxValueIncrementer#incrementIntValue()
	 */
	protected int incrementIntValue() {
		return nextMaxValueProvider.getNextIntValue();
	}

	/**
	 * @see com.interface21.jdbc.core.support.AbstractDataFieldMaxValueIncrementer#incrementLongValue()
	 */
	protected long incrementLongValue() {
		return nextMaxValueProvider.getNextLongValue();
	}

	/**
	 * @see com.interface21.jdbc.core.support.AbstractDataFieldMaxValueIncrementer#incrementDoubleValue()
	 */
	protected double incrementDoubleValue() {
		return nextMaxValueProvider.getNextDoubleValue();
	}

	/**
	 * @see com.interface21.jdbc.core.support.AbstractDataFieldMaxValueIncrementer#incrementStringValue()
	 */
	protected String incrementStringValue() {
		return nextMaxValueProvider.getNextStringValue();
	}

	// Private class that does the actual
	// job of getting the sequence.nextVal value
	private class NextMaxValueProvider extends AbstractNextMaxValueProvider {

		/** The Sql string for updating the sequence value */
		private String insertSql;

		/** The Sql string for retrieving the new sequence value */
		private String valueSql;

		/** The next id to serve */
		private int nextValueIx = -1;

		synchronized protected long getNextKey(int type) {
			if (dirty) {
				initPrepare();
			}
			if(nextValueIx < 0 || nextValueIx >= cacheSize) {
				/*
				* Need to use straight JDBC code because we need to make sure that the insert and select
				* are performed on the same connection (otherwise we can't be sure that last_insert_id()
				* returned the correct value)
				*/
				Connection con = null;
				Statement st = null;
				ResultSet rs = null;
				try {
					con = DataSourceUtils.getConnection(ds);
					st = con.createStatement();
					valueCache = new long[cacheSize];
					nextValueIx = 0;
					for (int i = 0; i < cacheSize; i++) {
						st.executeUpdate(insertSql);
						rs = st.executeQuery(valueSql);
						if (rs.next()) {
							valueCache[i] = rs.getLong(1);
						}
						else
							throw new InternalErrorException("last_insert_id() failed after executing an update");
					}
				}
				catch (SQLException ex) {
					throw new DataAccessResourceFailureException("Could not obtain last_insert_id", ex);
				}
				finally {
					if (null != rs) {
						try {
							rs.close();
						}
						catch (SQLException e) {
						}
					}
					if (null != st) {
						try {
							st.close();
						}
						catch (SQLException e) {
						}
						DataSourceUtils.closeConnectionIfNecessary(con, ds);
					}
				}
			}
			if (logger.isInfoEnabled())
				logger.info("Next sequence value is : " + valueCache[nextValueIx]);
			return valueCache[nextValueIx++];
		}

		private void initPrepare() {
			StringBuffer buf = new StringBuffer();
			buf.append("insert into ");
			buf.append(tableName);
			buf.append(" values(null)");
			insertSql = buf.toString();
			if (logger.isInfoEnabled())
				logger.info("insertSql = " + insertSql);
			valueSql = "select max(identity()) from " + tableName;
			nextValueIx = -1;
			dirty = false;
		}
	}

	/**
	 * Sets the data source.
	 * @param ds The data source to set
	 */
	public void setDataSource(DataSource ds) {
		this.ds = ds;
		dirty = true;
	}

	/**
	 * @see com.interface21.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	public void afterPropertiesSet() throws Exception {
		if (ds == null || tableName == null)
			throw new Exception("dsName, sequenceName properties must be set on " + getClass().getName());
	}

	/**
	 * Sets the prefixWithZero.
	 * @param prefixWithZero The prefixWithZero to set
	 */
	public void setPrefixWithZero(boolean prefixWithZero, int length) {
		this.nextMaxValueProvider.setPrefixWithZero(prefixWithZero, length);
	}

	/**
	 * Sets the tableName.
	 * @param tableName The tableName to set
	 */
	public void setTableName(String tableName) {
		this.tableName = tableName;
		dirty = true;
	}

	/**
	 * Sets the cacheSize.
	 * @param cacheSize The number of buffered keys
	 */
	public void setCacheSize(int cacheSize) {
		this.cacheSize = cacheSize;
		dirty = true;
	}

}

