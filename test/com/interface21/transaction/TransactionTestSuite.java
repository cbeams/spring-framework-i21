package com.interface21.transaction;

import junit.framework.TestCase;

import com.interface21.transaction.support.AbstractPlatformTransactionManager;
import com.interface21.transaction.support.TransactionCallbackWithoutResult;

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
		TransactionStatus status1 = tm.getTransaction(PlatformTransactionManager.PROPAGATION_SUPPORTS,
		                                              PlatformTransactionManager.ISOLATION_DEFAULT);
		assertTrue("Must not have transaction", status1.getTransaction() == null);

		TransactionStatus status2 = tm.getTransaction(PlatformTransactionManager.PROPAGATION_REQUIRED,
		                                              PlatformTransactionManager.ISOLATION_DEFAULT);
		assertTrue("Must have transaction", status2.getTransaction() != null);
		assertTrue("Must be new transaction", status2.isNewTransaction());

		try {
			TransactionStatus status3 = tm.getTransaction(PlatformTransactionManager.PROPAGATION_MANDATORY,
																										PlatformTransactionManager.ISOLATION_DEFAULT);
			fail("Should not have thrown NoTransactionException");
		}
		catch (NoTransactionException ex) {
			// expected
		}
	}

	public void testExistingTransaction() {
		PlatformTransactionManager tm = new TestTransactionManager(true, true);
		TransactionStatus status1 = tm.getTransaction(PlatformTransactionManager.PROPAGATION_SUPPORTS,
		                                              PlatformTransactionManager.ISOLATION_DEFAULT);
		assertTrue("Must have transaction", status1.getTransaction() != null);
		assertTrue("Must not be new transaction", !status1.isNewTransaction());

		TransactionStatus status2 = tm.getTransaction(PlatformTransactionManager.PROPAGATION_REQUIRED,
		                                              PlatformTransactionManager.ISOLATION_DEFAULT);
		assertTrue("Must have transaction", status2.getTransaction() != null);
		assertTrue("Must not be new transaction", !status2.isNewTransaction());

		try {
			TransactionStatus status3 = tm.getTransaction(PlatformTransactionManager.PROPAGATION_MANDATORY,
																										PlatformTransactionManager.ISOLATION_DEFAULT);
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
			TransactionStatus status = tm.getTransaction(PlatformTransactionManager.PROPAGATION_REQUIRED,
																									 PlatformTransactionManager.ISOLATION_DEFAULT);
			assertTrue("Must not have transaction", status.getTransaction() == null);
		}
		catch (NoTransactionException ex) {
			fail("Should not have thrown NoTransactionException");
		}
	}


	public void testCommitWithoutExistingTransaction() {
		TestTransactionManager tm = new TestTransactionManager(false, true);
		TransactionStatus status = tm.getTransaction(PlatformTransactionManager.PROPAGATION_REQUIRED,
		                                             PlatformTransactionManager.ISOLATION_DEFAULT);
		tm.commit(status);
		assertTrue("triggered begin", tm.begin);
		assertTrue("triggered commit", tm.commit);
		assertTrue("no rollback", !tm.rollback);
		assertTrue("no rollbackOnly", !tm.rollbackOnly);
	}

	public void testRollbackWithoutExistingTransaction() {
		TestTransactionManager tm = new TestTransactionManager(false, true);
		TransactionStatus status = tm.getTransaction(PlatformTransactionManager.PROPAGATION_REQUIRED,
		                                             PlatformTransactionManager.ISOLATION_DEFAULT);
		tm.rollback(status);
		assertTrue("triggered begin", tm.begin);
		assertTrue("no commit", !tm.commit);
		assertTrue("triggered rollback", tm.rollback);
		assertTrue("no rollbackOnly", !tm.rollbackOnly);
	}

	public void testRollbackOnlyWithoutExistingTransaction() {
		TestTransactionManager tm = new TestTransactionManager(false, true);
		TransactionStatus status = tm.getTransaction(PlatformTransactionManager.PROPAGATION_REQUIRED,
		                                             PlatformTransactionManager.ISOLATION_DEFAULT);
		status.setRollbackOnly();
		tm.commit(status);
		assertTrue("triggered begin", tm.begin);
		assertTrue("no commit", !tm.commit);
		assertTrue("triggered rollback", tm.rollback);
		assertTrue("no rollbackOnly", !tm.rollbackOnly);
	}


	public void testCommitWithExistingTransaction() {
		TestTransactionManager tm = new TestTransactionManager(true, true);
		TransactionStatus status = tm.getTransaction(PlatformTransactionManager.PROPAGATION_REQUIRED,
		                                             PlatformTransactionManager.ISOLATION_DEFAULT);
		tm.commit(status);
		assertTrue("no begin", !tm.begin);
		assertTrue("no commit", !tm.commit);
		assertTrue("no rollback", !tm.rollback);
		assertTrue("no rollbackOnly", !tm.rollbackOnly);
	}

	public void testRollbackWithExistingTransaction() {
		TestTransactionManager tm = new TestTransactionManager(true, true);
		TransactionStatus status = tm.getTransaction(PlatformTransactionManager.PROPAGATION_REQUIRED,
		                                             PlatformTransactionManager.ISOLATION_DEFAULT);
		tm.rollback(status);
		assertTrue("no begin", !tm.begin);
		assertTrue("no commit", !tm.commit);
		assertTrue("no rollback", !tm.rollback);
		assertTrue("triggered rollbackOnly", tm.rollbackOnly);
	}

	public void testRollbackOnlyWithExistingTransaction() {
		TestTransactionManager tm = new TestTransactionManager(true, true);
		TransactionStatus status = tm.getTransaction(PlatformTransactionManager.PROPAGATION_REQUIRED,
		                                             PlatformTransactionManager.ISOLATION_DEFAULT);
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

}
