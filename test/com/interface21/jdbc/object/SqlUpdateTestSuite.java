package com.interface21.jdbc.object;

import java.sql.Types;

import junit.framework.TestCase;

import com.interface21.jdbc.core.JdbcUpdateAffectedIncorrectNumberOfRowsException;
import com.interface21.jdbc.core.SqlParameter;
import com.interface21.jdbc.datasource.*;

public class SqlUpdateTestSuite extends TestCase {

	private static final String UPDATE =
		"update seat_status set booking_id = null";
	private static final String UPDATE_INT =
		"update seat_status set booking_id = null where performance_id = ?";
	private static final String UPDATE_INT_INT =
		"update seat_status set booking_id = null where performance_id = ? and price_band_id = ?";
	private static final String UPDATE_STRING =
		"update seat_status set booking_id = null where name = ?";
	private static final String UPDATE_OBJECTS =
		"update seat_status set booking_id = null where performance_id = ? and price_band_id = ? and name = ? and confirmed = ?";

	//private String sqlBase = "SELECT seat_id, name FROM SEAT WHERE seat_id = ";

	private SpringMockDataSource mockDataSource;
	private SpringMockConnection mockConnection;
	private SpringMockPreparedStatement mockPreparedStatement;

	public SqlUpdateTestSuite(String name) {
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
		if (mockPreparedStatement != null) {
			mockPreparedStatement.verify();
		}

	}

	public void testUpdate() {
		mockPreparedStatement =
			SpringMockJdbcFactory.preparedStatement(
				UPDATE,
				null,
				null,
				null,
				mockConnection);
		mockPreparedStatement.setupUpdateCount(1);
		mockPreparedStatement.setExpectedExecuteCalls(1);
		mockPreparedStatement.setExpectedCloseCalls(1);

		Updater pc = new Updater();
		int rowsAffected = pc.run();
		assertTrue("Update affected 1 row", rowsAffected == 1);
	}

	public void testUpdateInt() {
		mockPreparedStatement =
			SpringMockJdbcFactory.preparedStatement(
				UPDATE_INT,
				new Object[] { new Integer(1)},
				null,
				null,
				mockConnection);
		mockPreparedStatement.setupUpdateCount(1);
		mockPreparedStatement.setExpectedExecuteCalls(1);
		mockPreparedStatement.setExpectedCloseCalls(1);

		IntUpdater pc = new IntUpdater();
		int rowsAffected = pc.run(1);
		assertTrue("Update affected 1 row", rowsAffected == 1);
	}

	public void testUpdateIntInt() {
		mockPreparedStatement =
			SpringMockJdbcFactory.preparedStatement(
				UPDATE_INT_INT,
				new Object[] { new Integer(1), new Integer(1)},
				null,
				null,
				mockConnection);
		mockPreparedStatement.setupUpdateCount(1);
		mockPreparedStatement.setExpectedExecuteCalls(1);
		mockPreparedStatement.setExpectedCloseCalls(1);

		IntIntUpdater pc = new IntIntUpdater();
		int rowsAffected = pc.run(1, 1);
		assertTrue("Update affected 1 row", rowsAffected == 1);
	}

	public void testUpdateString() {
		mockPreparedStatement =
			SpringMockJdbcFactory.preparedStatement(
				UPDATE_STRING,
				new Object[] { new String("rod") },
				null,
				null,
				mockConnection);
		mockPreparedStatement.setupUpdateCount(1);
		mockPreparedStatement.setExpectedExecuteCalls(1);
		mockPreparedStatement.setExpectedCloseCalls(1);

		StringUpdater pc = new StringUpdater();
		int rowsAffected = pc.run("rod");
		assertTrue("Update affected 1 row", rowsAffected == 1);
	}

	public void testUpdateMixed() {
		mockPreparedStatement =
			SpringMockJdbcFactory.preparedStatement(
				UPDATE_STRING,
				new Object[] { new Integer(1), new Integer(1), "rod", new Boolean(true) },
				null,
				null,
				mockConnection);
		mockPreparedStatement.setupUpdateCount(1);
		mockPreparedStatement.setExpectedExecuteCalls(1);
		mockPreparedStatement.setExpectedCloseCalls(1);

		MixedUpdater pc = new MixedUpdater();
		int rowsAffected = pc.run(1,1,"rod",true);
		assertTrue("Update affected 1 row", rowsAffected == 1);
	}

	public void testUpdateConstructor() {
		mockPreparedStatement =
			SpringMockJdbcFactory.preparedStatement(
				UPDATE_STRING,
				new Object[] { new Integer(1), new Integer(1), "rod", new Boolean(true) },
				null,
				null,
				mockConnection);
		mockPreparedStatement.setupUpdateCount(1);
		mockPreparedStatement.setExpectedExecuteCalls(1);
		mockPreparedStatement.setExpectedCloseCalls(1);

		ConstructorUpdater pc = new ConstructorUpdater();
		int rowsAffected = pc.run(1,1,"rod",true);
		assertTrue("Update affected 1 row", rowsAffected == 1);
	}

	public void testUnderMaxRows() {
		mockPreparedStatement =
			SpringMockJdbcFactory.preparedStatement(
				UPDATE_STRING,
				null,
				null,
				null,
				mockConnection);
		mockPreparedStatement.setupUpdateCount(3);
		mockPreparedStatement.setExpectedExecuteCalls(1);
		mockPreparedStatement.setExpectedCloseCalls(1);

		MaxRowsUpdater pc = new MaxRowsUpdater();
		int rowsAffected = pc.run();
		assertTrue("Update affected 4 rows", rowsAffected == 3);
	}
	
