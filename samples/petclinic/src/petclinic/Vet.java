/*
 * Vet.java
 *
 */

package petclinic;

import java.util.List;

/**
 *  Simple JavaBean business object representing a veterinarian.
 *
 *  @author  Ken Krebs
 */
public class Vet extends Person {
    
    /** Holds value of property specialties. */
    private List specialties;
    
    /** Creates a new instance of Vet */
    public Vet() {
    }
    
    /** Getter for property specialties.
     * @return Value of property specialties.
     */
    public List getSpecialties() {
        return this.specialties;
    }
    
    /** Setter for property specialties.
     * @param specialties New value of property specialties.
     */
    public void setSpecialties(List specialties) {
        this.specialties = specialties;
    }
    
}
