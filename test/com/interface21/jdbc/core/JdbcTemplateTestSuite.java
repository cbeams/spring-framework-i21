package com.interface21.jdbc.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.LinkedList;
import java.util.List;

import javax.sql.DataSource;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.easymock.MockControl;

import com.interface21.dao.CleanupFailureDataAccessException;
import com.interface21.dao.DataAccessException;
import com.interface21.dao.DataAccessResourceFailureException;
import com.interface21.dao.UncategorizedDataAccessException;
import com.mockobjects.sql.MockConnection;

/** 
 *
 * @author Rod Johnson
 * @version $RevisionId$
 */
public class JdbcTemplateTestSuite extends TestCase {


	/** Creates new SeatingPlanTest */
	public JdbcTemplateTestSuite(String name) {
		super(name);
	}


	public void testBeanProperties() {
		MockControl dsControl = EasyMock.controlFor(DataSource.class);
		DataSource ds = (DataSource) dsControl.getMock();
		
		JdbcTemplate t = new JdbcTemplate(ds);
		assertTrue("datasource ok", t.getDataSource() == ds);
		assertTrue("ignores warnings by default", t.getIgnoreWarnings());
		t.setIgnoreWarnings(false);
		assertTrue("can set NOT to ignore warnings", !t.getIgnoreWarnings());
	}
	

	public void testUpdateCount() throws Exception {
		final String sql = "UPDATE INVOICE SET DATE_DISPATCHED = SYSDATE WHERE ID = ?";
		
		class Dispatcher implements PreparedStatementCreator {
			private int id;
			public Dispatcher(int id) {
				this.id = id;
			}
			public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
				System.out.println("Connection is " + conn);
				PreparedStatement ps = conn.prepareStatement(sql);
				ps.setInt(1, id);
				return ps;
			}
		};
		
		
		int idParam = 11111;
		
		MockControl dsControl = EasyMock.controlFor(DataSource.class);
		DataSource ds = (DataSource) dsControl.getMock();
				
		
		Dispatcher d = new Dispatcher(idParam);
		
		
		Connection con = MockConnectionFactory.updateWithPreparedStatement(sql, new Object[] { new Integer(idParam) }, 1, true);
		ds.getConnection();
		dsControl.setReturnValue(con);
		dsControl.activate();
		
		JdbcTemplate template = new JdbcTemplate(ds);
		
		int rowsAffected = template.update(d);
		assertTrue("1 update affected 1 row", rowsAffected == 1);
		
		/*
		d = new Dispatcher(idParam);
		rowsAffected = template.update(d);
		assertTrue("bogus update affected 0 rows", rowsAffected == 0);
		*/
		
