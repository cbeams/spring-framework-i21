package com.interface21.validation;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.interface21.beans.BeanWrapper;
import com.interface21.beans.BeanWrapperImpl;


/**
 *
 * @author Rod Johnson
 */
public class BindException extends Exception implements Errors {
	
	public static final String EXCEPTION_KEY = "com.interface21.validation.BindException";
	
	public static final String ERRORS_KEY = "com.interface21.validation.BindException.ERRORS";
	
	//---------------------------------------------------------------------
	// Instance data
	//---------------------------------------------------------------------
	private List errors = new LinkedList();
	
	/** String name -> bw */
	private HashMap bwHash = new HashMap();
	
	/** String name -> object */
	private HashMap targetHash = new HashMap();
	
	private BeanWrapper lastBeanWrapper;
	
	private String lastObjectName;
	
	/** Nested path */
	private String nestedPath = "";
	
	
	//---------------------------------------------------------------------
	// Constructors
	//---------------------------------------------------------------------
	public BindException(Object target, String name) {
		newTarget(target, name);
	}
	

	//---------------------------------------------------------------------
	// Implementation of Errors
	//---------------------------------------------------------------------
	/**
	 * Return a mapping of target name to target object
	 */
	public final Map getTargetMap() {
		HashMap m = new HashMap();
		m.putAll(this.targetHash);
		return m;
	}
	
	public void newTarget(Object target, String name) {
		this.lastBeanWrapper = new BeanWrapperImpl(target);
		bwHash.put(name, this.lastBeanWrapper);
		targetHash.put(name, target);
		this.lastObjectName = name;
		
		this.nestedPath = "";
	}
	
	protected BeanWrapper getLastBeanWrapper() {
		return lastBeanWrapper;
	}
	
	protected String lastObjectName() {
		return lastObjectName;
	}
	
	protected BeanWrapper getBeanWrapperFor(String name) throws InvalidBinderUsageException {
		BeanWrapper bw = (BeanWrapper) bwHash.get(name);
		if (bw == null)
			throw new InvalidBinderUsageException("No target with name '" + name + "'");
		return bw;
	}
	
	public Object getTarget(String name) throws InvalidBinderUsageException {
		BeanWrapper bw = getBeanWrapperFor(name);
		return bw.getWrappedInstance();
	}
	
	private BeanWrapper getBeanWrapperForSingleTarget() throws InvalidBinderUsageException {
		if (bwHash.size() != 1)
			throw new InvalidBinderUsageException("Multiple targets: can't get default target");
		BeanWrapper bw = (BeanWrapper) bwHash.values().iterator().next();
		return bw;
	}
	
	public Object getTarget() throws InvalidBinderUsageException {
		//BeanWrapper bw = getBeanWrapperForSingleTarget();
		//return bw.getWrappedInstance();
		return this.lastBeanWrapper;
	}
	
	/**
	 * Reject an update from the last binding target
	 */
	public void rejectValue(String field, String code, String message) throws InvalidBinderUsageException {
		rejectValue(this.lastObjectName, field, code, message);
	}
	
	public void rejectValue(String objName, String field, String code, String message) throws InvalidBinderUsageException {
		
		field = fixedField(field);
		Object newVal = getBeanWrapperFor(objName).getPropertyValue(field);
		FieldError fe = new FieldError(this.lastObjectName, field, newVal, code, message);
		errors.add(fe);
	}
	
	private String fixedField(String field) {
		// Add nested path, if present, allowing context changes
		field = nestedPath + field;
		//System.out.println("Fixed field = '" + field + "'");
		return field;
	}
	
	protected void addFieldError(FieldError fe) {
		errors.add(fe);
	}
	
	
	public int getErrorCount() {
		return errors.size();
	}
	
	public boolean hasError(String objName, String field) { 
		field = fixedField(field);
		return getError(objName, field) != null;
	}
	
	public boolean hasError(String field) { 
		return getError(this.lastObjectName, field) != null;
	}
	
	public boolean hasErrors() {
		return !errors.isEmpty();
	}
	
	
	/**
	 * Return value held in error if error, else
	 * 
	 */
	public Object getPropertyValueOrRejectedUpdate(String objName, String field) {
		field = fixedField(field);
		FieldError fe = getError(objName, field);
		if (fe == null)
			return getBeanWrapperFor(objName).getPropertyValue(field);
		return fe.getRejectedValue();
	}
	
	public Object getPropertyValueOrRejectedUpdate(String field) {
		return getPropertyValueOrRejectedUpdate(this.lastObjectName, field);
	}
	
	/**
	 * Return FieldError or null
	 */
	public FieldError getError(String objName, String field) {
		field = fixedField(field);
		for (int i = 0; i < errors.size(); i++) {
			FieldError fe = (FieldError) errors.get(i);
			if (fe.getObject().equals(objName) && fe.getField().equals(field))
				return fe;
		}
		// Return null if not found
		return null;
	}
	
	
	public FieldError getError(String field) {
		return getError(this.lastObjectName, field);
	}
	
	public FieldError[] getErrors() {
		return (FieldError[]) errors.toArray(new FieldError[0]);
	}
	
	
	/**
	 * List of all errors: order isn't guaranteed OR IS IT THAT ON FORM!?
	 */
	public List fieldErrors() {
		return errors;
	}
	
	
	public void setNestedPath(String nestedPath) {
		if (nestedPath == null)
			nestedPath = "";
		if (nestedPath.length() > 0)
			nestedPath += ".";
		this.nestedPath = nestedPath;
		//System.out.println("NESTEDPATH set to '" + this.nestedPath + "'");
	}
	
	/*
	 * Return model!?
	 */
	public final Map getModel() {
		if (hasErrors()) {
			HashMap m = new HashMap();
			m.put(ERRORS_KEY, getErrors());
			
			// or fieldErrors list?
			m.put(EXCEPTION_KEY, this);
			
			m.putAll(getTargetMap());
			return m;
		}
		else {
			// Mapping from name to target object
			return getTargetMap();

//			Map m = new HashMap();
//			Iterator itr = getTargetMap().keySet().iterator();
//			while (itr.hasNext()) {
//				String name = (String) itr.next();
//				Map mm = new HashMap();
//				BeanWrapper bw = getBeanWrapperFor(name);
//				PropertyDescriptor[] pds = bw.getPropertyDescriptors();
//				for (int i = 0; i < pds.length; i++) {
//					String prop = pds[i].getName();
//					if (bw.isReadableProperty(prop)) {
//						Object val = bw.getPropertyValue(prop);
//						if (val == null) val = "";
//						mm.put(prop, val);
//					}
//				}
//				m.put(name, mm);
//			}
//			return m;
		}
	}
	
	
	/**
	 * @return diagnostic information about the errors held in this object
	 */
	public String getMessage() {
		StringBuffer sb = new StringBuffer("BindException: " + getErrorCount() + " errors");
		System.out.println("command is " + getTarget() + "; ");
		Iterator itr = errors.iterator();
		while (itr.hasNext()) {
			sb.append("; " + itr.next());
		}
		return sb.toString();
	}
	
}
