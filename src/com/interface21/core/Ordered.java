package com.interface21.core;

/**
 * Interface that can be implemented by objects that should be
 * orderable, e.g. in a Collection. The actual order can be
 * interpreted as prioritization, the first object (with the
 * lowest order value) having the highest priority.
 *
 * @author Juergen Hoeller
 * @since 07.04.2003
 */
public interface Ordered {

  /**
   * Return the order value of this object,
   * higher value meaning greater in terms of sorting.
   * Normally starting with 0 or 1, Integer.MAX_VALUE
   * indicating greatest.
   * Same order values will result in arbitrary positions
   * for the affected objects.
   *
   * <p>Higher value can be interpreted as lower priority,
   * consequently the first object has highest priority
   * (somewhat analogous to Servlet "load-on-startup" values).
   *
   * @return the order value
   */
	public int getOrder();
}
