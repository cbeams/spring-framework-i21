package com.interface21.context.support;

import java.io.IOException;
import java.io.InputStream;

import com.interface21.context.ApplicationContextException;

/**
 * Standalone XML application context, taking the context definition
 * files from the classpath. Mainly useful for test harnesses,
 * but also for application contexts embedded within JARs.
 * <p>Note: Generally treats relative paths as class path resources
 * (when using ApplicationContext.getResourceAsStream).
 * @author Rod Johnson, Juergen Hoeller
 * @see com.interface21.context.ApplicationContext#getResourceAsStream
 * @see #getResourceByRelativePath
 */
public class ClassPathXmlApplicationContext extends FileSystemXmlApplicationContext {

	public ClassPathXmlApplicationContext(String locations) throws ApplicationContextException, IOException {
		super(locations);
	}

	public ClassPathXmlApplicationContext(String[] locations) throws ApplicationContextException, IOException {
		super(locations);
	}

	/**
	 * This implementation treats relative paths as class path resources.
	 */
	protected InputStream getResourceByRelativePath(String path) throws IOException {
		return getClass().getResourceAsStream(path);
	}

	/**
	 * This implementation returns null, as there is no base path for
	 * class path resources.
	 */
	public String getResourceBasePath() {
		return null;
	}

}
