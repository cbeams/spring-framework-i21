/*
 * FindOwnersValidator.java
 *
 */

package petclinic.validation;

import petclinic.Owner;

import com.interface21.validation.Errors;
import com.interface21.validation.Validator;

/**
 *  <code>Validator</code> for <code>FindOwnerForm</code>.
 *
 *  @author  Ken Krebs
 */
public class FindOwnersValidator implements Validator {
    
    public boolean supports(Class clazz) {
        return clazz.equals(Owner.class);
    }
    
    public void validate(Object obj, Errors errors) {
        Owner owner = (Owner) obj;
        String lastName = owner.getLastName();
        if(lastName == null || "".equals(lastName)) {
            errors.rejectValue("lastName", "error.required", null, "required");
        }
    }
    
}
