/*
 * The Spring Framework is published under the terms
 * of the Apache Software License.
 */
 
package com.interface21.transaction.interceptor;

import java.beans.PropertyEditorSupport;

import com.interface21.transaction.TransactionDefinition;
import com.interface21.util.StringUtils;

/**
 * PropertyEditor for TransactionAttribute objects. Takes Strings of form
 * <p><code>PROPAGATION_NAME,ISOLATION_NAME,readOnly,+Exception1,-Exception2</code>
 * <p>where only propagation code is required. For example:
 * <p><code>PROPAGATION_MANDATORY,ISOLATION_DEFAULT</code>
 *
 * <p>The tokens can be in any order. Propagation and isolation codes
 * must use the names of the constants in the TransactionDefinition class.
 *
 * <p>A "+" before an exception name substring indicates that
 * transactions should commit even if this exception is thrown;
 * a "-" that they should roll back.
 *
 * @author Rod Johnson
 * @since 24-Apr-2003
 * @version $Id$
 * @see com.interface21.transaction.TransactionDefinition
 * @see com.interface21.util.Constants
 */
public class TransactionAttributeEditor extends PropertyEditorSupport {

	public static final String READ_ONLY_MARKER = "readOnly";

	public static final String COMMIT_RULE_PREFIX = "+";

	public static final String ROLLBACK_RULE_PREFIX = "-";

	/**
	 * Format is PROPAGATION_NAME,ISOLATION_NAME,readOnly,+Exception1,-Exception2.
	 * Null or the empty string means that the method is non transactional.
	 * @see java.beans.PropertyEditor#setAsText(java.lang.String)
	 */
	public void setAsText(String s) throws IllegalArgumentException {
		if (s == null || "".equals(s)) {
			setValue(null);
		}
		else {	
			// tokenize it with ","
			String[] tokens = StringUtils.commaDelimitedListToStringArray(s);
			RuleBasedTransactionAttribute attr = new RuleBasedTransactionAttribute();

			for (int i = 0; i < tokens.length; i++) {
				String token = tokens[i];
				if (token.startsWith(TransactionDefinition.PROPAGATION_CONSTANT_PREFIX)) {
					attr.setPropagationBehaviorName(tokens[i]);
				}
				else if (token.startsWith(TransactionDefinition.ISOLATION_CONSTANT_PREFIX)) {
					attr.setIsolationLevelName(tokens[i]);
				}
				else if (token.equals(READ_ONLY_MARKER)) {
					attr.setReadOnly(true);
				}
				else if (token.startsWith(COMMIT_RULE_PREFIX)) {
					attr.getRollbackRules().add(new NoRollbackRuleAttribute(token.substring(1)));
				}
				else if (token.startsWith(ROLLBACK_RULE_PREFIX)) {
					attr.getRollbackRules().add(new RollbackRuleAttribute(token.substring(1)));
				}
				else {
					throw new IllegalArgumentException("Illegal transaction token: " + token);
				}
			}

			setValue(attr);
		}
	}

}
