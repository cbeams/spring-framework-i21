package com.interface21.validation;

/**
 * Encapsulates a field error, i.e. a reason for rejecting a
 * specific field value.
 *
 * <p>A field error gets created with a single code but uses
 * 3 codes for message resolution, in the following order:
 * <ul>
 * <li>first: code + "." + object name + "." + field;
 * <li>then: code + "." + field;
 * <li>finally: code.
 * </ul>
 *
 * <p>E.g.: code "typeMismatch", field "age", object name "user":
 * <ul>
 * <li>1. try "typeMismatch.user.age";
 * <li>2. try "typeMismatch.age";
 * <li>3. try "typeMismatch".
 * </ul>
 *
 * <p>Thus, this resolution algorithm can be leveraged for example
 * to show specific messages for binding errors like "required"
 * and "typeMismatch":
 * <ul>
 * <li>at the object + field level ("age" field, but only on "user");
 * <li>field level (all "age" fields, no matter which object name);
 * <li>or general level (all fields, on any object).
 * </ul>
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 */
public class FieldError extends ObjectError {

	public static final String CODE_SEPARATOR = ".";

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
		     new String[] {code + CODE_SEPARATOR + objectName + CODE_SEPARATOR + field,
		                   code + CODE_SEPARATOR + field,
		                   code},
		     args, defaultMessage);
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
