/*
 * The Spring Framework is published under the terms
 * of the Apache Software License.
 */

package com.interface21.transaction.interceptor;

import java.beans.PropertyEditorSupport;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.interface21.beans.propertyeditors.PropertiesEditor;

/**
 * Property editor that can convert String into TransactionAttributeSource.
 * The transaction attribute string must be parseable by the
 * TransactionAttributeEditor in this package.
 *
 * <p>Strings are in property syntax, with the form:<br>
 * <code>FQCN.methodName=&lt;transaction attribute string&gt;</code>
 *
 * <p>For example:<br>
 * <code>com.mycompany.mycode.MyClass.myMethod=PROPAGATION_MANDATORY,ISOLATION_DEFAULT</code>
 *
 * <p>Note: Will register all overloaded methods for a given name.
 * Does not support explicit registration of certain overloaded methods.
 * Supports "xxx*" mappings, e.g. "notify*" for "notify" and "notifyAll".
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 26-Apr-2003
 * @version $Id$
 * @see com.interface21.transaction.interceptor.TransactionAttributeEditor
 */
public class TransactionAttributeSourceEditor extends PropertyEditorSupport {

	protected final Log logger = LogFactory.getLog(getClass());

	public void setAsText(String s) throws IllegalArgumentException {
		MapTransactionAttributeSource source = new MapTransactionAttributeSource();
		if (s == null || "".equals(s)) {
			// Leave value in property editor null
		}
		else {
			// Use properties editor to tokenize the hold string
			PropertiesEditor propertiesEditor = new PropertiesEditor();
			propertiesEditor.setAsText(s);
			Properties p = (Properties) propertiesEditor.getValue();

			// Now we have properties, process each one individually
			Set keys = p.keySet();
			for (Iterator iter = keys.iterator(); iter.hasNext();) {
				String name = (String) iter.next();
				String value = p.getProperty(name);
				parseMethodDescriptor(name, value, source);
			}
		}
		setValue(source);
	}

	/**
	 * Handle a given property describing one transactional method.
	 * @param name name of the property. Contains class and method name.
	 * @param value value, which should be a string representation of a TransactionAttribute
	 * @param source private TransactionAttributeSource implementation that this
	 * method can continue to configure
	 */
	private void parseMethodDescriptor(String name, String value, MapTransactionAttributeSource source) {
		// Convert value to a transaction attribute
		TransactionAttributeEditor pe = newTransactionAttributeEditor();
		pe.setAsText(value);
		TransactionAttribute attr = (TransactionAttribute) pe.getValue();
		// Register name and attribute
		source.addTransactionalMethod(name, attr);
	}

	/**
	 * Getting a TransactionAttributeEditor is in a separate
	 * protected method to allow for effective unit testing.
	 * @return a new TransactionAttributeEditor instance
	 */
	protected TransactionAttributeEditor newTransactionAttributeEditor() {
		return new TransactionAttributeEditor();
	}

}
