/*
 * AddPetForm.java
 *
 * Created on June 18, 2003, 8:07 AM
 */

package petclinic.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import petclinic.Owner;
import petclinic.Pet;

import com.interface21.web.bind.RequestUtils;
import com.interface21.web.servlet.ModelAndView;

/**
 *  JavaBean Form controller that is used to add a new <code>Pet</code> to the system.
 *
 * @author  Ken Krebs
 */
public class AddPetForm extends AbstractClinicForm {
    
	public AddPetForm() {
		// need a session to hold the formBackingObject
		setSessionForm(true);
	}
    
	/** Method inserts a new <code>Pet</code>. */
    protected ModelAndView onSubmit(Object command) throws ServletException {
        Pet pet = (Pet) command;
        
		// delegate the insert to the Business layer
        getClinic().insert(pet);
        
        return new ModelAndView(getSuccessView(), "ownerId", Integer.toString(pet.getOwner().getId()));
    }
    
	/** Method creates a new <code>Pet</code> with the correct <code>Owner</code> info */
    protected Object formBackingObject(HttpServletRequest request) throws ServletException {
        Owner owner =  getClinic().findOwner(RequestUtils.getIntParameter(request, "ownerId", 0));
        if(owner == null)
            throw new ServletException("ownerId missing from request on " + getClass());
        Pet pet = new Pet();
        pet.setOwner(owner);
        return pet;
    }
    
    protected ModelAndView handleInvalidSubmit(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        return disallowDuplicateFormSubmission(request, response);
    }
    
}
