/*
 * AddOwnerForm.java
 *
 */

package petclinic.web;

import petclinic.Owner;

import com.interface21.web.servlet.ModelAndView;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

/**
 *  JavaBean Form controller that is used to add a new <code>Owner</code> to the system.
 *
 *  @author  Ken Krebs
 */
public class AddOwnerForm extends AbstractClinicForm {
    
    public AddOwnerForm() {
    	// OK to start with a blank command object
        setCommandClass(Owner.class);
    }
    
	/** Method inserts a new <code>Owner</code>. */
    protected ModelAndView onSubmit(Object command) throws ServletException {
        Owner owner = (Owner) command;
        
		// delegate the insert to the Business layer
        getClinic().insert(owner);
        
        return new ModelAndView(getSuccessView(), "ownerId", Integer.toString(owner.getId()));
    }
    
    protected ModelAndView handleInvalidSubmit(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        return disallowDuplicateFormSubmission(request, response);
    }
    
}
