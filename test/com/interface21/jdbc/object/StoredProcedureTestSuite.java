package com.interface21.jdbc.object;

import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import junit.framework.TestCase;

import com.interface21.dao.DataAccessException;
import com.interface21.dao.InvalidDataAccessApiUsageException;
import com.interface21.jdbc.core.BadSqlGrammarException;
import com.interface21.jdbc.core.SQLExceptionTranslater;
import com.interface21.jdbc.core.SqlParameter;
import com.interface21.jdbc.datasource.ConnectionHolder;
import com.interface21.jdbc.datasource.DataSourceUtils;
import com.interface21.jdbc.mock.SpringMockCallableStatement;
import com.interface21.jdbc.mock.SpringMockConnection;
import com.interface21.jdbc.mock.SpringMockDataSource;
import com.interface21.jdbc.mock.SpringMockJdbcFactory;


public class StoredProcedureTestSuite extends TestCase {

	//private String sqlBase = "SELECT seat_id, name FROM SEAT WHERE seat_id = ";

	private SpringMockDataSource mockDataSource;
	private SpringMockConnection mockConnection;
	private SpringMockCallableStatement mockCallable;

	public StoredProcedureTestSuite(String name) {
		super(name);
	}

	public void setUp() throws Exception {
		mockDataSource = SpringMockJdbcFactory.dataSource();
		mockConnection =
			SpringMockJdbcFactory.connection(false, mockDataSource);
	}

	protected void tearDown() throws Exception {
		mockDataSource.verify();
		mockConnection.verify();
		if (mockCallable != null) {
			mockCallable.verify();
		}
	}

	public void testNoSuchStoredProcedure() throws Exception {
		mockDataSource.setExpectedConnectCalls(2);
		mockCallable = new SpringMockCallableStatement();
		mockConnection.addExpectedCallableStatement(mockCallable);

		SQLException sex =
			new SQLException(
				"Syntax error or access violation exception",
				"42000");
		mockCallable.setupThrowExceptionOnExecute(sex);
		mockCallable.setExpectedExecuteCalls(1);

		NoSuchStoredProcedure sproc = new NoSuchStoredProcedure(mockDataSource);
		try {
			sproc.execute();
			fail("Shouldn't succeed in running stored procedure which doesn't exist");
		} catch (BadSqlGrammarException ex) {
			// OK
		}
	}

	private void testAddInvoice(final int amount, final int custid) throws Exception {
		AddInvoice adder = new AddInvoice(mockDataSource);
		int id = adder.execute(amount, custid);
		System.out.println("New row is " + id);
		assertTrue("Received correct new row id", id == 4);
	}

	public void testAddInvoices() throws Exception {
		mockCallable = new SpringMockCallableStatement();
		mockConnection.addExpectedCallableStatement(mockCallable);
		mockCallable.setExpectedExecuteCalls(1);
		mockDataSource.setExpectedConnectCalls(1);
		testAddInvoice(1106, 3);
	}

	public void testAddInvoicesWithinTransaction() throws Exception {
		mockCallable = new SpringMockCallableStatement();
		mockConnection.addExpectedCallableStatement(mockCallable);
		mockCallable.setExpectedExecuteCalls(1);
		mockDataSource.setExpectedConnectCalls(0);
		DataSourceUtils.getThreadObjectManager().bindThreadObject(mockDataSource, new ConnectionHolder(mockConnection));
		try {
			testAddInvoice(1106, 3);
		}
		finally {
			DataSourceUtils.getThreadObjectManager().removeThreadObject(mockDataSource);
		}
	}

	public void testNullArg() throws Exception {
		mockCallable = new SpringMockCallableStatement();
		mockConnection.addExpectedCallableStatement(mockCallable);
		mockCallable.setExpectedExecuteCalls(1);
		mockDataSource.setExpectedConnectCalls(1);

		NullArg na = new NullArg(mockDataSource);
		na.execute((String) null);
	}

	public void testUncompiled() throws Exception {
		UncompiledStoredProcedure uc = new UncompiledStoredProcedure(mockDataSource);
		try {
			uc.execute();
			fail("Shouldn't succeed in executing uncompiled stored procedure");
		} catch (InvalidDataAccessApiUsageException idaauex) {
			// OK
		}
	}

	public void testUnnamedParameter() throws Exception {
		try {
			UnnamedParameterStoredProcedure unp =
				new UnnamedParameterStoredProcedure(mockDataSource);
			fail("Shouldn't succeed in creating stored procedure with unnamed parameter");
		} catch (InvalidDataAccessApiUsageException idaauex) {
			// OK
		}
	}

