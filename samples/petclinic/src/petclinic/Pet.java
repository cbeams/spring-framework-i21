/*
 * Pet.java
 *
 */

package petclinic;

import java.util.Date;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

/**
 *  Simple JavaBean business object representing a pet.
 *
 *  @author  Ken Krebs
 */
public class Pet extends NamedEntity {
    
    /** Holds value of property birthDate. */
    private Date birthDate;
    
    /** Holds value of property typeId. */
    private int typeId;
    
    /** Holds value of property visits. */
    private List visits = new ArrayList();
    
    /** Holds value of property owner. */
    private Owner owner;
    
    /** Creates a new instance of Pet */
    public Pet() {
    }
    
    /** Getter for property birthDate.
     * @return Value of property birthDate.
     */
    public Date getBirthDate() {
        return this.birthDate;
    }
    
    /** Setter for property birthDate.
     * @param birthDate New value of property birthDate.
     */
    public void setBirthDate(Date birthDate) {
            this.birthDate = birthDate;
    }
    
    /** Getter for property typeId.
     * @return Value of property typeId.
     */
    public int getTypeId() {
        return this.typeId;
    }
    
    /** Setter for property typeId.
     * @param type New value of property typeId.
     */
    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }
    
    /** Getter for property visits.
     * @return Value of property visits.
     */
    public List getVisits() {
        return this.visits;
    }
    
    /** Setter for property visits.
     * @param visits New value of property visits.
     */
    public void setVisits(List visits) {
        this.visits = visits;
    }
    
    /** Getter for property owner.
     * @return Value of property owner.
     */
    public Owner getOwner() {
        return this.owner;
    }
    
    /** Setter for property owner.
     *  @param owner New value of property owner.
     */
    public void setOwner(Owner owner) {
        this.owner = owner;
    }
    
    /** Method to add a visit to the List of visits.
     *  @param visit New visit to be added to the List of visits
     */
    public void addVisit(Visit visit) {
        this.visits.add(visit);
    }
    
    
}
