/*
 * EditPetForm.java
 *
 */

package petclinic.web;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import petclinic.NoSuchEntityException;
import petclinic.Pet;

import com.interface21.web.bind.RequestUtils;
import com.interface21.web.servlet.ModelAndView;

/**
 *  JavaBean Form controller that is used to edit an existing <code>Pet</code>.
 *
 * @author  Ken Krebs
 */
public class EditPetForm extends AbstractClinicForm {
    
	/** Method updates an existing Pet. */
    protected ModelAndView onSubmit(Object command) throws ServletException {
    	// the edited object
        Pet newPet = (Pet) command;
        
        // get the original object
        Pet pet =  getClinic().findPet(newPet.getId());
        if(pet == null) {
            // should not happen unless id is corrupted
            throw new NoSuchEntityException(newPet);
        }
        
        // use the data from the edited object
		pet.copyPropertiesFrom(newPet);
        
        // delegate the update to the Business layer
        getClinic().update(pet);
        
        return new ModelAndView(getSuccessView(), "ownerId", Integer.toString(pet.getOwner().getId()));
    }
    
    /** Method forms a copy of an existing Pet for editing */
    protected Object formBackingObject(HttpServletRequest request) throws ServletException {
    	// get the Pet referred to by id in the request
        Pet pet =  getClinic().findPet(RequestUtils.getIntParameter(request, "petId", 0));
        if(pet == null) {
            throw new ServletException("petId missing from request on " + getClass());
        }
        
        //make a copy for editing
        Pet newPet = new Pet();
		newPet.copyPropertiesFrom(pet);
        return newPet;
    }
    
}
