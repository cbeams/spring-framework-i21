package com.interface21.core;
/**
 * Generic framework code included with 
 * <a href="http://www.amazon.com/exec/obidos/tg/detail/-/1861007841/">Expert One-On-One J2EE Design and Development</a>
 * by Rod Johnson (Wrox, 2002). 
 * This code is free to use and modify. 
 * Please contact <a href="mailto:rod.johnson@interface21.com">rod.johnson@interface21.com</a>
 * for commercial support.
 */

/**
*	Interface to be implemented by objects supporting the Visitor
*	design pattern.
*	@author Rod Johnson
*	@since 15.10.1999
*/
public interface  Visitable {
	
	/** Accept the given visitor.
	 * <br>There are two cases:
	 * <ol>
	 *        <li><i>The host (this Visitable object) is a composite element,
	 *        with subnodes:</i> The host is responsible for
	 *        calling the visitor's enterComposite() method,
	 *        calling the acceptVisitor(Visitor) method on any subnodes
	 *        implementing this interface, and calling the visitor's
	 *        exitComposite() method to end the visit. Note that the Visitor
	 *        can prevent the evaluation of subnodes by returning false
	 *        in enterComposite(), in which case this object should call
	 *        acceptVisitor() on itself instead of child nodes, and ignore
	 *        the call to exitComposite().
	 *        <li><i>The element is a leaf, with no subnodes:</i>
	 *        The element must call the Visitor's visit() method on itself
	 * </ol>
	 * @param v Visitor
	 * @param depth current depth of traversal, starting at 0
 */
	public void acceptVisitor(Visitor v, int depth);
}

