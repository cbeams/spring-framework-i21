/*
 * AbstractJdbcClinic.java
 *
 */

package petclinic.jdbc;

import petclinic.Clinic;
import petclinic.NoSuchEntityException;
import petclinic.Entity;
import petclinic.NamedEntity;
import petclinic.Owner;
import petclinic.Pet;
import petclinic.Vet;
import petclinic.Visit;

import com.interface21.beans.factory.InitializingBean;
import com.interface21.context.ApplicationContextException;
import com.interface21.dao.DataAccessException;
import com.interface21.jdbc.object.RdbmsOperation;
import com.interface21.jdbc.core.SqlParameter;
import com.interface21.jdbc.core.support.AbstractDataFieldMaxValueIncrementer;
import com.interface21.jdbc.object.MappingSqlQuery;
import com.interface21.jdbc.object.SqlUpdate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import javax.sql.DataSource;

/**
 * JavaBean base class for <code>Clinic</code> JDBC implementations
 * 
 * @author Ken Krebs
 */
abstract public class AbstractJdbcClinic implements Clinic, InitializingBean {

	/** Logger for this class and subclasses */
	protected final Log logger = LogFactory.getLog(getClass());

	/** <code>Vet</code> Identity <code>Map</code> */
	private Map vetsMap;

	/** <code>Pet</code> types Identity <code>Map</code> . */
	private Map typesMap = new HashMap();

	/** <code>Map</code> of all the <code>Vet</code> specialties. */
	private Map specialtiesMap = new HashMap();

	/** <code>Owner</code> Identity <code>Map</code> */
	private Map ownersMap = new HashMap();

	/** <code>Pet</code> Identity <code>Map</code> */
	private Map petsMap = new HashMap();

	/** Holds value of property dataSource. */
	private DataSource dataSource;

	/** Holds all vets Query Object. */
	private RdbmsOperation allVetsQuery;

	/** Holds pet types Query Object. */
	private RdbmsOperation typesQuery;

	/** Holds specialties Query Object. */
	private RdbmsOperation specialtiesQuery;

	/** Holds owners by name Query Object. */
	private RdbmsOperation ownersByNameQuery;

	/** Holds owner by id Query Object. */
	private RdbmsOperation ownerQuery;

	/** Holds pets by owner Query Object. */
	private RdbmsOperation petsByOwnerQuery;

	/** Holds visits Query Object. */
	private RdbmsOperation visitsQuery;

	/** Holds vet specialties Query Object. */
	private RdbmsOperation vetSpecialtiesQuery;

	/** Holds owner Insert Object. */
	private RdbmsOperation ownerInsert;

	/** Holds pet Insert Object. */
	private RdbmsOperation petInsert;

	/** Holds Visit Insert Object. */
	private RdbmsOperation visitInsert;

	/** Holds owner Update Object. */
	private RdbmsOperation ownerUpdate;

	/** Holds pet Update Object. */
	private RdbmsOperation petUpdate;

	/** Getter for property dataSource.
	 * @return value of property dataSource.
	 */
	public DataSource getDataSource() {
		return this.dataSource;
	}

	/** Setter for property dataSource.
	 * @param dataSource New value of property dataSource.
	 */
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	/** Setter for property allVetsQuery.
	 * @param operation New value of property allVetsQuery.
	 */
	public void setAllVetsQuery(RdbmsOperation operation) {
		allVetsQuery = operation;
	}

	/** Setter for property typesQuery.
	 * @param operation New value of property typesQuery.
	 */
	public void setTypesQuery(RdbmsOperation operation) {
		typesQuery = operation;
	}

	/** Setter for property specialtiesQuery.
	 * @param operation New value of property specialtiesQuery.
	 */
	public void setSpecialtiesQuery(RdbmsOperation operation) {
		specialtiesQuery = operation;
	}

	/** Setter for property ownersByNameQuery.
	 * @param operation New value of property ownersByNameQuery.
	 */
	public void setOwnersByNameQuery(RdbmsOperation operation) {
		ownersByNameQuery = operation;
	}

	/** Setter for property ownerQuery.
	 * @param operation New value of property ownerQuery.
	 */
	public void setOwnerQuery(RdbmsOperation operation) {
		ownerQuery = operation;
	}

