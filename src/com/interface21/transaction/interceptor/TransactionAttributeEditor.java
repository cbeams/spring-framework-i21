/*
 * The Spring Framework is published under the terms
 * of the Apache Software License.
 */
 
package com.interface21.transaction.interceptor;

import java.beans.PropertyEditorSupport;
import java.util.LinkedList;
import java.util.List;

import com.interface21.transaction.TransactionDefinition;
import com.interface21.transaction.interceptor.NoRollbackRuleAttribute;
import com.interface21.transaction.interceptor.RollbackRuleAttribute;
import com.interface21.transaction.interceptor.RuleBasedTransactionAttribute;
import com.interface21.util.Constants;
import com.interface21.util.StringUtils;

/**
 * PropertyEditor for TransactionAttribute objects.
 * Takes Strings of form
 * PROPAGATION_CODE,ISOLATION_CODE,+Exception1,-Exception2
 * where only propagation code is required.
 * Propagation and isolation codes must use the names specified
 * in the PlatformTransactionManager class.
 * A + before an exception name substring indicates that
 * transactions should commit even if this exception is thrown;
 * a - that they should roll back.
 * @see com.interface21.transaction.PlatformTransactionManager
 * @see com.interface21.util.Constants
 * @since 24-Apr-2003
 * @version $Id$
 * @author Rod Johnson
 */
public class TransactionAttributeEditor extends PropertyEditorSupport {
	
	public static final char ROLLBACK_PREFIX = '-';
	
	public static final char COMMIT_PREFIX = '+';

	/** 
	 * Helper enabling us to lookup constant names in
	 * PlatformTransactionManager interface, to save us retyping them
	 * here, with the risk of errors.
	 */ 
	private static Constants txConstants = new Constants(TransactionDefinition.class);

	/**
	 * Format is TXREQ,TX_REQ_NEW,+RemoteException,-RuntimeException
	 * Null or the empty string means that the method is non transactional.
	 * @see java.beans.PropertyEditor#setAsText(java.lang.String)
	 */
	public void setAsText(String s) throws IllegalArgumentException {
		if (s == null || "".equals(s)) {
			setValue(null);
		}
		else {	
			// Tokenize it with ,s
			String[] tokens = StringUtils.commaDelimitedListToStringArray(s);
			// Must have length at least one
			int propagationCode = txConstants.asInt(tokens[0]);
			
			int isolationLevel = TransactionDefinition.ISOLATION_DEFAULT;
			
			List rollbackRules = new LinkedList();
			
			if (tokens.length >= 2) {
				// We have isolation as well
				isolationLevel = txConstants.asInt(tokens[1]);
			}
			
			if (tokens.length >= 3) {
				// We have isolation codes
				for (int i = 2; i < tokens.length; i++) {
					if (tokens[i].length() <= 5)
						throw new IllegalArgumentException("RollbackRule '" + tokens[i] + "' too short");
						char prefix = tokens[i].charAt(0);
					if (prefix != COMMIT_PREFIX && prefix != ROLLBACK_PREFIX)
						throw new IllegalArgumentException("RollbackRule '" + tokens[i] + "' must begin with " + COMMIT_PREFIX + " or " + ROLLBACK_PREFIX);
					String throwablePattern = tokens[i].substring(1);
					RollbackRuleAttribute rr = null;
					if (prefix == COMMIT_PREFIX) {
						rr = new NoRollbackRuleAttribute(throwablePattern);
					}
					else {
						rr = new RollbackRuleAttribute(throwablePattern);
					}
					
					rollbackRules.add(rr);
				}
			}
			
			setValue(new RuleBasedTransactionAttribute(propagationCode, isolationLevel, rollbackRules));
		}
	}

}
