
package com.interface21.beans;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;

import com.interface21.core.ErrorCoded;
import com.interface21.core.HasRootCause;

/**
 * Exception used by PropertyVetosException to wrap failures.
 * Clients can throw these.
 * @author  Rod Johnson
 * @version $Id$
 */
public class ErrorCodedPropertyVetoException extends PropertyVetoException implements ErrorCoded, HasRootCause {
	
	public static final String TYPE_MISMATCH_ERROR_CODE = "typeMismatch";
	
	private static final String METHOD_INVOCATION_ERROR_CODE = "methodInvocation";
	
	private String errorCode = UNCODED;
	
	private Throwable rootCause;
	
	/**
	 * Creates new <code>ErrorCodedPropertyVetoException</code>.
	 */
	public ErrorCodedPropertyVetoException(String mesg, PropertyChangeEvent e, String errorCode) {
		super(mesg, e);
		this.errorCode = errorCode;
		// No root cause
	}
	
	/* package */ ErrorCodedPropertyVetoException(PropertyVetoException ex) {
		super(ex.getMessage(), ex.getPropertyChangeEvent());
		if (ex instanceof ErrorCoded) {
			errorCode = ((ErrorCoded) ex).getErrorCode();
		}		
		rootCause = ex;
	}
	
	/* package */ ErrorCodedPropertyVetoException(TypeMismatchException ex) {
		super(ex.getMessage(), ex.getPropertyChangeEvent());
		rootCause = ex.getRootCause();
		errorCode = TYPE_MISMATCH_ERROR_CODE;
	}
	
	/* package */ ErrorCodedPropertyVetoException(MethodInvocationException ex) {
		super(ex.getMessage(), ex.getPropertyChangeEvent());
		rootCause = ex.getRootCause();
		errorCode = METHOD_INVOCATION_ERROR_CODE;
	}
	
	/* package */ ErrorCodedPropertyVetoException(Object source, InvalidPropertyValuesException.MissingFieldException ex) {
		super(ex.getMessage(), new PropertyChangeEvent(source, ex.getField(), null, null));
		//rootCause = ex.getRootCause();
		errorCode = ex.getErrorCode();
	}
	
	/** Implementation of ErrorCoded interface.
	 * Return the error code associated with this failure.
	 * The GUI can render this anyway it pleases, allowing for Int8ln etc.
	 * @return a String error code associated with this failure
	 */
	public String getErrorCode() {
		return errorCode;
	}	
	
	/** Implementation of HasRootCause */
	public Throwable getRootCause() {
		return rootCause;
	}
	
	public String toString() {
		return "ErrorCodedPropertyVetoException: errorCode=[" + getErrorCode() + "]; message=(" + getMessage() + ")";
	}
	
}


