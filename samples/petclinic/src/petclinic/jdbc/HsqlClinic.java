/*
 * HsqlClinic.java
 *
 */

package petclinic.jdbc;

import com.interface21.jdbc.core.support.HsqlMaxValueIncrementer;

/**
 * JavaBean <code>Clinic</code> Hsql implementation
 * 
 * @author Ken Krebs
 */
public class HsqlClinic extends AbstractJdbcClinic {

	/** Method sets up Insert operations with Hsql key generators */
	protected void setupRdbmsOperations() {
		OwnerInsert ownerInsert = new OwnerInsert(new HsqlMaxValueIncrementer(getDataSource(), "owners_seq", "seq"));
		setOwnerInsert(ownerInsert);

		PetInsert petInsert = new PetInsert(new HsqlMaxValueIncrementer(getDataSource(), "pets_seq", "seq"));
		setPetInsert(petInsert);
		
		VisitInsert visitInsert = new VisitInsert(new HsqlMaxValueIncrementer(getDataSource(), "visits_seq", "seq"));
		setVisitInsert(visitInsert);
	}
	
}
