/*
 * The Spring Framework is published under the terms
 * of the Apache Software License.
 */
 
package com.interface21.transaction;

/**
 * Exception that represents a transaction failure caused by heuristics.
 * @author Rod, Johnson, Juergen Hoeller
 * @since 17-Mar-2003
 * @version $Revision$
 */
public class HeuristicCompletionException extends TransactionException {

	public static final int STATE_UNKNOWN = 0;
	public static final int STATE_COMMITTED = 1;
	public static final int STATE_ROLLED_BACK = 2;
	public static final int STATE_MIXED = 3;

	/**
	 * The outcome state of the transaction: have some or all resources been committed?
	 */
	private int outcomeState = STATE_UNKNOWN;

	public static String getStateString(int state) {
		switch (state) {
			case STATE_COMMITTED:
				return "committed";
			case STATE_ROLLED_BACK:
				return "rolled back";
			case STATE_MIXED:
				return "mixed";
			default:
				return "unknown";
		}
	}

	public HeuristicCompletionException(int outcomeState, Throwable ex) {
		super("Heuristic completion: outcome state is " + getStateString(outcomeState), ex);
		this.outcomeState = outcomeState;
	}

	public int getOutcomeState() {
		return outcomeState;
	}
}

