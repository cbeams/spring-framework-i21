package com.interface21.jdbc.datasource;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.mockobjects.sql.MockConnection;
import junit.framework.TestCase;
import org.easymock.EasyMock;
import org.easymock.MockControl;

import com.interface21.jdbc.core.JdbcTemplate;
import com.interface21.jdbc.core.MockConnectionFactory;
import com.interface21.transaction.CannotCreateTransactionException;
import com.interface21.transaction.InvalidTimeoutException;
import com.interface21.transaction.PlatformTransactionManager;
import com.interface21.transaction.TransactionDefinition;
import com.interface21.transaction.TransactionStatus;
import com.interface21.transaction.TransactionSystemException;
import com.interface21.transaction.UnexpectedRollbackException;
import com.interface21.transaction.support.TransactionCallbackWithoutResult;
import com.interface21.transaction.support.TransactionTemplate;

/**
 * @author Juergen Hoeller
 * @since 04.07.2003
 */
public class DataSourceTransactionManagerTests extends TestCase {

	public DataSourceTransactionManagerTests(String msg) {
		super(msg);
	}

	public void testDataSourceTransactionManagerWithSingleConnection() throws Exception {
		final String sql = "UPDATE NOSUCHTABLE SET DATE_DISPATCHED = SYSDATE WHERE ID = 4";
		final int rowsAffected = 33;

		MockConnection con = MockConnectionFactory.updateWithPreparedStatement(sql, null, rowsAffected, true, null, null);
		con.setExpectedCloseCalls(0);
		con.setExpectedCommitCalls(1);
		con.setExpectedRollbackCalls(0);
		final DataSource ds = new SingleConnectionDataSource(con, true);

		PlatformTransactionManager tm = new DataSourceTransactionManager(ds) {
			protected boolean isExistingTransaction(Object transaction) {
				return false;
			}
		};

		TransactionTemplate tt = new TransactionTemplate(tm);
		assertTrue("Hasn't thread connection", !DataSourceUtils.getThreadObjectManager().hasThreadObject(ds));

		tt.execute(new TransactionCallbackWithoutResult() {
			protected void doInTransactionWithoutResult(TransactionStatus status) throws RuntimeException {
				assertTrue("Has thread connection", DataSourceUtils.getThreadObjectManager().hasThreadObject(ds));
				JdbcTemplate template = new JdbcTemplate(ds);
				int actualRowsAffected = template.update(sql);
				assertTrue("Actual rows affected is correct", actualRowsAffected == rowsAffected);
			}
		});

		assertTrue("Hasn't thread connection", !DataSourceUtils.getThreadObjectManager().hasThreadObject(ds));
		con.verify();
	}

	public void testDataSourceTransactionManagerWithCommit() throws Exception {
		final String sql = "UPDATE NOSUCHTABLE SET DATE_DISPATCHED = SYSDATE WHERE ID = 4";

		final MockControl dsControl = EasyMock.controlFor(DataSource.class);
		final DataSource ds = (DataSource) dsControl.getMock();
		final int rowsAffected = 33;

		// It's because Integers aren't canonical
		MockConnection con = MockConnectionFactory.updateWithPreparedStatement(sql, null, rowsAffected, true, null, null);
		con.setExpectedCloseCalls(1);

		ds.getConnection();
		dsControl.setReturnValue(con);
		dsControl.activate();

		PlatformTransactionManager tm = new DataSourceTransactionManager(ds);
		TransactionTemplate tt = new TransactionTemplate(tm);
		assertTrue("Hasn't thread connection", !DataSourceUtils.getThreadObjectManager().hasThreadObject(ds));

		tt.execute(new TransactionCallbackWithoutResult() {
			protected void doInTransactionWithoutResult(TransactionStatus status) throws RuntimeException {
				assertTrue("Has thread connection", DataSourceUtils.getThreadObjectManager().hasThreadObject(ds));
				assertTrue("Is new transaction", status.isNewTransaction());
				JdbcTemplate template = new JdbcTemplate(ds);
				int actualRowsAffected = template.update(sql);
				assertTrue("Actual rows affected is correct", actualRowsAffected == rowsAffected);
			}
		});

		assertTrue("Hasn't thread connection", !DataSourceUtils.getThreadObjectManager().hasThreadObject(ds));
		con.verify();
	}

