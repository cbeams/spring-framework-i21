package com.interface21.validation;

import com.interface21.context.support.MessageSourceResolvableImpl;

/**
 * Encapsulates an object error, i.e. a global reason for rejection.
 *
 * <p>Normally, an ObjectError has a single code for message resolution.
 *
 * @author Juergen Hoeller
 * @since 10.03.2003
 * @see FieldError
 */
public class ObjectError extends MessageSourceResolvableImpl {

  private final String objectName;

  /**
   * Create a new ObjectError instance, using multiple codes.
   * <p>This is only meant to be used by subclasses like FieldError.
   * @see com.interface21.context.MessageSourceResolvable#getCodes
   */
	protected ObjectError(String objectName, String[] codes, Object[] args, String defaultMessage) {
    super(codes, args, defaultMessage);
    this.objectName = objectName;
  }

	/**
	 * Create a new ObjectError instance, using a default code.
	 */
	public ObjectError(String objectName, String code, Object[] args, String defaultMessage) {
	  this(objectName, new String[] {code}, args, defaultMessage);
	}

  public String getObjectName() {
    return objectName;
  }

  public String toString() {
    return "Error occurred in object [" + this.objectName + "]: " + resolvableToString();
  }

}
