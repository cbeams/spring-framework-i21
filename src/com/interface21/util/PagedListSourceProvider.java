package com.interface21.util;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author Jean-Pierre PAWLAK
 */
public interface PagedListSourceProvider {
	
	public List loadList(Map filters, Object extendedInfo, Locale locale);
	
}
