package com.interface21.validation;

import java.io.Serializable;

import java.util.Locale;
import com.interface21.context.MessageSourceResolvable;
import com.interface21.context.MessageSource;
import com.interface21.context.NoSuchMessageException;

/**
 *
 * @author Rod Johnson / Tony Falabella
 * @version
 */
public class FieldError
    extends ObjectError
    implements Serializable {
  //~ Instance variables -----------------------------------------------------

  private Object rejectedValue;
  private String field;

  //~ Constructors -----------------------------------------------------------

  // TYPOE?

  /**
   * USERS SHOULD NOT CALL THIS METHOD.
   * See <code>{@link AbstractMessageSourceResolver}</code> for description.
   */
  public FieldError() {
    super();
  }


  /**
   * Creates a new FieldError object.
   *
   * @param controlledBy DOCUMENT ME!
   * @param errorCode DOCUMENT ME!
   */
  public FieldError(String objectName, String field,
                                     MessageSourceResolvable resolvable) {
    super(objectName, resolvable);
    this.field = field;
  }

  /**
   * @param objectName
   * @param field
   * @param rejectedValue
   * @param errorCode
   * @param locale
   * @param errorArgs
   * @param defaultMessage
   */
  public FieldError(String objectName, String field, Object rejectedValue,
                    String errorCode, Locale locale, Object[] errorArgs,
                    String defaultMessage) {
    super(objectName, errorCode, locale, errorArgs, defaultMessage);
    this.rejectedValue = rejectedValue;
    this.field = field;

  }

  /**
   * @param objectName
   * @param field
   * @param rejectedValue
   * @param errorCode
   * @param locale
   * @param defaultMessage
   */
  public FieldError(String objectName, String field, Object rejectedValue,
                    String errorCode, Locale locale, String defaultMessage) {
    super(objectName, errorCode, locale, defaultMessage);
    this.rejectedValue = rejectedValue;
    this.field = field;

  }

  /**
   * Using this overload, it is assumed that the caller will then
   * ultimately use the "getMessage(Locale locale)" method to resolve the
   * message.
   *
   * If it is not used in this manner, the ResourceBundleMessageSource
       * will take matters into it's own hands when deciding what locale you are in.
   * @param objectName
   * @param field
   * @param rejectedValue
   * @param errorCode
   * @param errorArgs
   * @param defaultMessage
   */
  public FieldError(String objectName, String field, Object rejectedValue,
                    String errorCode, Object[] errorArgs,
                    String defaultMessage) {
    super(objectName, errorCode, errorArgs, defaultMessage);
    this.rejectedValue = rejectedValue;
    this.field = field;

  }

  /**
   * Creates a new FieldError object.
   *
   * @param objectName DOCUMENT ME!
   * @param errorCode DOCUMENT ME!
   * @param defaultMessage DOCUMENT ME!
   */
  public FieldError(String objectName, String field, Object rejectedValue,
                    String errorCode,
                    String defaultMessage) {
    super(objectName, errorCode, defaultMessage);
    this.rejectedValue = rejectedValue;
    this.field = field;
  }

  /**
   * Since we can't throw an exception within a constructor we will make the
   * defaultMessage be something like:
   *    "Unable to resolve the message for errorCode=[xx], locale=[yy]"
   * @param objectName
   * @param field
   * @param rejectedValue
   * @param errorCode
   * @param locale
   * @param errorArgs
   */
  public FieldError(String objectName, String field, Object rejectedValue,
                    String errorCode, Locale locale, Object[] errorArgs) {
    this(objectName, field, rejectedValue, errorCode, locale, errorArgs,
         "**** The " + FieldError.class.getName() +
         " class was unable to resolve the message for errorCode=[" +
         errorCode + "], having locale=[" + locale + "].");
  }

  /**
   * Using this overload, it is assumed that the caller will then
   * ultimately use the "getMessage(Locale locale)" method to resolve the
   * message.
   *
   * If it is not used in this manner, the ResourceBundleMessageSource
       * will take matters into it's own hands when deciding what locale you are in.
   * @param objectName
   * @param field
   * @param rejectedValue
   * @param errorCode
   * @param errorArgs
   */
  public FieldError(String objectName, String field, Object rejectedValue,
                    String errorCode, Object[] errorArgs) {
    this(objectName, field, rejectedValue, errorCode, errorArgs,
         "**** The " + FieldError.class.getName() +
         " class was unable to resolve the message for errorCode=[" +
         errorCode + "], having locale=[null].");
  }

  //~ Methods ----------------------------------------------------------------

  /**
   * DOCUMENT ME!
   *
   * @return DOCUMENT ME!
   */
  public String getField() {
    return field;
  }

  /** May be null if missing */
  public Object getRejectedValue() {
    return rejectedValue;
  }

  /**
   * DOCUMENT ME!
   *
   * @return DOCUMENT ME!
   */
  public String toString() {
    StringBuffer errMsgBuff = new StringBuffer();
    errMsgBuff.append("FieldError occurred in object [" + objectName + "] on [" +
                      field + "]");

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

    errMsgBuff.append("]; defaultMessage=[" + getDefaultMessage() + "]; ");
    errMsgBuff.append("rejectedValue=[" +
                      rejectedValue + "]");
    return errMsgBuff.toString();

  }
}