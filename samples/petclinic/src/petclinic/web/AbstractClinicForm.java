/*
 * AbstractClinicForm.java
 *
 */

package petclinic.web;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import petclinic.Clinic;

import com.interface21.beans.propertyeditors.CustomDateEditor;
import com.interface21.context.ApplicationContextException;
import com.interface21.web.bind.ServletRequestDataBinder;
import com.interface21.web.servlet.ModelAndView;
import com.interface21.web.servlet.mvc.SimpleFormController;

/**
 *
 * @author  Ken Krebs
 */
abstract public class AbstractClinicForm extends SimpleFormController {
    
    /** Holds value of property clinic. */
    private Clinic clinic;
    
    /** Setter for property clinic.
     * @param clinic New value of property plinic.
     */
    public void setClinic(Clinic clinic) {
        this.clinic = clinic;
    }
    
    /** Getter for property clinic.
     * @return clinic value of property plinic.
     */
    protected Clinic getClinic() {
        return this.clinic;
    }
    
    public void afterPropertiesSet() throws Exception {
        if(clinic == null)
            throw new ApplicationContextException("Must set clinic bean property on " + getClass());
    }
    
    protected Map referenceData(HttpServletRequest request) throws ServletException {
        Map model = new HashMap();
        model.put("types", clinic.getTypes());
        return model;
    }
    
    protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(java.util.Date.class, null, new CustomDateEditor(dateFormat, false));
    }
    
    /** Method disallows duplicate form submission.
     *  Typically used to prevent duplicate insertion of <code>Entity</code>s
     *  into the datastore. Shows a new form with an error message.
     *  @see #handleInvalidSubmit(HttpServletRequest,HttpServletResponse)
     */
    protected ModelAndView disallowDuplicateSubmission(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        ServletRequestDataBinder errors = createBinder(request, formBackingObject(request));
        errors.reject("error.duplicateForm", null, "duplicate form submission");
        return showForm(request, response, errors);
    }
    
}
