
package com.interface21.jdbc.core;

import java.sql.SQLException;
import java.sql.ResultSet;

/**
 * Default implementation of the ColumnExtractor interface. Tested
 * in Oracle 8 and Access. As it isn't a critical part of the
 * JDBC framework in this package, it has not been tested more rigorously.
 * @author  Rod Johnson
 * @since May 2, 2001
 */
public class DefaultColumnExtractor implements ColumnExtractor {
    
    //---------------------------------------------------------------------
    // Implementation of ColumnExtractor
    //---------------------------------------------------------------------
    /** Extract the given column from this row of the given ResultSet, ensuring that the
     * returned object is of the required type. 
     * <br>This implementation works on Oracle 8 and Access.
     * @param columnName name of the column we want from the ResultSet
     * @param requiredType class of object we must return
     * @param rs ResultSet to extract the column value from
     * @return the value of the specified column in the ResultSet as an instance of the
     * required type
     * @throws SQLException if there is any problem getting this column value
     */
    public Object extractColumn(String columnName, Class requiredType, ResultSet rs) throws SQLException {
        return extractColumn(columnName, -1, requiredType, rs);
    }   // extractColumn
    
    
     /** Extract the given column from this row of the given ResultSet, ensuring that the
     * returned object is of the required type. Should implement the same conversion
     * as extractColumn(columnName...).
     * @param i index (from 1) of the column we want from the ResultSet
     * @param requiredType class of object we must return
     * @param rs ResultSet to extract the column value from
     * @return the value of the specified column in the ResultSet as an instance of the
     * required type
     * @throws SQLException if there is any problem getting this column value. Implementations
     * of this interface do not need to worry about handling such exceptions; they can
     * assume they will only be called by code that correctly cleans up after any SQLExceptions
     */
    public Object extractColumn(int i, Class requiredType, ResultSet rs) throws SQLException {
        return extractColumn(null, i, requiredType, rs);
    }
    
	// Can do this with type maps?
	
	
    /** Private method to extract by either column name or index, depending on whether or not
     * column name is null. Implements extraction and conversion logic used by both public methods.
     */
    private Object extractColumn(String columnName, int i, Class requiredType, ResultSet rs) throws SQLException {
        Object value = null;       
        //JDBC 2.0 !?
        if (requiredType.equals(Integer.class) || requiredType.equals(int.class)) {
            value = new Integer((columnName != null) ? rs.getInt(columnName) : rs.getInt(i));
        }
        else if (requiredType.equals(java.math.BigDecimal.class)) {
            value = (columnName != null) ? rs.getBigDecimal(columnName) : rs.getBigDecimal(i);
        }
        else if (requiredType.equals(java.util.Date.class) || requiredType.equals(java.sql.Date.class)) {
            value = (columnName != null) ? rs.getDate(columnName) : rs.getDate(i);
        }
        else if (requiredType.equals(Long.class) || requiredType.equals(long.class)) {
            long l = (columnName != null) ? rs.getLong( columnName ) : rs.getLong(i);
            value = new Long(l);
        }
		 else if (requiredType.equals(float.class) || requiredType.equals(java.lang.Float.class)) {
			 // Otherwise will do as double
            float f = (columnName != null) ? rs.getFloat(columnName) : rs.getFloat(i);
			value = new Float(f);
        }
        else if (requiredType.equals(Boolean.class) || requiredType.equals(boolean.class)) {
            // Recognise column string values of Y/y, T/t, and TRUE/true as booleans
            value = (columnName != null) ? rs.getObject(columnName) : rs.getObject(i);
            if (value instanceof String) {
                String sval = ((String) value).toLowerCase();
                value = new Boolean("true".equals(sval) || "t".equals(sval) || "y".equals(sval));
            }
        }
        else {
            // Use the JDBC driver's default behaviour
            // It just _might_ work
            value = (columnName != null) ? rs.getObject(columnName) : rs.getObject(i);
        }
        return value;
    }   // extractColumn
    
}	// class DefaultColumnExtractor

