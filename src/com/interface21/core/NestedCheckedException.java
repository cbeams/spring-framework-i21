/**
 * Generic framework code included with 
 * <a href="http://www.amazon.com/exec/obidos/tg/detail/-/1861007841/">Expert One-On-One J2EE Design and Development</a>
 * by Rod Johnson (Wrox, 2002). 
 * This code is free to use and modify. 
 * Please contact <a href="mailto:rod.johnson@interface21.com">rod.johnson@interface21.com</a>
 * for commercial support.
 */

package com.interface21.core;

import java.io.PrintStream;
import java.io.PrintWriter;


/**
 * Handy class for wrapping runtime Exceptions with a root cause. This time-honoured
 * technique is no longer necessary in Java 1.4, which provides built-in
 * support for exception nesting. Thus exceptions in applications written to use Java 1.4 need not
 * extend this class.
 * <br/>printStackTrace() etc. are forwarded to the wrapped Exception.
 * <br/>Abstact to force the programmer to extend the class.
 * <br/>****TODO: could make this implement ErrorCoded to ensure internationalization.
 * The present assumption is that all application-specific exceptions that could be displayed to humans
 * (users, administrators etc.) will implement the ErrorCoded interface.
 * The similarity between this class and the NestedRuntimeException class is unavoidable, 
 * as Java forces these two classes to have different superclasses. (Ah, the inflexibility
 * of concrete inheritance!)
 * @author Rod Johnson
 * @version $Id$
 */
public abstract class NestedCheckedException extends Exception implements HasRootCause {

   /**
    * Nested Exception to hold wrapped exception.
    */
    private Throwable rootCause;
    
    
   /**
    * Constructs a <code>ExceptionWrapperException</code> with the specified
    * detail message.
    * @param s the detail message
    */
    public NestedCheckedException(String s) {
        super(s);
    }		
    
    
   /**
    * Constructs a <code>RemoteException</code> with the specified
    * detail message and nested exception.
    *
    * @param s the detail message
    * @param ex the nested exception
    */
    public NestedCheckedException(String s, Throwable ex) {
        super(s);
        rootCause = ex;
    }
	
	    
	
	/** May be null */
	public Throwable getRootCause() {
		return rootCause;
	}
   
  /**
     * Returns the detail message, including the message from the nested
     * exception if there is one.
     */
    public String getMessage() {
        if (rootCause == null)
            return super.getMessage();
        else
            return super.getMessage() + "; nested exception is: \n\t" + rootCause.toString();
    }
    
   /**
    * Prints the composite message and the embedded stack trace to
    * the specified stream <code>ps</code>.
    * @param ps the print stream
    */
    public void printStackTrace(PrintStream ps) {

        if (rootCause == null) {
            super.printStackTrace(ps);
        }
        else {
            ps.println(this);
            rootCause.printStackTrace(ps);
        } // else

    }
	
	 /**
    * Prints the composite message and the embedded stack trace to
    * the specified print writer <code>pw</code>
    * @param pw the print writer
    */
    public void printStackTrace(PrintWriter pw) {

        if (rootCause == null) {
            super.printStackTrace(pw);
        }
        else {     
			pw.println(this);
			rootCause.printStackTrace(pw);
        }

    }
    
   /**
    * Prints the composite message to <code>System.err</code>.
    */
    public void printStackTrace() {
        printStackTrace(System.err);
       // printStackTrace(System.out);
    }
  
    
}	// NestedCheckedException
