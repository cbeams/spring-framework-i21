package com.interface21.jdbc.object;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.sql.DataSource;
import jdbc.*;
import jdbc.TestDataSource;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import com.interface21.dao.InvalidDataAccessApiUsageException;
import com.interface21.jdbc.core.*;
import com.interface21.jdbc.core.JdbcHelper;
import com.interface21.jdbc.core.UncategorizedSQLException;
import com.interface21.jdbc.core.JdbcTemplate;
import com.interface21.jdbc.core.SqlParameter;

public class SqlQueryTestSuite extends TestCase {

	//private String sqlBase = "SELECT seat_id, name FROM SEAT WHERE seat_id = ";

	private DataSource ds;

	private JdbcTemplate template;

	private JdbcHelper helper;

	public SqlQueryTestSuite(String name) {
		super(name);
	}

	public void setUp() throws Exception {
		ds = new TestDataSource();
		template = new JdbcTemplate(ds);
		helper = new JdbcHelper(ds);
	}

	public void testQueryWithoutParams() {
		SqlQuery q = new ManualExtractionSqlQueryWithParameters() {
			protected Object extract(ResultSet rs, int rownum, Object[] params) throws SQLException {
				assertTrue("params were null", params == null);
				return new Integer(rs.getInt(1));
			}

		};
		q.setDataSource(ds);
		q.setSql("select id from custmr");
		q.compile();
		Collection c = q.execute();
		assertTrue("Found customers", c.size() != 0);
		for (Iterator itr = c.iterator(); itr.hasNext();) {
			Integer I = (Integer) itr.next();
		}
	}

	public void testQueryWithoutEnoughParams() {
		ManualExtractionSqlQuery q = new ManualExtractionSqlQuery() {
			protected Object extract(ResultSet rs, int rownum) throws SQLException {
				return new Integer(rs.getInt(1));
			}

		};
		q.setDataSource(ds);
		q.setSql("select id from custmr where forename=? and id=?");
		q.declareParameter(new SqlParameter("id", Types.NUMERIC));
		q.declareParameter(new SqlParameter("forename", Types.VARCHAR));
		q.compile();

		try {
			Collection c = q.execute();
			fail("Shouldn't succeed in running query without enough params");
		}
		catch (InvalidDataAccessApiUsageException ex) {
			// OK
		}
	}
	
	public void testBindVariableCountWrong() {
		ManualExtractionSqlQuery q = new ManualExtractionSqlQuery() {
			protected Object extract(ResultSet rs, int rownum) throws SQLException {
				return new Integer(rs.getInt(1));
			}
		};
		q.setDataSource(ds);
		q.setSql("select id from custmr where forename=? and id=?");
		q.declareParameter(new SqlParameter("id", Types.NUMERIC));
		q.declareParameter(new SqlParameter("forename", Types.VARCHAR));
		q.declareParameter(new SqlParameter("NONEXISTENT", Types.VARCHAR));
		try {
			q.compile();
			fail("Shouldn't succeed in compiling query with bind var mismatch");
		}
		catch (InvalidDataAccessApiUsageException ex) {
			// OK
			System.out.println(ex);
		}
	}

	public void testUncompiledQuery() {
		ManualExtractionSqlQuery q = new ManualExtractionSqlQuery() {
			protected Object extract(ResultSet rs, int rownum) throws SQLException {
				return new Integer(rs.getInt(1));
			}

		};
		q.setDataSource(ds);
		q.setSql("select id from custmr");
		//q.compile();
		try {
			Collection c = q.execute();
			fail("Shouldn't succeed in running uncompiled query");
		}
		catch (InvalidDataAccessApiUsageException ex) {
			// OK
		}
	}

	public void testQueryWithMixedParamTypes() {
		SqlQuery q = new ManualExtractionSqlQueryWithParameters() {
			protected Object extract(ResultSet rs, int rownum, Object[] params) throws SQLException {
				assertTrue("params were null", params.length == 2);
				assertTrue("param 0 correct type", params[0] instanceof Integer);
				assertTrue("param 1 correct type", params[1] instanceof String);
				return new Integer(rs.getInt(1));
			}

		};
		q.setDataSource(ds);
		q.setSql("select id from custmr where id=? and forename=?");
		q.declareParameter(new SqlParameter("id", Types.NUMERIC));
		q.declareParameter(new SqlParameter("forename", Types.VARCHAR));
		q.compile();

		Collection c = q.execute(new Object[] { new Integer(1), "rod" });
		assertTrue("Found 1 customer", c.size() == 1);
		for (Iterator itr = c.iterator(); itr.hasNext();) {
			Integer I = (Integer) itr.next();
		}
		c = q.execute(new Object[] { new Integer(1), "Roger" });
		assertTrue("Found 0 customers", c.size() == 0);
	}

	public void testStringQueryWithResults() throws Exception {
		StringQuery sq = new StringQuery(ds, "select forename from custmr");
		String[] sa = sq.run();
		assertTrue("Array is non null", sa != null);
		assertTrue("Found results", sa.length > 0);
		for (int i = 0; i < sa.length; i++) {
			// BREAKS ON ' in name
			int dbCount = helper.runSQLFunction("SELECT COUNT(FORENAME) FROM CUSTMR WHERE FORENAME='" + sa[i] + "'");
			assertTrue("found in db", dbCount == 1);
		}
	}

