package com.interface21.beans.propertyeditors;

import java.beans.PropertyEditorSupport;
import java.util.Locale;

import com.interface21.util.StringUtils;

/**
 * Editor for java.util.Locale, to directly feed a Locale property.
 * Expects the same syntax as Locale.toString, i.e. language + optionally
 * country + optionally variant, separated by "_" (e.g. "en", "en_US").
 * @author Juergen Hoeller
 * @since 26.05.2003
 */
public class LocaleEditor extends PropertyEditorSupport {

	public void setAsText(String text) {
		String[] parts = StringUtils.delimitedListToStringArray(text, "_");
		String language = parts.length > 0 ? parts[0] : "";
		String country = parts.length > 1 ? parts[1] : "";
		String variant = parts.length > 2 ? parts[2] : "";
		setValue(language.length() > 0 ? new Locale(language, country, variant) : null);
	}

}
