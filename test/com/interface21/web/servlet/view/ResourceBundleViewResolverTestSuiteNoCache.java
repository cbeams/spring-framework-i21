package com.interface21.web.servlet.view;

public class ResourceBundleViewResolverTestSuiteNoCache extends ResourceBundleViewResolverTestSuite {
	
	protected boolean getCache() {
		return false;
	}
	
	public ResourceBundleViewResolverTestSuiteNoCache(String name) {
		super(name);
	}
}

 