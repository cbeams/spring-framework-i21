package com.interface21.web.mock;

import java.util.Enumeration;
import java.util.Hashtable;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;


/**
 * This class acts as a test environment for servlets.
 **/
public class MockServletConfig implements ServletConfig {
	
	private String _name;
	
	private Hashtable _initParameters = new Hashtable();
	
	private ServletContext _context;
	
	public MockServletConfig(ServletContext sc, String name) {
		this._context = sc;
		this._name = name;
	}
	
	public void addInitParameter(String name, String value) {
		_initParameters.put(name, value);
	}
	
	
	
	//-------------------------------------------- ServletConfig methods ---------------------------------------------------
	
	
	/**
	 * Returns the value of the specified init parameter, or null if no such init parameter is defined.
	 **/
	public String getInitParameter( String name ) {
		return (String) _initParameters.get( name );
	}
	
	
	/**
	 * Returns an enumeration over the names of the init parameters.
	 **/
	public Enumeration getInitParameterNames() {
		return _initParameters.keys();
	}
	
	
	/**
	 * Returns the current servlet context.
	 **/
	public ServletContext getServletContext() {
		return _context;
	}
	
	
	/**
	 * Returns the registered name of the servlet, or its class name if it is not registered.
	 **/
	public java.lang.String getServletName() {
		return _name;
	}
	
}
