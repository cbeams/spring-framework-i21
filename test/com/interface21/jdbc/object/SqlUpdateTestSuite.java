package com.interface21.jdbc.object;

import java.sql.Types;

import javax.sql.DataSource;

import junit.framework.TestCase;

import com.interface21.jdbc.TestDataSource;
import com.interface21.jdbc.core.JdbcHelper;
import com.interface21.jdbc.core.JdbcTemplate;
import com.interface21.jdbc.core.SqlParameter;

public class SqlUpdateTestSuite extends TestCase {

	//private String sqlBase = "SELECT seat_id, name FROM SEAT WHERE seat_id = ";

	private DataSource ds;

	private JdbcTemplate template;

	private JdbcHelper helper;

	public SqlUpdateTestSuite(String name) {
		super(name);
	}

	public void setUp() throws Exception {
		ds = new TestDataSource();
		template = new JdbcTemplate(ds);
		helper = new JdbcHelper(ds);
	}

	public void testValidUpdate() {
		PerformanceCleaner pc = new PerformanceCleaner();
		pc.clearBookings(1,1);
	}
	
	public void testBogusSql() {
	}
	
	
	class PerformanceCleaner extends SqlUpdate {
		public PerformanceCleaner() {
			setSql("update seat_status set booking_id = null where performance_id = ? and price_band_id = ?");
			setDataSource(ds);
			declareParameter(new SqlParameter(Types.NUMERIC));
			declareParameter(new SqlParameter(Types.NUMERIC));
			compile();
		}
		
		public int clearBookings(int performanceId, int type) {
			return update(new Object[] { new Integer(performanceId), new Integer(type) });
		}
	}

}

