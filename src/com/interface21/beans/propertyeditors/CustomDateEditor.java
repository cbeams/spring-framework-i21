package com.interface21.beans.propertyeditors;

import java.beans.PropertyEditorSupport;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

/**
 * PropertyEditor for Date, supporting a custom DateFormat.
 *
 * <p>This is not meant to be used as system PropertyEditor
 * but rather as locale-specific date editor within custom
 * controller code, to parse user-entered date strings into
 * Date properties of beans, and render them in the UI form.
 *
 * <p>In web MVC code, this editor will typically be
 * registered with binder.registerCustomEditor calls in an
 * implementation of BaseCommandController's initBinder method.
 *
 * @author Juergen Hoeller
 * @since 28.04.2003
 * @see com.interface21.validation.DataBinder#registerCustomEditor
 * @see com.interface21.web.servlet.mvc.BaseCommandController#initBinder
 */
public class CustomDateEditor extends PropertyEditorSupport {

	private final DateFormat dateFormat;

	private final boolean allowEmpty;

	/**
	 * Create a new instance, using the given DateFormat for
	 * parsing and rendering.
	 * <p>The allowEmpty parameter states if an empty String should
	 * be allowed for parsing, i.e. get interpreted as null value.
	 * Else, an IllegalArgumentException gets thrown in that case.
	 * @param dateFormat DateFormat to use for parsing and rendering
	 * @param allowEmpty if empty strings should be allowed
	 */
	public CustomDateEditor(DateFormat dateFormat, boolean allowEmpty) {
		this.dateFormat = dateFormat;
		this.allowEmpty = allowEmpty;
	}

	/**
	 * Parse the Date from the given text, using the specified DateFormat.
	 */
	public void setAsText(String text) throws IllegalArgumentException {
		if (this.allowEmpty && text.trim().equals("")) {
			// treat empty String as null value
			setValue(null);
		}
		else {
			try {
				setValue(this.dateFormat.parse(text));
			}
			catch (ParseException ex) {
				throw new IllegalArgumentException("Could not parse date: " + ex.getMessage());
			}
		}
	}

	/**
	 * Format the Date as String, using the specified DateFormat.
	 */
	public String getAsText() {
		return this.dateFormat.format((Date) getValue());
	}

}
