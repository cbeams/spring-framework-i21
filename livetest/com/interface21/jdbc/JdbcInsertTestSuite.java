package com.interface21.jdbc;

import com.interface21.jdbc.core.*;
import com.interface21.jdbc.datasource.*;
import com.interface21.jdbc.core.support.MySQLMaxValueIncrementer;
import com.interface21.jdbc.object.*;

import java.sql.SQLException;
import java.sql.Types;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.util.List;

import junit.framework.*;

/** 
 * mySQL test suite for key autogeneration
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

	int numRows = tpl.update("insert into insert_test1 values(1, 1, 1)");
	assertTrue("Row was not inserted", 1 == numRows);

	int[] types = new int[] { Types.INTEGER, Types.INTEGER, Types.INTEGER };
	Object[] params = new Object[] { new Integer(2), new Integer(2), new Integer(2) };

	PreparedStatementCreator psc = 
	    PreparedStatementCreatorFactory.newPreparedStatementCreator("insert into insert_test1 values(?, ?, ?)", types, params);
	numRows = tpl.update(psc);
	assertTrue("Row was not inserted", 1 == numRows);
    }

    /**
     * Test an insert that is using sequencing
     * @throws Exception if anything goes wrong
     */
    public void testInsertWithSequence() throws Exception {

	JdbcTemplate tpl = new JdbcTemplate(ds);

	int[] types = new int[] { Types.INTEGER, Types.INTEGER, Types.INTEGER };
	Object[] params = new Object[] { null, new Integer(2), new Integer(2) };

	MySQLMaxValueIncrementer incr = new MySQLMaxValueIncrementer(ds, "insert_test_seq1", "seq1");
	params[0] = new Integer(incr.nextIntValue());
	System.out.println("key = " + params[0]);
	assertTrue("Key should have been 101", 101 == ((Integer)params[0]).intValue());

	PreparedStatementCreator psc = 
	    PreparedStatementCreatorFactory.newPreparedStatementCreator(
	    "insert into insert_test1 values(?, ?, ?)", types, params);
	int numRows = tpl.update(psc);
	assertTrue("Row was not inserted", 1 == numRows);
    }

    /**
     * Test incrementBy
     * @throws Exception if anything goes wrong
     */
    public void testIncrementBy() throws Exception {

	JdbcTemplate tpl = new JdbcTemplate(ds);

	int[] types = new int[] { Types.INTEGER, Types.INTEGER, Types.INTEGER };
	Object[] params = new Object[] { null, new Integer(2), new Integer(2) };

	MySQLMaxValueIncrementer incr = new MySQLMaxValueIncrementer(ds, "insert_test_seq1", "seq1", 2);

	for (int i = 0; i < 4; i++) {
	    params[0] = new Integer(incr.nextIntValue());
	    System.out.println("key = " + params[0]);
	    assertTrue("Key should have been " + 100 + i + 1, 100 + i + 1 == ((Integer)params[0]).intValue());
	}
    }

    /**
     * Test an insert that is using sequencing using SqlUpdate
     * @throws Exception if anything goes wrong
     */
    public void testInsertWithSequenceAndSqlUpdate() throws Exception {

	int[] types = new int[] { Types.INTEGER, Types.INTEGER, Types.INTEGER };
	Object[] params = new Object[] { null, new Integer(3), new Integer(3) };
	MySQLMaxValueIncrementer incr = new MySQLMaxValueIncrementer(ds, "insert_test_seq1", "seq1");
	params[0] = new Integer(incr.nextIntValue());
	System.out.println("key = " + params[0]);
	assertTrue("Key should have been 101", 101 == ((Integer)params[0]).intValue());
	SqlUpdate upd = new SqlUpdate(ds, "insert into insert_test1 values(?, ?, ?)", types, 1);
	upd.compile();
	upd.update(params);
    }

    /**
     * Multithreaded test
     * @throws Exception if anything goes wrong
     */
    public void testMultiThreaded() throws Exception {
	DriverManagerDataSource localds = new DriverManagerDataSource("com.mysql.jdbc.Driver", "jdbc:mysql:///test", "test", "test");

	Thread[] threads = new Thread[10];
	class MyRunnable implements Runnable {
	    private AbstractDataSource ds;
	    private String name;
	    MyRunnable(AbstractDataSource ds, int index) {
		this.ds = ds;
		this.name = "thread" + index;
	    }
	    public void run() {
		try {
		    int[] types = new int[] { Types.INTEGER, Types.INTEGER, Types.INTEGER };
		    Object[] params = new Object[] { null, new Integer(3), new Integer(3) };
		    MySQLMaxValueIncrementer incr = new MySQLMaxValueIncrementer(ds, "insert_test_seq1", "seq1", 2);
		    SqlUpdate upd = new SqlUpdate(ds, "insert into insert_test1 values(?, ?, ?)", types, 1);
		    upd.compile();
		    for (int i = 0; i < 10; i++) {
			params[0] = new Integer(incr.nextIntValue());
			System.out.println(name + " : key = " + ((Integer)params[0]).intValue());
			upd.update(params);
			Thread.yield();
		    }
		} catch (Throwable e) {
		    System.out.println("Exception in thread " + name);
		    e.printStackTrace();
		}
	    }
	}
	for (int i = 0; i < 10; i++) {
	    threads[i] = new Thread(new MyRunnable(localds, i));
	    threads[i].setName("thread" + i);
	}
	for (int i = 0; i < 10; i++) {
	    threads[i].start();
	}
	for (int i = 0; i < 10; i++) {
	    threads[i].join();
	}
	SqlQuery query = new MappingSqlQuery(ds, "select seq1 from insert_test_seq1") {
		protected Object mapRow(ResultSet rs, int rownum) throws SQLException {
		    return new Long(rs.getLong(1));
		}
	    };
	query.compile();
	List l = query.execute();
	assertTrue("Seuence table should contain only one record", 1 == l.size());
	Long key = (Long) l.get(0);
	System.out.println("Last key value : " + key.longValue());
	assertTrue("Last key value should have been 200", 200 == key.longValue());
    }

    private void createTestTables() throws SQLException {

	Statement stmt = conn.createStatement();
        try {
            stmt.executeUpdate("drop table insert_test1");
        } catch (SQLException sqlEx) { ;/*ignore*/ }
        try {
            stmt.executeUpdate("drop table insert_test2");
        } catch (SQLException sqlEx) { ;/*ignore*/ }
        try {
            stmt.executeUpdate("drop table insert_test_seq1");
        } catch (SQLException sqlEx) { ;/*ignore*/ }
        try {
            stmt.executeUpdate("drop table insert_test_seq2");
        } catch (SQLException sqlEx) { ;/*ignore*/ }

        stmt.executeUpdate("create table insert_test1 (id integer not null primary key, junk11 integer not null, junk12 integer not null)");
        stmt.executeUpdate("create table insert_test2 (id integer not null primary key, junk21 integer not null, junk22 integer not null)");
        stmt.executeUpdate("create table insert_test_seq1 (seq1 int unsigned not null auto_increment primary key)");
	stmt.executeUpdate("insert into insert_test_seq1 values(100)");
        stmt.executeUpdate("create table insert_test_seq2 (seq2 int unsigned not null auto_increment primary key)");
	stmt.executeUpdate("insert into insert_test_seq2 values(100)");

	stmt.close();
    }

    public static void main(String[] args) {
	junit.textui.TestRunner.run(suite());
    }

    public static Test suite() { 
	return new TestSuite(JdbcInsertTestSuite.class);
    }

}
