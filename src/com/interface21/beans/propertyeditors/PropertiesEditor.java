package com.interface21.beans.propertyeditors;

import java.beans.PropertyEditorSupport;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
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
 * @version $Id$
 */
public class PropertiesEditor extends PropertyEditorSupport {
	
	/**
	 * Any of these characters, if they're first after whitespace
	 * or first on a line, mean that the line is a comment and should
	 * be ignored.
	 */
	private final static String COMMENT_MARKERS = "#!";
	
	/**
	 * @see java.beans.PropertyEditor#setAsText(String)
	 */
	public void setAsText(String s) throws IllegalArgumentException {
		
		if (s == null)
			throw new IllegalArgumentException("Cannot set properties to null");
	
		Properties props =
			load(s); 
			//parse(s);
		setValue(props);
	}
	
	
	/**
	 * Parse the string ourselves.
	 * Workaround for Orion 1.6 issue
	 * @param s
	 * @return Properties
	 */
	private Properties parse(String s) {
		Properties props = new Properties();
		
		// Zap whitespace
		StringTokenizer st = new StringTokenizer(s);
		while (st.hasMoreTokens()) {
			String tok = st.nextToken();

			// Tokens look like "/welcome.html=mainController"
			int eqpos = tok.indexOf("=");
			if (eqpos == -1) {
				// We only have the property name, but
				// the value is the the empty string
				props.put(tok, "");
			}
			else {
				String key = tok.substring(0, eqpos);
				String value = tok.substring(eqpos + 1);
				props.put(key, value);
			}
		}
		
		return props;
	}	// parse


	/** NB: the following code, using properties default works in JBoss 3.0.0,
	 * but not Orion 1.6
	*/
	private Properties load(String s) {
		Properties props = new Properties();
		try {
			props.load(new ByteArrayInputStream(s.getBytes()));
			dropComments(props);
		}
		catch (IOException ex) {
			// Shouldn't happen
			throw new IllegalArgumentException("Failed to read String");
		}

		return props;
	}


	/**
	 * Remove comment lines. We shouldn't need to do this,
	 * according to java.util.Properties documentation, but
	 * if we don't we end up with properties like "#this=is a comment"
	 * if we have whitespace before the comment marker.
	 */
	private void dropComments(Properties props) {
		Iterator keys = props.keySet().iterator();
		List commentKeys = new LinkedList();
		while (keys.hasNext()) {
			String key = (String) keys.next();
			// A comment line starts with one of our comment markers
			if (key.length() > 0 && COMMENT_MARKERS.indexOf(key.charAt(0)) != -1) {
				// We can't actually remove it as we'll get a 
				// concurrent modification exception with the iterator
				commentKeys.add(key);
			}
		}
		for (int i = 0; i < commentKeys.size(); i++) {
			String key = (String) commentKeys.get(i);
			props.remove(key);
		}
	}

}

