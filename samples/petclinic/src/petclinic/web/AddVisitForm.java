/*
 * AddVisitForm.java
 *
 */

package petclinic.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import petclinic.Pet;
import petclinic.Visit;

import com.interface21.web.bind.RequestUtils;
import com.interface21.web.servlet.ModelAndView;

/**
 *  Form controller that is used to add a new <code>Visit</code> to the system.
 *
 *  @author  Ken Krebs
 */
public class AddVisitForm extends AbstractClinicForm {
    
    protected ModelAndView onSubmit(Object command) throws ServletException {
        Visit visit = (Visit) command;
        getClinic().insert(visit);
        return new ModelAndView(getSuccessView(), "ownerId", Integer.toString(visit.getPet().getOwner().getId()));
    }
    
    /** Method creates a new <code>Visit</code> */
    protected Object formBackingObject(HttpServletRequest request) throws ServletException {
        Pet pet =  getClinic().findPet(RequestUtils.getIntParameter(request, "petId", 0));
        if(pet == null)
            throw new ServletException("petId missing from request on " + getClass());
        Visit visit = new Visit();
        visit.setPetId(pet.getId());
        visit.setPet(pet);
        return visit;
    }
    
    /** Method disallows duplicate add form submission */
    protected ModelAndView handleInvalidSubmit(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        return disallowDuplicateSubmission(request, response);
    }
    
}
