/*
 * AddOwnerForm.java
 *
 */

package petclinic.web;

import petclinic.Owner;

import com.interface21.web.servlet.ModelAndView;
import com.interface21.web.bind.ServletRequestDataBinder;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

/**
 *  Form controller that is used to add a new <code>Owner</code> to the system.
 *
 *  @author  Ken Krebs
 */
public class AddOwnerForm extends AbstractClinicForm {
    
    public AddOwnerForm() {
        setCommandClass(Owner.class);
    }
    
    protected ModelAndView onSubmit(Object command) throws ServletException {
        Owner owner = (Owner) command;
        getClinic().insert(owner);
        return new ModelAndView(getSuccessView(), "ownerId", Integer.toString(owner.getId()));
    }
    
    /** Method disallows duplicate add form submission */
    protected ModelAndView handleInvalidSubmit(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        return disallowDuplicateSubmission(request, response);
    }
    
}
