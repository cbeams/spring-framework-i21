package com.interface21.jdbc.object;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Iterator;
import java.util.List;

import javax.sql.DataSource;

import junit.framework.TestCase;

import com.interface21.dao.InvalidDataAccessApiUsageException;
import com.interface21.jdbc.core.JdbcHelper;
import com.interface21.jdbc.core.SqlParameter;
import com.interface21.jdbc.mock.SpringMockDataSource;
import com.interface21.jdbc.mock.SpringMockConnection;
import com.interface21.jdbc.mock.SpringMockPreparedStatement;
import com.interface21.jdbc.mock.SpringMockJdbcFactory;

import com.mockobjects.sql.MockResultSet;

public class SqlQueryTestSuite extends TestCase {

	private static final String SELECT_ID = "select id from custmr";
	private static final String SELECT_ID_WHERE =
		"select id from custmr where forename = ? and id = ?";
	private static final String SELECT_FORENAME = "select forename from custmr";
	private static final String SELECT_FORENAME_EMPTY =
		"select forename from custmr WHERE 1 = 2";
	private static final String SELECT_ID_FORENAME_WHERE =
		"select id, forename from custmr where id = ?";

	private static final String[] COLUMN_NAMES =
		new String[] { "id", "forename" };
	private static final int[] COLUMN_TYPES =
		new int[] { Types.INTEGER, Types.VARCHAR };

	private SpringMockDataSource mockDataSource;
	private SpringMockConnection mockConnection;
	private SpringMockPreparedStatement[] mockPreparedStatement;
	private MockResultSet[] mockResultSet;

	public SqlQueryTestSuite(String name) {
		super(name);
	}

	public void setUp() throws Exception {
		super.setUp();
		mockDataSource = SpringMockJdbcFactory.dataSource();
		mockConnection =
			SpringMockJdbcFactory.connection(false, mockDataSource);
		mockPreparedStatement = null;
	}

