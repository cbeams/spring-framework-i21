/*
 * EditPetForm.java
 *
 */

package petclinic.web;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import petclinic.NoSuchIdException;
import petclinic.Pet;

import com.interface21.beans.BeanUtils;
import com.interface21.web.bind.RequestUtils;
import com.interface21.web.servlet.ModelAndView;

/**
 *  Form controller that is used to edit an existing <code>Pet</code>.
 *
 * @author  Ken Krebs
 */
public class EditPetForm extends AbstractClinicForm {
    
    protected ModelAndView onSubmit(Object command) throws ServletException {
        Pet pet = (Pet) command;
        Pet oldPet =  getClinic().findPet(pet.getId());
        if(oldPet == null) {
            // should not happen unless object is corrupted
            throw new NoSuchIdException(oldPet, pet.getId());
        }
        BeanUtils.copyProperties(pet, oldPet);
        getClinic().update(oldPet);
        return new ModelAndView(getSuccessView(), "ownerId", Integer.toString(pet.getOwner().getId()));
    }
    
    protected Object formBackingObject(HttpServletRequest request) throws ServletException {
        Pet pet =  getClinic().findPet(RequestUtils.getIntParameter(request, "petId", 0));
        if(pet == null) {
            throw new ServletException("petId missing from request on " + getClass());
        }
        Pet newPet = new Pet();
        BeanUtils.copyProperties(pet, newPet);
        return newPet;
    }
    
}
