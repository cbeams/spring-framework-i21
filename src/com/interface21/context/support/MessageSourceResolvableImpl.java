package com.interface21.context.support;

import java.io.Serializable;

import com.interface21.context.MessageSourceResolvable;

/**
 * Easy way to store all the necessary values an object needs
 * to resolve messages from things like a <code>Context</code>.
 *
 * @author Tony Falabella
 * @version $Id$
 */
public class MessageSourceResolvableImpl implements MessageSourceResolvable, Serializable {

  //~ Instance variables -----------------------------------------------------

  private String code = null;
  private Object[] args = null;
	private String defaultMessage = null;

  //~ Constructors -----------------------------------------------------------

  /**
   * USERS SHOULD NOT CALL THIS METHOD.  It is here so that
   * subclasses may create additional overloads of the constructorm.
   */
  public MessageSourceResolvableImpl() {
    super();
  }

  /**
   * Creates a new MessageSourceResolvableImpl object.
   *
   * @param code DOCUMENT ME!
   * @param args DOCUMENT ME!
   * @param defaultMessage DOCUMENT ME!
   */
  public MessageSourceResolvableImpl(String code, Object[] args, String defaultMessage) {
    this.code = code;
    this.args = args;
    this.defaultMessage = defaultMessage;
  }

  /**
   * Creates a new MessageSourceResolvableImpl object.
   *
   * @param code
   * @param args
   */
  public MessageSourceResolvableImpl(String code, Object[] args) {
    this(code, args, null);
  }

  /**
   * Creates a new MessageSourceResolvableImpl object.
   *
   * @param code DOCUMENT ME!
   */
  public MessageSourceResolvableImpl(String code) {
    this(code, null, null);
  }

  /**
   * Creates a new MessageSourceResolvableImpl object.
   *
   * @param code DOCUMENT ME!
   * @param defaultMessage DOCUMENT ME!
   */
  public MessageSourceResolvableImpl(String code, String defaultMessage) {
    this(code, null, defaultMessage);
  }

  /**
   * Creates a new MessageSourceResolvableImpl object.
   *
   * @param resolvable DOCUMENT ME!
   */
  public MessageSourceResolvableImpl(MessageSourceResolvable resolvable) {
    this(resolvable.getCode(), resolvable.getArgs(), resolvable.getDefaultMessage());
  }

  /**
   * DOCUMENT ME!
   *
   * @param code DOCUMENT ME!
   */
  public void setCode(String code) {
    this.code = code;
  }

  public String getCode() {
    return code;
  }

	/**
	 * DOCUMENT ME!
	 *
	 * @param args DOCUMENT ME!
	 */
	public void setArgs(Object[] args) {
	  this.args = args;
	}

	public Object[] getArgs() {
	  return args;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param defaultMessage DOCUMENT ME!
	 */
	public void setDefaultMessage(String defaultMessage) {
	  this.defaultMessage = defaultMessage;
	}

	/**
	 * Return the defaultMessage that was used to resolve this message.
	 * If message was not able to be resolved as message like the following
	 * will be returned:
	 *    "Unable to resolve the message for code=[xx], locale=[yy]"
	 * @return The defaultMessage that was used to resolve this message.
	 */
	public String getDefaultMessage() {
	  return defaultMessage;
	}

  /**
   * DOCUMENT ME!
   *
   * @return DOCUMENT ME!
   */
  protected String getDefaultToString() {
    StringBuffer msgBuff = new StringBuffer();

    msgBuff.append("code=[" + getCode() + "]; args=[");

    if (args == null) {
      msgBuff.append("null");
    }
    else {
      for (int i = 0; i < getArgs().length; i++) {
        msgBuff.append("(" + getArgs()[i].getClass().getName() +
                       ")[" + getArgs()[i] + "]");
	      if (i < getArgs().length-1)
		      msgBuff.append(", ");
      }
    }

    msgBuff.append("]; defaultMessage=[" + getDefaultMessage() + "]");

    return msgBuff.toString();
  }

	public String toString() {
		return getDefaultToString();
	}
}
