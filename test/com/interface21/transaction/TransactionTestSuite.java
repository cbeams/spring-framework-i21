package com.interface21.transaction;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.mockobjects.sql.MockConnection;
import junit.framework.TestCase;
import org.easymock.EasyMock;
import org.easymock.MockControl;

import com.interface21.jdbc.core.JdbcTemplate;
import com.interface21.jdbc.core.MockConnectionFactory;
import com.interface21.jdbc.datasource.DataSourceUtils;
import com.interface21.jdbc.datasource.SingleConnectionDataSource;
import com.interface21.transaction.datasource.DataSourceTransactionManager;
import com.interface21.transaction.support.AbstractPlatformTransactionManager;
import com.interface21.transaction.support.DefaultTransactionDefinition;
import com.interface21.transaction.support.TransactionCallbackWithoutResult;
import com.interface21.transaction.support.TransactionTemplate;

/**
 * @author Juergen Hoeller
 * @since 29.04.2003
 */
public class TransactionTestSuite extends TestCase {

	public TransactionTestSuite(String location) {
		super(location);
	}


	public void testNoExistingTransaction() {
		PlatformTransactionManager tm = new TestTransactionManager(false, true);
		TransactionStatus status1 = tm.getTransaction(new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_SUPPORTS));
		assertTrue("Must not have transaction", status1.getTransaction() == null);

