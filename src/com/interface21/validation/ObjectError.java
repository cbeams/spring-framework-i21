package com.interface21.validation;

import com.interface21.context.MessageSource;
import com.interface21.context.support.MessageSourceResolvableImpl;

import java.io.Serializable;

import java.util.Locale;
import com.interface21.context.MessageSourceResolvable;
import com.interface21.context.NoSuchMessageException;

/**
 * Encapsulates an object error, i.e. a global reason for rejection.
 * @author Juergen Hoeller / Tony Falabella
 * @since 10.03.2003
 */
public class ObjectError
    extends MessageSourceResolvableImpl
    implements Serializable {
  //~ Instance variables -----------------------------------------------------

  protected String objectName;

  //~ Constructors -----------------------------------------------------------

  /**
   * USERS SHOULD NOT CALL THIS METHOD.  It is hear so that
   * subclasses may create additional overloads of this method.
   */
  public ObjectError() {
    super();
  }

  /**
   * Creates a new ObjectError object.
   *
   * @param objectName DOCUMENT ME!
   * @param errorCode DOCUMENT ME!
   * @param locale DOCUMENT ME!
   * @param errorArgs DOCUMENT ME!
   * @param defaultMessage DOCUMENT ME!
   */
  public ObjectError(String objectName, String errorCode, Locale locale,
                     Object[] errorArgs, String defaultMessage) {
    super(ObjectError.class, errorCode, locale, errorArgs, defaultMessage);
    this.objectName = objectName;
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
  public ObjectError(String objectName, String errorCode, Object[] errorArgs,
                     String defaultMessage) {
    super(ObjectError.class, errorCode, errorArgs, defaultMessage);

    this.objectName = objectName;
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
  public ObjectError(String objectName, String errorCode, Object[] errorArgs) {
    super(ObjectError.class, errorCode, errorArgs);

    this.objectName = objectName;
  }

  /**
   * Creates a new ObjectError object.
   *
   * @param objectName DOCUMENT ME!
   * @param errorCode DOCUMENT ME!
   * @param locale DOCUMENT ME!
   */
  public ObjectError(String objectName, String errorCode, Locale locale) {
    super(ObjectError.class, errorCode, locale);

    this.objectName = objectName;
  }

  /**
   * Creates a new ObjectError object.
   *
   * @param objectName DOCUMENT ME!
   * @param errorCode DOCUMENT ME!
   * @param locale DOCUMENT ME!
   * @param defaultMessage DOCUMENT ME!
   */
  public ObjectError(String objectName, String errorCode, Locale locale,
                     String defaultMessage) {
    super(ObjectError.class, errorCode, locale, defaultMessage);

    this.objectName = objectName;
  }

  /**
   * Creates a new ObjectError object.
   *
   * @param objectName DOCUMENT ME!
   * @param errorCode DOCUMENT ME!
   */
  public ObjectError(String objectName, String errorCode) {
    super(ObjectError.class, errorCode);

    this.objectName = objectName;
  }

  /**
   * Creates a new ObjectError object.
   *
   * @param controlledBy DOCUMENT ME!
   * @param errorCode DOCUMENT ME!
   */
  public ObjectError(String objectName,
                                     MessageSourceResolvable resolvable) {
    super(ObjectError.class, resolvable);
    this.objectName = objectName;
  }

  /**
   * Creates a new ObjectError object.
   *
   * @param objectName DOCUMENT ME!
   * @param errorCode DOCUMENT ME!
   * @param defaultMessage DOCUMENT ME!
   */
  public ObjectError(String objectName, String errorCode,
                     String defaultMessage) {
    super(ObjectError.class, errorCode, defaultMessage);

    this.objectName = objectName;
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
  public ObjectError(String objectName, String errorCode, Locale locale,
                     Object[] errorArgs) {
    super(ObjectError.class, errorCode, locale, errorArgs);
    this.objectName = objectName;
  }

  //~ Methods ----------------------------------------------------------------

  /**
   * DOCUMENT ME!
   *
   * @return DOCUMENT ME!
   */
  public String getObjectName() {
    return objectName;
  }

  /**
   * DOCUMENT ME!
   *
   * @return DOCUMENT ME!
   */
  public String toString() {
    StringBuffer errMsgBuff = new StringBuffer();

    errMsgBuff.append("Error occurred in object [" + objectName + "]");

    if (getControlledBy()instanceof MessageSource) {
      String resolvedMsg = null;
      try {
        resolvedMsg = ( (MessageSource) getControlledBy()).getMessage( (
            MessageSourceResolvable)this);
      }
      catch (NoSuchMessageException ex) {
        resolvedMsg =

            "**** The " + this.getClass().getName() +
            " class was unable to resolve the message for errorCode=[" +
            getErrorCode() + "], having locale=[" + getLocale() + "].";

      }
      errMsgBuff.append(": [" + resolvedMsg +
                        "]; ");
    }

    errMsgBuff.append("errorCode=[" + getErrorCode() + "]; errorArgs=[");

    if (getErrorArgs() == null) {
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
}