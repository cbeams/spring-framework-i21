/*
 * EditPetForm.java
 *
 */

package petclinic.web;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import petclinic.NoSuchEntityException;
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
        Pet newPet = (Pet) command;
        Pet pet =  getClinic().findPet(newPet.getId());
        if(pet == null) {
            // should not happen unless object is corrupted
            throw new NoSuchEntityException(newPet);
        }
        BeanUtils.copyProperties(newPet, pet);
        getClinic().update(pet);
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
