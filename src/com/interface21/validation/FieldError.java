package com.interface21.validation;

/**
 * Encapsulates an field error, i.e. a reason for rejecting
 * a specific field value.
 *
 * <p>A field error gets created with a single code but uses
 * 2 codes for message resolution, in the following order:
 * first one consisting of code + separator + field name,
 * then one solely consisting of the code.
 * E.g.: code "typeMismatch", field name "age" -> first try
 * "typeMismatch.age", second try "typeMismatch".
 *
 * @author Rod Johnson, Juergen Hoeller
 * @version 1.0
 */
public class FieldError extends ObjectError {

	public static final String CODE_FIELD_SEPARATOR = ".";

	private final String field;

	private final Object rejectedValue;

	/**
	 * Create a new FieldError instance, using multiple codes.
	 * <p>This is only meant to be used by subclasses.
	 * @see com.interface21.context.MessageSourceResolvable#getCodes
	 */
	protected FieldError(String objectName, String field, Object rejectedValue,
	                     String[] codes, Object[] args, String defaultMessage) {
		super(objectName, codes, args, defaultMessage);
		this.field = field;
		this.rejectedValue = rejectedValue;
	}

	/**
	 * Create a new FieldError instance, using a default code.
	 */
	public FieldError(String objectName, String field, Object rejectedValue,
	                  String code, Object[] args, String defaultMessage) {
		this(objectName, field, rejectedValue,
		     new String[]{code + CODE_FIELD_SEPARATOR + field, code}, args, defaultMessage);
	}

	public String getField() {
		return field;
	}

	public Object getRejectedValue() {
		return rejectedValue;
	}

	public String toString() {
		return "FieldError occurred in object [" + getObjectName() + "] on [" +
		    this.field + "]: rejectedValue [" + this.rejectedValue + "]; " +
		    resolvableToString();
	}

}
