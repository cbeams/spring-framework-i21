package com.interface21.util;

import java.util.List;
import java.util.ArrayList;

import junit.framework.TestCase;

import com.interface21.beans.TestBean;

/**
 * @author Juergen Hoeller
 * @since 20.05.2003
 */
public class PagedListHolderTests extends TestCase {

	public PagedListHolderTests(String msg) {
		super(msg);
	}

	public void testPagedListHolder() {
		TestBean tb1 = new TestBean();
		tb1.setName("eva");
		tb1.setAge(25);
		TestBean tb2 = new TestBean();
		tb2.setName("juergen");
		tb2.setAge(99);
		TestBean tb3 = new TestBean();
		tb3.setName("Rod");
		tb3.setAge(32);
		List tbs = new ArrayList();
		tbs.add(tb1);
		tbs.add(tb2);
		tbs.add(tb3);

		PagedListHolder holder = new PagedListHolder(tbs);
		assertTrue("Correct source", holder.getSource() == tbs);
		assertTrue("Correct number of elements", holder.getNrOfElements() == 3);
		assertTrue("Correct number of pages", holder.getNrOfPages() == 1);
		assertTrue("Correct page size", holder.getPageSize() == PagedListHolder.DEFAULT_PAGE_SIZE);
		assertTrue("Correct page number", holder.getPageNr() == 0);
		assertTrue("Correct first element", holder.getFirstElementOnPage() == 0);
		assertTrue("Correct first element", holder.getLastElementOnPage() == 2);
		assertTrue("Correct page list size", holder.getPageList().size() == 3);
		assertTrue("Correct page list contents", holder.getPageList().get(0) == tb1);
		assertTrue("Correct page list contents", holder.getPageList().get(1) == tb2);
		assertTrue("Correct page list contents", holder.getPageList().get(2) == tb3);

		holder.setPageSize(2);
		assertTrue("Correct number of pages", holder.getNrOfPages() == 2);
		assertTrue("Correct page size", holder.getPageSize() == 2);
		assertTrue("Correct page number", holder.getPageNr() == 0);
		assertTrue("Correct first element", holder.getFirstElementOnPage() == 0);
		assertTrue("Correct first element", holder.getLastElementOnPage() == 1);
		assertTrue("Correct page list size", holder.getPageList().size() == 2);
		assertTrue("Correct page list contents", holder.getPageList().get(0) == tb1);
		assertTrue("Correct page list contents", holder.getPageList().get(1) == tb2);

		holder.setPageNr(1);
		assertTrue("Correct page number", holder.getPageNr() == 1);
		assertTrue("Correct first element", holder.getFirstElementOnPage() == 2);
		assertTrue("Correct first element", holder.getLastElementOnPage() == 2);
		assertTrue("Correct page list size", holder.getPageList().size() == 1);
		assertTrue("Correct page list contents", holder.getPageList().get(0) == tb3);

		holder.setPageSize(3);
		assertTrue("Correct number of pages", holder.getNrOfPages() == 1);
		assertTrue("Correct page size", holder.getPageSize() == 3);
		assertTrue("Correct page number", holder.getPageNr() == 0);
		assertTrue("Correct first element", holder.getFirstElementOnPage() == 0);
		assertTrue("Correct first element", holder.getLastElementOnPage() == 2);

		holder.setPageSize(2);
		holder.setPageNr(1);
		holder.sortByProperty("name", true, false);
		assertTrue("Correct source", holder.getSource() == tbs);
		assertTrue("Correct number of elements", holder.getNrOfElements() == 3);
		assertTrue("Correct number of pages", holder.getNrOfPages() == 2);
		assertTrue("Correct page size", holder.getPageSize() == 2);
		assertTrue("Correct page number", holder.getPageNr() == 0);
		assertTrue("Correct first element", holder.getFirstElementOnPage() == 0);
		assertTrue("Correct first element", holder.getLastElementOnPage() == 1);
		assertTrue("Correct page list size", holder.getPageList().size() == 2);
		assertTrue("Correct page list contents", holder.getPageList().get(0) == tb3);
		assertTrue("Correct page list contents", holder.getPageList().get(1) == tb1);

		holder.setPageNr(1);
		assertTrue("Correct page list size", holder.getPageList().size() == 1);
		assertTrue("Correct page list contents", holder.getPageList().get(0) == tb2);
	}

}
