package com.interface21.context.support;

import com.interface21.context.ApplicationContextException;
import com.interface21.context.MessageSource;

import com.interface21.core.*;
import com.interface21.core.ErrorCoded;

import java.io.Serializable;

import java.util.Locale;
import com.interface21.context.MessageSourceResolvable;

/**
 * Easy way to store all the necessary values an object needs
 * to resolve messages from things like a <code>Context</code>.
 *
 * @author Tony Falabella
 * @version $Id$
 */
public class MessageSourceResolvableImpl
    implements Serializable {
  //~ Instance variables -----------------------------------------------------

  private Locale locale = null;
  private Object controlledBy = null;
  private String defaultMessage = null;
  private String errorCode = ErrorCoded.UNCODED;
  private Object[] errorArgs = null;

  //~ Constructors -----------------------------------------------------------

  /**
   * USERS SHOULD NOT CALL THIS METHOD.  It is here so that
   * subclasses may create additional overloads of the constructorm.
   */
  public MessageSourceResolvableImpl() {
    super();
  }

  /**
   * Creates a new MessageSourceResolvableImpl object.
   *
   * @param controlledBy DOCUMENT ME!
   * @param errorCode DOCUMENT ME!
   * @param locale DOCUMENT ME!
   * @param errorArgs DOCUMENT ME!
   * @param defaultMessage DOCUMENT ME!
   */
  public MessageSourceResolvableImpl(Object controlledBy, String errorCode,
                                     Locale locale, Object[] errorArgs,
                                     String defaultMessage) {
    this.controlledBy = controlledBy;
    this.errorCode = errorCode;
    this.locale = locale;
    this.errorArgs = errorArgs;
    this.defaultMessage = defaultMessage;
  }

  /**
   * Using this overload, it is assumed that the caller will then
   * ultimately use the "getMessage(Locale locale)" method to resolve the
   * message.
   *
   * If it is not used in this manner, the ResourceBundleMessageSource
       * will take matters into it's own hands when deciding what locale you are in.
   * @param controlledBy
   * @param errorCode
   * @param errorArgs
   * @param defaultMessage
   */
  public MessageSourceResolvableImpl(Object controlledBy, String errorCode,
                                     Object[] errorArgs,
                                     String defaultMessage) {
    this.controlledBy = controlledBy;
    this.errorCode = errorCode;
    this.locale = null;
    this.errorArgs = errorArgs;
    this.defaultMessage = defaultMessage;
  }

  /**
   * Using this overload, it is assumed that the caller will then
   * ultimately use the "getMessage(Locale locale)" method to resolve the
   * message.
   *
   * If it is not used in this manner, the ResourceBundleMessageSource
       * will take matters into it's own hands when deciding what locale you are in.
   * @param controlledBy
   * @param errorCode
   * @param errorArgs
   */
  public MessageSourceResolvableImpl(Object controlledBy, String errorCode,
                                     Object[] errorArgs) {
    this(controlledBy, errorCode, errorArgs,
         "**** The " + controlledBy.getClass().getName() +
         " class was unable to resolve the message for errorCode=[" +
         errorCode + "], having locale=[null].");
  }

  /**
   * Creates a new MessageSourceResolvableImpl object.
   *
   * @param controlledBy DOCUMENT ME!
   * @param errorCode DOCUMENT ME!
   * @param locale DOCUMENT ME!
   */
  public MessageSourceResolvableImpl(Object controlledBy, String errorCode,
                                     Locale locale) {
    this(controlledBy, errorCode, locale, (Object[])null);
  }

  /**
   * Creates a new MessageSourceResolvableImpl object.
   *
   * @param controlledBy DOCUMENT ME!
   * @param errorCode DOCUMENT ME!
   * @param locale DOCUMENT ME!
   * @param defaultMessage DOCUMENT ME!
   */
  public MessageSourceResolvableImpl(Object controlledBy, String errorCode,
                                     Locale locale, String defaultMessage) {
    this(controlledBy, errorCode, locale, null, defaultMessage);
  }

  /**
   * Creates a new MessageSourceResolvableImpl object.
   *
   * @param controlledBy DOCUMENT ME!
   * @param errorCode DOCUMENT ME!
   */
  public MessageSourceResolvableImpl(Object controlledBy, String errorCode) {
    this(controlledBy, errorCode, (Object[])null);
  }

  /**
   * Creates a new MessageSourceResolvableImpl object.
   *
   * @param controlledBy DOCUMENT ME!
   * @param errorCode DOCUMENT ME!
   */
  public MessageSourceResolvableImpl(Object controlledBy,
                                     MessageSourceResolvable resolvable) {
    this(controlledBy, resolvable.getErrorCode(), resolvable.getLocale(),
         resolvable.getErrorArgs(), resolvable.getDefaultMessage());
  }

  /**
   * Creates a new MessageSourceResolvableImpl object.
   *
   * @param controlledBy DOCUMENT ME!
   * @param errorCode DOCUMENT ME!
   * @param defaultMessage DOCUMENT ME!
   */
  public MessageSourceResolvableImpl(Object controlledBy, String errorCode,
                                     String defaultMessage) {
    this(controlledBy, errorCode, (Object[])null, defaultMessage);
  }

  /**
   * Since we can't throw an exception within a constructor we will make the
   * defaultMessage be something like:
   *    "Unable to resolve the message for errorCode=[xx], locale=[yy]"
   * @param controlledBy
   * @param errorCode
   * @param locale
   * @param errorArgs
   */
  public MessageSourceResolvableImpl(Object controlledBy, String errorCode,
                                     Locale locale, Object[] errorArgs) {
    this(controlledBy, errorCode, locale, errorArgs,
         "**** The " + controlledBy.getClass().getName() +
         " class was unable to resolve the message for errorCode=[" +
         errorCode + "], having locale=[" + locale + "].");
  }

  /**
   * DOCUMENT ME!
   *
   * @return DOCUMENT ME!
   */
  public Object getControlledBy() {
    return controlledBy;
  }

  /**
   * DOCUMENT ME!
   *
   * @param defaultMessage DOCUMENT ME!
   */
  public void setDefaultMessage(String defaultMessage) {
    this.defaultMessage = defaultMessage;
  }

  /**
   * Return the defaultMessage that was used to resolve this message.
   * If message was not able to be resolved as message like the following
   * will be returned:
   *    "Unable to resolve the message for errorCode=[xx], locale=[yy]"
   * @return The defaultMessage that was used to resolve this message.
   */
  public String getDefaultMessage() {
    return defaultMessage;
  }

  /**
   * DOCUMENT ME!
   *
   * @param errorArgs DOCUMENT ME!
   */
  public void setErrorArgs(Object[] errorArgs) {
    this.errorArgs = errorArgs;
  }

  /**
   * Implementation of ParameterizableErrorCoded interface.
   * Return the array of args that was used to resolve this message.
   * @return An array of objects to be used as params to replace
   * placeholders within the errorCode message text.
   */
  public Object[] getErrorArgs() {
    return errorArgs;
  }

  /**
   * DOCUMENT ME!
   *
   * @param errorCode DOCUMENT ME!
   */
  public void setErrorCode(String errorCode) {
    this.errorCode = errorCode;
  }

  /**
   * Implementation of ErrorCoded interface.
   * Return the errorCode  that was used to resolve this message.
   * @return a String error code associated with this failure
   */
  public String getErrorCode() {
    return errorCode;
  }

  /**
   * DOCUMENT ME!
   *
   * @param locale DOCUMENT ME!
   */
  public void setLocale(Locale locale) {
    this.locale = locale;
  }

  /**
   * Return the locale that was used to resolve this message.
   */
  public Locale getLocale() {
    return locale;
  }

  /**
   * DOCUMENT ME!
   *
   * @return DOCUMENT ME!
   */
  public String toString() {
    StringBuffer errMsgBuff = new StringBuffer();

    if (controlledBy != null) {
      errMsgBuff.append(controlledBy.getClass().getName());
    }
    else {
      errMsgBuff.append(
          "No controller yet (possibly created from BeanFactory)");
    }

    if (controlledBy instanceof MessageSource) {
      errMsgBuff.append(": [" +
                        ( (MessageSource) getControlledBy()).getMessage(
          getErrorCode(), getLocale(),
          getErrorArgs(), getDefaultMessage()) +
                        "]; ");
    }

    errMsgBuff.append("errorCode=[" + getErrorCode() + "]; errorArgs=[");

    if (errorArgs == null) {
      errMsgBuff.append("null");
    }
    else {
      for (int i = 0; i < getErrorArgs().length; i++) {
        errMsgBuff.append("(" +
                          getErrorArgs()[i].getClass().getName() +
                          ")[" + getErrorArgs()[i] + "], ");
      }

      // Get rid of extra ", " at end
      errMsgBuff.deleteCharAt(errMsgBuff.length() - 1);
      errMsgBuff.deleteCharAt(errMsgBuff.length() - 1);
    }

    errMsgBuff.append("]; defaultMessage=[" + getDefaultMessage() + "]");

    return errMsgBuff.toString();
  }

  protected void setControlledBy(Object obj) {
    this.controlledBy = obj;
  }
}