		dsControl.verify();
	}


	public void testBogusUpdate() throws Exception {
		final String sql = "UPDATE NOSUCHTABLE SET DATE_DISPATCHED = SYSDATE WHERE ID = ?";
		
		final int iParam = 6666;
		
		class Dispatcher implements PreparedStatementCreator {
			private int id;
			public Dispatcher(int id) {
				this.id = id;
			}
			public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
				PreparedStatement ps = conn.prepareStatement(sql);
				ps.setInt(1, id);
				return ps;
			}
		};
		
		
		// It's because Integers aren't canonical
		SQLException sex = new SQLException("bad update");
		MockConnection con = MockConnectionFactory.updateWithPreparedStatement(sql, new Object[] { new Integer(iParam) }, 1, true, sex, null);
		con.setExpectedCloseCalls(1);
		
		MockControl dsControl = EasyMock.controlFor(DataSource.class);
		DataSource ds = (DataSource) dsControl.getMock();
		ds.getConnection();
				dsControl.setReturnValue(con);
				dsControl.activate();
				
		Dispatcher d = new Dispatcher(iParam);
		JdbcTemplate template = new JdbcTemplate(ds);
		
		try {
			int rowsAffected = template.update(d);
			fail("Bogus update should throw exception");
		}
		catch (UncategorizedDataAccessException ex) {
			// pass
			System.out.println(ex);
			assertTrue("Correct exception", ex instanceof UncategorizedSQLException);
			UncategorizedSQLException je = (UncategorizedSQLException) ex;
			assertTrue("Root cause is correct", ex.getRootCause() == sex);
			//assertTrue("no update occurred", !je.getDataWasUpdated());
		}
		
		dsControl.verify();
	}

	public void testStrings() throws Exception {
		class StringHandler implements RowCallbackHandler {
			private List l = new LinkedList();

			public void processRow(ResultSet rs) throws SQLException {
				l.add(rs.getString(1));
			}

			public String[] getStrings() {
				return (String[]) l.toArray(new String[l.size()]);
			}
		}
		StringHandler sh = new StringHandler();
		
		
		String sql  = "SELECT FORENAME FROM CUSTMR";
		MockControl dsControl = EasyMock.controlFor(DataSource.class);
		DataSource ds = (DataSource) dsControl.getMock();

		String[][] results = {
			{ "rod" },
			{ "gary" },
			{" portia" }
		};
		
		MockConnection con = MockConnectionFactory.preparedStatement(sql, null, results, true);
		con.setExpectedCloseCalls(1);
		
		ds.getConnection();
		dsControl.setReturnValue(con);
		dsControl.activate();
		
		JdbcTemplate template = new JdbcTemplate(ds);
		
		template.query(sql, sh);
		
		// Match
		String[] forenames = sh.getStrings();
		assertTrue("same length", forenames.length == results.length);
		for (int i = 0; i < forenames.length; i++) {
			assertTrue("Row " + i + " matches", forenames[i].equals(results[i][0]));
		}
		
		dsControl.verify();
	}
	
	
	// REFACTOR!?
	public void testStringsWithPreparedStatement() throws Exception {
		class StringHandler implements RowCallbackHandler {
			private List l = new LinkedList();

			public void processRow(ResultSet rs) throws SQLException {
				l.add(rs.getString(1));
			}

			public String[] getStrings() {
				return (String[]) l.toArray(new String[l.size()]);
			}
		}
		StringHandler sh = new StringHandler();
		
		final String sql  = "SELECT FORENAME FROM CUSTMR WHERE ID>?";
		String[][] results = {
			{ "rod" },
			{ "gary" },
			{" portia" }
		};
		
		final MockConnection con = MockConnectionFactory.preparedStatement(sql, new Integer[] { new Integer(1) }, results, true);
		con.setExpectedCloseCalls(1);
		
		MockControl dsControl = EasyMock.controlFor(DataSource.class);
		DataSource ds = (DataSource) dsControl.getMock();
		ds.getConnection();
		dsControl.setReturnValue(con);
		dsControl.activate();
		
		JdbcTemplate template = new JdbcTemplate(ds);
		
		PreparedStatementCreator psc = new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
				assertTrue("Conn is correct", conn == con);
				PreparedStatement ps = conn.prepareStatement(sql);
				
				// FIX!>
				ps.setInt(1, 1);
				return ps;
			}
		};
		
		template.query(psc, sh);
		
		// Match
		String[] forenames = sh.getStrings();
		assertTrue("same length", forenames.length == results.length);
		for (int i = 0; i < forenames.length; i++) {
			assertTrue("Row " + i + " matches", forenames[i].equals(results[i][0]));
		}
		
		dsControl.verify();
	}
	
	
	public void testLeaveConnOpenOnRequest() throws Exception {
		
		String sql  = "SELECT ID, FORENAME FROM CUSTMR WHERE ID < 3";
		
		MockControl dsControl = EasyMock.controlFor(DataSource.class);
		
		DataSource ds = (DataSource) dsControl.getMock();
		MockConnection con = MockConnectionFactory.preparedStatement(sql, null, new Object[0][0], false);
		ds.getConnection();
		dsControl.setReturnValue(con);
		dsControl.activate();
		
		SingleConnectionDataSource scf = new SingleConnectionDataSource(ds.getConnection());
		JdbcTemplate template2 = new JdbcTemplate(scf);
		RowCountCallbackHandler rcch = new RowCountCallbackHandler();
		template2.query(sql, rcch);
		
		//assertTrue("SingleConnectionFactory must return same connection", con == scf.getConnection());
		//assertTrue("Single connection " + con + " shouldn't have been closed", !con.isClosed());
	//	scf.close();
	
		dsControl.verify();
	}
	




	public void testCloseConnOnRequest() throws Exception {
		String sql = "SELECT ID, FORENAME FROM CUSTMR WHERE ID < 3";
		MockControl dsControl = EasyMock.controlFor(DataSource.class);
		DataSource ds = (DataSource) dsControl.getMock();
		
		
		MockConnection con = MockConnectionFactory.preparedStatement(sql, null, new Object[0][0], false);
		con.setExpectedCloseCalls(1);
		ds.getConnection();
		dsControl.setReturnValue(con);
		dsControl.activate();
		
		JdbcTemplate template2 = new JdbcTemplate(ds);
		RowCountCallbackHandler rcch = new RowCountCallbackHandler();
		template2.query(sql, rcch);
		dsControl.verify();
		con.verify();
	}


	//public void testSQLExceptionsATranslated() throws Exception {
	//}

// This really tests the translater: shouldn't the SQLTranslater have its own tests?
// We just need to check that the translater is invoked and that it's exception is correctly used


