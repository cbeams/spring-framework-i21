/*
 * Visit.java
 *
 */

package petclinic;

import java.util.Date;

/**
 *  Simple JavaBean business object representing a visit.
 * 
 *  @author  Ken Krebs
 */
public class Visit extends Entity {
    
    /** Holds value of property petId. */
    private int petId;
    
    /** Holds value of property visitDate. */
    private Date visitDate;
    
    /** Holds value of property description. */
    private String description;
    
    /** Holds value of property pet. */
    private Pet pet;
    
    /** Creates a new instance of Visit */
    public Visit() {
        this.visitDate = new Date();
    }
    
    /** Getter for property petId.
     * @return Value of property petId.
     */
    public int getPetId() {
        return this.petId;
    }
    
    /** Setter for property petId.
     * @param petId New value of property petId.
     */
    public void setPetId(int petId) {
        this.petId = petId;
    }
    
    /** Getter for property visitDate.
     * @return Value of property visitDate.
     */
    public Date getVisitDate() {
        return this.visitDate;
    }
    
    /** Setter for property visitDate.
     * @param date New value of property visitDate.
     */
    public void setVisitDate(Date visitDate) {
        this.visitDate = visitDate;
    }
    
    /** Getter for property description.
     * @return Value of property description.
     */
    public String getDescription() {
        return this.description;
    }
    
    /** Setter for property description.
     * @param description New value of property description.
     */
    public void setDescription(String description) {
        this.description = description;
    }
    
    /** Getter for property pet.
     * @return Value of property pet.
     */
    public Pet getPet() {
        return this.pet;
    }
    
    /** Setter for property pet.
     * @param pet New value of property pet.
     */
    public void setPet(Pet pet) {
        this.pet = pet;
    }
    
}
