package com.interface21.util;

import java.util.*;

import com.interface21.beans.BeanUtils;

/**
 * PagedListHolder is a simple state holder for handling lists of objects,
 * separating them into pages. Page numbering starts with 0.
 *
 * <p>This is mainly targetted at usage in web UIs. Typically, an instance will be
 * instantiated with a list of objects, put into the user's session, and occasionally
 * fed with changed page numbers and/or page sizes. A web controller can export
 * the current page (see getPageList) as model.
 *
 * @author Juergen Hoeller
 * @since 19.05.2003
 * @see #getPageList
 */
public class PagedListHolder {

	public static final int DEFAULT_PAGE_SIZE = 10;

	private List source = null;

	private int pageSize = DEFAULT_PAGE_SIZE;

	private int pageNr = 0;

	public PagedListHolder() {
	}

	public PagedListHolder(List source) {
		this.source = source;
	}

	public PagedListHolder(List source, int pageSize) {
		this.source = source;
		this.pageSize = pageSize;
	}

	public void setSource(List source) {
		this.source = source;
	}

	public List getSource() {
		return source;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
		this.pageNr = 0;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageNr(int pageNr) {
		this.pageNr = pageNr;
	}

	public int getPageNr() {
		return pageNr;
	}

	public int getNrOfPages() {
		float nrOfPages = (float) this.source.size() / this.pageSize;
		return (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages);
	}

	public int getNrOfElements() {
		return source.size();
	}

	protected int getLowerIndex() {
		return (this.pageSize * this.pageNr);
	}

	protected int getUpperIndex() {
		int endIndex = this.pageSize * (this.pageNr + 1);
		return (endIndex > this.source.size() ? this.source.size() : endIndex);
	}

	public int getFirstElementOnPage() {
		return getLowerIndex();
	}

	public int getLastElementOnPage() {
		return getUpperIndex() - 1;
	}

	public List getPageList() {
		return this.source.subList(getLowerIndex(), getUpperIndex());
	}

	/**
	 * Sort the current source list by the given propertyName and direction,
	 * resetting the page number.
	 */
	public void sortByProperty(String propertyName, boolean ascending, boolean ignoreCase) {
		BeanUtils.sortByProperty(this.source, propertyName, ascending, ignoreCase);
		this.pageNr = 0;
	}

}
