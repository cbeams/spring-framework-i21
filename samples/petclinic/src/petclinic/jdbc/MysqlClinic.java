/*
 * MysqlClinic.java
 *
 */

package petclinic.jdbc;

import com.interface21.jdbc.core.support.MySQLMaxValueIncrementer;

/**
 * JavaBean <code>Clinic</code> Mysql implementation
 * 
 * @author Ken Krebs
 */
public class MysqlClinic extends AbstractJdbcClinic {

	/** Method sets up Insert operations with Mysql key generators */
	protected void setupRdbmsOperations() {
		OwnerInsert ownerInsert = new OwnerInsert(new MySQLMaxValueIncrementer(getDataSource(), "owners_seq", "seq"));
		setOwnerInsert(ownerInsert);

		PetInsert petInsert = new PetInsert(new MySQLMaxValueIncrementer(getDataSource(), "pets_seq", "seq"));
		setPetInsert(petInsert);
		
		VisitInsert visitInsert = new VisitInsert(new MySQLMaxValueIncrementer(getDataSource(), "visits_seq", "seq"));
		setVisitInsert(visitInsert);
	}

}