	public void testDataSourceTransactionManagerWithRollback() throws Exception {
		final String sql = "UPDATE NOSUCHTABLE SET DATE_DISPATCHED = SYSDATE WHERE ID = 4";
		final int rowsAffected = 33;

		MockConnection con = MockConnectionFactory.updateWithPreparedStatement(sql, null, rowsAffected, true, null, null);
		con.setExpectedCloseCalls(0);
		con.setExpectedCommitCalls(0);
		con.setExpectedRollbackCalls(1);
		final DataSource ds = new SingleConnectionDataSource(con, true);

		PlatformTransactionManager tm = new DataSourceTransactionManager(ds) {
			protected boolean isExistingTransaction(Object transaction) {
				return false;
			}
		};

		TransactionTemplate tt = new TransactionTemplate(tm);
		assertTrue("Hasn't thread connection", !DataSourceUtils.getThreadObjectManager().hasThreadObject(ds));

		final RuntimeException ex = new RuntimeException("Application exception");
		try {
			tt.execute(new TransactionCallbackWithoutResult() {
				protected void doInTransactionWithoutResult(TransactionStatus status) throws RuntimeException {
					assertTrue("Has thread connection", DataSourceUtils.getThreadObjectManager().hasThreadObject(ds));
					assertTrue("Is new transaction", status.isNewTransaction());
					JdbcTemplate template = new JdbcTemplate(ds);
					int actualRowsAffected = template.update(sql);
					assertTrue("Actual rows affected is correct", actualRowsAffected == rowsAffected);
					throw ex;
				}
			});
			fail("Should have thrown RuntimeException");
		}
		catch (RuntimeException ex2) {
			// expected
			assertTrue("Hasn't thread connection", !DataSourceUtils.getThreadObjectManager().hasThreadObject(ds));
			assertTrue("Correct exception thrown", ex2.equals(ex));
			con.verify();
		}
	}

	public void testDataSourceTransactionManagerWithRollbackOnly() throws Exception {
		final String sql = "UPDATE NOSUCHTABLE SET DATE_DISPATCHED = SYSDATE WHERE ID = 4";
		final int rowsAffected = 33;

		MockConnection con = MockConnectionFactory.updateWithPreparedStatement(sql, null, rowsAffected, true, null, null);
		con.setExpectedCloseCalls(0);
		con.setExpectedCommitCalls(0);
		con.setExpectedRollbackCalls(0);
		final DataSource ds = new SingleConnectionDataSource(con, true);

		PlatformTransactionManager tm = new DataSourceTransactionManager(ds) {
			protected boolean isExistingTransaction(Object transaction) {
				return true;
			}
		};

		TransactionTemplate tt = new TransactionTemplate(tm);
		assertTrue("Hasn't thread connection", !DataSourceUtils.getThreadObjectManager().hasThreadObject(ds));

		final RuntimeException ex = new RuntimeException("Application exception");
		try {
			tt.execute(new TransactionCallbackWithoutResult() {
				protected void doInTransactionWithoutResult(TransactionStatus status) throws RuntimeException {
					assertTrue("Hasn't thread connection", !DataSourceUtils.getThreadObjectManager().hasThreadObject(ds));
					assertTrue("Is existing transaction", !status.isNewTransaction());
					JdbcTemplate template = new JdbcTemplate(ds);
					int actualRowsAffected = template.update(sql);
					assertTrue("Actual rows affected is correct", actualRowsAffected == rowsAffected);
					throw ex;
				}
			});
			fail("Should have thrown RuntimeException");
		}
		catch (RuntimeException ex2) {
			// expected
			assertTrue("Hasn't thread connection", !DataSourceUtils.getThreadObjectManager().hasThreadObject(ds));
			assertTrue("Correct exception thrown", ex2.equals(ex));
			con.verify();
		}
	}

