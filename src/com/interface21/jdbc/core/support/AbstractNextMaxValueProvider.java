package com.interface21.jdbc.core.support;

import java.sql.Types;

import com.interface21.core.InternalErrorException;
import com.interface21.jdbc.object.SqlFunction;
import com.interface21.jdbc.util.JdbcUtils;

/**
 * Abstract implementation of Classes NextMaxValueProvider used as 
 * inner Class by several NextMaxValueProvider classes.
 * It translates the key in different types: long, int, double and String. 
 * Subclasses should provide implementations of protected abstract method getNextKey(int).
 * @author <a href="mailto:jp.pawlak@tiscali.fr">Jean-Pierre Pawlak</a>
 * @author <a href="mailto:isabelle@meta-logix.com">Isabelle Muszynski</a>
 * @version $Id$
 */
public abstract class AbstractNextMaxValueProvider {

	/** Should the string result pre pre-pended with zeroes */
	protected boolean prefixWithZero;

	/** The length to which the string result should be pre-pended with zeroes */
	protected int paddingLength;

	public int getNextIntValue() {
		return (int)getNextKey(Types.INTEGER);
	}

	public long getNextLongValue() {
		return getNextKey(Types.BIGINT);
	}

	public double getNextDoubleValue() {
		return getNextKey(Types.INTEGER);
	}

	public String getNextStringValue() {
		String s = new Integer((int)getNextKey(Types.INTEGER)).toString();
		if (prefixWithZero) {
			int len = s.length();
			if (len < paddingLength + 1) {
				StringBuffer buff = new StringBuffer(paddingLength);
				for (int i = 0; i < paddingLength - len; i++)
				buff.append("0");
				buff.append(s);
				s = buff.toString();
			}
		}
		return s;
	}

	/**
	 * Sets the prefixWithZero.
	 * @param prefixWithZero The prefixWithZero to set
	 */
	public void setPrefixWithZero(boolean prefixWithZero, int length) {
		this.prefixWithZero = prefixWithZero;
		this.paddingLength = length;
	}
	
	/**
	 * Give the key to use as a long.
	 * @param type The Sql type of the key in the database.
	 * @return The key to use as a long. It will eventually be converted later
	 * in another format by the public concrete methods of this class.
	 */
	abstract protected long getNextKey(int type);

	/**
	 * @param sqlf	The compiled SqlFunction
	 * @param type	SqlType returned by the SqlFunction
	 * @return The result of the SqlFunction as a long
	 */
	protected long getLongValue(SqlFunction sqlf, int type) {
		long fLong = 0;
		switch(JdbcUtils.translateType(type)) {
			case Types.BIGINT:
				fLong = ((Long)sqlf.runGeneric()).longValue();
				break;
			case Types.INTEGER:
				fLong = ((Integer)sqlf.runGeneric()).intValue();
				break;
			case Types.NUMERIC:
				fLong = (long)((Double)sqlf.runGeneric()).doubleValue();
				break;
			case Types.VARCHAR:
				try {
					fLong = Long.parseLong((String)sqlf.runGeneric());
				} catch (NumberFormatException ex) {
					throw new InternalErrorException("Key value could not be converted to long");
				}
				break;
			default:
				throw new InternalErrorException("Unhandled SQL type: " + type);
		}
		return fLong;	
	}

}