	/** Setter for property petsByOwnerQuery.
	 * @param operation New value of property petsByOwnerQuery.
	 */
	public void setPetsByOwnerQuery(RdbmsOperation operation) {
		petsByOwnerQuery = operation;
	}

	/** Setter for property visitsQuery.
	 * @param operation New value of property visitsQuery.
	 */
	public void setVisitsQuery(RdbmsOperation operation) {
		visitsQuery = operation;
	}

	/** Setter for property vetSpecialtiesQuery.
	 * @param operation New value of property vetSpecialtiesQuery.
	 */
	public void setVetSpecialtiesQuery(RdbmsOperation operation) {
		vetSpecialtiesQuery = operation;
	}

	/** Setter for property ownerInsert.
	 * @param operation New value of property ownerInsert.
	 */
	public void setOwnerInsert(RdbmsOperation operation) {
		ownerInsert = operation;
	}

	/** Setter for property petInsert.
	 * @param operation New value of property petInsert.
	 */
	public void setPetInsert(RdbmsOperation operation) {
		petInsert = operation;
	}

	/** Setter for property visitInsert.
	 * @param operation New value of property visitInsert.
	 */
	public void setVisitInsert(RdbmsOperation operation) {
		visitInsert = operation;
	}

	/** Setter for property ownerUpdate.
	 * @param operation New value of property ownerUpdate.
	 */
	public void setOwnerUpdate(RdbmsOperation operation) {
		ownerUpdate = operation;
	}

	/** Setter for property petUpdate.
	 * @param operation New value of property petUpdate.
	 */
	public void setPetUpdate(RdbmsOperation operation) {
		petUpdate = operation;
	}

	/** Method completes initialization of the Clinic object */
	public void afterPropertiesSet() throws Exception {
		if (dataSource == null) {
			throw new ApplicationContextException(
				"Must set dataSource bean property on " + getClass());
		}

		// let subclasses setup their JDBC objects first
		setupRdbmsOperations();

		// provide default JDBC objects for those not setup by the subclass
		if (allVetsQuery == null)
			allVetsQuery = new VetsQuery(dataSource);
		if (typesQuery == null)
			typesQuery = new TypesQuery(dataSource);
		if (specialtiesQuery == null)
			specialtiesQuery = new SpecialtiesQuery(dataSource);
		if (ownersByNameQuery == null)
			ownersByNameQuery = new OwnersByNameQuery(dataSource);
		if (ownerQuery == null)
			ownerQuery = new OwnerQuery(dataSource);
		if (petsByOwnerQuery == null)
			petsByOwnerQuery = new PetsByOwnerQuery(dataSource);
		if (visitsQuery == null)
			visitsQuery = new VisitsQuery(dataSource);
		if (vetSpecialtiesQuery == null)
			vetSpecialtiesQuery = new VetSpecialtiesQuery(dataSource);
		if (ownerUpdate == null)
			ownerUpdate = new OwnerUpdate(dataSource);
		if (petUpdate == null)
			petUpdate = new PetUpdate(dataSource);

		// make sure subclasses provide needed insert objects
		if (ownerInsert == null)
			throw new ApplicationContextException("Must set ownerInsert property on " + getClass());
		if (petInsert == null)
			throw new ApplicationContextException("Must set petInsert property on " + getClass());
		if (visitInsert == null)
			throw new ApplicationContextException("Must set visitInsert property on " + getClass());

		loadVets();
		typesMap = mapEntityList(doFindTypes());
	}

	/**
	 *  Subclasses can supply customized versions of the RdbmsOperations
	 *  by implementing setupRdbmsOperations() and overriding the corresponding AbstractJdbcClinic 
	 *  "do" methods. At a minimum, all needed "insert" objects and their associated incrementers
	 *  should be setup. 
	 */
	abstract protected void setupRdbmsOperations();

	/** 
	 * Method maps a List of Entitys keyed to their ids
	 * @param list containing Entitys
	 * @return Map containing Entitys 
	 */
	protected final Map mapEntityList(List list) {
		Map map = new HashMap();
		Iterator iterator = list.iterator();
		while (iterator.hasNext()) {
			Entity entity = (Entity) iterator.next();
			map.put(new Integer(entity.getId()), entity);
		}
		return map;
	}


// START of Clinic implementation section *******************************