	public void testDataSourceTransactionManagerWithExistingTransaction() throws Exception {
		final String sql = "UPDATE NOSUCHTABLE SET DATE_DISPATCHED = SYSDATE WHERE ID = 4";
		final int rowsAffected = 33;

		MockConnection con = MockConnectionFactory.updateWithPreparedStatement(sql, null, rowsAffected, true, null, null);
		con.setExpectedCloseCalls(0);
		con.setExpectedCommitCalls(0);
		con.setExpectedRollbackCalls(1);
		final DataSource ds = new SingleConnectionDataSource(con, true);

		PlatformTransactionManager tm = new DataSourceTransactionManager(ds);
		final TransactionTemplate tt = new TransactionTemplate(tm);
		assertTrue("Hasn't thread connection", !DataSourceUtils.getThreadObjectManager().hasThreadObject(ds));

		tt.execute(new TransactionCallbackWithoutResult() {
			protected void doInTransactionWithoutResult(TransactionStatus status) throws RuntimeException {
				assertTrue("Is new transaction", status.isNewTransaction());
				tt.execute(new TransactionCallbackWithoutResult() {
					protected void doInTransactionWithoutResult(TransactionStatus status) throws RuntimeException {
						assertTrue("Has thread connection", DataSourceUtils.getThreadObjectManager().hasThreadObject(ds));
						assertTrue("Is existing transaction", !status.isNewTransaction());
						JdbcTemplate template = new JdbcTemplate(ds);
						int actualRowsAffected = template.update(sql);
						assertTrue("Actual rows affected is correct", actualRowsAffected == rowsAffected);
						status.setRollbackOnly();
					}
				});
				assertTrue("Is new transaction", status.isNewTransaction());
			}
		});
	}

	public void testDataSourceTransactionManagerWithTimeout() throws Exception {
		MockControl conControl = EasyMock.controlFor(Connection.class);
		Connection con = (Connection) conControl.getMock();
		MockControl dsControl = EasyMock.controlFor(DataSource.class);
		DataSource ds = (DataSource) dsControl.getMock();
		ds.getConnection();
		dsControl.setReturnValue(con);
		conControl.activate();
		dsControl.activate();

		DataSourceTransactionManager tm = new DataSourceTransactionManager();
		tm.setDataSource(ds);
		assertTrue("Correct DataSource set", tm.getDataSource() == ds);

		TransactionTemplate tt = new TransactionTemplate(tm);
		tt.setTimeout(10);
		assertTrue("Hasn't thread connection", !DataSourceUtils.getThreadObjectManager().hasThreadObject(ds));
		try {
			tt.execute(new TransactionCallbackWithoutResult() {
				protected void doInTransactionWithoutResult(TransactionStatus status) {
					// something transactional
				}
			});
			fail("Should have thrown InvalidTimeoutException");
		}
		catch (InvalidTimeoutException ex) {
			// expected
			assertTrue("Hasn't thread connection", !DataSourceUtils.getThreadObjectManager().hasThreadObject(ds));
		}
	}

	public void testDataSourceTransactionManagerWithIsolation() throws Exception {
		MockControl conControl = EasyMock.controlFor(Connection.class);
		final Connection con = (Connection) conControl.getMock();
		MockControl dsControl = EasyMock.controlFor(DataSource.class);
		DataSource ds = (DataSource) dsControl.getMock();
		ds.getConnection();
		dsControl.setReturnValue(con);
		con.getTransactionIsolation();
		conControl.setReturnValue(Connection.TRANSACTION_READ_COMMITTED);
		con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
		conControl.setVoidCallable();
		con.setAutoCommit(false);
		conControl.setVoidCallable();
		con.commit();
		conControl.setVoidCallable();
		con.setAutoCommit(true);
		conControl.setVoidCallable();
		con.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
		conControl.setVoidCallable();
		con.close();
		conControl.setVoidCallable();
		conControl.activate();
		dsControl.activate();

		PlatformTransactionManager tm = new DataSourceTransactionManager(ds);
		TransactionTemplate tt = new TransactionTemplate(tm);
		tt.setIsolationLevel(TransactionDefinition.ISOLATION_SERIALIZABLE);
		tt.execute(new TransactionCallbackWithoutResult() {
			protected void doInTransactionWithoutResult(TransactionStatus status) {
				// something transactional
			}
		});

		conControl.verify();
	}

