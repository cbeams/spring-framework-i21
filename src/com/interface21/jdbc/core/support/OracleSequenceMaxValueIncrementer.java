package com.interface21.jdbc.core.support;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.interface21.beans.factory.InitializingBean;
import com.interface21.jdbc.object.SqlFunction;

/**
 * Class to retrieve the next value of a given Oracle SEQUENCE 
 * If the cacheSize is set then we will retrive that number of values from sequence and
*  then serve the intermediate values without querying the database
 * @author Dmitriy Kopylenko
 * @author Isabelle Muszynski
 * @author Jean-Pierre Pawlak
 * @author Thomas Risberg
 * @version $Id$
 */
public class OracleSequenceMaxValueIncrementer
	extends AbstractDataFieldMaxValueIncrementer
	implements InitializingBean {

	protected final Log logger = LogFactory.getLog(getClass());

	//-----------------------------------------------------------------
	// Instance data
	//-----------------------------------------------------------------
	private DataSource ds;

	private String sequenceName;

	/** The number of keys buffered in a cache, and the cache itself. */
	private int cacheSize = 1;
	private long[] valueCache = null;

	/** Flag if dirty definition */
	private boolean dirty = true;
	
	private NextMaxValueProvider nextMaxValueProvider;

	//-----------------------------------------------------------------
	// Constructors
	//-----------------------------------------------------------------
	/**
	 * Default constructor
	 **/
	public OracleSequenceMaxValueIncrementer() {
		this.nextMaxValueProvider = new NextMaxValueProvider();
	}

	/**
	 * Constructor
	 * @param ds the datasource to use
	 * @param seqName the sequence name to use for fetching key values
	 */
	public OracleSequenceMaxValueIncrementer(DataSource ds, String seqName) {
		this.ds = ds;
		this.sequenceName = seqName;
		this.nextMaxValueProvider = new NextMaxValueProvider();
	}

	/**
	 * Constructor
	 * @param ds the datasource to use
	 * @param seqName the sequence name to use for fetching key values
	 * @param cacheSize the number of buffered keys
	 **/
	public OracleSequenceMaxValueIncrementer(DataSource ds, String seqName, int cacheSize) {
		this.ds = ds;
		this.sequenceName = seqName;
		this.cacheSize = cacheSize;
		this.nextMaxValueProvider = new NextMaxValueProvider();
	}

	/**
	 * Constructor
	 * @param ds the datasource to be used
	 * @param seqName the sequence name to use for fetching key values
	 * @param prefixWithZero in case of a String return value, should the string be prefixed with zeroes
	 * @param padding the length to which the string return value should be padded with zeroes
	 */
	public OracleSequenceMaxValueIncrementer(DataSource ds, String seqName, boolean prefixWithZero, int padding) {
		this.ds = ds;
		this.sequenceName = seqName;
		this.nextMaxValueProvider = new NextMaxValueProvider();
		this.nextMaxValueProvider.setPrefixWithZero(prefixWithZero, padding);
	}

	/**
	 * Constructor
	 * @param ds the datasource to be used
	 * @param seqName the sequence name to use for fetching key values
	 * @param prefixWithZero in case of a String return value, should the string be prefixed with zeroes
	 * @param padding the length to which the string return value should be padded with zeroes
	 * @param cacheSize the number of buffered keys
	 */
	public OracleSequenceMaxValueIncrementer(DataSource ds, String seqName, boolean prefixWithZero, int padding, int cacheSize) {
		this.ds = ds;
		this.sequenceName = seqName;
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

		/** The next id to serve */
		private int nextValueIx = -1;

		protected long getNextKey(int type) {
			if (dirty) { initPrepare(); }
			if(nextValueIx < 0 || nextValueIx >= cacheSize) {
				SqlFunction sqlf = new SqlFunction(ds, "SELECT " + sequenceName + ".NEXTVAL FROM DUAL", type);
				sqlf.compile();
				valueCache = new long[cacheSize];
				nextValueIx = 0;
				for (int i = 0; i < cacheSize; i++) {
					valueCache[i] = getLongValue(sqlf, type);
				}
			}
			if (logger.isInfoEnabled())
				logger.info("Next sequence value is : " + valueCache[nextValueIx]);
			return valueCache[nextValueIx++];
		}
	
		private void initPrepare() {
			/* Correct definitions are set */
			nextValueIx = -1;
			dirty = false; 			
		}
	
	}

	/**
	 * Sets the datasource.
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
		if (ds == null || sequenceName == null)
			throw new Exception("ds, sequenceName properties must be set on " + getClass().getName());
	}

	/**
	 * Sets the prefixWithZero.
	 * @param prefixWithZero The prefixWithZero to set
	 */
	public void setPrefixWithZero(boolean prefixWithZero, int length) {
		this.nextMaxValueProvider.setPrefixWithZero(prefixWithZero, length);
	}

	/**
	 * Sets the sequenceName.
	 * @param sequenceName The sequenceName to set
	 */
	public void setSequenceName(String sequenceName) {
		this.sequenceName = sequenceName;
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
