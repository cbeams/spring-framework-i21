package com.interface21.transaction;

import javax.sql.DataSource;

import com.mockobjects.sql.MockConnection;
import junit.framework.TestCase;
import org.easymock.EasyMock;
import org.easymock.MockControl;

import com.interface21.jdbc.core.JdbcTemplate;
import com.interface21.jdbc.core.MockConnectionFactory;
import com.interface21.jdbc.datasource.DataSourceUtils;
import com.interface21.jdbc.datasource.SingleConnectionDataSource;
import com.interface21.transaction.support.AbstractPlatformTransactionManager;
import com.interface21.transaction.datasource.DataSourceTransactionManager;
import com.interface21.transaction.support.TransactionCallbackWithoutResult;
import com.interface21.transaction.support.TransactionTemplate;
import com.interface21.transaction.support.DefaultTransactionDefinition;

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
		try {
			TransactionStatus status = tm.getTransaction(null);
			assertTrue("Must not have transaction", status.getTransaction() == null);
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

	public void testDataSourceTransactionManager() throws Exception {
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
				dsControl.verify();
				assertTrue("Has thread connection", DataSourceUtils.getThreadObjectManager().hasThreadObject(ds));
				JdbcTemplate template = new JdbcTemplate(ds);
				int actualRowsAffected = template.update(sql);
				assertTrue("Actual rows affected is correct", actualRowsAffected == rowsAffected);
			}
		});

		assertTrue("Hasn't thread connection", !DataSourceUtils.getThreadObjectManager().hasThreadObject(ds));
		con.verify();
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

}
