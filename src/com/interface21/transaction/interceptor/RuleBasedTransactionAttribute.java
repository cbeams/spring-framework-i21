/*
 * The Spring Framework is published under the terms
 * of the Apache Software License.
 */
 
package com.interface21.transaction.interceptor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * TransactionAttribute implementation that works out whether a 
 * given exception should cause transaction rollback by applying
 * a number of rollback rules, both positive and negative.
 * If no rules are relevant to the exception, it behaves
 * like DefaultTransactionAttribute (rolling back on
 * runtime exceptions).
 * <br>
 * The TransactionAttributeEditor property editor creates objects
 * of this class.
 * @since 09-Apr-2003
 * @version $Id$
 * @author Rod Johnson
 */
public class RuleBasedTransactionAttribute extends DefaultTransactionAttribute {
	
	protected final Log logger = LogFactory.getLog(getClass());

	private List rollbackRules;
	
	public RuleBasedTransactionAttribute() {
		this.rollbackRules = new ArrayList();
	}

	public RuleBasedTransactionAttribute(int propagationBehavior, List rollbackRules) {
		super(propagationBehavior);
		this.rollbackRules = rollbackRules;
	}

	public void setRollbackRules(List rollbackRules) {
		this.rollbackRules = rollbackRules;
	}

	public List getRollbackRules() {
		return rollbackRules;
	}

	/**
	 * Winning rule is the shallowest rule (that is, the closest
	 * in the inheritance hierarchy to the exception). If no rule applies (-1),
	 * return false.
	 * @see com.interface21.transaction.interceptor.TransactionAttribute#rollbackOn(java.lang.Throwable)
	 */
	public boolean rollbackOn(Throwable t) {
		logger.debug("Applying rules to determine whether transaction should rollback on " + t);
		RollbackRuleAttribute winner = null;
		int deepest = Integer.MAX_VALUE;

		if (this.rollbackRules != null) {
			for (Iterator iter = this.rollbackRules.iterator(); iter.hasNext();) {
				Object next = iter.next();
				// Ignore elements of unknown type
				if (next instanceof RollbackRuleAttribute) {
					RollbackRuleAttribute rule = (RollbackRuleAttribute) next;
					int depth = rule.getDepth(t);
					if (depth >= 0 && depth < deepest) {
						deepest = depth;
						winner = rule;
					}
				}
			}
		}
		logger.debug("Winning rollback rule is: " + winner);
		
		// User superclass behaviour (rollback on unchecked)
		// if no rule matches
		if (winner == null) {
			logger.debug("No relevant rollback rule found: applying superclass default");
			return super.rollbackOn(t);
		}
			
		return !(winner instanceof NoRollbackRuleAttribute);
	}

}
