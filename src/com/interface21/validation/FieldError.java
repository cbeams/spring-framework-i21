package com.interface21.validation;

/**
 * Encapsulates an field error, i.e. a reason for rejecting a specific field value.
 * @author Rod Johnson, Juergen Hoeller, Tony Falabella
 * @version
 */
public class FieldError extends ObjectError {
	
  //~ Instance variables -----------------------------------------------------

	private String field;

  private Object rejectedValue;

  //~ Constructors -----------------------------------------------------------

  public FieldError() {
    super();
  }

  public FieldError(String objectName, String field, Object rejectedValue,
                    String errorCode, Object[] errorArgs, String defaultMessage) {
    super(objectName, errorCode, errorArgs, defaultMessage);
    this.rejectedValue = rejectedValue;
    this.field = field;

  }

  public FieldError(String objectName, String field, Object rejectedValue,
                    String errorCode, String defaultMessage) {
    super(objectName, errorCode, defaultMessage);
    this.rejectedValue = rejectedValue;
    this.field = field;

  }

  public FieldError(String objectName, String field, Object rejectedValue,
                    String errorCode, Object[] errorArgs) {
    this(objectName, field, rejectedValue, errorCode, errorArgs, null);
  }

  //~ Methods ----------------------------------------------------------------

  public String getField() {
    return field;
  }

  public Object getRejectedValue() {
    return rejectedValue;
  }

  public String toString() {
    return "FieldError occurred in object [" + objectName + "] on [" +
           field + "]: rejectedValue [" + rejectedValue + "]; " + getDefaultToString();
  }
}