	public void testMissingParameter() throws Exception {
		mockCallable = new SpringMockCallableStatement();
		mockConnection.addExpectedCallableStatement(mockCallable);
		mockCallable.setExpectedExecuteCalls(0);
		mockDataSource.setExpectedConnectCalls(1);

		try {
			MissingParameterStoredProcedure mp =
				new MissingParameterStoredProcedure(mockDataSource);
			mp.execute();
			fail("Shouldn't succeed in running stored procedure with missing required parameter");
		} catch (InvalidDataAccessApiUsageException idaauex) {
			// OK
		}
	}

	public void testStoredProcedureExceptionTranslator() throws Exception {
		mockDataSource.setExpectedConnectCalls(1);
		mockCallable = new SpringMockCallableStatement();
		mockConnection.addExpectedCallableStatement(mockCallable);

		SQLException sex =
			new SQLException(
				"Syntax error or access violation exception",
				"42000");
		mockCallable.setupThrowExceptionOnExecute(sex);
		mockCallable.setExpectedExecuteCalls(1);

		StoredProcedureExceptionTranslator sproc = new StoredProcedureExceptionTranslator(mockDataSource);
		try {
			sproc.execute();
			fail("Custom exception should be thrown");
		} catch (CustomDataException ex) {
			// OK
		}
	}

	// Could implement an interface
	private class AddInvoice extends StoredProcedure {

		/**
		 * Constructor for AddInvoice.
		 * @param cf
		 * @param name
		 */
		public AddInvoice(DataSource ds) {
			setDataSource(ds);
			setSql("add_invoice");
			declareParameter(new SqlParameter("amount", Types.INTEGER));
			declareParameter(new SqlParameter("custid", Types.INTEGER));
			declareParameter(new OutputParameter("newid", Types.INTEGER));
			compile();
		}

		public int execute(int amount, int custid) {
			Map in = new HashMap();
			in.put("amount", new Integer(amount));
			in.put("custid", new Integer(custid));
			in.put("newid", new Integer(-1));
			Map out = execute(in);
			Number Id = (Number) out.get("newid");
			return Id.intValue();
		}
	}

	private class NullArg extends StoredProcedure {

		/**
		 * Constructor for AddInvoice.
		 * @param cf
		 * @param name
		 */
		public NullArg(DataSource ds) {
			setDataSource(ds);
			setSql("takes_null");
			declareParameter(new SqlParameter("ptest", Types.VARCHAR));
			compile();
		}

		public void execute(String s) {
			Map in = new HashMap();
			in.put("ptest", s);
			Map out = execute(in);
		}
	}

	private class NoSuchStoredProcedure extends StoredProcedure {

		/**
		 * Constructor for AddInvoice.
		 */
		public NoSuchStoredProcedure(DataSource ds) {
			setDataSource(ds);
			setSql("no_sproc_with_this_name");
			compile();
		}

		public void execute() {
			execute(new HashMap());
		}
	}

	private class UncompiledStoredProcedure extends StoredProcedure {

		public UncompiledStoredProcedure(DataSource ds) {
			super(ds, "uncompile_sp");
		}

		public void execute() {
			execute(new HashMap());
		}

	}

	private class UnnamedParameterStoredProcedure extends StoredProcedure {

		public UnnamedParameterStoredProcedure(DataSource ds) {
			super(ds, "unnamed_parameter_sp");
			declareParameter(new SqlParameter(Types.INTEGER));
			compile();
		}

		public void execute(int id) {
			Map in = new HashMap();
			in.put("id", new Integer(id));
			Map out = execute(in);

		}

	}

	private class MissingParameterStoredProcedure extends StoredProcedure {

		public MissingParameterStoredProcedure(DataSource ds) {
			setDataSource(ds);
			setSql("takes_string");
			declareParameter(new SqlParameter("mystring", Types.VARCHAR));
			compile();
		}

		public void execute() {
			execute(new HashMap());
		}
	}

	private class StoredProcedureExceptionTranslator extends StoredProcedure {

		/**
		 * Constructor for AddInvoice.
		 */
		public StoredProcedureExceptionTranslator(DataSource ds) {
			setDataSource(ds);
			setSql("no_sproc_with_this_name");
			setExceptionTranslater( new SQLExceptionTranslater() {
				public DataAccessException translate(String task, String sql, SQLException sqlex) {
					return new CustomDataException(sql, sqlex);
				}

			});
			compile();
		}

		public void execute() {
			execute(new HashMap());
		}
	}

	private class CustomDataException extends DataAccessException {

		public CustomDataException(String s) {
			super(s);
		}

		public CustomDataException(String s, Throwable ex) {
			super(s, ex);
		}
	}

}

