/*
 * Entity.java
 *
 * Created on June 17, 2003, 8:52 AM
 */

package petclinic;

/**
 *  Simple JavaBean business object with an id property.
 *  Used as a base class for objects needing this property.
 *
 * @author  Ken Krebs
 */
public class Entity {
    
    /** Holds value of property id. */
    private int id;
    
    /** Creates a new instance of Entity */
    public Entity() {
    }
    
    /** Getter for property id.
     * @return Value of property id.
     */
    public int getId() {
        return this.id;
    }
    
    /** Setter for property id.
     * @param id New value of property id.
     */
    public void setId(int id) {
        this.id = id;
    }
    
}