	public Map getVets() {
		return vetsMap;
	}

	public Map getTypes() {
		return typesMap;
	}

	/** Method loads owners plus pets and visits if not already loaded */
	public List findOwners(String lastName) {
		List owners = doFindOwners(lastName);
		loadOwnersPetsAndVisits(owners);
		return owners;
	}

	/** Method loads an owner plus pets and visits if not already loaded */
	public Owner findOwner(int id) {
		Owner owner = (Owner) ownersMap.get(new Integer(id));
		if (owner == null) {
			owner = doFindOwner(id);
		}
		if (owner != null && owner.getPets().size() == 0) {
			loadPetsAndVisits(owner);
			ownersMap.put(new Integer(owner.getId()), owner);
		}
		return owner;
	}

	public Pet findPet(int id) {
		return (Pet) petsMap.get(new Integer(id));
	}

	public void insert(Owner owner) {
		doInsert(owner);
		ownersMap.put(new Integer(owner.getId()), owner);
	}

	public void insert(Pet pet) {
		doInsert(pet);
		pet.getOwner().addPet(pet);
		petsMap.put(new Integer(pet.getId()), pet);
	}

	public void insert(Visit visit) {
		doInsert(visit);
		visit.getPet().addVisit(visit);
	}

	public void update(Owner owner) throws NoSuchEntityException {
		if (ownersMap.get(new Integer(owner.getId())) == null)
			throw new NoSuchEntityException(owner);

		doUpdate(owner);
	}

	public void update(Pet pet) throws NoSuchEntityException {
		if (petsMap.get(new Integer(pet.getId())) == null)
			throw new NoSuchEntityException(pet);

		doUpdate(pet);
	}

// END of Clinic implementation section *******************************


	/** Method establishes the map/list of all vets and their specialties. */
	private void loadVets() {
		// establish the Map of all vets
		List vets = doFindVets();
		vetsMap = mapEntityList(vets);

		// establish the map of all the possible specialties
		specialtiesMap = mapEntityList(doFindSpecialties());

		// establish each vet's List of specialties
		Iterator vi = vets.iterator();
		while (vi.hasNext()) {
			Vet vet = (Vet) vi.next();
			List vetSpecialtiesIds = doFindVetSpecialties(vet);
			List vetSpecialties = new ArrayList();
			Iterator vsi = vetSpecialtiesIds.iterator();
			while (vsi.hasNext()) {
				Integer specialtyId = (Integer) vsi.next();
				NamedEntity specialty =
					(NamedEntity) specialtiesMap.get(specialtyId);
				vetSpecialties.add(specialty.getName());
			}
			if (vetSpecialties.size() == 0)
				vetSpecialties.add("none");
			vet.setSpecialties(vetSpecialties);
		}

	}

	/**
	 *  Method to retrieve the <code>Pet</code> and <code>Visit</code>
	 *  data for an <code>Owner</code>s.
	 *  @param owner
	 */
	private void loadPetsAndVisits(Owner owner) {
		List pets = doFindPets(owner);
		Iterator pi = pets.iterator();
		while (pi.hasNext()) {
			Pet pet = (Pet) pi.next();
			pet.setOwner(owner);
			petsMap.put(new Integer(pet.getId()), pet);
			List visits = doFindVisits(pet);
			Iterator vi = visits.iterator();
			while (vi.hasNext()) {
				Visit visit = (Visit) vi.next();
				visit.setPet(pet);
			}
			pet.setVisits(visits);
		}
		owner.setPets(pets);
	}

	/**
	 *  Method to retrieve a <code>List</code> of <code>Owner</code>s
	 * 	and their <code>Pet</code> and <code>Visit</code> data.
	 *  @param owners <code>List</code>.
	 *  @see #loadPetsAndVisits(Owner)
	 */
	private void loadOwnersPetsAndVisits(List owners) {
		Iterator oi = owners.iterator();
		while (oi.hasNext()) {
			Owner owner = (Owner) oi.next();
			loadPetsAndVisits(owner);
		}
	}


//	START of "do" methods section *******************************

	/**
	 * Method gets a List of all the pet types
	 *	@return List of all pet types
	 */
	protected List doFindTypes() throws DataAccessException {
		return ((TypesQuery) typesQuery).execute();
	}