		TransactionStatus status2 = tm.getTransaction(new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRED));
		assertTrue("Must have transaction", status2.getTransaction() != null);
		assertTrue("Must be new transaction", status2.isNewTransaction());

		try {
			TransactionStatus status3 = tm.getTransaction(new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_MANDATORY));
			fail("Should not have thrown NoTransactionException");
		}
		catch (NoTransactionException ex) {
			// expected
		}
	}

	public void testExistingTransaction() {
		PlatformTransactionManager tm = new TestTransactionManager(true, true);
		TransactionStatus status1 = tm.getTransaction(new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_SUPPORTS));
		assertTrue("Must have transaction", status1.getTransaction() != null);
		assertTrue("Must not be new transaction", !status1.isNewTransaction());

		TransactionStatus status2 = tm.getTransaction(new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRED));
		assertTrue("Must have transaction", status2.getTransaction() != null);
		assertTrue("Must not be new transaction", !status2.isNewTransaction());

		try {
			TransactionStatus status3 = tm.getTransaction(new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_MANDATORY));
			assertTrue("Must have transaction", status3.getTransaction() != null);
			assertTrue("Must not be new transaction", !status3.isNewTransaction());
		}
		catch (NoTransactionException ex) {
			fail("Should not have thrown NoTransactionException");
		}
	}

	public void testNonTransactionalExecution() {
		AbstractPlatformTransactionManager tm = new TestTransactionManager(false, false);
		tm.setAllowNonTransactionalExecution(true);
		assertTrue("Correctly allowed non-transaction execution", tm.getAllowNonTransactionalExecution());
		try {
			TransactionStatus status = tm.getTransaction(null);
			assertTrue("Must not have transaction", status.getTransaction() == null);
			tm.rollback(status);
		}
		catch (NoTransactionException ex) {
			fail("Should not have thrown NoTransactionException");
		}
	}


	public void testCommitWithoutExistingTransaction() {
		TestTransactionManager tm = new TestTransactionManager(false, true);
		TransactionStatus status = tm.getTransaction(null);
		tm.commit(status);
		assertTrue("triggered begin", tm.begin);
		assertTrue("triggered commit", tm.commit);
		assertTrue("no rollback", !tm.rollback);
		assertTrue("no rollbackOnly", !tm.rollbackOnly);
	}

	public void testRollbackWithoutExistingTransaction() {
		TestTransactionManager tm = new TestTransactionManager(false, true);
		TransactionStatus status = tm.getTransaction(null);
		tm.rollback(status);
		assertTrue("triggered begin", tm.begin);
		assertTrue("no commit", !tm.commit);
		assertTrue("triggered rollback", tm.rollback);
		assertTrue("no rollbackOnly", !tm.rollbackOnly);
	}

	public void testRollbackOnlyWithoutExistingTransaction() {
		TestTransactionManager tm = new TestTransactionManager(false, true);
		TransactionStatus status = tm.getTransaction(null);
		status.setRollbackOnly();
		tm.commit(status);
		assertTrue("triggered begin", tm.begin);
		assertTrue("no commit", !tm.commit);
		assertTrue("triggered rollback", tm.rollback);
		assertTrue("no rollbackOnly", !tm.rollbackOnly);
	}


	public void testCommitWithExistingTransaction() {
		TestTransactionManager tm = new TestTransactionManager(true, true);
		TransactionStatus status = tm.getTransaction(null);
		tm.commit(status);
		assertTrue("no begin", !tm.begin);
		assertTrue("no commit", !tm.commit);
		assertTrue("no rollback", !tm.rollback);
		assertTrue("no rollbackOnly", !tm.rollbackOnly);
	}

	public void testRollbackWithExistingTransaction() {
		TestTransactionManager tm = new TestTransactionManager(true, true);
		TransactionStatus status = tm.getTransaction(null);
		tm.rollback(status);
		assertTrue("no begin", !tm.begin);
		assertTrue("no commit", !tm.commit);
		assertTrue("no rollback", !tm.rollback);
		assertTrue("triggered rollbackOnly", tm.rollbackOnly);
	}

	public void testRollbackOnlyWithExistingTransaction() {
		TestTransactionManager tm = new TestTransactionManager(true, true);
		TransactionStatus status = tm.getTransaction(null);
		status.setRollbackOnly();
		tm.commit(status);
		assertTrue("no begin", !tm.begin);
		assertTrue("no commit", !tm.commit);
		assertTrue("no rollback", !tm.rollback);
		assertTrue("triggered rollbackOnly", tm.rollbackOnly);
	}


	public void testTransactionTemplateWithoutError() {
		TestTransactionManager tm = new TestTransactionManager(false, true);
		TransactionTemplate template = new TransactionTemplate(tm);
		try {
			template.execute(new TransactionCallbackWithoutResult() {
				protected void doInTransactionWithoutResult(TransactionStatus status) throws RuntimeException {
				}
			});
			assertTrue("triggered begin", tm.begin);
			assertTrue("triggered commit", tm.commit);
			assertTrue("no rollback", !tm.rollback);
			assertTrue("no rollbackOnly", !tm.rollbackOnly);
		}
		catch (RuntimeException ex) {
			fail("Should not have thrown RuntimeException");
		}
	}

	public void testTransactionTemplateWithError() {
		TestTransactionManager tm = new TestTransactionManager(false, true);
		TransactionTemplate template = new TransactionTemplate(tm);
		try {
			template.execute(new TransactionCallbackWithoutResult() {
				protected void doInTransactionWithoutResult(TransactionStatus status) throws RuntimeException {
					throw new RuntimeException("Some application exception");
				}
			});
			fail("Should have propagated RuntimeException");
		}
		catch (RuntimeException ex) {
			// expected
			assertTrue("triggered begin", tm.begin);
			assertTrue("no commit", !tm.commit);
			assertTrue("triggered rollback", tm.rollback);
			assertTrue("no rollbackOnly", !tm.rollbackOnly);
		}
	}

	public void testTransactionTemplateInitialization() {
		TestTransactionManager tm = new TestTransactionManager(false, true);
		TransactionTemplate template = new TransactionTemplate();
		template.setTransactionManager(tm);
		assertTrue("correct transaction manager set", template.getTransactionManager() == tm);

		try {
			template.setIsolationLevelName("TIMEOUT_DEFAULT");
			fail("Should have thrown IllegalArgumentException");
		}
		catch (IllegalArgumentException ex) {
			// expected
		}
		template.setIsolationLevelName("ISOLATION_SERIALIZABLE");
		assertTrue("Correct isolation level set", template.getIsolationLevel() == TransactionDefinition.ISOLATION_SERIALIZABLE);

		try {
			template.setPropagationBehaviorName("TIMEOUT_DEFAULT");
			fail("Should have thrown IllegalArgumentException");
		}
		catch (IllegalArgumentException ex) {
			// expected
		}
		template.setPropagationBehaviorName("PROPAGATION_SUPPORTS");
		assertTrue("Correct propagation behavior set", template.getPropagationBehavior() == TransactionDefinition.PROPAGATION_SUPPORTS);

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

	public void testDataSourceTransactionManagerWithExceptionOnClose() throws Exception {
		MockControl conControl = EasyMock.controlFor(Connection.class);
		final Connection con = (Connection) conControl.getMock();
		MockControl dsControl = EasyMock.controlFor(DataSource.class);
		DataSource ds = (DataSource) dsControl.getMock();
		ds.getConnection();
		dsControl.setReturnValue(con);
		con.setAutoCommit(false);
		conControl.setVoidCallable();
		con.commit();
		conControl.setVoidCallable();
		con.setAutoCommit(true);
		conControl.setThrowable(new SQLException("cannot reset autoCommit"));
		con.close();
		conControl.setVoidCallable();
		conControl.activate();
		dsControl.activate();

		PlatformTransactionManager tm = new DataSourceTransactionManager(ds);
		TransactionTemplate tt = new TransactionTemplate(tm);
		try {
			tt.execute(new TransactionCallbackWithoutResult() {
				protected void doInTransactionWithoutResult(TransactionStatus status) throws RuntimeException {
					// something transactional
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
