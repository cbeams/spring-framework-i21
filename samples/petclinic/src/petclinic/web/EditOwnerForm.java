/*
 * EditOwnerForm.java
 *
 */

package petclinic.web;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import petclinic.NoSuchEntityException;
import petclinic.Owner;

import com.interface21.beans.BeanUtils;
import com.interface21.web.bind.RequestUtils;
import com.interface21.web.servlet.ModelAndView;

/**
 *  Form controller that is used to edit an existing <code>Owner</code>.
 *
 *  @author  Ken Krebs
 */
public class EditOwnerForm extends AbstractClinicForm {
    
    /**
     *  Method updates an existing Owner.
     */
    protected ModelAndView onSubmit(Object command) throws ServletException {
        Owner newOwner = (Owner) command;
        Owner owner =  getClinic().findOwner(newOwner.getId());
        if(owner == null) {
            // should not happen unless object is corrupted
            throw new NoSuchEntityException(newOwner);
        }
        BeanUtils.copyProperties(newOwner, owner);
        getClinic().update(owner);
        return new ModelAndView(getSuccessView(), "ownerId", Integer.toString(owner.getId()));
    }
    
    protected Object formBackingObject(HttpServletRequest request) throws ServletException {
        Owner owner = getClinic().findOwner(RequestUtils.getIntParameter(request, "ownerId", 0));
        if(owner == null) {
            throw new ServletException("ownerId missing from request on " + getClass());
        }
        Owner newOwner = new Owner();
        BeanUtils.copyProperties(owner, newOwner);
        return newOwner;
    }
    
}