	public void testStringQueryWithoutResults() throws Exception {
		StringQuery sq = new StringQuery(ds, "select forename from custmr WHERE 1 = 2");
		String[] sa = sq.run();
		assertTrue("Array is non null", sa != null);
		assertTrue("Found 0 results", sa.length == 0);
	}

	public void testBogusStringQuery() throws Exception {
		StringQuery sq = new StringQuery(ds, "SELECT NOSUCHCOLUMN FROM CUSTMR");
		try {
			String[] sa = sq.run();
			fail("Should have thrown exception from run with bogus query");
		}
		catch (BadSqlGrammarException ex) {
			// OK
		}
	}
	
	
	private void testCustomerQuery(SqlQuery q) {
		List l = q.execute();
		assertTrue("Query " + q + " returned customers", !l.isEmpty());
		for (int i = 0; i < l.size(); i++) {
			Customer cust = (Customer) l.get(i);
			System.out.println(cust);
		}
	}
	
	public void testAnonCustomerQuery() {
		SqlQuery customerQuery = new ManualExtractionSqlQuery() {
			protected Object extract(ResultSet rs, int rownum) throws SQLException {
				Customer cust = new Customer();
				cust.setForename(rs.getString("forename"));
				cust.setId(rs.getInt("id"));
				return cust;
			}
		};
		customerQuery.setDataSource(ds);
		customerQuery.setSql("SELECT ID AS ID, FORENAME AS FORENAME FROM CUSTMR WHERE ID=?");
		customerQuery.declareParameter(new SqlParameter(Types.NUMERIC));
		customerQuery.compile();
		
		List l = customerQuery.execute(1);
		
		try {
			l = customerQuery.execute();
			fail("Shouldn't have executed without arguments");
		}
		catch (InvalidDataAccessApiUsageException ex) {
			// ok
		}
	}
	
	
	public void testFancyCustomerQuery() {
		class CustomerQuery extends ManualExtractionSqlQuery {
			
			public CustomerQuery(DataSource ds) {
				super(ds, "SELECT ID AS ID, FORENAME AS FORENAME FROM CUSTMR WHERE ID=?");
				declareParameter(new SqlParameter(Types.NUMERIC));
				compile();
			}
			
			protected Object extract(ResultSet rs, int rownum) throws SQLException {
				Customer cust = new Customer();
				cust.setForename(rs.getString("forename"));
				cust.setId(rs.getInt("id"));
				return cust;
			}
			
			public Customer findCustomer(int id) {
				return (Customer) findObject(id);
			}
		};
		CustomerQuery customerQuery = new CustomerQuery(ds);
		Customer cust = (Customer) customerQuery.findCustomer(1);
	}
	
	public void testManualCustomerQuery() {
		ManCustQuery q = new ManCustQuery(ds, "SELECT ID AS ID, FORENAME AS FORENAME FROM CUSTMR");
		testCustomerQuery(q);
	}
	
//	public void testReflectionCustomerQuery() {
//		ReflectionCustQuery q = new ReflectionCustQuery(ds, "SELECT id, forename FROM CUSTMR");
//		testCustomerQuery(q);
//	}
	

	public static void main(String[] args) {
		TestRunner.run(new TestSuite(SqlQueryTestSuite.class));
	}

	// Could implement an interface
	private static class StringQuery extends ManualExtractionSqlQuery {

		public StringQuery(DataSource ds, String sql) {
			super(ds, sql);
			compile();
		}

		/*
		 * @see CustomExtractionQueryCommand#extract(ResultSet, int)
		 */
		protected Object extract(ResultSet rs, int rownum) throws SQLException {
			return rs.getString(1);
		}

		public String[] run() {
			Collection c = execute();
			String[] sa = (String[]) c.toArray(new String[c.size()]);
			return sa;
		}
	}
	
	
	
	private static class ManCustQuery extends ManualExtractionSqlQuery {

		public ManCustQuery(DataSource ds, String sql) {
			super(ds, sql);
			compile();
		}

		/*
		 * @see CustomExtractionQueryCommand#extract(ResultSet, int)
		 */
		protected Object extract(ResultSet rs, int rownum) throws SQLException {
			Customer cust = new Customer();
			cust.setForename(rs.getString("forename"));
			cust.setId(rs.getInt("id"));
			return cust;
		}
	}
	
	
//	private static class ReflectionCustQuery extends ReflectionExtractionSqlQuery {
//
//		public ReflectionCustQuery(DataSource ds, String sql) {
//			setDataSource(ds);
//			setSql(sql);
//			setResultClass(Customer.class); 
//			compile();
//			// No parameters
//		}
//
//	}
	

	public static class Customer {
		private int id;
		private String forename;
		
		public Customer() {
		}

		/**
		 * Gets the id.
		 * @return Returns a int
		 */
		public int getId() {
			return id;
		}

		/**
		 * Sets the id.
		 * @param id The id to set
		 */
		public void setId(int id) {
			this.id = id;
		}

		/**
		 * Gets the forename.
		 * @return Returns a String
		 */
		public String getForename() {
			return forename;
		}

		/**
		 * Sets the forename.
		 * @param forename The forename to set
		 */
		public void setForename(String forename) {
			this.forename = forename;
		}

		public String toString() {
			return "Customer: id=" + id + "; forename=" + forename;
		}

	}

}