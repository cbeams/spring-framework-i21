/*
 * NamedEntity.java
 *
 */

package petclinic;

/**
 *  Simple JavaBean business object adds a name property to <code>Entity</code>.
 *  Used as a base class for objects needing these properties.
 * 
 *  @author  Ken Krebs
 */
public class NamedEntity extends Entity {
    
    /** Holds value of property name. */
    private String name;
    
    /** Creates a new instance of NamedEntity */
    public NamedEntity() {
    }
    
    /** Getter for property name.
     * @return Value of property name.
     */
    public String getName() {
        return this.name;
    }
    
    /** Setter for property name.
     * @param name New value of property name.
     */
    public void setName(String name) {
        this.name = name;
    }
    
}
