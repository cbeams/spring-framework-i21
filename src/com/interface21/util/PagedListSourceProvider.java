package com.interface21.util;

import java.util.List;
import java.util.Locale;

/**
 * Callback that provides the source for a reloadable List.
 * Used by RefreshablePagedListHolder.
 * @author Jean-Pierre PAWLAK
 * @author Juergen Hoeller
 * @see RefreshablePagedListHolder#setSourceProvider
 */
public interface PagedListSourceProvider {

	/**
	 * Load the List for the given Locale and filter settings.
	 * The filter object can be of any custom class, preferably a bean
	 * for easy data binding from a request. An instance will simply
	 * get passed through to this callback method.
	 * @param locale Locale that the List should be loaded for,
	 * or null if not locale-specific
	 * @param filter object representing filter settings,
	 * or null if no filter options are used
	 * @return the loaded List
	 * @see RefreshablePagedListHolder#setLocale
	 * @see RefreshablePagedListHolder#setFilter
	 */
	public List loadList(Locale locale, Object filter);

}
