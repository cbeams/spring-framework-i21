package com.interface21.beans.propertyeditors;

import java.beans.PropertyEditorSupport;
import java.util.Properties;
import java.util.StringTokenizer;


/**
 * Editor for java.util.Properties objects. Handles
 * conversion from String to Properties object. Not
 * a GUI editor.
 * <br>NB: this editor must be registered with the JavaBeans API before it
 * will be available. Editors in this package are
 * registered by BeanWrapperImpl.
 * <br>The required format is defined in java.util.Properties documentation.
 * Each property must be on a new line.
 * @author Rod Johnson
 */
public class PropertiesEditor extends PropertyEditorSupport {
	
	/**
	 * @see PropertyEditor#setAsText(String)
	 */
	public void setAsText(String s) throws IllegalArgumentException {
		Properties props = new Properties();
		
		if (s == null)
			throw new IllegalArgumentException("Cannot set properties to null");

		//System.out.println("String is [" + s + "]");

		// Zap whitespace
		StringTokenizer st = new StringTokenizer(s);
		while (st.hasMoreTokens()) {
			String tok = st.nextToken();
			//System.out.println("Tok=[" + tok + "]");

			// Tokens look like "/welcome.html=mainController"
			int eqpos = tok.indexOf("=");
			if (eqpos == -1) {
				props.put(tok, "");
			}
			else {
				String key = tok.substring(0, eqpos);
				String value = tok.substring(eqpos + 1);
				props.put(key, value);
			}
			
		}


		// NB: the following code, using properties default works in JBoss,
		// but not Orion
//		try {
//			props.load(new ByteArrayInputStream(s.getBytes()));
//		}
//		catch (IOException ex) {
//			// Shouldn't happen
//			throw new IllegalArgumentException("Failed to read String");
//		}

		setValue(props);
	}

}

