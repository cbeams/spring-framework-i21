/*
 * VisitValidator.java
 *
 */

package petclinic.validation;

import petclinic.Visit;

import com.interface21.validation.Errors;
import com.interface21.validation.Validator;

/**
 *  <code>Validator</code> for <code>Visit</code> Forms.
 *
 *  @author  Ken Krebs
 */
public class VisitValidator implements Validator {
    
    public boolean supports(Class clazz) {
        return clazz.equals(Visit.class);
    }
    
    public void validate(Object obj, Errors errors) {
        Visit visit = (Visit) obj;
        String description = visit.getDescription();
        if(description == null || "".equals(description)) {
            errors.rejectValue("description", "error.required", null, "required");
        }
    }
    
}
