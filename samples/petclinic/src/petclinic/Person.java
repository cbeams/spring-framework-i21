/*
 * Person.java
 *
 * Created on June 17, 2003, 9:00 AM
 */

package petclinic;

/**
 *  Simple JavaBean business object representing an person.
 *
 * @author  Ken Krebs
 */
public class Person extends Entity {
    
    /** Holds value of property firstName. */
    private String firstName;
    
    /** Holds value of property lastName. */
    private String lastName;
    
    /** Holds value of property address. */
    private String address;
    
    /** Holds value of property city. */
    private String city;
    
    /** Holds value of property telephone. */
    private String telephone;
    
    /** Creates a new instance of Person */
    public Person() {
    }
    
    /** Getter for property firstName.
     * @return Value of property firstName.
     */
    public String getFirstName() {
        return this.firstName;
    }
    
    /** Setter for property firstName.
     * @param firstName New value of property firstName.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    /** Getter for property lastName.
     * @return Value of property lastName.
     */
    public String getLastName() {
        return this.lastName;
    }
    
    /** Setter for property lastName.
     * @param lastName New value of property lastName.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    /** Getter for property address.
     * @return Value of property address.
     */
    public String getAddress() {
        return this.address;
    }
    
    /** Setter for property address.
     * @param address New value of property address.
     */
    public void setAddress(String address) {
        this.address = address;
    }
    
    /** Getter for property city.
     * @return Value of property city.
     */
    public String getCity() {
        return this.city;
    }
    
    /** Setter for property city.
     * @param city New value of property city.
     */
    public void setCity(String city) {
        this.city = city;
    }
    
    /** Getter for property telephone.
     * @return Value of property telephone.
     */
    public String getTelephone() {
        return this.telephone;
    }
    
    /** Setter for property telephone.
     * @param tel New value of property telephone.
     */
    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
    
}
