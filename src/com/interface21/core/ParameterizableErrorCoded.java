package com.interface21.core;

/**
 * Sub-interface that can be implemented by exceptions etc. that
 * are error coded. The error code is a String, rather than a number,
 * so it can be given user-readable values, such as "object.failureDescription".
 * These codes will be resolved by a com.interface21.context.MessageSource
 * object.
 *
 * The message itself may contain placeholders for parameters that
 * need to be filled in.  When this occurs it is necessary that your exception
 * implement the ParameterizableErrorCoded interface, since it is the "Object[] getErrorArgs"
 * method that will be called to fill in values for the placeholders.
 *
 * <br/>This interface is necessary because both runtime and checked
 * exceptions are useful, and they cannot share a common,
 * framework-specific, superclass.
 * @author  Rod Johnson / Tony Falabella
 * @version $Id$
 */
public interface ParameterizableErrorCoded extends ErrorCoded {
  /**
   * Return the Array of arguments that will be filled in for params within
   * the message that is stored for the errorCode that will be looked up.
   * NOTE: params look like "{0}", "{1,date}", "{2,time}" within a message.
   * @see <a href="http://java.sun.com/j2se/1.3/docs/api/java/text/MessageFormat.html">java.text.MessageFormat</a>
   * @return An array of Objects representing the args that
   * need to be passed into the String the errorCode will resolve to.  Return null if there are no args that the message needs.
   */
  Object[] getErrorArgs();

}
