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
import java.util.List;
import java.util.ArrayList;

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
 *
 * <p>For example:
 * com.mycompany.mycode.MyClass.myMethod=PROPAGATION_MANDATORY,ISOLATION_DEFAULT
 * <p>The transaction attribute string must be parseable by the
 * TransactionAttributeEditor in this package.
 *
 * TODO address method overloading and * or regexp syntax
 *
 * @author Rod Johnson
 * @since 26-Apr-2003
 * @version $Id$
 * @see com.interface21.transaction.interceptor.TransactionAttributeEditor
 */
public class TransactionAttributeSourceEditor extends PropertyEditorSupport {
	
	protected final Log logger = LogFactory.getLog(getClass());

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
	}
	
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
			
			// TODO address method overloading? At present this will
			// simply match all methods that have the given name.
			// Consider EJB syntax (int, String) etc.?
			Method[] methods = clazz.getDeclaredMethods();
			List matchingMethods = new ArrayList();
			for (int i = 0; i < methods.length; i++) {
				if (isMatch(methods[i].getName(), methodName)) {
					matchingMethods.add(methods[i]);
				}
			}
			if (matchingMethods.isEmpty())
				throw new TransactionUsageException("Couldn't find method '" + methodName + "' on " + clazz);
				
			// convert value to a transaction attribute
			TransactionAttributeEditor pe = newTransactionAttributeEditor();
			pe.setAsText(value);
			TransactionAttribute ta = (TransactionAttribute) pe.getValue();

			// register all matching methods
			for (Iterator it = matchingMethods.iterator(); it.hasNext();) {
				tasi.addTransactionalMethod((Method) it.next(), ta);
			}
		}
		catch (ClassNotFoundException ex) {
			throw new TransactionUsageException("Class '" + className + "' not found");
		}
	}

	/**
	 * Return if the given method name matches the mapped name.
	 * The default implementation checks for direct and "xxx*" matches.
	 * Can be overridden in subclasses.
	 * @param methodName the method name of the class
	 * @param mappedName the name in the descriptor
	 * @return if the names match
	 */
	protected boolean isMatch(String methodName, String mappedName) {
		return methodName.equals(mappedName) ||
		    (mappedName.endsWith("*") && methodName.startsWith(mappedName.substring(0, mappedName.length()-1)));
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
