package com.interface21.context.support;

import java.io.IOException;
import java.io.InputStream;

import com.interface21.context.ApplicationContextException;

/**
 * Standalone XML application context useful for test harnesses.
 * Takes XML from the classpath.
 * EXAMPLE: ORDER?
 * @author Rod Johnson
 */
public class ClassPathXmlApplicationContext extends XmlApplicationContextSupport {

	public ClassPathXmlApplicationContext(String paths) throws ApplicationContextException, IOException {
		super(paths);
	}

	public ClassPathXmlApplicationContext(String[] paths) throws ApplicationContextException, IOException {
		super(paths);
	}
	
	protected InputStream getInputStream(String path) throws IOException {
		InputStream is = getClass().getResourceAsStream(path);	
		System.out.println("Path is '" + path + "'; is=" + is);
	
		return is;
	}

}