	/** 
	 * Method gets a List of all the vets
	 *	@return List of all vets
	 */
	protected List doFindVets() throws DataAccessException {
		return ((VetsQuery) allVetsQuery).execute();
	}

	/** 
	 * Method gets a List of all the vet specialties
	 *	@return List of all vet specialties
	 */
	protected List doFindSpecialties() throws DataAccessException {
		return ((SpecialtiesQuery) specialtiesQuery).execute();
	}

	/** 
	 * Method gets a List of all of a vet's specialties 
	 *	@param vet to query
	 *	@return List of specialties for a Vet
	 */
	protected List doFindVetSpecialties(Vet vet) throws DataAccessException {
		return ((VetSpecialtiesQuery) vetSpecialtiesQuery).execute(vet.getId());
	}

	/** 
	 * Method gets a List of owners with a particular last name 
	 *	@param lastName of owners
	 *	@return Owners with given last name
	 */
	protected List doFindOwners(String lastName) throws DataAccessException {
		return ((OwnersByNameQuery) ownersByNameQuery).execute(lastName);
	}

	/** 
	 * Method gets an owner by id
	 *	@param id of owner
	 *	@return Owner with given id
	 */
	protected Owner doFindOwner(int id) throws DataAccessException {
		return (Owner) ((OwnerQuery) ownerQuery).findObject(id);
	}

	/** 
	 * Method gets a List of pets belonging to a particular owner
	 *	@param owner of pets
	 *	@return List of Pets for an Owner
	 */
	protected List doFindPets(Owner owner) throws DataAccessException {
		return ((PetsByOwnerQuery) petsByOwnerQuery).execute(owner.getId());
	}

	/** 
	 * Method gets a List of visits for a particular pet
	 *	@param pet to query
	 *	@return List of Visits for a Pet
	 */
	protected List doFindVisits(Pet pet) throws DataAccessException {
		return ((VisitsQuery) visitsQuery).execute(pet.getId());
	}

	/** 
	 * Method inserts a new Owner
	 *	@param owner to insert
	 */
	protected void doInsert(Owner owner) throws DataAccessException {
		((OwnerInsert) ownerInsert).insert(owner);
	}

	/** 
	 * Method inserts a new Pet
	 *	@param pet to insert
	 */
	protected void doInsert(Pet pet) throws DataAccessException {
		((PetInsert) petInsert).insert(pet);
	}

	/** 
	 * Method inserts a new Visit 
	 *	@param visit to insert
	 */
	protected void doInsert(Visit visit) throws DataAccessException {
		((VisitInsert) visitInsert).insert(visit);
	}

	/** 
	 * Method updates an existing Owner
	 *	@param owner to update
	 */
	protected void doUpdate(Owner owner) throws DataAccessException {
		((OwnerUpdate) ownerUpdate).update(owner);
	}

	/** 
	 * Method updates an existing Pet 
	 *	@param pet to update
	 */
	protected void doUpdate(Pet pet) throws DataAccessException {
		((PetUpdate) petUpdate).update(pet);
	}

//	END of "do" methods section *******************************


// ************* RdbmsOperation Objects section ***************

	/**
	 *  Base class for all <code>Vet</code> Query Objects.
	 */
	class VetsQuery extends MappingSqlQuery {

		/** 
		 *  Creates a new instance of VetsQuery
		 *  @param ds the DataSource to use for the query.
		 *  @param sql Value of the SQL to use for the query. 
		 */
		protected VetsQuery(DataSource ds, String sql) {
			super(ds, sql);
		}

		/** 
		 *  Creates a new instance of VetsQuery that returns all vets
		 *  @param ds the DataSource to use for the query.
		 */
		protected VetsQuery(DataSource ds) {
			super(ds, "SELECT id,first_name,last_name FROM vets ORDER BY last_name,first_name");
			compile();
		}

		protected Object mapRow(ResultSet rs, int rownum) throws SQLException {
			Vet vet = new Vet();
			vet.setId(rs.getInt("id"));
			vet.setFirstName(rs.getString("first_name"));
			vet.setLastName(rs.getString("last_name"));
			return vet;
		}

	}

	/**
	 *  All <code>Vet</code>s specialties Query Object.
	 */
	class SpecialtiesQuery extends MappingSqlQuery {

