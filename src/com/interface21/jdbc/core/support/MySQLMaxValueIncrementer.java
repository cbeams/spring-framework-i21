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
 * Class to increment maximum value of a given MySQL table with the equivalent of an auto-increment column
 * (note : if you use this class, your MySQL key column should NOT be auto-increment, as the sequence table
 * does the job)
 * <br>The sequence is kept in a table; there should be one sequence table per table that needs an auto-generated key.
 * <p>
 * Example
 * <code>
 * create table tab (id int unsigned not null primary key, text varchar(100));
 * create table tab_sequence (seq int unsigned not null auto_increment primary key);
 * insert into tab_sequence values(0);
 * </code>
 * </p>
 * <p>If incrementBy is set, the intermediate values are served without querying the
 * database. If the server is stopped or crashes, the unused values will never be
 * served. The maximum hole size in numbering is consequently the value of incrementBy.
 * </p>
 * @author <a href="mailto:isabelle@meta-logix.com">Isabelle Muszynski</a>
 * @author <a href="mailto:jp.pawlak@tiscali.fr">Jean-Pierre Pawlak</a>
 * @version $Id$
 */

public class MySQLMaxValueIncrementer
    extends AbstractDataFieldMaxValueIncrementer
    implements InitializingBean {

	protected final Log logger = LogFactory.getLog(getClass());

	private DataSource ds;

	/** The name of the table containing the sequence */
	private String tableName;

	/** The name of the column to use for this sequence */
	private String columnName;

	/** The number of keys buffered in a bunch. */
	private int incrementBy = 1;

	/** Flag if dirty definition */
	private boolean dirty = true;

	private NextMaxValueProvider nextMaxValueProvider;

	/**
	 * Default constructor
	 **/
	public MySQLMaxValueIncrementer() {
		this.nextMaxValueProvider = new NextMaxValueProvider();
	}

	/**
	 * Constructor
	 * @param ds the datasource to use
	 * @param tableName the name of the sequence table to use
	 * @param columnName the name of the column in the sequence table to use
	 **/
	public MySQLMaxValueIncrementer(DataSource ds, String tableName, String columnName) {
		this.ds = ds;
		this.tableName = tableName;
		this.columnName = columnName;
		this.nextMaxValueProvider = new NextMaxValueProvider();
	}

	/**
	 * Constructor
	 * @param ds the datasource to use
	 * @param tableName the name of the sequence table to use
	 * @param columnName the name of the column in the sequence table to use
	 * @param incrementBy the number of buffered keys
	 **/
	public MySQLMaxValueIncrementer(DataSource ds, String tableName, String columnName, int incrementBy) {
		this.ds = ds;
		this.tableName = tableName;
		this.columnName = columnName;
		this.incrementBy = incrementBy;
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
	public MySQLMaxValueIncrementer(DataSource ds, String tableName, String columnName, boolean prefixWithZero, int padding) {
		this.ds = ds;
		this.tableName = tableName;
		this.columnName = columnName;
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
	 * @param incrementBy the number of buffered keys
	 **/
	public MySQLMaxValueIncrementer(DataSource ds, String tableName, String columnName, boolean prefixWithZero, int padding, int incrementBy) {
		this.ds = ds;
		this.tableName = tableName;
		this.columnName = columnName;
		this.incrementBy = incrementBy;
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
		private String selectSql = "select last_insert_id()";

		/** The Sql string for cleaning the sequence table */
		private String deleteSql;

		/** The next id to serve */
		private long nextId = 0;

		/** The max id to serve */
		private long maxId = 0;

		synchronized protected long getNextKey(int type) {
			if (dirty) {
				initPrepare();
			}
			if (maxId == nextId) {
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
					// Increment the sequence column
					int count = incrementBy;
					while (count-- > 0)
						st.executeUpdate(insertSql);
					// Retrieve the new max of the sequence column
					rs = st.executeQuery(selectSql);
					// Zap excess records
					st.executeUpdate(deleteSql);
					if (rs.next()) {
						maxId = rs.getLong(1);
						if (logger.isInfoEnabled())
							logger.info("new maxId is : " + maxId);
					}
					else
						throw new InternalErrorException("last_insert_id() failed after executing an update");
					nextId = maxId - incrementBy;
					nextId++;
					if (logger.isInfoEnabled())
						logger.info("nextId is : " + nextId);
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
			else
				nextId++;
			return nextId;
		}

		private void initPrepare() {
			StringBuffer buf = new StringBuffer();
			buf.append("insert into  ");
			buf.append(tableName);
			buf.append("(");
			buf.append(columnName);
			buf.append(") values(NULL)");
			insertSql = buf.toString();
			if (logger.isInfoEnabled())
				logger.info("insertSql = " + insertSql);
			buf.delete(0, buf.length());
			buf.append("delete from ");
			buf.append(tableName);
			buf.append(" where ");
			buf.append(columnName);
			buf.append(" < last_insert_id()");
			deleteSql = buf.toString();
			if (logger.isInfoEnabled())
				logger.info("deleteSql = " + deleteSql);
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
		if (ds == null || tableName == null || columnName == null)
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
	 * Sets the columnName.
	 * @param columnName The columnName to set
	 */
	public void setColumnName(String columnName) {
		this.columnName = columnName;
		dirty = true;
	}

	/**
	 * Sets the incrementBy.
	 * @param incrementBy The number of buffered keys
	 */
	public void setIncrementBy(int incrementBy) {
		this.incrementBy = incrementBy;
		dirty = true;
	}

}

