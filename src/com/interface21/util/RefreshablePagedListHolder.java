package com.interface21.util;

import java.util.Locale;

import com.interface21.beans.BeanUtils;

/**
 * RefreshablePagedListHolder is a PagedListHolder subclass with reloading capabilities.
 * It automatically re-requests the List from the source provider, in case of Locale or
 * filter changes.
 *
 * <p>Data binding works just like with PagedListHolder. The locale can be specified in
 * Locale's toString syntax, e.g. "locale=en_US". The filter object can be of any
 * custom class, preferably a bean for easy data binding from a request. An instance
 * will simply get passed through to PagedListSourceProvider.loadList. A filter property
 * can be specified via "filter.myFilterProperty", for example.
 *
 * <p>The scenario in the controller could be:
 * <code>
 * RefreshablePagedListHolder holder = request.getSession("mySessionAttr");<br>
 * if (holder == null) {<br>
 *   holder = new RefreshablePagedListHolder();<br>
 *   holder.setSourceProvider(new MyAnonymousOrEmbeddedSourceProvider());<br>
 *   holder.setFilter(new MyAnonymousOrEmbeddedFilter());<br>
 *   request.getSession().setAttribute("mySessionAttr", holder);<br>
 * }<br>
 * holder.refresh(false);
 * BindException ex = BindUtils.bind(request, listHolder, "myModelAttr");<br>
 * return ModelAndView("myViewName", ex.getModel());<br>
 * <br>
 * ...<br>
 * <br>
 * private class MyAnonymousOrEmbeddedSourceProvider implements PagedListSourceProvider {<br>
 *   public List loadList(Locale locale, Object filter) {<br>
 *     MyAnonymousOrEmbeddedFilter filter = (MyAnonymousOrEmbeddedFilter) filter;<br<
 *     // an empty name mask should lead to all objects being loaded
 *     return myBusinessService.loadMyObjectsByNameMask(filter.getName());<br>
 * }<br>
 * <br>
 * private class MyAnonymousOrEmbeddedFilter {<br>
 *   private String name = "";<br>
 *   public String getName() {<br>
 *     return name;<br<
 *   }<br>
 *   public void setName(String name) {<br>
 *     this.name = name;<br>
 *   }<br>
 * }<br>
 * </code>
 *
 * @author Jean-Pierre Pawlak
 * @author Juergen Hoeller
 * @since 24.05.2003
 * @see PagedListSourceProvider
 * @see com.interface21.beans.propertyeditors.LocaleEditor
 */
public class RefreshablePagedListHolder extends PagedListHolder {

	private PagedListSourceProvider sourceProvider;

	private Locale locale;

	private Locale localeUsed;

	private Object filter;

	private Object filterUsed;

	/**
	 * Create a new list holder.
	 * You'll need to set a source provider to be able to use the holder.
	 * @see #setSourceProvider
	 */
	public RefreshablePagedListHolder() {
		super();
	}

	/**
	 * Create a new list holder with the given source provider.
	 */
	public RefreshablePagedListHolder(PagedListSourceProvider sourceProvider) {
		super();
		this.sourceProvider = sourceProvider;
	}

	/**
	 * Set the callback class for reloading the List when necessary.
	 * If the list is definitely not modifiable, i.e. not locale aware
	 * and no filtering, use PagedListHolder.
	 * @see PagedListHolder
	 */
	public void setSourceProvider(PagedListSourceProvider sourceProvider) {
		this.sourceProvider = sourceProvider;
	}

	/**
	 * Return the callback class for reloading the List when necessary.
	 */
	public PagedListSourceProvider getSourceProvider() {
		return sourceProvider;
	}

	/**
	 * Set the Locale that the source provider should use for loading the list.
	 * This can either be populated programmatically (e.g. with the request locale),
	 * or via binding (using Locale's toString syntax, e.g. "locale=en_US").
	 * @param locale the current Locale, or null
	 */
	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	/**
	 * Return the Locale that the source provider should use for loading the list.
	 * @return the current Locale, or null
	 */
	public Locale getLocale() {
		return locale;
	}

	/**
	 * Set the filter object that the source provider should use for loading the list.
	 * This will typically be a bean, for easy data binding.
	 * @param filter the filter object, or null
	 */
	public void setFilter(Object filter) {
		this.filter = filter;
	}

	/**
	 * Return the filter that the source provider should use for loading the list.
	 * @return the current filter, or null
	 */
	public Object getFilter() {
		return filter;
	}

	/**
	 * Reload the underlying list from the source provider if necessary
	 * (i.e. if the locale and/or the filter has changed), and resort it.
	 * @param force whether a reload should be performed in any case
	 */
	public void refresh(boolean force) {
		if (this.sourceProvider != null && (force ||
		    (this.locale != null && !this.locale.equals(this.localeUsed)) ||
		    (this.filter != null && !this.filter.equals(this.filterUsed)))) {
			setSource(this.sourceProvider.loadList(this.locale, this.filter));
			if (this.filter != null && !this.filter.equals(this.filterUsed)) {
				this.setPage(0);
			}
			this.localeUsed = this.locale;
			this.filterUsed = BeanUtils.instantiateClass(this.filter.getClass());
			BeanUtils.copyProperties(this.filter, this.filterUsed);
		}
		resort();
	}

}