		/** 
		 *  Creates a new instance of SpecialtiesQuery
		 *  @param ds the DataSource to use for the query.
		 */
		protected SpecialtiesQuery(DataSource ds) {
			super(ds, "SELECT id,name FROM specialties");
			compile();
		}

		protected Object mapRow(ResultSet rs, int rownum) throws SQLException {
			NamedEntity specialty = new NamedEntity();
			specialty.setId(rs.getInt("id"));
			specialty.setName(rs.getString("name"));
			return specialty;
		}

	}

	/**
	 *  A particular <code>Vet</code>'s specialties Query Object.
	 */
	class VetSpecialtiesQuery extends MappingSqlQuery {

		/** 
		 *  Creates a new instance of VetSpecialtiesQuery
		 *  @param ds the DataSource to use for the query.
		 */
		protected VetSpecialtiesQuery(DataSource ds) {
			super(ds, "SELECT specialty_id FROM vet_specialties WHERE vet_id=?");
			declareParameter(new SqlParameter(Types.INTEGER));
			compile();
		}

		protected Object mapRow(ResultSet rs, int rownum) throws SQLException {
			return new Integer(rs.getInt("specialty_id"));
		}

	}

	/**
	 *  All <code>Pet</code> types Query Object.
	 */
	class TypesQuery extends MappingSqlQuery {

		/** 
		 *  Creates a new instance of TypesQuery
		 *  @param ds the DataSource to use for the query.
		 */
		protected TypesQuery(DataSource ds) {
			super(ds, "SELECT id,name FROM types ORDER BY name");
			compile();
		}

		protected Object mapRow(ResultSet rs, int rownum) throws SQLException {
			NamedEntity type = new NamedEntity();
			type.setId(rs.getInt("id"));
			type.setName(rs.getString("name"));
			return type;
		}

	}

	/**
	 *  Abstract base class for all <code>Owner</code> Query Objects.
	 */
	abstract class OwnersQuery extends MappingSqlQuery {

		/** 
		 *  Creates a new instance of OwnersQuery
		 *  @param ds the DataSource to use for the query.
		 *  @param sql Value of the SQL to use for the query. 
		 */
		protected OwnersQuery(DataSource ds, String sql) {
			super(ds, sql);
		}

		protected Object mapRow(ResultSet rs, int rownum) throws SQLException {
			Owner owner = new Owner();
			owner.setId(rs.getInt("id"));
			owner.setFirstName(rs.getString("first_name"));
			owner.setLastName(rs.getString("last_name"));
			owner.setAddress(rs.getString("address"));
			owner.setCity(rs.getString("city"));
			owner.setTelephone(rs.getString("telephone"));
			return owner;
		}

	}

	/**
	 *  <code>Owner</code> by id Query Object.
	 */
	class OwnerQuery extends OwnersQuery {

		/** 
		 *  Creates a new instance of OwnerQuery
		 *  @param ds the DataSource to use for the query.
		 */
		protected OwnerQuery(DataSource ds) {
			super(ds, "SELECT id,first_name,last_name,address,city,telephone FROM owners WHERE id=?");
			declareParameter(new SqlParameter(Types.INTEGER));
			compile();
		}

	}

	/**
	 *  <code>Owner</code>s by last name Query Object.
	 */
	class OwnersByNameQuery extends OwnersQuery {

		/** 
		 *  Creates a new instance of OwnersByNameQuery
		 *  @param ds the DataSource to use for the query.
		 */
		protected OwnersByNameQuery(DataSource ds) {
			super(ds, "SELECT id,first_name,last_name,address,city,telephone FROM owners WHERE last_name=?");
			declareParameter(new SqlParameter(Types.VARCHAR));
			compile();
		}

	}

	/**
	 *  <code>Owner</code> Update Object.
	 */
	class OwnerUpdate extends SqlUpdate {

		/** 
		 *  Creates a new instance of OwnerUpdate
		 *  @param ds the DataSource to use for the update.
		 */
		protected OwnerUpdate(DataSource ds) {
			super(ds, "UPDATE owners SET first_name=?,last_name=?,address=?,city=?,telephone=? WHERE id=?");
			declareParameter(new SqlParameter(Types.VARCHAR));
			declareParameter(new SqlParameter(Types.VARCHAR));
			declareParameter(new SqlParameter(Types.VARCHAR));
			declareParameter(new SqlParameter(Types.VARCHAR));
			declareParameter(new SqlParameter(Types.VARCHAR));
			declareParameter(new SqlParameter(Types.INTEGER));
			compile();
		}

