package com.interface21.jdbc;

import com.interface21.jdbc.core.*;
import com.interface21.jdbc.datasource.SingleConnectionDataSource;
import com.interface21.jdbc.core.support.MySQLMaxValueIncrementer;
import com.interface21.jdbc.object.SqlUpdate;

import java.sql.SQLException;
import java.sql.Types;
import java.sql.Statement;
import java.sql.PreparedStatement;

import junit.framework.*;

/** 
 * A mySQL test datasource
 * @author  Isabelle Muszynski
 * @since April 21, 2003
 * @version $Id$
 */

public class JdbcInsertTestSuite extends BaseMySQLTestCase {

    /**
     * Creates a new JdbcInsertTestSuite object.
     * @param name the name of this test suite
     */
    public JdbcInsertTestSuite(String name) {
        super(name);
    }

    /**
     * Set up the test environment
     * @throws Exception if anything goes wrong while setting up the test tables
     */
    public void setUp() throws Exception {
        super.setUp();
        createTestTables();
    }

    /**
     * Test an insert that is not using sequencing
     * @throws Exception if anything goes wrong
     */
    public void testInsertNoSequence() throws Exception {

	JdbcTemplate tpl = new JdbcTemplate(ds);

	int numRows = tpl.update("insert into insert_test values(1, 1)");
	assertTrue("Row was not inserted", 1 == numRows);

	int[] types = new int[] { Types.INTEGER, Types.INTEGER };
	Object[] params = new Object[] { new Integer(2), new Integer(2) };

	PreparedStatementCreator psc = 
	    PreparedStatementCreatorFactory.newPreparedStatementCreator("insert into insert_test values(?, ?)", types, params);
	numRows = tpl.update(psc);
	assertTrue("Row was not inserted", 1 == numRows);
    }

    /**
     * Test an insert that is using sequencing
     * @throws Exception if anything goes wrong
     */
    public void testInsertWithSequence() throws Exception {

	KeyBinder binder = new KeyBinder() {
		public void bind(PreparedStatement ps, Object obj) throws SQLException {
		    ps.setObject(1, obj);
		}
	    };
	JdbcTemplate tpl = new JdbcTemplate(ds);
	MySQLMaxValueIncrementer incr = new MySQLMaxValueIncrementer(ds, "insert_test_seq", "seq2");
	JdbcTemplate.InsertRetval result = tpl.update("insert into insert_test values(?, 1)", binder, incr, int.class);
	assertTrue("Row was not inserted", 1 == result.getRowsAffected());
	assertTrue("Key should have been 101", 101 == ((Integer)result.getKey()).intValue());
    }


    /**
     * Test an insert that is using sequencing using SqlUpdate
     * @throws Exception if anything goes wrong
     */
    public void testInsertWithSequenceAndSqlUpdate() throws Exception {

	KeyBinder binder = new KeyBinder() {
		public void bind(PreparedStatement ps, Object obj) throws SQLException {
		    ps.setObject(1, obj);
		}
	    };
	SqlUpdate upd = new SqlUpdate(ds, "insert into insert_test values(?, 1)");
	upd.compile();
	MySQLMaxValueIncrementer incr = new MySQLMaxValueIncrementer(ds, "insert_test_seq", "seq2");
	JdbcTemplate.InsertRetval result = upd.update(binder, incr, int.class);
	assertTrue("Row was not inserted", 1 == result.getRowsAffected());
	assertTrue("Key should have been 102", 102 == ((Integer)result.getKey()).intValue());
    }

    private void createTestTables() throws SQLException {

	Statement stmt = conn.createStatement();
        try {
            stmt.executeUpdate("drop table insert_test");
        } catch (SQLException sqlEx) { ;/*ignore*/ }
        try {
            stmt.executeUpdate("drop table insert_test_seq");
        } catch (SQLException sqlEx) { ;/*ignore*/ }

        stmt.executeUpdate("create table insert_test (id integer not null primary key, junk integer not null)");
        stmt.executeUpdate(
			   "create table insert_test_seq (seq1 int unsigned not null, seq2 int unsigned not null," +
			   "unique(seq1),unique(seq2))");
	stmt.executeUpdate("insert into insert_test_seq values(100, 100)");

	stmt.close();
    }

    public static void main(String[] args) {
	junit.textui.TestRunner.run(suite());
    }

    public static Test suite() { 
	return new TestSuite(JdbcInsertTestSuite.class);
    }

}