	/**
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();

		mockDataSource.verify();
		mockConnection.verify();

		if (mockPreparedStatement != null) {
			for (int i = 0; i < mockPreparedStatement.length; i++) {
				mockPreparedStatement[i].verify();
			}
		}

		if (mockResultSet != null) {
			for (int i = 0; i < mockResultSet.length; i++) {
				mockResultSet[i].verify();
			}
		}
	}

	public void testQueryWithoutParams() {
		mockPreparedStatement =
			new SpringMockPreparedStatement[] {
				 SpringMockJdbcFactory.preparedStatement(
					SELECT_ID,
					null,
					null,
					null,
					mockConnection)};
		mockPreparedStatement[0].setExpectedExecuteCalls(1);
		mockPreparedStatement[0].setExpectedCloseCalls(1);

		mockResultSet =
			new MockResultSet[] {
				SpringMockJdbcFactory
				.resultSet(new Integer[][] { { new Integer(1)}
			}, null, mockPreparedStatement[0])
			};
		mockResultSet[0].setExpectedNextCalls(2);

		SqlQuery query = new ManualExtractionSqlQueryWithParameters() {
			protected Object extract(ResultSet rs, int rownum, Object[] params)
				throws SQLException {
				assertTrue("params were null", params == null);
				return new Integer(rs.getInt(1));
			}
		};

		query.setDataSource(mockDataSource);
		query.setSql(SELECT_ID);
		query.compile();
		List list = query.execute();
		assertTrue("Found customers", list.size() != 0);
		for (Iterator itr = list.iterator(); itr.hasNext();) {
			Integer id = (Integer) itr.next();
			assertTrue(
				"Customer id was assigned correctly",
				id.intValue() == 1);
		}
	}

	public void testQueryWithoutEnoughParams() {
		ManualExtractionSqlQuery query = new ManualExtractionSqlQuery() {
			protected Object extract(ResultSet rs, int rownum)
				throws SQLException {
				return new Integer(rs.getInt(1));
			}

		};
		query.setDataSource(mockDataSource);
		query.setSql(SELECT_ID_WHERE);
		query.declareParameter(
			new SqlParameter(COLUMN_NAMES[0], COLUMN_TYPES[0]));
		query.declareParameter(
			new SqlParameter(COLUMN_NAMES[1], COLUMN_TYPES[1]));
		query.compile();

		try {
			List list = query.execute();
			fail("Shouldn't succeed in running query without enough params");
		} catch (InvalidDataAccessApiUsageException ex) {
			// OK
		}
	}

	public void testBindVariableCountWrong() {
		ManualExtractionSqlQuery query = new ManualExtractionSqlQuery() {
			protected Object extract(ResultSet rs, int rownum)
				throws SQLException {
				return new Integer(rs.getInt(1));
			}
		};
		query.setDataSource(mockDataSource);
		query.setSql(SELECT_ID_WHERE);
		query.declareParameter(
			new SqlParameter(COLUMN_NAMES[0], COLUMN_TYPES[0]));
		query.declareParameter(
			new SqlParameter(COLUMN_NAMES[1], COLUMN_TYPES[1]));
		query.declareParameter(new SqlParameter("NONEXISTENT", Types.VARCHAR));
		try {
			query.compile();
			fail("Shouldn't succeed in compiling query with bind var mismatch");
		} catch (InvalidDataAccessApiUsageException ex) {
			// OK
		}
	}

	public void testUncompiledQuery() {
		ManualExtractionSqlQuery query = new ManualExtractionSqlQuery() {
			protected Object extract(ResultSet rs, int rownum)
				throws SQLException {
				return new Integer(rs.getInt(1));
			}

		};
		query.setDataSource(mockDataSource);
		query.setSql(SELECT_ID);
		try {
			List list = query.execute();
			fail("Shouldn't succeed in running uncompiled query");
		} catch (InvalidDataAccessApiUsageException ex) {
			// OK
		}
	}

	public void testStringQueryWithResults() throws Exception {
		Object[][] forenames = { { "Alpha" }, {
				"Beta" }, {
				"Charlie" }
		};

		mockPreparedStatement =
			new SpringMockPreparedStatement[forenames.length + 1];
		mockResultSet = new MockResultSet[forenames.length + 1];

		mockPreparedStatement[0] =
			SpringMockJdbcFactory.preparedStatement(
				SELECT_FORENAME,
				null,
				null,
				null,
				mockConnection);
		mockPreparedStatement[0].setExpectedExecuteCalls(1);
		mockPreparedStatement[0].setExpectedCloseCalls(1);

		mockResultSet[0] =
			SpringMockJdbcFactory.resultSet(
				forenames,
				null,
				mockPreparedStatement[0]);
		mockResultSet[0].setExpectedNextCalls(4);

		for (int i = 0; i < forenames.length; i++) {
			mockPreparedStatement[i + 1] =
				SpringMockJdbcFactory.preparedStatement(
					"SELECT COUNT(FORENAME) FROM CUSTMR WHERE FORENAME='"
						+ forenames[i][0]
						+ "'",
					null,
					null,
					null,
					mockConnection);
			mockPreparedStatement[i + 1].setExpectedExecuteCalls(1);
			mockPreparedStatement[i + 1].setExpectedCloseCalls(1);

			mockResultSet[i + 1] =
				SpringMockJdbcFactory
					.resultSet(new Integer[][] { { new Integer(1)}
			}, null, mockPreparedStatement[i + 1]);
			mockResultSet[i + 1].setExpectedNextCalls(2);
		}

		JdbcHelper helper = new JdbcHelper(mockDataSource);

		StringQuery query = new StringQuery(mockDataSource, SELECT_FORENAME);
		query.setRowsExpected(3);
		String[] results = query.run();
		assertTrue("Array is non null", results != null);
		assertTrue("Found results", results.length > 0);
		assertTrue(
			"Found expected number of results",
			query.getRowsExpected() == 3);
		for (int i = 0; i < results.length; i++) {
			// BREAKS ON ' in name
			int dbCount =
				helper.runSQLFunction(
					"SELECT COUNT(FORENAME) FROM CUSTMR WHERE FORENAME='"
						+ results[i]
						+ "'");
			assertTrue("found in db", dbCount == 1);
		}
	}

	public void testStringQueryWithoutResults() throws Exception {
		mockPreparedStatement =
			new SpringMockPreparedStatement[] {
				 SpringMockJdbcFactory.preparedStatement(
					SELECT_FORENAME_EMPTY,
					null,
					null,
					null,
					mockConnection)};
		mockPreparedStatement[0].setExpectedExecuteCalls(1);
		mockPreparedStatement[0].setExpectedCloseCalls(1);

		mockResultSet =
			mockResultSet =
				new MockResultSet[] {
					SpringMockJdbcFactory
					.resultSet(new Integer[][] {
			}, null, mockPreparedStatement[0])
			};
		mockResultSet[0].setExpectedNextCalls(1);

		StringQuery query =
			new StringQuery(mockDataSource, SELECT_FORENAME_EMPTY);
		String[] results = query.run();
		assertTrue("Array is non null", results != null);
		assertTrue("Found 0 results", results.length == 0);
	}

	public void testAnonCustomerQuery() {
		mockPreparedStatement =
			new SpringMockPreparedStatement[] {
				 SpringMockJdbcFactory.preparedStatement(
					SELECT_ID_FORENAME_WHERE,
					new Object[] { new Integer(1)},
					null,
					null,
					mockConnection)
		};
		mockPreparedStatement[0].setExpectedExecuteCalls(1);
		mockPreparedStatement[0].setExpectedCloseCalls(1);

		mockResultSet =
			new MockResultSet[] {
				SpringMockJdbcFactory
				.resultSet(new Object[][] { { new Integer(1), "rod" }
			}, COLUMN_NAMES, mockPreparedStatement[0])
			};
		mockResultSet[0].setExpectedNextCalls(2);

		SqlQuery query = new ManualExtractionSqlQuery() {
			protected Object extract(ResultSet rs, int rownum)
				throws SQLException {
				Customer cust = new Customer();
				cust.setId(rs.getInt(COLUMN_NAMES[0]));
				cust.setForename(rs.getString(COLUMN_NAMES[1]));
				return cust;
			}
		};
		query.setDataSource(mockDataSource);
		query.setSql(SELECT_ID_FORENAME_WHERE);
		query.declareParameter(new SqlParameter(Types.NUMERIC));
		query.compile();

		List list = query.execute(1);
		assertTrue("List is non null", list != null);
		assertTrue("Found 1 result", list.size() == 1);
		Customer cust = (Customer) list.get(0);
		assertTrue("Customer id was assigned correctly", cust.getId() == 1);
		assertTrue(
			"Customer forename was assigned correctly",
			cust.getForename().equals("rod"));

		try {
			list = query.execute();
			fail("Shouldn't have executed without arguments");
		} catch (InvalidDataAccessApiUsageException ex) {
			// ok
		}
	}

	public void testFindCustomerIntInt() {
		mockPreparedStatement =
			new SpringMockPreparedStatement[] {
				 SpringMockJdbcFactory.preparedStatement(
					SELECT_ID_FORENAME_WHERE,
					new Object[] { new Integer(1), new Integer(1)},
					null,
					null,
					mockConnection)
		};
		mockPreparedStatement[0].setExpectedExecuteCalls(1);
		mockPreparedStatement[0].setExpectedCloseCalls(1);

		mockResultSet =
			new MockResultSet[] {
				SpringMockJdbcFactory
				.resultSet(new Object[][] { { new Integer(1), "rod" }
			}, COLUMN_NAMES, mockPreparedStatement[0])
			};
		mockResultSet[0].setExpectedNextCalls(2);

		class CustomerQuery extends ManualExtractionSqlQuery {

			public CustomerQuery(DataSource ds) {
				super(ds, SELECT_ID_WHERE);
				declareParameter(new SqlParameter(Types.NUMERIC));
				declareParameter(new SqlParameter(Types.NUMERIC));
				compile();
			}

			protected Object extract(ResultSet rs, int rownum)
				throws SQLException {
				Customer cust = new Customer();
				cust.setId(rs.getInt(COLUMN_NAMES[0]));
				cust.setForename(rs.getString(COLUMN_NAMES[1]));
				return cust;
			}

			public Customer findCustomer(int id, int otherNum) {
				return (Customer) findObject(id, otherNum);
			}
		};
		CustomerQuery query = new CustomerQuery(mockDataSource);
		Customer cust = (Customer) query.findCustomer(1, 1);

		assertTrue("Customer id was assigned correctly", cust.getId() == 1);
		assertTrue(
			"Customer forename was assigned correctly",
			cust.getForename().equals("rod"));
	}

	public void testFindCustomerString() {
		mockPreparedStatement =
			new SpringMockPreparedStatement[] {
				 SpringMockJdbcFactory.preparedStatement(
					SELECT_ID_FORENAME_WHERE,
					new Object[] { "rod" },
					null,
					null,
					mockConnection)
		};
		mockPreparedStatement[0].setExpectedExecuteCalls(1);
		mockPreparedStatement[0].setExpectedCloseCalls(1);

		mockResultSet =
			new MockResultSet[] {
				SpringMockJdbcFactory
				.resultSet(new Object[][] { { new Integer(1), "rod" }
			}, COLUMN_NAMES, mockPreparedStatement[0])
			};
		mockResultSet[0].setExpectedNextCalls(2);

		class CustomerQuery extends ManualExtractionSqlQuery {

			public CustomerQuery(DataSource ds) {
				super(ds, SELECT_ID_FORENAME_WHERE);
				declareParameter(new SqlParameter(Types.VARCHAR));
				compile();
			}

			protected Object extract(ResultSet rs, int rownum)
				throws SQLException {
				Customer cust = new Customer();
				cust.setId(rs.getInt(COLUMN_NAMES[0]));
				cust.setForename(rs.getString(COLUMN_NAMES[1]));
				return cust;
			}

			public Customer findCustomer(String id) {
				return (Customer) findObject(id);
			}
		};
		CustomerQuery query = new CustomerQuery(mockDataSource);
		Customer cust = (Customer) query.findCustomer("rod");

		assertTrue("Customer id was assigned correctly", cust.getId() == 1);
		assertTrue(
			"Customer forename was assigned correctly",
			cust.getForename().equals("rod"));
	}

	public void testFindCustomerMixed() {
		mockPreparedStatement = new SpringMockPreparedStatement[2];
		mockResultSet = new MockResultSet[2];

		mockPreparedStatement[0] =
			SpringMockJdbcFactory.preparedStatement(
				SELECT_ID_WHERE,
				new Object[] { new Integer(1), "rod" },
				null,
				null,
				mockConnection);
		mockPreparedStatement[0].setExpectedExecuteCalls(1);
		mockPreparedStatement[0].setExpectedCloseCalls(1);

		mockResultSet[0] =
			SpringMockJdbcFactory
				.resultSet(new Object[][] { { new Integer(1), "rod" }
		}, COLUMN_NAMES, mockPreparedStatement[0]);
		mockResultSet[0].setExpectedNextCalls(2);

		mockPreparedStatement[1] =
			SpringMockJdbcFactory.preparedStatement(
				SELECT_ID_WHERE,
				new Object[] { new Integer(1), "Roger" },
				null,
				null,
				mockConnection);
		mockPreparedStatement[1].setExpectedExecuteCalls(1);
		mockPreparedStatement[1].setExpectedCloseCalls(1);

		mockResultSet[1] = SpringMockJdbcFactory.resultSet(new Object[][] {
		}, COLUMN_NAMES, mockPreparedStatement[1]);
		mockResultSet[1].setExpectedNextCalls(1);

		class CustomerQuery extends ManualExtractionSqlQuery {

			public CustomerQuery(DataSource ds) {
				super(ds, SELECT_ID_WHERE);
				declareParameter(
					new SqlParameter(COLUMN_NAMES[0], COLUMN_TYPES[0]));
				declareParameter(
					new SqlParameter(COLUMN_NAMES[1], COLUMN_TYPES[1]));
				compile();
			}

			protected Object extract(ResultSet rs, int rownum)
				throws SQLException {
				Customer cust = new Customer();
				cust.setId(rs.getInt(COLUMN_NAMES[0]));
				cust.setForename(rs.getString(COLUMN_NAMES[1]));
				return cust;
			}

			public Customer findCustomer(int id, String name) {
				return (Customer) findObject(
					new Object[] { new Integer(id), name });
			}
		};
		CustomerQuery query = new CustomerQuery(mockDataSource);

		Customer cust1 = (Customer) query.findCustomer(1, "rod");
		assertTrue("Found customer", cust1 != null);
		assertTrue("Customer id was assigned correctly", cust1.id == 1);

		Customer cust2 = (Customer) query.findCustomer(1, "Roger");
		assertTrue("No customer found", cust2 == null);
	}

	public void testFindTooManyCustomers() {
		mockPreparedStatement =
			new SpringMockPreparedStatement[] {
				 SpringMockJdbcFactory.preparedStatement(
					SELECT_ID_FORENAME_WHERE,
					new Object[] { "rod" },
					null,
					null,
					mockConnection)
		};
		mockPreparedStatement[0].setExpectedExecuteCalls(1);
		mockPreparedStatement[0].setExpectedCloseCalls(1);

		mockResultSet =
			new MockResultSet[] {
				SpringMockJdbcFactory
				.resultSet(new Object[][] { { new Integer(1), "rod" }, {
					new Integer(2), "dave" }
			}, COLUMN_NAMES, mockPreparedStatement[0])
			};
		mockResultSet[0].setExpectedNextCalls(3);
		mockResultSet[0].setExpectedCloseCalls(1);

		class CustomerQuery extends ManualExtractionSqlQuery {

			public CustomerQuery(DataSource ds) {
				super(ds, SELECT_ID_FORENAME_WHERE);
				declareParameter(new SqlParameter(Types.VARCHAR));
				compile();
			}

			protected Object extract(ResultSet rs, int rownum)
				throws SQLException {
				Customer cust = new Customer();
				cust.setId(rs.getInt(COLUMN_NAMES[0]));
				cust.setForename(rs.getString(COLUMN_NAMES[1]));
				return cust;
			}

			public Customer findCustomer(String id) {
				return (Customer) findObject(id);
			}
		};
		CustomerQuery query = new CustomerQuery(mockDataSource);
		try {
			Customer cust = (Customer) query.findCustomer("rod");
			fail("Should fail if more than one row found");
		} catch (InvalidDataAccessApiUsageException idaauex) {
			// OK
		}
	}

	public void testListCustomersIntInt() {
		mockPreparedStatement =
			new SpringMockPreparedStatement[] {
				 SpringMockJdbcFactory.preparedStatement(
					SELECT_ID_WHERE,
					new Object[] { new Integer(1), new Integer(1)},
					null,
					null,
					mockConnection)
		};
		mockPreparedStatement[0].setExpectedExecuteCalls(1);
		mockPreparedStatement[0].setExpectedCloseCalls(1);

		mockResultSet =
			new MockResultSet[] {
				SpringMockJdbcFactory
				.resultSet(new Object[][] { { new Integer(1), "rod" }, {
					new Integer(2), "dave" }
			}, COLUMN_NAMES, mockPreparedStatement[0])
			};
		mockResultSet[0].setExpectedNextCalls(3);

		class CustomerQuery extends ManualExtractionSqlQuery {

			public CustomerQuery(DataSource ds) {
				super(ds, SELECT_ID_WHERE);
				declareParameter(new SqlParameter(Types.NUMERIC));
				declareParameter(new SqlParameter(Types.NUMERIC));
				compile();
			}

			protected Object extract(ResultSet rs, int rownum)
				throws SQLException {
				Customer cust = new Customer();
				cust.setId(rs.getInt(COLUMN_NAMES[0]));
				cust.setForename(rs.getString(COLUMN_NAMES[1]));
				return cust;
			}

		};
		CustomerQuery query = new CustomerQuery(mockDataSource);

		List list = query.execute(1, 1);
		assertTrue("2 results in list", list.size() == 2);
		for (Iterator itr = list.iterator(); itr.hasNext();) {
			Customer cust = (Customer) itr.next();
		}
	}

	public void testListCustomersString() {
		mockPreparedStatement =
			new SpringMockPreparedStatement[] {
				 SpringMockJdbcFactory.preparedStatement(
					SELECT_ID_FORENAME_WHERE,
					new Object[] { "one" },
					null,
					null,
					mockConnection)
		};
		mockPreparedStatement[0].setExpectedExecuteCalls(1);
		mockPreparedStatement[0].setExpectedCloseCalls(1);

		mockResultSet =
			new MockResultSet[] {
				SpringMockJdbcFactory
				.resultSet(new Object[][] { { new Integer(1), "rod" }, {
					new Integer(2), "dave" }
			}, COLUMN_NAMES, mockPreparedStatement[0])
			};
		mockResultSet[0].setExpectedNextCalls(3);

		class CustomerQuery extends ManualExtractionSqlQuery {

			public CustomerQuery(DataSource ds) {
				super(ds, SELECT_ID_FORENAME_WHERE);
				declareParameter(new SqlParameter(Types.VARCHAR));
				compile();
			}

			protected Object extract(ResultSet rs, int rownum)
				throws SQLException {
				Customer cust = new Customer();
				cust.setId(rs.getInt(COLUMN_NAMES[0]));
				cust.setForename(rs.getString(COLUMN_NAMES[1]));
				return cust;
			}

		};
		CustomerQuery query = new CustomerQuery(mockDataSource);

		List list = query.execute("one");
		assertTrue("2 results in list", list.size() == 2);
		for (Iterator itr = list.iterator(); itr.hasNext();) {
			Customer cust = (Customer) itr.next();
		}
	}

	public void testFancyCustomerQuery() {
		mockPreparedStatement =
			new SpringMockPreparedStatement[] {
				 SpringMockJdbcFactory.preparedStatement(
					SELECT_ID_FORENAME_WHERE,
					new Object[] { new Integer(1)},
					null,
					null,
					mockConnection)
		};
		mockPreparedStatement[0].setExpectedExecuteCalls(1);
		mockPreparedStatement[0].setExpectedCloseCalls(1);

		mockResultSet =
			new MockResultSet[] {
				SpringMockJdbcFactory
				.resultSet(new Object[][] { { new Integer(1), "rod" }
			}, COLUMN_NAMES, mockPreparedStatement[0])
			};
		mockResultSet[0].setExpectedNextCalls(2);

		class CustomerQuery extends ManualExtractionSqlQuery {

			public CustomerQuery(DataSource ds) {
				super(ds, SELECT_ID_FORENAME_WHERE);
				declareParameter(new SqlParameter(Types.NUMERIC));
				compile();
			}

			protected Object extract(ResultSet rs, int rownum)
				throws SQLException {
				Customer cust = new Customer();
				cust.setId(rs.getInt(COLUMN_NAMES[0]));
				cust.setForename(rs.getString(COLUMN_NAMES[1]));
				return cust;
			}

			public Customer findCustomer(int id) {
				return (Customer) findObject(id);
			}
		};
		CustomerQuery query = new CustomerQuery(mockDataSource);
		Customer cust = (Customer) query.findCustomer(1);

		assertTrue("Customer id was assigned correctly", cust.getId() == 1);
		assertTrue(
			"Customer forename was assigned correctly",
			cust.getForename().equals("rod"));
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
		protected Object extract(ResultSet rs, int rownum)
			throws SQLException {
			return rs.getString(1);
		}

		public String[] run() {
			List list = execute();
			String[] results = (String[]) list.toArray(new String[list.size()]);
			return results;
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
		protected Object extract(ResultSet rs, int rownum)
			throws SQLException {
			Customer cust = new Customer();
			cust.setId(rs.getInt(COLUMN_NAMES[0]));
			cust.setForename(rs.getString(COLUMN_NAMES[1]));
			return cust;
		}
	}

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