		/**
		 *  Method to update an <code>Owner</code>'s data.
		 *  @param owner to update.
		 *  @return the number of rows affected by the update
		 */
		protected int update(Owner owner) {
			return this.update(
				new Object[] {
					owner.getFirstName(),
					owner.getLastName(),
					owner.getAddress(),
					owner.getCity(),
					owner.getTelephone(),
					new Integer(owner.getId())});
		}

	}

	/**
	 *  <code>Owner</code> Insert Object.
	 */
	class OwnerInsert extends SqlUpdate {

		/** Holds key generator Object */
		private AbstractDataFieldMaxValueIncrementer incrementer;

		/** 
		 *  Creates a new instance of OwnerInsert
		 *  @param ds the DataSource to use for the insert.
		 */
		protected OwnerInsert(AbstractDataFieldMaxValueIncrementer incrementer) {
			super(incrementer.getDataSource(), "INSERT INTO owners VALUES(?,?,?,?,?,?)");
			declareParameter(new SqlParameter(Types.INTEGER));
			declareParameter(new SqlParameter(Types.VARCHAR));
			declareParameter(new SqlParameter(Types.VARCHAR));
			declareParameter(new SqlParameter(Types.VARCHAR));
			declareParameter(new SqlParameter(Types.VARCHAR));
			declareParameter(new SqlParameter(Types.VARCHAR));
			compile();
			this.incrementer = incrementer;
		}

		/**
		 *  Method to insert a new <code>Owner</code>.
		 *  @param owner to insert.
		 */
		protected void insert(Owner owner) {
			owner.setId(incrementer.nextIntValue());
			Object[] objs =
				new Object[] {
					new Integer(owner.getId()),
					owner.getFirstName(),
					owner.getLastName(),
					owner.getAddress(),
					owner.getCity(),
					owner.getTelephone()};
			update(objs);
		}

	}

	/**
	 *  Abstract base class for all <code>Pet</code> Query Objects.
	 */
	abstract class PetsQuery extends MappingSqlQuery {

		/** 
		 *  Creates a new instance of PetsQuery
		 *  @param ds the DataSource to use for the query.
		 *  @param sql Value of the SQL to use for the query. 
		 */
		protected PetsQuery(DataSource ds, String sql) {
			super(ds, sql);
		}

		protected Object mapRow(ResultSet rs, int rownum) throws SQLException {
			Pet pet = new Pet();
			pet.setId(rs.getInt("id"));
			pet.setName(rs.getString("name"));
			pet.setBirthDate(rs.getDate("birth_date"));
			pet.setTypeId(rs.getInt("type_id"));
			return pet;
		}

	}

	/**
	 *  <code>Pet</code> by id Query Object.
	 */
	class PetQuery extends PetsQuery {

		/** 
		 *  Creates a new instance of PetQuery
		 *  @param ds the DataSource to use for the query.
		 */
		protected PetQuery(DataSource ds) {
			super(ds, "SELECT id,name,birth_date,type_id FROM pets WHERE id=?");
			declareParameter(new SqlParameter(Types.INTEGER));
			compile();
		}

	}

	/**
	 *  <code>Pet</code>s by <code>Owner</code> Query Object.
	 */
	class PetsByOwnerQuery extends PetsQuery {

		/** 
		 *  Creates a new instance of PetsByOwnerQuery
		 *  @param ds the DataSource to use for the query.
		 */
		protected PetsByOwnerQuery(DataSource ds) {
			super(ds, "SELECT id,name,birth_date,type_id FROM pets WHERE owner_id=?");
			declareParameter(new SqlParameter(Types.INTEGER));
			compile();
		}

	}

	/**
	 *  <code>Pet</code> Update Object.
	 */
	class PetUpdate extends SqlUpdate {

