/*
 * JdbcClinicTest.java
 * Live JUnit based test
 *
 */

package petclinic.jdbc;

import junit.framework.*;

import petclinic.Clinic;
import petclinic.Owner;
import petclinic.Pet;
import petclinic.Visit;

import com.interface21.context.support.ClassPathXmlApplicationContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;

/**
 *  Live Unit tests for JdbcClinic implementations.
 * 	"testContext.xml" determines which implementation is live-tested.
 *
 *  @author Ken Krebs
 */
public class JdbcClinicTest extends TestCase {

	/** Logger for this class */
	private final Log logger = LogFactory.getLog(getClass());

	private Clinic clinic;

	public JdbcClinicTest(java.lang.String testName) {
		super(testName);
	}

	protected void setUp() throws Exception {
		super.setUp();
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("/testContext.xml");
		clinic = (Clinic) ctx.getBean("clinic");
	}

	/** Test of findOwners method, of class Clinic. */
	public void testFindOwners() {
		System.out.println("testFindOwners");

		List owners = clinic.findOwners("Davis");
		assertEquals(2, owners.size());
		owners = clinic.findOwners("s");
		assertEquals(0, owners.size());
	}

	/** Test of findOwner method, of class Clinic. */
	public void testFindOwner() {
		System.out.println("testGetOwner");

		Owner o1 = clinic.findOwner(1);
		assertEquals("Franklin", o1.getLastName());
		Owner o10 = clinic.findOwner(10);
		assertEquals("Carlos", o10.getFirstName());
	}

	/** Test of getVets method, of class Clinic. */
	public void testGetVets() {
		System.out.println("testGetVets");

		assertEquals(6, clinic.getVets().size());
	}

	/** Test of findPet method, of class Clinic. */
	public void testFindPet() {
		System.out.println("testFindPet");

		Owner o6 = clinic.findOwner(6);
		Pet p7 = clinic.findPet(7);
		assertEquals("Samantha", p7.getName());
		Pet p8 = clinic.findPet(8);
		assertEquals("Max", p8.getName());
	}

	/** Test of insert(Owner) method, of class Clinic. */
	public void testInsertOwner() {
		System.out.println("testInsertOwner");

		Owner owner = new Owner();
		owner.setLastName("Schultz");
		List owners = clinic.findOwners("Schultz");
		assertEquals(0, owners.size());
		clinic.insert(owner);
		owners = clinic.findOwners("Schultz");
		assertEquals(1, owners.size());
	}

	/** Test of insert(Pet) method, of class Clinic. */
	public void testInsertPet() {
		System.out.println("testInsertPet");

		Owner o6 = clinic.findOwner(6);
		Pet pet = new Pet();
		pet.setName("bowser");
		pet.setOwner(o6);
		pet.setTypeId(2);
		pet.setBirthDate(new java.util.Date());
		assertEquals(2, o6.getPets().size());
		clinic.insert(pet);
		assertEquals(3, o6.getPets().size());
	}

	/** Test of insert(Visit) method, of class Clinic. */
	public void testInsertVisit() {
		System.out.println("testInsertVisit");

		Owner o6 = clinic.findOwner(6);
		Pet p7 = clinic.findPet(7);
		List visits = p7.getVisits();
		assertEquals(2, visits.size());
		Visit visit = new Visit();
		visit.setPet(p7);
		visit.setDescription("test");
		clinic.insert(visit);
		assertTrue(visit.getId() > 0);
	}

	/** Test of update(Owner) method, of class Clinic. */
	public void testUpdateOwner() throws Exception {
		System.out.println("testUpdateOwner");

		Owner o1 = clinic.findOwner(1);
		assertEquals("Franklin", o1.getLastName());
		o1.setLastName("Williams");
		clinic.update(o1);
		setUp();
		o1 = clinic.findOwner(1);
		assertEquals("Williams", o1.getLastName());
	}

	/** Test of update(Pet) method, of class Clinic. */
	public void testUpdatePet() throws Exception {
		System.out.println("testUpdatePet");

		Owner o6 = clinic.findOwner(6);
		Pet p7 = clinic.findPet(7);
		assertEquals("Samantha", p7.getName());
		p7.setName("Samurai");
		clinic.update(p7);
		setUp();
		o6 = clinic.findOwner(6);
		p7 = clinic.findPet(7);
		assertEquals("Samurai", p7.getName());
	}

}