	public void testDataSourceTransactionManagerWithExceptionOnBegin() throws Exception {
		MockControl conControl = EasyMock.controlFor(Connection.class);
		final Connection con = (Connection) conControl.getMock();
		MockControl dsControl = EasyMock.controlFor(DataSource.class);
		DataSource ds = (DataSource) dsControl.getMock();
		ds.getConnection();
		dsControl.setReturnValue(con);
		con.setAutoCommit(false);
		conControl.setThrowable(new SQLException("cannot begin"));
		conControl.activate();
		dsControl.activate();

		PlatformTransactionManager tm = new DataSourceTransactionManager(ds);
		TransactionTemplate tt = new TransactionTemplate(tm);
		try {
			tt.execute(new TransactionCallbackWithoutResult() {
				protected void doInTransactionWithoutResult(TransactionStatus status) {
					// something transactional
				}
			});
			fail("Should have thrown CannotCreateTransactionException");
		}
		catch (CannotCreateTransactionException ex) {
			// expected
		}

		conControl.verify();
	}

	public void testDataSourceTransactionManagerWithExceptionOnCommit() throws Exception {
		MockControl conControl = EasyMock.controlFor(Connection.class);
		final Connection con = (Connection) conControl.getMock();
		MockControl dsControl = EasyMock.controlFor(DataSource.class);
		DataSource ds = (DataSource) dsControl.getMock();
		ds.getConnection();
		dsControl.setReturnValue(con);
		con.setAutoCommit(false);
		conControl.setVoidCallable();
		con.commit();
		conControl.setThrowable(new SQLException("cannot commit"));
		con.setAutoCommit(true);
		conControl.setVoidCallable();
		con.close();
		conControl.setVoidCallable();
		conControl.activate();
		dsControl.activate();

		PlatformTransactionManager tm = new DataSourceTransactionManager(ds);
		TransactionTemplate tt = new TransactionTemplate(tm);
		try {
			tt.execute(new TransactionCallbackWithoutResult() {
				protected void doInTransactionWithoutResult(TransactionStatus status) {
					// something transactional
				}
			});
			fail("Should have thrown UnexpectedRollbackException");
		}
		catch (UnexpectedRollbackException ex) {
			// expected
		}

		conControl.verify();
	}

	public void testDataSourceTransactionManagerWithExceptionOnRollback() throws Exception {
		MockControl conControl = EasyMock.controlFor(Connection.class);
		final Connection con = (Connection) conControl.getMock();
		MockControl dsControl = EasyMock.controlFor(DataSource.class);
		DataSource ds = (DataSource) dsControl.getMock();
		ds.getConnection();
		dsControl.setReturnValue(con);
		con.setAutoCommit(false);
		conControl.setVoidCallable();
		con.rollback();
		conControl.setThrowable(new SQLException("cannot rollback"));
		con.setAutoCommit(true);
		conControl.setVoidCallable();
		con.close();
		conControl.setVoidCallable();
		conControl.activate();
		dsControl.activate();

		PlatformTransactionManager tm = new DataSourceTransactionManager(ds);
		TransactionTemplate tt = new TransactionTemplate(tm);
		try {
			tt.execute(new TransactionCallbackWithoutResult() {
				protected void doInTransactionWithoutResult(TransactionStatus status) throws RuntimeException {
					status.setRollbackOnly();
				}
			});
			fail("Should have thrown TransactionSystemException");
		}
		catch (TransactionSystemException ex) {
			// expected
		}

		conControl.verify();
	}

}
