
package com.interface21.core;
/**
 * Test visitor. Useful during development.
 * Produces console output as it visits each node
 * that shows the tree's structure.
 * @author Rod Johnson
 * @version 1.0
 */
public class PrettyPrintVisitor implements Visitor {

	/** Creates new PrettyPrintVisitor */
    public PrettyPrintVisitor() {
    }

	/** Make an entire visit to a terminal node
	 * @param host node to be visited
	 * @param depth depth of traversal (starting at 0)
	 */
	public void visitLeaf(Visitable host, int depth) {
		System.out.println(padding(depth) + host);
	}

	/** Begin a visit to a composite element, with subnodes.
	 * Return false to just treat it as a terminal.
	 * <br/>Note that either this method or exitComposite(), depending
	 * on implementation, must do the actual visit to the composite,
	 * as visit() will never be called on this node unless this
	 * method returns false.
	 * @param host Node accepting the Visitor
	 * @param depth current depth in traversal, starting at 0.
	 * @return true to treat this as a composite, and traverse
	 * any children; false to treat it as a terminal, ignoring
	 * any children.
	 */
	public boolean enterComposite(Visitable host, int depth) {
		System.out.println(padding(depth) + host);
		return true;
	}

	/** End a visit to a composite element, with subnodes
	 * @param host node visited
	 */
	public void exitComposite(Visitable host) {
	}

	String padding = "                                                      ";

	private String padding(int depth) {
		return padding.substring(0, depth * 4);
	}

}
