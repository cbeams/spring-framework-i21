package com.interface21.context;



import com.interface21.core.ErrorCoded;

import java.util.Locale;


public interface MessageSourceResolvable extends MessageSource {
    //~ Methods ----------------------------------------------------------------

    public Object getControlledBy();


    /**
     * Return the defaultMessage that was used to resolve this message.
     * If message was not able to be resolved as message like the following
     * will be returned:
     *    "Unable to resolve the message for errorCode=[xx], locale=[yy]"
     * @return The defaultMessage that was used to resolve this message.
     */
    public String getDefaultMessage();


    /**
     * Implementation of ParameterizableErrorCoded interface.
    * Return the array of args that was used to resolve this message.
     * @return An array of objects to be used as params to replace
     * placeholders within the errorCode message text.
     */
    public Object[] getErrorArgs();


    /**
     * Implementation of ErrorCoded interface.
     * Return the errorCode  that was used to resolve this message.
     * @return a String error code associated with this failure
     */
    public String getErrorCode();


    /**
     * Return the locale that was used to resolve this message.
     */
    public Locale getLocale();
}