package com.interface21.context;

/**
 * Interface for objects that are suitable for message resolution,
 * e.g. validation errors.
 * @author Tony Falabella
 * @see MessageSource#getMessage
 */
public interface MessageSourceResolvable {

	/**
	 * Return the codes to be used to resolve this message,
	 * in the order they should get tried. The last code will
	 * therefore be the default one.
	 * @return a String code associated with this message
	 */
	public String[] getCodes();

	/**
	 * Return the array of args to be used to resolve this message.
	 * @return An array of objects to be used as params to replace
	 * placeholders within the code message text.
	 */
	public Object[] getArgs();

	/**
	 * Return the defaultMessage to be used to resolve this message.
	 * @return The defaultMessage, or null if no default.
	 */
	public String getDefaultMessage();
}
