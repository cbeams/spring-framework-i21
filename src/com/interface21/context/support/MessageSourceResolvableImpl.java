package com.interface21.context.support;

import java.io.Serializable;

import com.interface21.context.MessageSourceResolvable;
import com.interface21.util.StringUtils;

/**
 * Easy way to store all the necessary values needed
 * to resolve messages from a MessageSource.
 *
 * @author Tony Falabella
 * @version $Id$
 */
public class MessageSourceResolvableImpl implements MessageSourceResolvable, Serializable {

  private final String[] codes;

  private final Object[] args;

	private final String defaultMessage;

  /**
   * Create a new instance, using multiple codes and a
   * default message.
   * @see MessageSourceResolvable#getCodes
   */
	public MessageSourceResolvableImpl(String[] codes, Object[] args, String defaultMessage) {
    this.codes = codes;
    this.args = args;
    this.defaultMessage = defaultMessage;
  }

	/**
	 * Create a new instance, using multiple codes.
	 * @see MessageSourceResolvable#getCodes
	 */
  public MessageSourceResolvableImpl(String[] codes, Object[] args) {
    this(codes, args, null);
  }

	/**
	 * Copy constructor: Create a new instance from another resolvable.
	 */
  public MessageSourceResolvableImpl(MessageSourceResolvable resolvable) {
    this(resolvable.getCodes(), resolvable.getArgs(), resolvable.getDefaultMessage());
  }

  public String[] getCodes() {
    return codes;
  }

	/**
	 * Return the default code of this resolvable,
	 * i.e. the last one in the codes array.
	 */
	public String getCode() {
		return (codes != null && codes.length > 0) ? codes[codes.length-1] : null;
	}

	public Object[] getArgs() {
	  return args;
	}

	public String getDefaultMessage() {
	  return defaultMessage;
	}

  protected String resolvableToString() {
    StringBuffer msgBuff = new StringBuffer();

    msgBuff.append("codes=[" + StringUtils.arrayToDelimitedString(getCodes(), ",") + "]; args=[");

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
		return resolvableToString();
	}

}
