package com.interface21.jdbc.object;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import com.interface21.jdbc.TestDataSource;
import com.interface21.jdbc.core.BadSqlGrammarException;
import com.interface21.jdbc.core.JdbcTemplate;
import com.interface21.jdbc.core.RowCountCallbackHandler;
import com.interface21.jdbc.core.SqlParameter;

public class StoredProcedureTestSuite extends TestCase {

	//private String sqlBase = "SELECT seat_id, name FROM SEAT WHERE seat_id = ";

	private DataSource ds;
	
	private JdbcTemplate template;
	
	public StoredProcedureTestSuite(String name) {
		super(name);
	}

	public void setUp() throws Exception {
		ds = new TestDataSource();
		template = new JdbcTemplate(ds);
	}
	
	
	public void testNoSuchStoredProcedure() throws Exception {
		NoSuchStoredProcedure sproc = new NoSuchStoredProcedure(ds);
		try {
			sproc.execute();
		}
		catch (BadSqlGrammarException ex) {
			// OK
		}
		
	}

	private void testAddInvoice(final int amount, final int custid) throws Exception {
		AddInvoice adder = new AddInvoice(ds);
		int id = adder.execute(amount, custid);
		System.out.println("New row is " + id);
		 
		// Check withselect
	 	class Checker extends RowCountCallbackHandler {
	 		int dbamount, dbcustid;
	 		public void processRow(ResultSet rs, int rownum) throws SQLException {
				if (rownum != 0)
					throw new RuntimeException("More than one row retrieved");	 			
	 			dbamount = rs.getInt(1);
	 			dbcustid = rs.getInt(2);
	 		}
	 	}
	 	Checker checker = new Checker();
	 	template.query("SELECT AMOUNT, FK_CUST_ID FROM INVOICE WHERE ID = " + id, checker);
	 	assertTrue("Amount and cust id match", checker.dbamount == amount && checker.dbcustid == custid);
	 	System.out.println("MATCHED OK");
	}
	
	public void testAddInvoices() throws Exception {
		testAddInvoice(1106, 3);
	}
	
	
	public void testNullArg() throws Exception {
		NullArg na = new NullArg(ds);
		na.execute((String) null);
	}
	
	
	public static void main(String[] args) {
		TestRunner.run(new TestSuite(StoredProcedureTestSuite.class));
	}
}

// Could implement an interface
class AddInvoice extends StoredProcedure {
 
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
		Map out = execute(in);
		Number Id = (Number) out.get("newid");
		return Id.intValue();
	}
}


//create or replace
//procedure takes_null(ptest in varchar)
//as 
//begin
//	update seat set name='' where name=ptest;
//	
//end;
///


class NullArg extends StoredProcedure {
 
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

class NoSuchStoredProcedure extends StoredProcedure {
 
	/**
	 * Constructor for AddInvoice.
	 * @param cf
	 * @param name 
	 */
	public NoSuchStoredProcedure(DataSource ds) {
		setDataSource(ds);
		setSql("no_sproc_with_this_name");
		compile();
	}

	public void execute() {
		Map out = execute(new HashMap());
	}
}