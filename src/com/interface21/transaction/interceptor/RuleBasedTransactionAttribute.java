/*
 * The Spring Framework is published under the terms
 * of the Apache Software License.
 */
 
package com.interface21.transaction.interceptor;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.interface21.transaction.interceptor.DefaultTransactionAttribute;
import com.interface21.transaction.interceptor.NoRollbackRuleAttribute;
import com.interface21.transaction.interceptor.RollbackRuleAttribute;

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

	/** List of RollbackRule. May not be null after construction. */
	private List rollbackRules;
	
	public RuleBasedTransactionAttribute(List rollbackRules) {
		this(PROPAGATION_REQUIRED, ISOLATION_DEFAULT, rollbackRules);
	}

	/**
	 * @param propagationBehavior
	 * @param isolationLevel
	 */
	public RuleBasedTransactionAttribute(int propagationBehavior, int isolationLevel, List rollbackRules) {
		super(propagationBehavior, isolationLevel);
		if (rollbackRules == null || rollbackRules.size() == 0) {
			rollbackRules = new LinkedList();
		}
		this.rollbackRules = rollbackRules;
	}

	/**
	 * Winning rule is the shallowest rule (that is, the closest
	 * in the inheritance hierarchy to the exception). If no rule applies (-1),
	 * return false.
	 * @see com.interface21.transaction.interceptor.TransactionAttribute#rollBackOn(java.lang.Throwable)
	 */
	public boolean rollBackOn(Throwable t) {
		logger.debug("Applying rules to determine whether transaction should rollback on " + t);
		RollbackRuleAttribute winner = null;
		int deepest = Integer.MAX_VALUE;
		for (Iterator iter = this.rollbackRules.iterator(); iter.hasNext();) {
			Object next = iter.next();
			
			// Ignore elements of unknown type
			if (next instanceof RollbackRuleAttribute) {
				RollbackRuleAttribute rule = (RollbackRuleAttribute) next;
				int depth = rule.getDepth(t);
				//System.out.println("Depth=" + depth + "; deepest=" + deepest + ": checking rule" + rule);
				if (depth >= 0 && depth < deepest) {
					deepest = depth;
					winner = rule;
				}
			}
		}
		logger.debug("Winning rollback rule is " + winner);
		
		// User superclass behaviour (rollback on unchecked)
		// if no rule matches
		if (winner == null) {
			logger.debug("No relevant rollback rule found: applying superclass default");
			return super.rollBackOn(t);
		}
			
		return !(winner instanceof NoRollbackRuleAttribute);
	}

}
