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
    
    /** Creates a new instance of Owner */
    public Owner() {
    }
    
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

    /** Method to find an owner's pet by id number
     * @param id The id of the pet to find
     * @return The pet if found
     */
    public Pet findPet(int id) {
        Iterator pi = this.pets.iterator();
        while(pi.hasNext()) {
            Pet pet = (Pet) pi.next();
            if(id == pet.getId())
                return pet;
        }
        return null;
    }
    
    /** Method to test whether an <code>Owner</code> already has
     * a <code>Pet</code> with a particular name.
     * @param name to test
     * @return the pet if name is already in use, else null
     */
    public Pet hasPet(String name) {
        Iterator pi = this.pets.iterator();
        while(pi.hasNext()) {
            Pet pet = (Pet) pi.next();
            if(name.equalsIgnoreCase(pet.getName()))
                return pet;
        }
        return null;
    }
    
    /** Method to add a pet to the List of pets.
     * @param pet New pet to be added to the List of pets
     */
    public void addPet(Pet pet) {
        this.pets.add(pet);
    }
    
    
}
