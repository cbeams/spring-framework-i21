/**
 * Generic framework code included with 
 * <a href="http://www.amazon.com/exec/obidos/tg/detail/-/1861007841/">Expert One-On-One J2EE Design and Development</a>
 * by Rod Johnson (Wrox, 2002). 
 * This code is free to use and modify. However, please
 * acknowledge the source and include the above URL in each
 * class using or derived from this code. 
 * Please contact <a href="mailto:rod.johnson@interface21.com">rod.johnson@interface21.com</a>
 * for commercial support.
 */

package com.interface21.web.servlet;

import java.util.HashMap;
import java.util.Map;

/**
 * Holder for both Model and View in our MVC framework.
 * Note that these are entirely distinct. This class merely holds
 * both to make it possible for a controller to return both
 * model and view in a single return value.
 * <br>Class to represent a model and view returned by
 * an handler used by a ControllerServlet.
 * The View can take the form of a reference to a View
 * object, or a String view name, which will need
 * to be resolved by a ViewResolver object.
 * The model is a Map, allowing the use of multiplee data objects.
 * @author  Rod Johnson
 */
public class ModelAndView {

	//---------------------------------------------------------------------
	// Instance data
	//---------------------------------------------------------------------
	/** Model */
	private Map model;

	/** View if we hold an object reference */
	private View view;

	/** 
	 * View name if we hold a view name that will be resolved by the ControllerServlet
	 */
	private String viewName;

	//---------------------------------------------------------------------
	// Constructors
	//---------------------------------------------------------------------
	/** 
	 * Creates new ModelAndView given a View reference and a model
	 * @param view view to render this model
	 * @param model Map of model names (Strings) to
	 * models (Objects). Model entries may not be null, but the
	 * model may be null if there is no model data.
	 */
	public ModelAndView(View view, Map model) {
		this.view = view;
		// Less efficient than simply copying reference?
		this.model = new HashMap(model);
	}

	/** 
	 * Creates new ModelAndView given a viewName and a model
	 * @param viewName name of the View to render this model.
	 * This will be resolved by the Controller servlet at runtime.
	 * @param model Map of model names (Strings) to
	 * models (Objects). Model entries may not be null, but the
	 * model may be null if there is no model data.
	 */
	public ModelAndView(String viewName, Map model) {
		this.viewName = viewName;
		// Less efficient than simply copying reference?
		this.model = new HashMap(model);
	}

	/** 
	 * Convenient constructor to take a single model
	 * @param viewName name of the view
	 * @param modelname name of the single entry in the model
	 * @param model model data object
	 */
	public ModelAndView(String viewName, String modelname, Object model) {
		this(viewName);
		this.model.put(modelname, model);
	}
	

	/**
	 * Constructor taking a view name and two model entries.
	 * @param viewName name of the view
	 * @param key1 key for first model entry
	 * @param value1 value for first model entry
	 * @param key2 key for second model entry
	 * @param value2 value for second model entry
	 */
	public ModelAndView(String viewName, String key1, Object value1, String key2, Object value2) {
		this(viewName);
		this.model.put(key1, value1);
		this.model.put(key2, value2);
	}

	/** 
	 * Convenient constructor to take a single model
	 * @param view view reference
	 * @param modelname name of the single entry in the model
	 * @param model model data object
	 */
	public ModelAndView(View view, String modelname, Object model) {
		this(view);
		this.model.put(modelname, model);
	}

	/** 
	 * Convenient constructor when there is no model data to expose
	 * @param view view reference
	 */
	public ModelAndView(View view) {
		this.view = view;
		this.model = new HashMap();
	}


	/** 
	 * Convenient constructor when there is no model data to expose
	 * @param viewName view name, resolved by the controller servlet
	 */
	public ModelAndView(String viewName) {
		this.viewName = viewName;
		this.model = new HashMap();
	}

	//---------------------------------------------------------------------
	// Public methods
	//---------------------------------------------------------------------
	/**
	 * Add an object to the model.
	 * @param name name of the object to add to the model
	 * @param o object to add to the model. May not be null.
	 * @return this object, convenient to allow usages like
	 * return modelAndView.addObject("foo", bar);
	 */
	public ModelAndView addObject(String name, Object o) {
		this.model.put(name, o);
		return this;
	}

	/**
	 * @return whether we use a view reference
	 */
	public boolean isReference() {
		return viewName != null;
	}

	/**
	 * @return the View reference, or null if we are using a viewName
	 * to be resolved by the controller servlet.
	 */
	public View getView() {
		return view;
	}

	/**
	 * @return the viewName, or null if we are using a View
	 * reference
	 */
	public String getViewName() {
		return viewName;
	}

	/**
	 * @return the model map. Map be null.
	 */
	public Map getModel() {
		return model;
	}

	/**
	 * @return diagnostic information about this model and view.
	 */
	public String toString() {
		String s = "ModelAndView: ";
		s += isReference() ? "reference to view with name '" + viewName + "'" : "materialized View is " + view;
		s += "; Model=[" + model + "]";
		return s;
	}

}
