/*
 * EditOwnerForm.java
 *
 */

package petclinic.web;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import petclinic.NoSuchEntityException;
import petclinic.Owner;

import com.interface21.web.bind.RequestUtils;
import com.interface21.web.servlet.ModelAndView;

/**
 *  JavaBean Form controller that is used to edit an existing <code>Owner</code>.
 *
 *  @author  Ken Krebs
 */
public class EditOwnerForm extends AbstractClinicForm {
    
    /** Method updates an existing Owner. */
    protected ModelAndView onSubmit(Object command) throws ServletException {
		// the edited object
        Owner newOwner = (Owner) command;
       
		// get the original object
        Owner owner =  getClinic().findOwner(newOwner.getId());
        if(owner == null) {
            // should not happen unless object is corrupted
            throw new NoSuchEntityException(newOwner);
        }
        
		// use the data from the edited object
		owner.copyPropertiesFrom(newOwner);
        
		// delegate the update to the Business layer
        getClinic().update(owner);
        
        return new ModelAndView(getSuccessView(), "ownerId", Integer.toString(owner.getId()));
    }
    
	/** Method forms a copy of an existing Owner for editing */
    protected Object formBackingObject(HttpServletRequest request) throws ServletException {
		// get the Owner referred to by id in the request
        Owner owner = getClinic().findOwner(RequestUtils.getIntParameter(request, "ownerId", 0));
        if(owner == null) {
            throw new ServletException("ownerId missing from request on " + getClass());
        }
        
		//make a copy for editing
        Owner newOwner = new Owner();
		newOwner.copyPropertiesFrom(owner);
        return newOwner;
    }
    
}
