package com.interface21.beans;

import com.interface21.core.ErrorCoded;
import com.interface21.core.HasRootCause;
import com.interface21.core.ParameterizableErrorCoded;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;


/**
 * Exception used by PropertyVetosException to wrap failures.
 * Clients can throw these.
 * @author  Tony Falabella
 * @version $Id$
 */
public class ParameterizableErrorCodedPropertyVetoException
    extends ErrorCodedPropertyVetoException implements ParameterizableErrorCoded {
    //~ Instance variables -----------------------------------------------------

    private Object[] errorArgs = null;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new <code>ErrorCodedPropertyVetoException</code>.
     * This signature will be called when either the caller has an
     * object that has an ErrorCoded interface and
     * they are calling us with that or if they want to use the ErrorCoded
     * ability of this exception.
     * NOTE:  Mesg passed in will already have been "resolved".
     *        We will take the string passed in literally as is.
     *        This means that the caller of this method either created a literal string
     *        and passed it to us, OR the caller looked up the string value
     *        for a mesg themself in a msgCat BEFORE calling us.
     */
    public ParameterizableErrorCodedPropertyVetoException(String mesg,
                                                          PropertyChangeEvent e,
                                                          String errorCode) {
        super(mesg, e, errorCode);
        this.errorArgs = null;

        // No root cause
    }

    /**
     * Creates new <code>ErrorCodedPropertyVetoException</code>.
     * This signature will be called when either the caller has an
     * object that has an ParameterizableErrorCoded interface and
     * they are calling us with info from that or if they want to use
     * the ParameterizableErrorCoded ability of this exception.
     * NOTE:  Mesg passed in will NOT have been "resolved".
     *        We will take the errorCode passed in and attempt to look
     *        it up in a msgCatalog.  The resolved msg will then be
     *        used as the "msg" arg when calling the superclass.
     */
    public ParameterizableErrorCodedPropertyVetoException(PropertyChangeEvent e,
                                                          String errorCode,
                                                          Object[] errorArgs) {
        //****TODO : CODE ME
        // super(resolveMsg(errorCode, errorArgs), e);
        super(
                "ParameterizableErrorCodedPropertyVetoException(PropertyChangeEvent, String, Object[]) constructor NOT yet implemented", e);
        this.errorArgs = errorArgs;

        // No root cause
    }

    /* package */ ParameterizableErrorCodedPropertyVetoException(PropertyVetoException ex) {
        super(ex);

        /*
          Although the mesg has already been resolved, still
          save the args since someone else may want them if we rethrow
          this exception. (ie: perhaps they will re-resolve the msg
          from a msgCat based on the errorCode and will need the args for this
        */
        if (ex instanceof ParameterizableErrorCoded) {
            this.errorArgs = ((ParameterizableErrorCoded) ex).getErrorArgs();
        } else {
            this.errorArgs = null;
        }
    }

    /* package */ ParameterizableErrorCodedPropertyVetoException(TypeMismatchException ex) {
        super(ex);

        this.errorArgs = null;
    }

    /* package */ ParameterizableErrorCodedPropertyVetoException(MethodInvocationException ex) {
        super(ex);

        this.errorArgs = null;
    }

    /* package */ ParameterizableErrorCodedPropertyVetoException(Object source,
                                                     InvalidPropertyValuesException.MissingFieldException ex) {
        super(source, ex);


        // MissingFieldException only implements ErrorCoded interface
        this.errorArgs = null;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * Implementation of ParameterizableErrorCoded interface.
     * @return An array of objects to be used as params to replace
     * placeholders within the errorCode message text.
     */
    public Object[] getErrorArgs() {
        return errorArgs;
    }

    public String toString() {
        StringBuffer errMsgBuff = null;

        if (errorArgs == null) {
            errMsgBuff.append("ErrorCodedPropertyVetoException: errorCode=[" +
                              getErrorCode() + "]; message=(" + getMessage() +
                              ")");
        } else {
            errMsgBuff.append("ErrorCodedPropertyVetoException: errorCode=[" +
                              getErrorCode() + "]; errorArgs=[");

            for (int i = 0; i < errorArgs.length; i++)
                errMsgBuff.append("(" + errorArgs[i].getClass().getName() +
                                  ")[" + errorArgs[i] + "], ");


            // Get rid of extra ", " at end
            errMsgBuff.deleteCharAt(errMsgBuff.length() - 1);
            errMsgBuff.deleteCharAt(errMsgBuff.length() - 1);

            errMsgBuff.append("]; message=(" + getMessage() + ")");
        }

        return errMsgBuff.toString();
    }
}