/*
 * FindOwnersForm.java
 *
 */

package petclinic.web;

import petclinic.Clinic;
import petclinic.Owner;

import com.interface21.beans.factory.InitializingBean;
import com.interface21.web.servlet.ModelAndView;
import com.interface21.context.ApplicationContextException;

import java.util.List;

import javax.servlet.ServletException;

/**
 *  Form controller that is used to search for <code>Owner</code>s by last name.
 *
 *  @author  Ken Krebs
 */
public class FindOwnersForm extends AbstractSearchFormController implements InitializingBean {
    
    /** Holds value of property clinic. */
    private Clinic clinic;
    
    /** Creates a new instance of FindOwnersForm */
    public FindOwnersForm() {
        setCommandClass(Owner.class);
    }
    
    public void afterPropertiesSet() throws Exception {
        if(clinic == null)
            throw new ApplicationContextException("Must set clinic bean property on " + getClass());
    }
    
    /** Setter for property clinic.
     * @param clinic New value of property plinic.
     */
    public void setClinic(Clinic clinic) {
        this.clinic = clinic;
    }
    
    protected ModelAndView onSubmit(Object command) throws ServletException  {
        return new ModelAndView(getSuccessView(), "ownerId", Integer.toString(((Owner) command).getId()));
    }
    
    /**
     *  Implementation of template callback Method to search for an
     *  <code>Owner</code> by Last Name.
     *  @param command the owner to search for.
     *  @return a <code>List</code> of matching <code>Owner</code>s
     */
    protected List search(Object command) {
        Owner owner = (Owner) command;
        String search = owner.getLastName();
        List results = clinic.findOwners(search);
        //NOTE: Mysql disregards case, Hsql is case-sensitive
        if(results.size() > 0) {
            return results;
        }
        
        //try capitalized search
        StringBuffer sb = new StringBuffer(search);
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        for(int i = 1; i < sb.length(); ++i) {
            sb.setCharAt(i, Character.toLowerCase(sb.charAt(i)));
        }
        results = clinic.findOwners(sb.toString());
        if(results.size() > 0) {
            return results;
        }
        
        //try uppercase search
        results = clinic.findOwners(search.toUpperCase());
        if(results.size() > 0) {
            return results;
        }
        
        //try lowercase search
        return clinic.findOwners(search.toLowerCase());
    }
    
}
