package com.interface21.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import junit.framework.TestCase;

import com.interface21.beans.BeanWrapper;
import com.interface21.beans.BeanWrapperImpl;
import com.interface21.beans.MutableSortDefinition;
import com.interface21.beans.TestBean;

/**
 * @author Juergen Hoeller
 * @author Jean-Pierre PAWLAK
 * @since 20.05.2003
 */
public class PagedListHolderTests extends TestCase {

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
		assertTrue("Correct page number", holder.getPage() == 0);
		assertTrue("First page", holder.isFirstPage());
		assertTrue("Last page", holder.isLastPage());
		assertTrue("Correct first element", holder.getFirstElementOnPage() == 0);
		assertTrue("Correct first element", holder.getLastElementOnPage() == 2);
		assertTrue("Correct page list size", holder.getPageList().size() == 3);
		assertTrue("Correct page list contents", holder.getPageList().get(0) == tb1);
		assertTrue("Correct page list contents", holder.getPageList().get(1) == tb2);
		assertTrue("Correct page list contents", holder.getPageList().get(2) == tb3);

		holder.setPageSize(2);
		assertTrue("Correct number of pages", holder.getNrOfPages() == 2);
		assertTrue("Correct page size", holder.getPageSize() == 2);
		assertTrue("Correct page number", holder.getPage() == 0);
		assertTrue("First page", holder.isFirstPage());
		assertFalse("Last page", holder.isLastPage());
		assertTrue("Correct first element", holder.getFirstElementOnPage() == 0);
		assertTrue("Correct first element", holder.getLastElementOnPage() == 1);
		assertTrue("Correct page list size", holder.getPageList().size() == 2);
		assertTrue("Correct page list contents", holder.getPageList().get(0) == tb1);
		assertTrue("Correct page list contents", holder.getPageList().get(1) == tb2);

		holder.setPage(1);
		assertTrue("Correct page number", holder.getPage() == 1);
		assertFalse("First page", holder.isFirstPage());
		assertTrue("Last page", holder.isLastPage());
		assertTrue("Correct first element", holder.getFirstElementOnPage() == 2);
		assertTrue("Correct first element", holder.getLastElementOnPage() == 2);
		assertTrue("Correct page list size", holder.getPageList().size() == 1);
		assertTrue("Correct page list contents", holder.getPageList().get(0) == tb3);

		holder.setPageSize(3);
		assertTrue("Correct number of pages", holder.getNrOfPages() == 1);
		assertTrue("Correct page size", holder.getPageSize() == 3);
		assertTrue("Correct page number", holder.getPage() == 0);
		assertTrue("First page", holder.isFirstPage());
		assertTrue("Last page", holder.isLastPage());
		assertTrue("Correct first element", holder.getFirstElementOnPage() == 0);
		assertTrue("Correct first element", holder.getLastElementOnPage() == 2);

		holder.setPageSize(2);
		holder.setPage(1);
		((MutableSortDefinition) holder.getSort()).setProperty("name");
		((MutableSortDefinition) holder.getSort()).setIgnoreCase(false);
		holder.resort();
		assertTrue("Correct source", holder.getSource() == tbs);
		assertTrue("Correct number of elements", holder.getNrOfElements() == 3);
		assertTrue("Correct number of pages", holder.getNrOfPages() == 2);
		assertTrue("Correct page size", holder.getPageSize() == 2);
		assertTrue("Correct page number", holder.getPage() == 0);
		assertTrue("First page", holder.isFirstPage());
		assertFalse("Last page", holder.isLastPage());
		assertTrue("Correct first element", holder.getFirstElementOnPage() == 0);
		assertTrue("Correct first element", holder.getLastElementOnPage() == 1);
		assertTrue("Correct page list size", holder.getPageList().size() == 2);
		assertTrue("Correct page list contents", holder.getPageList().get(0) == tb3);
		assertTrue("Correct page list contents", holder.getPageList().get(1) == tb1);

		((MutableSortDefinition) holder.getSort()).setProperty("name");
		holder.resort();
		assertTrue("Correct page list contents", holder.getPageList().get(0) == tb2);
		assertTrue("Correct page list contents", holder.getPageList().get(1) == tb1);

