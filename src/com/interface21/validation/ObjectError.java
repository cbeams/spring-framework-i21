package com.interface21.validation;

import com.interface21.context.support.MessageSourceResolvableImpl;

/**
 * Encapsulates an object error, i.e. a global reason for rejection.
 * @author Juergen Hoeller, Tony Falabella
 * @since 10.03.2003
 */
public class ObjectError extends MessageSourceResolvableImpl {

  //~ Instance variables -----------------------------------------------------

  protected String objectName;

  //~ Constructors -----------------------------------------------------------

  public ObjectError() {
    super();
  }

  public ObjectError(String objectName, String code, Object[] args, String defaultMessage) {
    super(code, args, defaultMessage);
    this.objectName = objectName;
  }

  public ObjectError(String objectName, String code, Object[] args) {
    super(code, args);
    this.objectName = objectName;
  }

	public ObjectError(String objectName, String code, String defaultMessage) {
	  super(code, defaultMessage);
	  this.objectName = objectName;
	}

  public ObjectError(String objectName, String code) {
    super(code);
    this.objectName = objectName;
  }

  //~ Methods ----------------------------------------------------------------

  public String getObjectName() {
    return objectName;
  }

  public String toString() {
    return "Error occurred in object [" + objectName + "]: " + getDefaultToString();
  }
}