/*
	public void testSQLExceptionIsTranslated() throws Exception {
		
		class TestSqlExceptionTranslater implements SQLExceptionTranslater {
			private int invoked;
			public DataAccessException translate(String task, String sql, SQLException sqlex) {
				// Any subclass will do: can't really check all, can we?
				// YES, we can: loop throuigh with exception as parameter
				System.out.println("Our translater");
				++invoked;
				return new BadSqlGrammarException("Exception doing " + task,sql, sqlex);
			}
		}
		
		TestSqlExceptionTranslater trans = new TestSqlExceptionTranslater();
		
		String sql = "SELECT"; // doesn't really matter what this is
		
		SQLException sex = new SQLException("Any type of SQLException");
		//MockConnection con = MockConnectionFactory.preparedStatement(sql, null, new Object[0][0], false, sex, null);
		
		MockControl conControl = EasyMock.controlFor(Connection.class);
		Connection con = (Connection) conControl.getMock();
		con.prepareStatement(sql);
		
		MockControl psControl = EasyMock.controlFor(PreparedStatement.class);
		PreparedStatement ps = (PreparedStatement) psControl.getMock();
		ps.executeQuery();
		MockSingleRowResultSet rs = new MockSingleRowResultSet();
		rs.setExpectedCloseCalls(1);
		//rs.setupMetaData()
		
		psControl.setReturnValue(rs);
		psControl.activate();
		conControl.setReturnValue(ps);
		con.close();
		conControl.activate();
		
		
		MockControl dsControl = EasyMock.controlFor(DataSource.class);
		DataSource ds = (DataSource) dsControl.getMock();
		ds.getConnection();
		dsControl.setReturnValue(con);
		dsControl.activate();
		
		
		JdbcTemplate template = new JdbcTemplate(ds);
		template.setExceptionTranslater(trans);
		RowCountCallbackHandler rcch = new RowCountCallbackHandler();
		try {
			template.query(PreparedStatementCreatorFactory.newPreparedStatementCreator(sql), rcch);
			fail("Exceptioo should be translated");
		}
		catch (DataAccessException ex) {
			SQLException se2 = (SQLException) ex.getRootCause();
			assertTrue("Found SQL exception", se2 == sex);
			//System.out.println("VENDOR CODE IS " + sex.getErrorCode());
			//System.out.println("SQLSTATE IS " + sex.getSQLState());
		}
		dsControl.verify();
		conControl.verify();
	}
	
	*/

	/**
	 * Test that we see a runtime exception back
	 */
	public void testExceptionComesBack() throws Exception {
		
		final RuntimeException rex = new RuntimeException("What I want to see");
		final String sql = "SELECT ID FROM CUSTMR";
		
		MockControl dsControl = EasyMock.controlFor(DataSource.class);
		DataSource ds = (DataSource) dsControl.getMock();
		
		Object[][] results = new Object[][] {
			{ new Integer(1) },
			{ new Integer(2) }
		};
		Connection con = MockConnectionFactory.preparedStatement(sql, null, results, true);
		ds.getConnection();
		dsControl.setReturnValue(con);
		dsControl.activate();
		
		JdbcTemplate template = new JdbcTemplate(ds);
		try {
			template.query(sql, new RowCallbackHandler() {
				public void processRow(java.sql.ResultSet rs) throws java.sql.SQLException {
					throw rex;
				}
			});
			fail("Should have thrown exception");
		}
		catch (RuntimeException ex) {
			System.out.println(ex);
			assertTrue("Wanted same exception back, not " + ex, ex == rex);
		}	
	}
	
	
	/**
	 * Test update with static SQL
	 */
	
	public void testSqlUpdateEncountersSqlException() throws Exception {
		final String sql = "UPDATE NOSUCHTABLE SET DATE_DISPATCHED = SYSDATE WHERE ID = 4";
		
		MockControl dsControl = EasyMock.controlFor(DataSource.class);
		DataSource ds = (DataSource) dsControl.getMock();
		
		// It's because Integers aren't canonical
		SQLException sex = new SQLException("bad update");
		MockConnection con = MockConnectionFactory.updateWithPreparedStatement(sql, null, 0, true, sex, null);
		con.setExpectedCloseCalls(1);
		
		ds.getConnection();
		dsControl.setReturnValue(con);
		dsControl.activate();
		
		JdbcTemplate template = new JdbcTemplate(ds);
		
		try {
			template.update(sql);
		}
		catch (DataAccessException ex) {
			assertTrue("root cause is correct", ex.getRootCause() == sex);
			// CHECK SQL!?
		}
		
		dsControl.verify();
	}
	
	
	
	public void testSqlUpdate() throws Exception {
		final String sql = "UPDATE NOSUCHTABLE SET DATE_DISPATCHED = SYSDATE WHERE ID = 4";
		
		MockControl dsControl = EasyMock.controlFor(DataSource.class);
		DataSource ds = (DataSource) dsControl.getMock();
		int rowsAffected = 33;
		
		// It's because Integers aren't canonical
		MockConnection con = MockConnectionFactory.updateWithPreparedStatement(sql, null, rowsAffected, true, null, null);
		con.setExpectedCloseCalls(1);
		
		ds.getConnection();
		dsControl.setReturnValue(con);
		dsControl.activate();
		
		JdbcTemplate template = new JdbcTemplate(ds);
		
		int actualRowsAffected = template.update(sql);
		assertTrue("Actual rows affected is correct", actualRowsAffected == rowsAffected);
		
		dsControl.verify();
	}


	public void testCouldntConnect() throws Exception {
		
		MockControl dsControl = EasyMock.controlFor(DataSource.class);
		DataSource ds = (DataSource) dsControl.getMock();
		
		SQLException sex = new SQLException("foo");
		ds.getConnection();
		dsControl.setThrowable(sex);
		dsControl.activate();
		
		JdbcTemplate template2 = new JdbcTemplate(ds);
		RowCountCallbackHandler rcch = new RowCountCallbackHandler();
		try {
			template2.query("SELECT ID, FORENAME FROM CUSTMR WHERE ID < 3", rcch);
			fail("Shouldn't have executed query without a connection");
		}
		catch (DataAccessResourceFailureException ex) {
			// pass
			System.out.println(ex);
			assertTrue("Check root cause", ex.getRootCause() == sex);
		}
	}
	
	
	public void testCouldntClose() throws Exception {
		MockControl dsControl = EasyMock.controlFor(DataSource.class);
		DataSource ds = (DataSource) dsControl.getMock();
		
		MockControl conControl = EasyMock.controlFor(Connection.class);
		Connection con = (Connection) conControl.getMock();
		ds.getConnection();
		dsControl.setReturnValue(con);
		dsControl.activate();
		
		
		SQLException sex = new SQLException();
		con.close();
		conControl.setThrowable(sex);
		conControl.activate();
		
		JdbcTemplate template2 = new JdbcTemplate(ds);
		RowCountCallbackHandler rcch = new RowCountCallbackHandler();
		try {
			template2.query("SELECT ID, FORENAME FROM CUSTMR WHERE ID < 3", rcch);
			fail("Should throw exception on failure to close");
		}
		catch (CleanupFailureDataAccessException ex) {
			// pass
			System.out.println(ex);
			assertTrue("Check root cause", ex.getRootCause() == sex);
		}
		
		dsControl.verify();
		conControl.verify();
	}


	/**
	 * Mock objects allow us to produce warnings at will
	 */
	
	public void testFatalWarning() throws Exception {
		String sql = "SELECT forename from custmr";
		
		MockControl dsControl = EasyMock.controlFor(DataSource.class);
		DataSource ds = (DataSource) dsControl.getMock();
		SQLWarning warnings = new SQLWarning("My warning");
		MockConnection con = MockConnectionFactory.preparedStatement(sql, null, new Object[0][0], true, null, warnings);
		
		ds.getConnection();
		dsControl.setReturnValue(con);
		dsControl.activate();
		
		JdbcTemplate t = new JdbcTemplate(ds);
		
		t.setIgnoreWarnings(false);
		try {
			t.query(sql, new RowCallbackHandler() {
				public void processRow(java.sql.ResultSet rs) throws java.sql.SQLException {
					rs.getByte(1);
				}
			});
			fail("Should have thrown exception on warning");
		}
		catch (SQLWarningException ex) {
			// Pass
			System.out.println("WARNING WAS " + ex);
			assertTrue("Root cause of warning was correct", ex.getRootCause() == warnings);
		}
		
		dsControl.verify();
	}


	public void testIgnoredWarning() throws Exception {
		// REFACTOR FROM ABOVE?
		String sql = "SELECT forename from custmr";
		MockControl dsControl = EasyMock.controlFor(DataSource.class);
			DataSource ds = (DataSource) dsControl.getMock();
			
		SQLWarning warnings = new SQLWarning("My warning");
		MockConnection con = MockConnectionFactory.preparedStatement(sql, null, new Object[0][0], true, null, warnings);
		
		ds.getConnection();
		dsControl.setReturnValue(con);
		dsControl.activate();
		
		// Too long: truncation
		JdbcTemplate t = new JdbcTemplate(ds);
		
		t.setIgnoreWarnings(true);
		t.query(sql, new RowCallbackHandler() {
			public void processRow(java.sql.ResultSet rs) throws java.sql.SQLException {
				rs.getByte(1);
			}
		});
		
		dsControl.verify();
	}
	
}