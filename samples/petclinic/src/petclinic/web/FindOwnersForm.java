/*
 * FindOwnersForm.java
 *
 */

package petclinic.web;

import petclinic.Owner;

import com.interface21.beans.factory.InitializingBean;
import com.interface21.validation.BindException;
import com.interface21.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

/**
 *  JavaBean Form controller that is used to search for <code>Owner</code>s by last name.
 *
 *  @author  Ken Krebs
 */
public class FindOwnersForm
	extends AbstractClinicForm
	implements InitializingBean {

	/** Holds value of property selectView. */
	private String selectView;

	/**
	 * Set the name of the view that should be used for selection display.
	 */
	public void setSelectView(String selectView) {
		this.selectView = selectView;
	}

	/** Creates a new instance of FindOwnersForm */
	public FindOwnersForm() {
		// OK to start with a blank command object
		setCommandClass(Owner.class);
	}

	/** Method used to search for owners renders View depending on how many are found */
	protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response,
		Object command, BindException errors) throws ServletException, IOException {

		Owner owner = (Owner) command;
		
		// find owners by last name
		List results = getClinic().findOwners(owner.getLastName());
		
		if (results.size() < 1) {
			// no owners found
			errors.rejectValue("lastName", "notFound", null, "not found");		
			return showForm(request, response, errors);
		}
		
		if (results.size() > 1) {
			// multiple owners found
			if (this.selectView == null)
				throw new ServletException("selectView isn't set");
				
			return new ModelAndView(this.selectView, "selections", results);
		}
		
		// 1 owner found
		owner = (Owner) results.get(0);
		return new ModelAndView(getSuccessView(), "ownerId", Integer.toString(owner.getId()));
	}

}
