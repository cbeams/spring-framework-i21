
package com.interface21.core;
import java.util.LinkedList;
import java.util.List;

/**
 * Test Visitor. Useful for testing.
 */
public class NodeCountVisitor implements Visitor {

	private int nodeCount;
	
	private List visited = new LinkedList();

	/**
	 * @see Visitor#enterComposite(Visitable, int)
	 */
	public boolean enterComposite(Visitable host, int depth) {
		recordVisit(host);
		return true;
	}
	
	protected void recordVisit(Visitable host) {
		if (visited.contains(host))
			throw new RuntimeException("Node [" + host + "] visited twice");
		++nodeCount;
		visited.add(host);
	}

	/**
	 * @see Visitor#exitComposite(Visitable)
	 */
	public void exitComposite(Visitable host) {
	}

	/**
	 * @see Visitor#visitLeaf(Visitable, int)
	 */
	public void visitLeaf(Visitable host, int depth) {
		recordVisit(host);
	}
	
	public int getNodeCount() {
		return nodeCount;
	}

}
