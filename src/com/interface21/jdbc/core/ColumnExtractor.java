
package com.interface21.jdbc.core;

import java.sql.SQLException;
import java.sql.ResultSet;

/**
 * Interface to be implemented by objects that know how to extract
 * columns from a ResultSet and ensure that they are of a given type.
 * <br>Different drivers and databases may require slightly
 * different implementations.
 * <br>This is a useful concept, although there is little requirement
 * for it in the framework classes in this package.
 * @author  Rod Johnson
 * @since May 2, 2001
 */
public interface ColumnExtractor {

    /** Extract the given column from this row of the given ResultSet, ensuring that the
     * returned object is of the required type. A naive implementation of this might simply
     * call ResultSet.getObject(columnName). Note that if there is a fundamental mismatch between
     * column type and the required Java type, implementing classes are not required to succeed.
     * @param columnName name of the column we want from the ResultSet
     * @param requiredType class of object we must return
     * @param rs ResultSet to extract the column value from
     * @return the value of the specified column in the ResultSet as an instance of the
     * required type
     * @throws SQLException if there is any problem getting this column value. Implementations
     * of this interface do not need to worry about handling such exceptions; they can
     * assume they will only be called by code that correctly cleans up after any SQLExceptions
     */
    Object extractColumn(String columnName, Class requiredType, ResultSet rs) throws SQLException;
    
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
    Object extractColumn(int i, Class requiredType, ResultSet rs) throws SQLException;

}