	public void testMaxRows() {
		mockPreparedStatement =
			SpringMockJdbcFactory.preparedStatement(
				UPDATE_STRING,
				null,
				null,
				null,
				mockConnection);
		mockPreparedStatement.setupUpdateCount(5);
		mockPreparedStatement.setExpectedExecuteCalls(1);
		mockPreparedStatement.setExpectedCloseCalls(1);

		MaxRowsUpdater pc = new MaxRowsUpdater();
		int rowsAffected = pc.run();
		assertTrue("Update affected 4 rows", rowsAffected == 5);
	}

	public void testOverMaxRows() {
		mockPreparedStatement =
			SpringMockJdbcFactory.preparedStatement(
				UPDATE_STRING,
				null,
				null,
				null,
				mockConnection);
		mockPreparedStatement.setupUpdateCount(8);
		mockPreparedStatement.setExpectedExecuteCalls(1);
		mockPreparedStatement.setExpectedCloseCalls(1);

		MaxRowsUpdater pc = new MaxRowsUpdater();
		try {
		int rowsAffected = pc.run();
		fail("Shouldn't continue when too many rows affected");
		} catch (JdbcUpdateAffectedIncorrectNumberOfRowsException juaicrex) {
			// OK
		}
	}

	public void testRequiredRows() {
		mockPreparedStatement =
			SpringMockJdbcFactory.preparedStatement(
				UPDATE_STRING,
				null,
				null,
				null,
				mockConnection);
		mockPreparedStatement.setupUpdateCount(3);
		mockPreparedStatement.setExpectedExecuteCalls(1);
		mockPreparedStatement.setExpectedCloseCalls(1);

		RequiredRowsUpdater pc = new RequiredRowsUpdater();
		int rowsAffected = pc.run();
		assertTrue("Update affected 3 rows", rowsAffected == 3);	
		}

	public void testNotRequiredRows() {
		mockPreparedStatement =
			SpringMockJdbcFactory.preparedStatement(
				UPDATE_STRING,
				null,
				null,
				null,
				mockConnection);
		mockPreparedStatement.setupUpdateCount(2);
		mockPreparedStatement.setExpectedExecuteCalls(1);
		mockPreparedStatement.setExpectedCloseCalls(1);

		RequiredRowsUpdater pc = new RequiredRowsUpdater();
		try {
		int rowsAffected = pc.run();
		fail("Shouldn't continue when too many rows affected");
		} catch (JdbcUpdateAffectedIncorrectNumberOfRowsException juaicrex) {
			// OK
		}
	}

	class Updater extends SqlUpdate {
		public Updater() {
			setSql(UPDATE);
			setDataSource(mockDataSource);
			compile();
		}

		public int run() {
			return update();
		}
	}

	class IntUpdater extends SqlUpdate {
		public IntUpdater() {
			setSql(UPDATE_INT);
			setDataSource(mockDataSource);
			declareParameter(new SqlParameter(Types.NUMERIC));
			compile();
		}

		public int run(int performanceId) {
			return update(performanceId);
		}
	}

	class IntIntUpdater extends SqlUpdate {
		public IntIntUpdater() {
			setSql(UPDATE_INT_INT);
			setDataSource(mockDataSource);
			declareParameter(new SqlParameter(Types.NUMERIC));
			declareParameter(new SqlParameter(Types.NUMERIC));
			compile();
		}

		public int run(int performanceId, int type) {
			return update(performanceId, type);
		}
	}

	class StringUpdater extends SqlUpdate {
		public StringUpdater() {
			setSql(UPDATE_STRING);
			setDataSource(mockDataSource);
			declareParameter(new SqlParameter(Types.VARCHAR));
			compile();
		}

		public int run(String name) {
			return update(name);
		}
	}

	class MixedUpdater extends SqlUpdate {
		public MixedUpdater() {
			setSql(UPDATE_OBJECTS);
			setDataSource(mockDataSource);
			declareParameter(new SqlParameter(Types.NUMERIC));
			declareParameter(new SqlParameter(Types.NUMERIC));
			declareParameter(new SqlParameter(Types.VARCHAR));
			declareParameter(new SqlParameter(Types.BOOLEAN));
			compile();
		}

		public int run(int performanceId, int type, String name, boolean confirmed) {
			Object[] params = new Object[] { new Integer(performanceId),new Integer(type), name, new Boolean(confirmed) };
			return update(params);
		}
	}

	class ConstructorUpdater extends SqlUpdate {
		public ConstructorUpdater() {
			super(mockDataSource, UPDATE_OBJECTS, new int[] { Types.NUMERIC, Types.NUMERIC, Types.VARCHAR, Types.BOOLEAN } );
			compile();
		}

		public int run(int performanceId, int type, String name, boolean confirmed) {
			Object[] params = new Object[] { new Integer(performanceId),new Integer(type), name, new Boolean(confirmed) };
			return update(params);
		}
	}

	class MaxRowsUpdater extends SqlUpdate {
		public MaxRowsUpdater() {
			setSql(UPDATE);
			setDataSource(mockDataSource);
			setMaxRowsAffected(5);
			compile();
		}

		public int run() {
			return update();
		}
	}

	class RequiredRowsUpdater extends SqlUpdate {
		public RequiredRowsUpdater() {
			setSql(UPDATE);
			setDataSource(mockDataSource);
			setRequiredRowsAffected(3);
			compile();
		}

		public int run() {
			return update();
		}
	}

}