		/** 
		 *  Creates a new instance of PetUpdate
		 *  @param ds the DataSource to use for the update.
		 */
		protected PetUpdate(DataSource ds) {
			super(ds, "UPDATE pets SET name=?,birth_date=?,type_id=?,owner_id=? WHERE id=?");
			declareParameter(new SqlParameter(Types.VARCHAR));
			declareParameter(new SqlParameter(Types.DATE));
			declareParameter(new SqlParameter(Types.INTEGER));
			declareParameter(new SqlParameter(Types.INTEGER));
			declareParameter(new SqlParameter(Types.INTEGER));
			compile();
		}

		/**
		 *  Method to update an <code>Pet</code>'s data.
		 *  @param pet to update.
		 *  @return the number of rows affected by the update
		 */
		protected int update(Pet pet) {
			return this.update(
				new Object[] {
					pet.getName(),
					new java.sql.Date(pet.getBirthDate().getTime()),
					new Integer(pet.getTypeId()),
					new Integer(pet.getOwner().getId()),
					new Integer(pet.getId())
					});
		}

	}

	/**
	 *  <code>Pet</code> Insert Object.
	 */
	class PetInsert extends SqlUpdate {

		/** Holds key generator Object */
		private AbstractDataFieldMaxValueIncrementer incrementer;

		/** 
		 *  Creates a new instance of PetInsert
		 *  @param ds the DataSource to use for the insert.
		 */
		protected PetInsert(AbstractDataFieldMaxValueIncrementer incrementer) {
			super(incrementer.getDataSource(), "INSERT INTO pets VALUES(?,?,?,?,?)");
			declareParameter(new SqlParameter(Types.INTEGER));
			declareParameter(new SqlParameter(Types.VARCHAR));
			declareParameter(new SqlParameter(Types.DATE));
			declareParameter(new SqlParameter(Types.INTEGER));
			declareParameter(new SqlParameter(Types.INTEGER));
			compile();
			this.incrementer = incrementer;
		}

		/**
		 *  Method to insert a new <code>Pet</code>.
		 *  @param pet to insert.
		 */
		protected void insert(Pet pet) {
			pet.setId(incrementer.nextIntValue());
			Object[] objs =
				new Object[] {
					new Integer(pet.getId()),
					pet.getName(),
					new java.sql.Date(pet.getBirthDate().getTime()),
					new Integer(pet.getTypeId()),
					new Integer(pet.getOwner().getId()),
					};
			update(objs);
		}

	}

	/**
	 *  <code>Visit</code>s by <code>Pet</code> Query Object.
	 */
	class VisitsQuery extends MappingSqlQuery {

		/** 
		 *  Creates a new instance of VisitsQuery
		 *  @param ds the DataSource to use for the update.
		 */
		protected VisitsQuery(DataSource ds) {
			super(ds, "SELECT id,visit_date,description FROM visits WHERE pet_id=?");
			declareParameter(new SqlParameter(Types.INTEGER));
			compile();
		}

		protected Object mapRow(ResultSet rs, int rownum) throws SQLException {
			Visit visit = new Visit();
			visit.setId(rs.getInt("id"));
			visit.setDate(rs.getDate("visit_date"));
			visit.setDescription(rs.getString("description"));
			return visit;
		}

	}

	/**
	 *  <code>Visit</code> Insert Object.
	 */
	class VisitInsert extends SqlUpdate {

		/** Holds key generator Object */
		private AbstractDataFieldMaxValueIncrementer incrementer;

		/** 
		 *  Creates a new instance of VisitInsert
		 *  @param ds the DataSource to use for the insert.
		 */
		protected VisitInsert(AbstractDataFieldMaxValueIncrementer incrementer) {
			super(incrementer.getDataSource(), "INSERT INTO visits VALUES(?,?,?,?)");
			declareParameter(new SqlParameter(Types.INTEGER));
			declareParameter(new SqlParameter(Types.INTEGER));
			declareParameter(new SqlParameter(Types.DATE));
			declareParameter(new SqlParameter(Types.VARCHAR));
			compile();
			this.incrementer = incrementer;
		}

		/**
		 *  Method to insert a new <code>Visit</code>.
		 *  @param visit to insert.
		 */
		protected void insert(Visit visit) {
			visit.setId(incrementer.nextIntValue());
			Object[] objs =
				new Object[] {
					new Integer(visit.getId()),
					new Integer(visit.getPet().getId()),
					new java.sql.Date(visit.getDate().getTime()),
					visit.getDescription()
					};
			update(objs);
		}

	}

}
