package com.interface21.jdbc.core;

import com.interface21.dao.InvalidDataAccessApiUsageException;

/**
 * <P>Exception thrown when a unauthorised method from a ResultSet instance is 
 * invoked. Mostly thrown in the unauthorised ReadOnlyResultSet methods in the
 * JdbcTemplate.</P>
 * 
 * @author Yann Caroff
 */
public class InvalidResultSetMethodInvocationException extends InvalidDataAccessApiUsageException {
   
  private final String resultSetMethod;

  /**
   * Constructor for InvalidResultSetMethodInvocationException.
   * @param resultSetMethod
   */
  public InvalidResultSetMethodInvocationException(String resultSetMethod) {
    super("Cannot invoke [" + resultSetMethod + "()] ResultSet's method");
    this.resultSetMethod = resultSetMethod;
  }
  
  /**
   * Returns the name of the unauthorised ResultSet method called from a 
   * ReadOnlyResultSet.
   * @return the invalid method name
   */
  public String getResultSetMethod() {
    return resultSetMethod;
  }
}