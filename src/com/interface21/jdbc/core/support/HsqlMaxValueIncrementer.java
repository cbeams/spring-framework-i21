package com.interface21.jdbc.core.support;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.interface21.core.InternalErrorException;
import com.interface21.dao.DataAccessException;
import com.interface21.dao.DataAccessResourceFailureException;
import com.interface21.jdbc.datasource.DataSourceUtils;

/**
 * Class to increment maximum value of a given HSQL table with the equivalent
 * of an auto-increment column. Note: if you use this class, your HSQL key
 * column should <i>NOT</i> be auto-increment, as the sequence table does the job.
 *
 * <p>The sequence is kept in a table. There should be one sequence table per
 * table that needs an auto-generated key.
 *
 * <p>Example:
 * <p><code>
 * &nbsp;&nbsp;create table tab (id int not null primary key, text varchar(100));<br>
 * &nbsp;&nbsp;create table tab_sequence (value identity);<br>
 * &nbsp;&nbsp;insert into tab_sequence values(0);<br>
 * </code>
 *
 * <p>If cacheSize is set, the intermediate values are served without querying the
 * database. If the server or your application is stopped or crashes or a transaction
 * is rolled back, the unused values will never be served. The maximum hole size in
 * numbering is consequently the value of cacheSize.
 *
 * @author Isabelle Muszynski
 * @author Jean-Pierre Pawlak
 * @author Thomas Risberg
 * @version $Id$
 */

public class HsqlMaxValueIncrementer extends AbstractDataFieldMaxValueIncrementer {

	protected final Log logger = LogFactory.getLog(getClass());

	private long[] valueCache = null;

	private NextMaxValueProvider nextMaxValueProvider;

	/**
	 * Default constructor.
	 **/
	public HsqlMaxValueIncrementer() {
		this.nextMaxValueProvider = new NextMaxValueProvider();
	}

	/**
	 * Alternative constructor.
	 * @param ds the datasource to use
	 * @param incrementerName the name of the sequence/table to use
	 * @param columnName the name of the column in the sequence table to use
	 **/
	public HsqlMaxValueIncrementer(DataSource ds, String incrementerName, String columnName) {
		super(ds, incrementerName, columnName);
		this.nextMaxValueProvider = new NextMaxValueProvider();
	}

	/**
	 * Alternative constructor.
	 * @param ds the datasource to use
	 * @param incrementerName the name of the sequence/table to use
	 * @param columnName the name of the column in the sequence table to use
	 * @param cacheSize the number of buffered keys
	 **/
	public HsqlMaxValueIncrementer(DataSource ds, String incrementerName, String columnName, int cacheSize) {
		super(ds, incrementerName, columnName, cacheSize);
		this.nextMaxValueProvider = new NextMaxValueProvider();
	}

	/**
	 * Alternative constructor.
	 * @param ds the datasource to use
	 * @param incrementerName the name of the sequence/table to use
	 * @param columnName the name of the column in the sequence table to use
	 * @param prefixWithZero in case of a String return value, should the string be prefixed with zeroes
	 * @param padding the length to which the string return value should be padded with zeroes
	 **/
	public HsqlMaxValueIncrementer(DataSource ds, String incrementerName, String columnName, boolean prefixWithZero, int padding) {
		super(ds, incrementerName, columnName);
		this.nextMaxValueProvider = new NextMaxValueProvider();
		this.nextMaxValueProvider.setPrefixWithZero(prefixWithZero, padding);
	}

	/**
	 * Alternative constructor.
	 * @param ds the datasource to use
	 * @param incrementerName the name of the sequence/table to use
	 * @param columnName the name of the column in the sequence table to use
	 * @param prefixWithZero in case of a String return value, should the string be prefixed with zeroes
	 * @param padding the length to which the string return value should be padded with zeroes
	 * @param cacheSize the number of buffered keys
	 **/
	public HsqlMaxValueIncrementer(DataSource ds, String incrementerName, String columnName, boolean prefixWithZero, int padding, int cacheSize) {
		super(ds, incrementerName, columnName, cacheSize);
		this.nextMaxValueProvider = new NextMaxValueProvider();
		this.nextMaxValueProvider.setPrefixWithZero(prefixWithZero, padding);
	}

	/**
	 * Set whether to prefix with zero.
	 */
	public void setPrefixWithZero(boolean prefixWithZero, int length) {
		this.nextMaxValueProvider.setPrefixWithZero(prefixWithZero, length);
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

		/** The Sql string for removing old sequence values */
		private String deleteSql;

		/** The next id to serve */
		private int nextValueIx = -1;

		synchronized protected long getNextKey(int type) throws DataAccessException {
			if (isDirty()) {
				initPrepare();
			}
			if (nextValueIx < 0 || nextValueIx >= getCacheSize()) {
				/*
				* Need to use straight JDBC code because we need to make sure that the insert and select
				* are performed on the same connection (otherwise we can't be sure that last_insert_id()
				* returned the correct value)
				*/
				Connection con = null;
				Statement st = null;
				ResultSet rs = null;
				try {
					con = DataSourceUtils.getConnection(getDataSource());
					st = con.createStatement();
					valueCache = new long[getCacheSize()];
					nextValueIx = 0;
					for (int i = 0; i < getCacheSize(); i++) {
						st.executeUpdate(insertSql);
						rs = st.executeQuery(valueSql);
						if (rs.next()) {
							valueCache[i] = rs.getLong(1);
						}
						else
							throw new InternalErrorException("last_insert_id() failed after executing an update");
					}
					long maxValue = valueCache[(valueCache.length - 1)];
					st.executeUpdate(deleteSql + maxValue);
					logger.info("Delete SQL is : " + deleteSql + maxValue);
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
						DataSourceUtils.closeConnectionIfNecessary(con, getDataSource());
					}
				}
			}
			if (logger.isInfoEnabled())
				logger.info("Next sequence value is : " + valueCache[nextValueIx]);
			return valueCache[nextValueIx++];
		}

		private void initPrepare() throws InvalidMaxValueIncrementerApiUsageException {
			afterPropertiesSet();
			if (getIncrementerName() == null)
				throw new InvalidMaxValueIncrementerApiUsageException("IncrementerName property must be set on " + getClass().getDeclaringClass().getName());
			if (getColumnName() == null)
				throw new InvalidMaxValueIncrementerApiUsageException("ColumnName property must be set on " + getClass().getDeclaringClass().getName());
			StringBuffer buf = new StringBuffer();
			buf.append("insert into ");
			buf.append(getIncrementerName());
			buf.append(" values(null)");
			insertSql = buf.toString();
			if (logger.isInfoEnabled())
				logger.info("insertSql = " + insertSql);
			valueSql = "select max(identity()) from " + getIncrementerName();
			nextValueIx = -1;
			buf = new StringBuffer();
			buf.append("delete from ");
			buf.append(getIncrementerName());
			buf.append(" where ");
			buf.append(getColumnName());
			buf.append(" < ");
			deleteSql = buf.toString();
			setDirty(false);
		}
	}

}
