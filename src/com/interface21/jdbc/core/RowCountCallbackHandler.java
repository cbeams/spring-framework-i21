/**
 * Generic framework code included with 
 * <a href="http://www.amazon.com/exec/obidos/tg/detail/-/1861007841/">Expert One-On-One J2EE Design and Development</a>
 * by Rod Johnson (Wrox, 2002). 
 * This code is free to use and modify. 
 * Please contact <a href="mailto:rod.johnson@interface21.com">rod.johnson@interface21.com</a>
 * for commercial support.
 */

package com.interface21.jdbc.core;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * Implementation of RowCallbackHandler.
 * Convenient superclass for callback handlers.
 * We can either use this on its own (for example, in a test case, to ensure
 * that our result sets have valid dimensions), or use it as a superclass
 * for callback handlers that actually do something, and will benefit
 * from the dimension information it provides.
 * Can only be used once
 * @author  Rod Johnson
 * @since May 3, 2001
 * @version $Id$
 */
public class RowCountCallbackHandler implements RowCallbackHandler {

	/** Rows we've seen so far */
	private int rowCount;

	/** Columns we've seen so far */
	private int columnCount;

	/** Indexed from 0. Type (as in java.sql.Types) for the columns
	 * as returned by ResultSetMetaData object
	 */
	private int[] columnTypes;
	
	private String[] columnNames;

	/**
	 * Implementation of ResultSetCallbackHandler.
	 * Work out column size if this is the first row,
	 * otherwise just count rows.
	 * <br/>Subclasses can perform custom extraction or processing
	 * by overriding the processRow(ResultSet, int) method.
	 */
	public final void processRow(ResultSet rs) throws SQLException {
		if (rowCount == 0) {
			ResultSetMetaData rsmd = rs.getMetaData();
			columnCount = rsmd.getColumnCount();
			columnTypes = new int[columnCount];
			columnNames = new String[columnCount];
			for (int i = 0; i < columnCount; i++) {
				columnTypes[i] = rsmd.getColumnType(i + 1);
				columnNames[i] = rsmd.getColumnName(i + 1);
			}
			// Could also get column names
		}
		processRow(rs, rowCount++);
	}

	/** 
	 * Subclasses may override this to perform custom extraction
	 * or processing. This class's implementation does nothing.
	 * @param rs ResultSet to extract data from. This method is
	 * invoked for each row
	 * @param rowNum number of the current row (starting from 0)
	 */
	protected void processRow(ResultSet rs, int rowNum) throws SQLException {
	}

	/** 
	 * Return the types of the columns as java.sql.Types constants
	 * Valid after processRow is invoked the first time.
	 * @return the types of the columns as java.sql.Types constants.
	 * <b>Indexed from 0 to n-1.</b>
	 */
	public final int[] getColumnTypes() {
		return columnTypes;
	}
	
	/** 
	 * Return the names of the columns.
	 * Valid after processRow is invoked the first time.
	 * @return the names of the columns.
	 * <b>Indexed from 0 to n-1.</b>
	 */
	public final String[] getColumnNames() {
		return columnNames;
	}

	/** 
	 * Return the row count of this ResultSet
	 * Only valid after processing is complete
	 * @return the number of rows in this ResultSet
	 */
	public final int getRowCount() {
		return rowCount;
	}


	/** 
	 * Return the number of columns in this result set.
	 * Valid once we've seen the first row,
	 * so subclasses can use it during processing
	 * @return the number of columns in this result set
	 */
	public final int getColumnCount() {
		return columnCount;
	}

}
