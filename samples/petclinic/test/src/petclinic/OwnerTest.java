/*
 * OwnerTest.java
 *
 */
 
package petclinic;

import junit.framework.*;

/**
 * 	JUnit test for Owner
 * 
 * @author Ken Krebs
 */
public class OwnerTest extends TestCase {

	/** Test of hasPet method, of class Owner. */
	public void testHasPet() {
		System.out.println("testHasPet");

		Owner owner = new Owner();
		Pet fido = new Pet();
		fido.setName("Fido");
		assertFalse(owner.hasPet("Fido"));
		assertFalse(owner.hasPet("fido"));
		owner.addPet(fido);
		assertTrue(owner.hasPet("Fido"));
		assertTrue(owner.hasPet("fido"));
	}


}
