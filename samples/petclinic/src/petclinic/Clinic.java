/*
 * Clinic.java
 *
 */

package petclinic;

import java.util.Map;
import java.util.List;

/**
 *  The high-level petclinic business interface.
 *
 *  @author  Ken Krebs
 */
public interface Clinic {
    
    /** Method to retrieve all <code>Vet</code>s from the datastore.
     *  @return a <code>Map</code> of <code>Vet</code>s.
     */
    public Map getVets();
    
    /** Method to retrieve all <code>Pet</code> types from the datastore.
     *  @return a <code>List</code> of types.
     */
    public Map getTypes();
    
    /** Method to retrieve <code>Owner</code>s from the datastore by last name.
     *  @param lastName Value to search for.
     *  @return a <code>List</code> of matching <code>Owner</code>s.
     */
    public List findOwners(String lastName);
    
    /** Method to retrieve an <code>Owner</code> from the datastore by id.
     *  @param id Value to search for.
     *  @return the <code>Owner</code> if found.
     */
    public Owner findOwner(int id);
    
    /** Method to retrieve a <code>Pet</code> from the datastore by id.
     *  @param id Value to search for.
     *  @return the <code>Pet</code> if found.
     */
    public Pet findPet(int id);
    
    /** Method to add a new <code>Owner</code> to the datastore. 
     *  @param owner to add.
     */
    public void insert(Owner owner);
    
    /** Method to add a new <code>Pet</code> to the datastore. 
     *  @param pet to add.
     */
    public void insert(Pet pet);
    
    /** Method to add a new <code>Visit</code> to the datastore. 
     *  @param visit to add.
     */
    public void insert(Visit visit);
    
    /** Method to update the datastore with an <code>Owner</code>'s 
     *  revised information.
     *  @param owner to update.
     */
    public void update(Owner owner) throws NoSuchEntityException;
    
    /** Method to update the datastore with a <code>Pet</code>'s 
     *  revised information.
     *  @param pet to update.
     */
    public void update(Pet pet) throws NoSuchEntityException;
    
}
