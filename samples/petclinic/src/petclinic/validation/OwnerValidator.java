/*
 * OwnerValidator.java
 *
 */

package petclinic.validation;

import petclinic.Owner;

import com.interface21.validation.Errors;
import com.interface21.validation.Validator;

/**
 *  <code>Validator</code> for <code>Owner</code> Forms.
 *
 *  @author  Ken Krebs
 */
public class OwnerValidator implements Validator {
    
    public boolean supports(Class clazz) {
        return clazz.equals(Owner.class);
    }
    
    public void validate(Object obj, Errors errors) {
        Owner owner = (Owner) obj;
        String firstName = owner.getFirstName();
        if(firstName == null || "".equals(firstName)) {
            errors.rejectValue("firstName", "error.required", null, "required");
        }
        String lastName = owner.getLastName();
        if(lastName == null || "".equals(lastName)) {
            errors.rejectValue("lastName", "error.required", null, "required");
        }
        String address = owner.getAddress();
        if(address == null || "".equals(address)) {
            errors.rejectValue("address", "error.required", null, "required");
        }
        String city = owner.getCity();
        if(city == null || "".equals(city)) {
            errors.rejectValue("city", "error.required", null, "required");
        }
        String telephone = owner.getTelephone();
        if(telephone == null || "".equals(telephone)) {
            errors.rejectValue("telephone", "error.required", null, "required");
            return;
        }
        for(int i = 0; i < telephone.length(); ++i) {
            if((Character.isDigit(telephone.charAt(i))) == false) {
                errors.rejectValue("telephone", "error.non-numeric", null, "non-numeric");
                break;
            }
        }
    }
    
}
