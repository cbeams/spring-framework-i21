package com.interface21.core;

import java.util.Comparator;

/**
 * Comparator implementation for Ordered objects,
 * sorting by order value ascending (resp. by priority descending).
 *
 * <p>Non-Ordered objects are treated as greatest order values,
 * thus ending up at the end of the list, in arbitrary order
 * (just like same order values of Ordered objects).
 *
 * @author Juergen Hoeller
 * @since 07.04.2003
 * @see Ordered
 */
public class OrderComparator implements Comparator {

  public int compare(Object o1, Object o2) {
    int i1 = (o1 instanceof Ordered ? ((Ordered) o1).getOrder() : Integer.MAX_VALUE);
	  int i2 = (o2 instanceof Ordered ? ((Ordered) o2).getOrder() : Integer.MAX_VALUE);

	  // direct evaluation instead of Integer.compareTo to avoid unnecessary object creation
	  if (i1 < i2)
			return -1;
	  else if (i1 > i2)
		  return 1;
	  else
		  return 0;
  }
}
