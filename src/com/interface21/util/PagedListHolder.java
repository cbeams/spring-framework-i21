package com.interface21.util;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

import com.interface21.beans.BeanUtils;
import com.interface21.beans.MutableSortDefinition;
import com.interface21.beans.PropertyComparator;
import com.interface21.beans.SortDefinition;

/**
 * PagedListHolder is a simple state holder for handling lists of objects,
 * separating them into pages. Page numbering starts with 0.
 *
 * <p>This is mainly targetted at usage in web UIs. Typically, an instance will be
 * instantiated with a list of beans, put into the session, and exported as model.
 * The properties can all be set/get programmatically, but the most common way will
 * be data binding, i.e. populating the bean from request parameters. The getters
 * will mainly be used by the view.
 *
 * <p>Supports sorting the underlying list via a SortDefinition implementation,
 * available as property "sort". By default, a MutableSortDefinition instance
 * that toggles the ascending value on setting the same property again is used.
 *
 * <p>The data binding names have to be called "pageSize" and "sort.ascending",
 * as expected by BeanWrapper. Note that the names and the nesting syntax match
 * the respective JSTL EL expressions, like "myModelAttr.pageSize" and
 * "myModelAttr.sort.ascending".
 *
 * <p>This class just provides support for an unmodifiable List of beans.
 * If you need on-demand refresh because of Locale or filter changes,
 * consider RefreshablePagedListHolder.
 *
 * @author Juergen Hoeller
 * @since 19.05.2003
 * @see #getPageList
 * @see RefreshablePagedListHolder
 * @see com.interface21.beans.MutableSortDefinition
 */
public class PagedListHolder {

	public static final int DEFAULT_PAGE_SIZE = 10;

	public static final int DEFAULT_MAX_LINKED_PAGES = 10;

	private List source;

	private Date refreshDate;

	private SortDefinition sort;

	private SortDefinition sortUsed;

	private int pageSize = DEFAULT_PAGE_SIZE;

	private int page = 0;

	private int maxLinkedPages = DEFAULT_MAX_LINKED_PAGES;


	/**
	 * Create a new holder instance.
	 * You'll need to set a source list to be able to use the holder.
	 */
	public PagedListHolder() {
		this(new ArrayList(0));
	}

	/**
	 * Create a new holder instance with the given source list.
	 */
	public PagedListHolder(List source) {
		setSource(source);
		setSort(new MutableSortDefinition(true));
	}


	/**
	 * Set the source list for this holder.
	 */
	public void setSource(List source) {
		this.source = source;
		this.refreshDate = new Date();
		this.sortUsed = null;
	}

	/**
	 * Return the source list for this holder.
	 */
	public List getSource() {
		return source;
	}

	/**
	 * Return the last time the list has been fetched from the source provider.
	 */
	public Date getRefreshDate() {
		return refreshDate;
	}

	/**
	 * Set the sort definition for this holder.
	 * Typically an instance of MutableSortDefinition.
	 * @see com.interface21.beans.MutableSortDefinition
	 */
	public void setSort(SortDefinition sort) {
		this.sort = sort;
	}

	/**
	 * Return the sort definition for this holder.
	 */
	public SortDefinition getSort() {
		return sort;
	}

	/**
	 * Set the current page size.
	 * Resets the current page number if changed.
	 * <p>Default value is 10.
	 */
	public void setPageSize(int pageSize) {
		if (pageSize != this.pageSize) {
			this.pageSize = pageSize;
			this.page = 0;
		}
	}

	/**
	 * Return the current page size.
	 */
	public int getPageSize() {
		return pageSize;
	}

	/**
	 * Set the current page number.
	 * Page numbering starts with 0.
	 */
	public void setPage(int page) {
		this.page = page;
	}

	/**
	 * Return the current page number.
	 * Page numbering starts with 0.
	 */
	public int getPage() {
		return page;
	}


	/**
	 * Return the number of pages for the current source list.
	 */
	public int getNrOfPages() {
		float nrOfPages = (float) this.source.size() / this.pageSize;
		return (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages);
	}

	/**
	 * Return the total number of elements in the source list.
	 */
	public int getNrOfElements() {
		return this.source.size();
	}

	/**
	 * Return the element index of the first element on the current page.
	 * Element numbering starts with 0.
	 */
	public int getFirstElementOnPage() {
		return (this.pageSize * this.page);
	}

	/**
	 * Return the element index of the last element on the current page.
	 * Element numbering starts with 0.
	 */
	public int getLastElementOnPage() {
		int endIndex = this.pageSize * (this.page + 1);
		return (endIndex > this.source.size() ? this.source.size() : endIndex) -1;
	}

	/**
	 * Return a sub-list representing the current page.
	 */
	public List getPageList() {
		return this.source.subList(getFirstElementOnPage(), getLastElementOnPage() +1);
	}

	/**
	 * Set the maximum number of page links to a few pages around the current one.
	 */
	public void setMaxLinkedPages(int maxLinkedPages) {
		this.maxLinkedPages = maxLinkedPages;
	}

	/**
	 * Return the maximum number of page links to a few pages around the current one.
	 */
	public int getMaxLinkedPages() {
		return maxLinkedPages;
	}

	/**
	 * Return the first page to which create a link around the current page.
	 */
	public int getFirstLinkedPage() {
		return Math.max(0, this.getPage() - (this.maxLinkedPages /2));
	}

	/**
	 * Return the first page to which create a link around the current page.
	 */
	public int getLastLinkedPage() {
		return Math.min(getFirstLinkedPage() + this.maxLinkedPages -1, this.getNrOfPages() -1);
	}


	/**
	 * Resort the list if necessary, i.e. if the current sort instance isn't equal
	 * to the backed-up sortUsed instance.
	 */
	public void resort() {
		if (this.sort != null && !"".equals(this.sort.getProperty()) && !this.sort.equals(this.sortUsed)) {
			PropertyComparator.sort(this.source, this.sort);
			this.sortUsed = (MutableSortDefinition) BeanUtils.instantiateClass(this.sort.getClass());
			BeanUtils.copyProperties(this.sort, this.sortUsed);
			setPage(0);
		}
	}

}
