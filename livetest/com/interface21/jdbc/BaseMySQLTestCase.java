package com.interface21.jdbc;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;

import com.interface21.jdbc.datasource.SingleConnectionDataSource;

import junit.framework.TestCase;

/** 
 * Base class for all test cases. Based on BaseTestCase and DataSourceTest in the MySQL test suite.
 * Creates connections, statements, etc. and closes them.
 * 
 * @author  Isabelle Muszynski
 * @version $Id$
 */

public abstract class BaseMySQLTestCase extends TestCase {

    /**
     * Datasource, intialized in setUp() and cleaned up in tearDown().
     **/
    protected DataSource ds;

    /** 
     * Connection to server, initialized in setUp() and
     * cleaned up in tearDown().
     */
    protected Connection conn;
    
    /**
     * JDBC URL
     */
    protected static String dbUrl = "jdbc:mysql:///test?user=test&password=test";

    // Constructors

    /**
     * Creates a new BaseMySQLTestCase object.
     * 
     * @param name The name of the JUnit test case
     */
    public BaseMySQLTestCase(String name) {
        super(name);
   }

    // Methods

    /**
     * Creates resources used by all tests.
     * @throws Exception if an error occurs.
     */
    public void setUp() throws Exception {
	ds = new SingleConnectionDataSource("com.mysql.jdbc.Driver", dbUrl, "test", "test", false);
	if (null != ds)
	    conn = ds.getConnection();   
	if (null == ds || null == conn)
	    throw new Exception("Unsuccessful creation of datasource or connection");
    }

    /**
     * Destroys resources created during the test case.
     * @throws Exception if anything goes wrong
     */
    public void tearDown() throws Exception {

        if (this.conn != null) {

            try {
                this.conn.close();
            } catch (SQLException SQLE) {
                ;
            }
        }
    }
}
