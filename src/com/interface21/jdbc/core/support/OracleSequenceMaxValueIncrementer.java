package com.interface21.jdbc.core.support;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
	extends AbstractDataFieldMaxValueIncrementer {

	protected final Log logger = LogFactory.getLog(getClass());

	private long[] valueCache = null;

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
	 * @param dataSource the datasource to use
	 * @param incrementerName the name of the sequence/table to use
	 */
	public OracleSequenceMaxValueIncrementer(DataSource ds, String incrementerName) {
        super(ds, incrementerName);
		this.nextMaxValueProvider = new NextMaxValueProvider();
	}

	/**
	 * Constructor
	 * @param ds the datasource to use
	 * @param incrementerName the name of the sequence/table to use
	 * @param cacheSize the number of buffered keys
	 **/
	public OracleSequenceMaxValueIncrementer(DataSource ds, String incrementerName, int cacheSize) {
        super(ds, incrementerName, cacheSize);
		this.nextMaxValueProvider = new NextMaxValueProvider();
	}

	/**
	 * Constructor
	 * @param ds the datasource to be used
	 * @param incrementerName the name of the sequence/table to use
	 * @param prefixWithZero in case of a String return value, should the string be prefixed with zeroes
	 * @param padding the length to which the string return value should be padded with zeroes
	 */
	public OracleSequenceMaxValueIncrementer(DataSource ds, String incrementerName, boolean prefixWithZero, int padding) {
        super(ds, incrementerName);
		this.nextMaxValueProvider = new NextMaxValueProvider();
		this.nextMaxValueProvider.setPrefixWithZero(prefixWithZero, padding);
	}

	/**
	 * Constructor
	 * @param ds the datasource to be used
	 * @param incrementerName the name of the sequence/table to use
	 * @param prefixWithZero in case of a String return value, should the string be prefixed with zeroes
	 * @param padding the length to which the string return value should be padded with zeroes
	 * @param cacheSize the number of buffered keys
	 */
	public OracleSequenceMaxValueIncrementer(DataSource ds, String incrementerName, boolean prefixWithZero, int padding, int cacheSize) {
        super(ds, incrementerName, cacheSize);
		this.nextMaxValueProvider = new NextMaxValueProvider();
		this.nextMaxValueProvider.setPrefixWithZero(prefixWithZero, padding);
	}

	/**
	 * Sets the prefixWithZero.
	 * @param prefixWithZero The prefixWithZero to set
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

		/** The next id to serve */
		private int nextValueIx = -1;

		protected long getNextKey(int type) {
			if (isDirty()) { initPrepare(); }
			if(nextValueIx < 0 || nextValueIx >= getCacheSize()) {
				SqlFunction sqlf = new SqlFunction(getDataSource(), "SELECT " + getIncrementerName() + ".NEXTVAL FROM DUAL", type);
				sqlf.compile();
				valueCache = new long[getCacheSize()];
				nextValueIx = 0;
				for (int i = 0; i < getCacheSize(); i++) {
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
			setDirty(false); 			
		}
	
	}

}
