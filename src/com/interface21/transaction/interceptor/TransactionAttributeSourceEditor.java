/*
 * The Spring Framework is published under the terms
 * of the Apache Software License.
 */
 
package com.interface21.transaction.interceptor;

import java.beans.PropertyEditorSupport;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.interface21.beans.propertyeditors.PropertiesEditor;
import com.interface21.transaction.TransactionUsageException;
import com.interface21.transaction.interceptor.MapTransactionAttributeSource;
import com.interface21.transaction.interceptor.TransactionAttribute;
import com.interface21.transaction.interceptor.TransactionAttributeEditor;

/**
 * PropertyEditor implementation. Can convert from Strings to TransactionAttributeSource.
 * Strings are in property syntax, with the form
 * FQN.methodName=&lt;transaction attribute string&gt;
 * For example:
 * com.mycompany.mycode.MyClass.myMethod=PROPAGATION_MANDATORY,ISOLATION_DEFAULT
 * The transaction attribute string must be parseable by the
 * TransactionAttributePropertyEditor in this package.
 * TODO address method overloading and * or regexp syntax
 * @since 26-Apr-2003
 * @version $Id$
 * @see com.interface21.transaction.interceptor.TransactionAttributeSourceEditor
 * @author Rod Johnson
 */
public class TransactionAttributeSourceEditor extends PropertyEditorSupport {
	
	protected final Log logger = LogFactory.getLog(getClass());

	/**
	 * @see java.beans.PropertyEditor#setAsText(java.lang.String)
	 */
	public void setAsText(String s) throws IllegalArgumentException {
		MapTransactionAttributeSource mtas = new MapTransactionAttributeSource();
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
				parseMethodDescriptor(name, value, mtas);
			}
		}
		
		setValue(mtas);
	}	// setAsText
	
	/**
	 * Handle a given property describing one transactional method.
	 * @param name name of the property. Contains class and method name.
	 * @param value value, which should be a string representation of a TransactionAttribute
	 * @param tasi private TransactionAttributeSource implementation that this
	 * method can continue to configure
	 */
	private void parseMethodDescriptor(String name, String value, MapTransactionAttributeSource tasi) {
		int lastDotIndex = name.lastIndexOf(".");
		if (lastDotIndex == -1) 
			throw new TransactionUsageException("'" + name + "' is not a valid method name: format is FQN.methodName");
		String className = name.substring(0, lastDotIndex);
		String methodName = name.substring(lastDotIndex + 1);
		logger.debug("Transactional method: " + className + "/" + methodName + 
				" with transaction attribute string " + value);
		
		try {
			Class clazz = Class.forName(className);
			
			// TODO address method overloading?
			// at present this will just match the first method
			// consider EJB syntax (int, String) etc.?
			Method[] methods = clazz.getDeclaredMethods();
			Method m = null;
			for (int i = 0; i < methods.length && m == null; i++) {
				if (methods[i].getName().equals(methodName)) {
					m = methods[i];
				}
			}
			if (m == null) 
				throw new TransactionUsageException("Couldn't find method '" + methodName + "' on " + clazz);
				
			// Convert value to a transaction attribute
			
			// TODO could keep this as in instance variable? Threading implications?
			TransactionAttributeEditor pe = transactionAttributePropertyEditor();
			pe.setAsText(value);
			TransactionAttribute ta = (TransactionAttribute) pe.getValue();
			
			tasi.addTransactionalMethod(m, ta);
		}
		catch (ClassNotFoundException ex) {
			throw new TransactionUsageException("Class '" + className + "' not found");
		}
	}	// handleOneProperty
	
	
	/**
	 * Getting a TransactionAttributePropertyEditor is in a separate
	 * protected method to allow for effective unit testing. 
	 * @return TransactionAttributePropertyEditor
	 */
	protected TransactionAttributeEditor transactionAttributePropertyEditor() {
		return new TransactionAttributeEditor();
	}
	
}