		((MutableSortDefinition) holder.getSort()).setProperty("name");
		holder.resort();
		assertTrue("Correct page list contents", holder.getPageList().get(0) == tb3);
		assertTrue("Correct page list contents", holder.getPageList().get(1) == tb1);

		holder.setPage(1);
		assertTrue("Correct page list size", holder.getPageList().size() == 1);
		assertTrue("Correct page list contents", holder.getPageList().get(0) == tb2);

		((MutableSortDefinition) holder.getSort()).setProperty("age");
		holder.resort();
		assertTrue("Correct page list contents", holder.getPageList().get(0) == tb1);
		assertTrue("Correct page list contents", holder.getPageList().get(1) == tb3);

		((MutableSortDefinition) holder.getSort()).setIgnoreCase(true);
		holder.resort();
		assertTrue("Correct page list contents", holder.getPageList().get(0) == tb1);
		assertTrue("Correct page list contents", holder.getPageList().get(1) == tb3);
	}

	public void testRefreshablePagedListHolder() {
		String s;
		int n;
		MockSourceProvider provider = new MockSourceProvider();
		RefreshablePagedListHolder holder = new RefreshablePagedListHolder(provider);
		holder.setFilter(new MockFilter());
		BeanWrapper bw = new BeanWrapperImpl(holder);

		holder.refresh(false);
		n = holder.getNrOfElements();
		assertTrue("A:number of elements: " + n, n == 12);
		n = holder.getNrOfPages();
		assertTrue("A:number of pages" + n, n == 2);
		n = holder.getPageSize();
		assertTrue("A:page size: " + n, n == 10);
		n = holder.getPage();
		assertTrue("A:page number: " + n, n == 0);
		n = holder.getFirstElementOnPage();
		assertTrue("A:first element: " + n, n == 0);
		n = holder.getLastElementOnPage();
		assertTrue("A:last element: " + n, n == 9);
		n = holder.getPageList().size();
		assertTrue("A:page list size: " + n, n == 10);
		assertTrue("A:correct filter", holder.getFilter() instanceof MockFilter);
		n = holder.getMaxLinkedPages();
		assertTrue("A:maxLinkedPages: " + n, n == RefreshablePagedListHolder.DEFAULT_MAX_LINKED_PAGES);
		n = holder.getFirstLinkedPage();
		assertTrue("A:first display page: " + n, n == 0);
		n = holder.getLastLinkedPage();
		assertTrue("A:last display page: " + n, n == 1);
		assertTrue("A:sort must be not null", holder.getSort().getProperty() != null);
		n = holder.getSort().getProperty().length();
		assertTrue("A:sort: " + n, n == 0);
		s = ((TestBean)holder.getPageList().get(0)).getName();
		assertTrue("A:page list contents0: " + s, s.equals("Calogero"));
		s = ((TestBean)holder.getPageList().get(4)).getName();
		assertTrue("A:page list contents4: " + s, s.equals("Jesus"));
		n = ((MockSourceProvider) holder.getSourceProvider()).getNrOfCalls();
		assertTrue("Correct number of times called: " + n, n == 1);

		// Go to the second page
		Map cmd = new HashMap();
		cmd.put("page", "1");
		bw.setPropertyValues(cmd);
		holder.refresh(false);
		n = holder.getPage();
		assertTrue("B:page number with array map: " + n, n == 1);
		n = holder.getPageList().size();
		assertTrue("B:page list size: " + n, n == 2);
		n = holder.getFirstElementOnPage();
		assertTrue("B:first element: " + n, n == 10);
		n = holder.getLastElementOnPage();
		assertTrue("B:last element: " + n, n == 11);
		s = ((TestBean)holder.getPageList().get(0)).getName();
		assertTrue("B:page list contents0: " + s, s.equals("Toma"));
		s = ((TestBean)holder.getPageList().get(1)).getName();
		assertTrue("B:page list contents1: " + s, s.equals("Sergio"));

		// Return to the first page and sort by name setting the page size to 5
		cmd.clear();
		cmd.put("sort.property", "name");
		cmd.put("pageSize", "2");
		bw.setPropertyValues(cmd);
		holder.refresh(false);
		n = holder.getNrOfPages();
		assertTrue("C:number of pages: " + n, n == 6);
		n = holder.getPageSize();
		assertTrue("C:page size: " + n, n == 2);
		n = holder.getPage();
		assertTrue("C:page number: " + n, n == 0);
		n = holder.getFirstLinkedPage();
		assertTrue("C:first display page: " + n, n == 0);
		n = holder.getLastLinkedPage();
		assertTrue("C:last display page: " + n, n == 5);
		n = holder.getFirstElementOnPage();
		assertTrue("C:first element: " + n, n == 0);
		n = holder.getLastElementOnPage();
		assertTrue("C:last element: " + n, n == 1);
		n = holder.getPageList().size();
		assertTrue("C:page list size: " + n, n == 2);
		s = ((TestBean)holder.getPageList().get(0)).getName();
		assertTrue("C:page list contents0: " + s, s.equals("amid"));
		s = ((TestBean)holder.getPageList().get(1)).getName();
		assertTrue("C:page list contents1: " + s, s.equals("Aziz"));
		assertTrue("C:sort must be not null", holder.getSort().getProperty() != null);
		s = holder.getSort().getProperty();
		assertTrue("C:sort: " + s, s.equals("name"));
		n = ((MockSourceProvider) holder.getSourceProvider()).getNrOfCalls();
		assertTrue("Correct number of times called: " + n, n == 1);

		// Ignore case in sort, maxLinkedPages set to 3
		cmd.clear();
		cmd.put("sort.ignoreCase", "false");
		cmd.put("maxLinkedPages", "3");
		bw.setPropertyValues(cmd);
		holder.refresh(false);
		s = ((TestBean)holder.getPageList().get(0)).getName();
		assertTrue("D:page list contents0: " + s, s.equals("Aziz"));
		s = ((TestBean)holder.getPageList().get(1)).getName();
		assertTrue("D:page list contents1: " + s, s.equals("Calogero"));
		n = holder.getFirstLinkedPage();
		assertTrue("D:first display page: " + n, n == 0);
		n = holder.getLastLinkedPage();
		assertTrue("D:last display page: " + n, n == 2);
		n = ((MockSourceProvider) holder.getSourceProvider()).getNrOfCalls();
		assertTrue("Correct number of times called: " + n, n == 1);

		// Go to the fourth page
		holder.setPage(3);
		s = ((TestBean)holder.getPageList().get(0)).getName();
		assertTrue("E:page list contents0: " + s, s.equals("Giuseppe"));
		s = ((TestBean)holder.getPageList().get(1)).getName();
		assertTrue("E:page list contents1: " + s, s.equals("Jesus"));
		n = holder.getFirstLinkedPage();
		assertTrue("E:first display page: " + n, n == 2);
		n = holder.getLastLinkedPage();
		assertTrue("E:last display page: " + n, n == 4);

		// Filter the name
		cmd.clear();
		cmd.put("filter.name", "G");
		bw.setPropertyValues(cmd);
		holder.refresh(false);
		n = holder.getNrOfPages();
		assertTrue("F:number of pages: " + n, n == 1);
		n = holder.getPageSize();
		assertTrue("F:page size: " + n, n == 2);
		n = holder.getPage();
		assertTrue("F:page number: " + n, n == 0);
		n = holder.getFirstLinkedPage();
		assertTrue("F:first display page: " + n, n == 0);
		n = holder.getLastLinkedPage();
		assertTrue("F:last display page: " + n, n == 0);
		n = holder.getFirstElementOnPage();
		assertTrue("F:first element: " + n, n == 0);
		n = holder.getLastElementOnPage();
		assertTrue("F:last element: " + n, n == 1);
		n = holder.getPageList().size();
		assertTrue("F:page list size: " + n, n == 2);
		s = ((TestBean)holder.getPageList().get(0)).getName();
		assertTrue("F:page list contents0: " + s, s.equals("Garcia"));
		s = ((TestBean)holder.getPageList().get(1)).getName();
		assertTrue("F:page list contents1: " + s, s.equals("Giuseppe"));
		s = ((MockFilter) holder.getFilter()).getName();
		assertTrue("F:filter name: " + s, s.equals("G"));
		n = ((MockSourceProvider) holder.getSourceProvider()).getNrOfCalls();
		assertTrue("Correct number of times called: " + n, n == 2);

		// Request sort on name will reverse the sort
		cmd.clear();
		cmd.put("sort.property", "name");
		bw.setPropertyValues(cmd);
		holder.refresh(false);
		s = ((TestBean)holder.getPageList().get(0)).getName();
		assertTrue("G:page list contents0: " + s, s.equals("Giuseppe"));
		s = ((TestBean)holder.getPageList().get(1)).getName();
		assertTrue("G:page list contents1: " + s, s.equals("Garcia"));

		// Filter the age
		cmd.clear();
		cmd.put("filter.age", "34");
		bw.setPropertyValues(cmd);
		holder.refresh(false);
		n = holder.getLastElementOnPage();
		assertTrue("H:last element: " + n, n == 0);
		n = holder.getPageList().size();
		assertTrue("H:page list size: " + n, n == 1);
		s = ((TestBean)holder.getPageList().get(0)).getName();
		assertTrue("H:page list contents0: " + s, s.equals("Garcia"));
		s = ((MockFilter) holder.getFilter()).getAge();
		assertTrue("H:filter age: " + s, s.equals("34"));
		s = ((MockFilter) holder.getFilter()).getName();
		assertTrue("H:filter name: " + s, s.equals("G"));
		n = ((MockSourceProvider) holder.getSourceProvider()).getNrOfCalls();
		assertTrue("Correct number of times called: " + n, n == 3);

		// Remove the name filter
		// The sort is always name reversed
		cmd.clear();
		cmd.put("filter.name", "");
		bw.setPropertyValues(cmd);
		holder.refresh(false);
		n = holder.getPageList().size();
		assertTrue("I:page list size: " + n, n == 2);
		s = ((MockFilter) holder.getFilter()).getName();
		assertTrue("H:filter name: " + s, s.equals(""));
		s = ((TestBean)holder.getPageList().get(0)).getName();
		assertTrue("I:page list contents0: " + s, s.equals("Garcia"));
		s = ((TestBean)holder.getPageList().get(1)).getName();
		assertTrue("I:page list contents1: " + s, s.equals("Djamel"));
		n = ((MockSourceProvider) holder.getSourceProvider()).getNrOfCalls();
		assertTrue("Correct number of times called: " + n, n == 4);

		// Go to the alternative list
		// The extendedInfo is provided to eventually pass a contextual data on the
		// source provider. BUT, BY ITSELF, IT DOESN'T IMPLY THE REFRESH OF THE LIST.
		cmd.clear();
		cmd.put("filter.extendedInfo", "F");
		bw.setPropertyValues(cmd);
		n = holder.getPageList().size();
		assertTrue("I:page list size: " + n, n == 2);
		s = ((TestBean)holder.getPageList().get(0)).getName();
		assertTrue("I:page list contents0: " + s, s.equals("Garcia"));
		s = ((TestBean)holder.getPageList().get(1)).getName();
		assertTrue("I:page list contents1: " + s, s.equals("Djamel"));

		// When the list is reloaded for any reason, the source provider
		// can use the extendedInfo.
		holder.refresh(true);
		n = holder.getPageList().size();
		assertTrue("J:page list size: " + n, n == 1);
		s = ((TestBean)holder.getPageList().get(0)).getName();
		assertTrue("J:page list contents0: " + s, s.equals("Catherine"));
		n = ((MockSourceProvider) holder.getSourceProvider()).getNrOfCalls();
		assertTrue("Correct number of times called: " + n, n == 5);

		// The locale change imply a refresh of the list.
		// In this test the list will be empty if a locale is provided
		cmd.clear();
		cmd.put("locale", "en_US");
		bw.setPropertyValues(cmd);
		holder.refresh(false);
		n = holder.getPageList().size();
		assertTrue("K:page list size: " + n, n == 0);
		n = holder.getNrOfPages();
		assertTrue("K:number of pages: " + n, n == 1);
		n = holder.getFirstElementOnPage();
		assertTrue("K:first element: " + n, n == 0);
		n = holder.getLastElementOnPage();
		assertTrue("K:last element: " + n, n == -1);
		n = holder.getPage();
		assertTrue("K:page number: " + n, n == 0);
		n = holder.getFirstLinkedPage();
		assertTrue("K:first display page: " + n, n == 0);
		n = holder.getLastLinkedPage();
		assertTrue("K:last display page: " + n, n == 0);
		n = ((MockSourceProvider) holder.getSourceProvider()).getNrOfCalls();
		assertTrue("Correct number of times called: " + n, n == 6);
		
		// The locale remains the same, no new request expected
		cmd.clear();
		cmd.put("locale", "en_US");
		n = ((MockSourceProvider) holder.getSourceProvider()).getNrOfCalls();
		assertTrue("Correct number of times called: " + n, n == 6);
		
	}


	public static class MockFilter {

		private String name = "";
		private String age = "";
		private String extendedInfo = "";

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getAge() {
			return age;
		}

		public void setAge(String age) {
			this.age = age;
		}

		public String getExtendedInfo() {
			return extendedInfo;
		}

		public void setExtendedInfo(String extendedInfo) {
			this.extendedInfo = extendedInfo;
		}

		public boolean equals(Object o) {
			if (this == o) return true;
			if (!(o instanceof MockFilter)) return false;

			final MockFilter mockFilter = (MockFilter) o;

			if (!age.equals(mockFilter.age)) return false;
			if (!extendedInfo.equals(mockFilter.extendedInfo)) return false;
			if (!name.equals(mockFilter.name)) return false;

			return true;
		}

		public int hashCode() {
			int result;
			result = name.hashCode();
			result = 29 * result + age.hashCode();
			result = 29 * result + extendedInfo.hashCode();
			return result;
		}
	}

	/**
	 * Normally it's an internal class in the Controller using business services
	 * for searching data from a database and using filters
	 * and/or extendedInfo for determining the parameters/method.
	 * The extendedInfo will generally be null or the HttpRequest.
	 */
	private static class MockSourceProvider implements PagedListSourceProvider {

		private List internalList;
		private List altList;
		private int nrOfCalls;

		public MockSourceProvider() {
			internalList = new ArrayList();
			internalList.add(new TestBean("Calogero",33));
			internalList.add(new TestBean("Giuseppe",38));
			internalList.add(new TestBean("Francesco",41));
			internalList.add(new TestBean("Djamel",34));
			internalList.add(new TestBean("Jesus",48));
			internalList.add(new TestBean("Cedric",30));
			internalList.add(new TestBean("amid",35));
			internalList.add(new TestBean("John",54));
			internalList.add(new TestBean("Aziz",26));
			internalList.add(new TestBean("Garcia",34));
			internalList.add(new TestBean("Toma",38));
			internalList.add(new TestBean("Sergio",50));
			altList = new ArrayList();
			altList.add(new TestBean("Cecile",36));
			altList.add(new TestBean("Elizabeth",49));
			altList.add(new TestBean("Denise",28));
			altList.add(new TestBean("Catherine",34));
		}

		public int getNrOfCalls() {
			return nrOfCalls;
		}

		public List loadList(Locale locale, Object filter) {
			nrOfCalls++;
			MockFilter mf = (MockFilter) filter;
			List baseList = internalList;
			if (null != mf.getExtendedInfo() && "F".equals(mf.getExtendedInfo())) {
				baseList = altList;
			}
			if ("".equals(mf.getName()) && "".equals(mf.getAge())) {
				return internalList;
			}
			List list = new ArrayList();
			Iterator it = baseList.iterator();
			while (it.hasNext()) {
				TestBean b = (TestBean)it.next();
				String fName = mf.getName();
				String tmp = mf.getAge();
				int fAge = -1;
				if (!"".equals(tmp)) {
					fAge = Integer.parseInt(tmp);
				}
				if (
					("".equals(fName) || b.getName().startsWith(fName)) &&
					(fAge < 0 || b.getAge() == fAge) &&
					locale == null
					) {
						 list.add(b);
					}
			}
			return list;
		}
	}

}
