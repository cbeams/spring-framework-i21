/*
 * Owner.java
 *
 */

package petclinic;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *  Simple JavaBean business object representing an owner.
 *
 *  @author  Ken Krebs
 */
public class Owner extends Person {
    
    /** Holds value of property pets. */
    private List pets = new ArrayList();
    
    /** Getter for property pets.
     * @return Value of property pets.
     */
    public List getPets() {
        return this.pets;
    }
    
    /** Setter for property pets.
     * @param pets New value of property pets.
     */
    public void setPets(List pets) {
        this.pets = pets;
    }
    
    /** Method to test whether an <code>Owner</code> already has
     * a <code>Pet</code> with a particular name (case-insensitive).
     * @param name to test
     * @return true if pet name is already in use
     */
    public boolean hasPet(String name) {
        Iterator pi = this.pets.iterator();
        while(pi.hasNext()) {
            Pet pet = (Pet) pi.next();
            if(name.equalsIgnoreCase(pet.getName()))
                return true;
        }
        return false;
    }
    
    /** Method to add a pet to the List of pets.
     * @param pet New pet to be added to the List of pets
     */
    public void addPet(Pet pet) {
        this.pets.add(pet);
    }
    
	/** Method to copy properties from another <code>Owner</code>.
	 * 	@param owner Properties source
	 */
	public void copyPropertiesFrom(Owner owner) {
		super.copyPropertiesFrom(owner);
		setPets(owner.getPets()); 
	}
      
}
