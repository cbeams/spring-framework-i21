package com.interface21.jdbc.core;

import com.interface21.dao.DataAccessException;

/**
 * Interface that defines contract of incrementing
 * any data store field's maximum value. Works much like
 * sequence number generator. Typical implementations could use
 * RDBMS SQL and/or Stored Procedures to do the job.
 * @author <a href="mailto:dkopylenko@acs.rutgers.edu>Dmitriy Kopylenko</a>
 * @author <a href="mailto:isabelle@meta-logix.com">Isabelle Muszynski</a>
 * @version 1.0
 *
 * History 
 * 17/04/2003 : donated to Spring by Dmitriy Kopylenko
 * 19/04/2003 : modified by Isabelle Muszynski, added nextDoubleValue
 */
public interface DataFieldMaxValueIncrementer {
	
    /**
     * Increments data store field's max value as int
     * @return int next data store value such as <b>max + 1</b>
     * @throws DataAccessException
     */
    int nextIntValue() throws DataAccessException;
	
    /**
     * Increments data store field's max value as double
     * @return next data store value such as <b>max + 1</b>
     * @throws DataAccessException
     */
    double nextDoubleValue() throws DataAccessException;
	
    /**
     * Increments data store field's max value as String
     * @return next data store value such as <b>max + 1</b>
     * @throws DataAccessException
     */
    String nextStringValue() throws DataAccessException;

    /**
     * Generic method to retrieve the next value.
     * Legal values for keyClass are int.class, double.class, String.class,
     * Integer.class and Double.class
     * @throws IllegalArgumentException if keyClass is not one of 
     * the expected types
     * @throws DataAccessException
     **/
    Object nextValue(Class keyClass) throws DataAccessException;
}
