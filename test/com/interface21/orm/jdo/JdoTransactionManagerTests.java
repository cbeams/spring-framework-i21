package com.interface21.orm.jdo;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Transaction;

import junit.framework.TestCase;
import org.easymock.EasyMock;
import org.easymock.MockControl;

import com.interface21.transaction.InvalidIsolationException;
import com.interface21.transaction.InvalidTimeoutException;
import com.interface21.transaction.PlatformTransactionManager;
import com.interface21.transaction.TransactionDefinition;
import com.interface21.transaction.TransactionStatus;
import com.interface21.transaction.support.TransactionCallback;
import com.interface21.transaction.support.TransactionCallbackWithoutResult;
import com.interface21.transaction.support.TransactionSynchronizationManager;
import com.interface21.transaction.support.TransactionTemplate;

/**
 * @author Juergen Hoeller
 */
public class JdoTransactionManagerTests extends TestCase {

	public void testTransactionCommit() {
		MockControl pmfControl = EasyMock.controlFor(PersistenceManagerFactory.class);
		final PersistenceManagerFactory pmf = (PersistenceManagerFactory) pmfControl.getMock();
		MockControl pmControl = EasyMock.controlFor(PersistenceManager.class);
		PersistenceManager pm = (PersistenceManager) pmControl.getMock();
		MockControl txControl = EasyMock.controlFor(Transaction.class);
		Transaction tx = (Transaction) txControl.getMock();
		pmf.getPersistenceManager();
		pmfControl.setReturnValue(pm, 1);
		pm.currentTransaction();
		pmControl.setReturnValue(tx, 3);
		pm.close();
		pmControl.setReturnValue(null, 1);
		tx.isActive();
		txControl.setReturnValue(false, 1);
		tx.begin();
		txControl.setVoidCallable(1);
		tx.commit();
		txControl.setVoidCallable(1);
		pmfControl.activate();
		pmControl.activate();
		txControl.activate();

		PlatformTransactionManager tm = new JdoTransactionManager(pmf);
		TransactionTemplate tt = new TransactionTemplate(tm);
		final List l = new ArrayList();
		l.add("test");
		assertTrue("Hasn't thread pm", !PersistenceManagerFactoryUtils.getThreadObjectManager().hasThreadObject(pmf));
		assertTrue("JTA synchronizations not active", !TransactionSynchronizationManager.isActive());

		try {
			Object result = tt.execute(new TransactionCallback() {
				public Object doInTransaction(TransactionStatus status) {
					assertTrue("Has thread pm", PersistenceManagerFactoryUtils.getThreadObjectManager().hasThreadObject(pmf));
					JdoTemplate jt = new JdoTemplate(pmf);
					return jt.execute(new JdoCallback() {
						public Object doInJdo(PersistenceManager pm) {
							return l;
						}
					});
				}
			});
			assertTrue("Correct result list", result == l);
		}
		catch (RuntimeException ex) {
			fail("Should not have thrown RuntimeException");
		}

		assertTrue("Hasn't thread pm", !PersistenceManagerFactoryUtils.getThreadObjectManager().hasThreadObject(pmf));
		assertTrue("JTA synchronizations not active", !TransactionSynchronizationManager.isActive());
		pmfControl.verify();
		pmControl.verify();
		txControl.verify();
	}

	public void testTransactionRollback() {
		MockControl pmfControl = EasyMock.controlFor(PersistenceManagerFactory.class);
		final PersistenceManagerFactory pmf = (PersistenceManagerFactory) pmfControl.getMock();
		MockControl pmControl = EasyMock.controlFor(PersistenceManager.class);
		PersistenceManager pm = (PersistenceManager) pmControl.getMock();
		MockControl txControl = EasyMock.controlFor(Transaction.class);
		Transaction tx = (Transaction) txControl.getMock();
		pmf.getPersistenceManager();
		pmfControl.setReturnValue(pm, 1);
		pm.currentTransaction();
		pmControl.setReturnValue(tx, 3);
		pm.close();
		pmControl.setReturnValue(null, 1);
		tx.isActive();
		txControl.setReturnValue(false, 1);
		tx.begin();
		txControl.setVoidCallable(1);
		tx.rollback();
		txControl.setVoidCallable(1);
		pmfControl.activate();
		pmControl.activate();
		txControl.activate();

		PlatformTransactionManager tm = new JdoTransactionManager(pmf);
		TransactionTemplate tt = new TransactionTemplate(tm);
		assertTrue("Hasn't thread pm", !PersistenceManagerFactoryUtils.getThreadObjectManager().hasThreadObject(pmf));
		assertTrue("JTA synchronizations not active", !TransactionSynchronizationManager.isActive());

		try {
			tt.execute(new TransactionCallback() {
				public Object doInTransaction(TransactionStatus status) {
					assertTrue("Has thread pm", PersistenceManagerFactoryUtils.getThreadObjectManager().hasThreadObject(pmf));
					JdoTemplate jt = new JdoTemplate(pmf);
					return jt.execute(new JdoCallback() {
						public Object doInJdo(PersistenceManager pm) {
							throw new RuntimeException("application exception");
						}
					});
				}
			});
			fail("Should have thrown RuntimeException");
		}
		catch (RuntimeException ex) {
			// expected
		}

		assertTrue("Hasn't thread pm", !PersistenceManagerFactoryUtils.getThreadObjectManager().hasThreadObject(pmf));
		assertTrue("JTA synchronizations not active", !TransactionSynchronizationManager.isActive());
		pmfControl.verify();
		pmControl.verify();
		txControl.verify();
	}

	public void testTransactionRollbackOnly() {
		MockControl pmfControl = EasyMock.controlFor(PersistenceManagerFactory.class);
		final PersistenceManagerFactory pmf = (PersistenceManagerFactory) pmfControl.getMock();
		MockControl pmControl = EasyMock.controlFor(PersistenceManager.class);
		PersistenceManager pm = (PersistenceManager) pmControl.getMock();
		MockControl txControl = EasyMock.controlFor(Transaction.class);
		Transaction tx = (Transaction) txControl.getMock();
		pmf.getPersistenceManager();
		pmfControl.setReturnValue(pm, 1);
		pm.currentTransaction();
		pmControl.setReturnValue(tx, 3);
		pm.close();
		pmControl.setReturnValue(null, 1);
		tx.isActive();
		txControl.setReturnValue(false, 1);
		tx.begin();
		txControl.setVoidCallable(1);
		tx.rollback();
		txControl.setVoidCallable(1);
		pmfControl.activate();
		pmControl.activate();
		txControl.activate();

		PlatformTransactionManager tm = new JdoTransactionManager(pmf);
		TransactionTemplate tt = new TransactionTemplate(tm);
		assertTrue("Hasn't thread pm", !PersistenceManagerFactoryUtils.getThreadObjectManager().hasThreadObject(pmf));

		try {
			tt.execute(new TransactionCallback() {
				public Object doInTransaction(TransactionStatus status) {
					assertTrue("Has thread pm", PersistenceManagerFactoryUtils.getThreadObjectManager().hasThreadObject(pmf));
					JdoTemplate jt = new JdoTemplate(pmf);
					jt.execute(new JdoCallback() {
						public Object doInJdo(PersistenceManager pm) {
							return null;
						}
					});
					status.setRollbackOnly();
					return null;
				}
			});
		}
		catch (RuntimeException ex) {
			fail("Should not have thrown RuntimeException");
		}

		assertTrue("Hasn't thread pm", !PersistenceManagerFactoryUtils.getThreadObjectManager().hasThreadObject(pmf));
		pmfControl.verify();
		pmControl.verify();
		txControl.verify();
	}

	public void testNestedTransactionCommit() {
		MockControl pmfControl = EasyMock.controlFor(PersistenceManagerFactory.class);
		final PersistenceManagerFactory pmf = (PersistenceManagerFactory) pmfControl.getMock();
		MockControl pmControl = EasyMock.controlFor(PersistenceManager.class);
		PersistenceManager pm = (PersistenceManager) pmControl.getMock();
		final MockControl txControl = EasyMock.controlFor(Transaction.class);
		final Transaction tx = (Transaction) txControl.getMock();
		pmf.getPersistenceManager();
		pmfControl.setReturnValue(pm, 1);
		pm.currentTransaction();
		pmControl.setReturnValue(tx, 4);
		pm.close();
		pmControl.setReturnValue(null, 1);
		tx.isActive();
		txControl.setReturnValue(false, 1);
		tx.begin();
		txControl.setVoidCallable(1);
		pmfControl.activate();
		pmControl.activate();
		txControl.activate();

		PlatformTransactionManager tm = new JdoTransactionManager(pmf);
		final TransactionTemplate tt = new TransactionTemplate(tm);
		final List l = new ArrayList();
		l.add("test");

		try {
			Object result = tt.execute(new TransactionCallback() {
				public Object doInTransaction(TransactionStatus status) {
					txControl.reset();
					tx.isActive();
					txControl.setReturnValue(true, 1);
					tx.commit();
					txControl.setVoidCallable(1);
					txControl.activate();

					return tt.execute(new TransactionCallback() {
						public Object doInTransaction(TransactionStatus status) {
							JdoTemplate jt = new JdoTemplate(pmf);
							return jt.execute(new JdoCallback() {
								public Object doInJdo(PersistenceManager pm) {
									return l;
								}
							});
						}
					});
				}
			});
			assertTrue("Correct result list", result == l);
		}
		catch (RuntimeException ex) {
			fail("Should not have thrown RuntimeException");
		}

		pmfControl.verify();
		pmControl.verify();
		txControl.verify();
	}

	public void testNestedTransactionRollback() {
		MockControl pmfControl = EasyMock.controlFor(PersistenceManagerFactory.class);
		final PersistenceManagerFactory pmf = (PersistenceManagerFactory) pmfControl.getMock();
		MockControl pmControl = EasyMock.controlFor(PersistenceManager.class);
		PersistenceManager pm = (PersistenceManager) pmControl.getMock();
		final MockControl txControl = EasyMock.controlFor(Transaction.class);
		final Transaction tx = (Transaction) txControl.getMock();
		pmf.getPersistenceManager();
		pmfControl.setReturnValue(pm, 1);
		pm.currentTransaction();
		pmControl.setReturnValue(tx, 4);
		pm.close();
		pmControl.setReturnValue(null, 1);
		tx.isActive();
		txControl.setReturnValue(false, 1);
		tx.begin();
		txControl.setVoidCallable(1);
		pmfControl.activate();
		pmControl.activate();
		txControl.activate();

		PlatformTransactionManager tm = new JdoTransactionManager(pmf);
		final TransactionTemplate tt = new TransactionTemplate(tm);
		try {
			tt.execute(new TransactionCallback() {
				public Object doInTransaction(TransactionStatus status) {
					txControl.reset();
					tx.isActive();
					txControl.setReturnValue(true, 1);
					tx.rollback();
					txControl.setVoidCallable(1);
					txControl.activate();

					return tt.execute(new TransactionCallback() {
						public Object doInTransaction(TransactionStatus status) {
							JdoTemplate jt = new JdoTemplate(pmf);
							return jt.execute(new JdoCallback() {
								public Object doInJdo(PersistenceManager pm) {
									throw new RuntimeException("application exception");
								}
							});
						}
					});
				}
			});
			fail("Should not thrown RuntimeException");
		}
		catch (RuntimeException ex) {
			// expected
		}
		pmfControl.verify();
		pmControl.verify();
		txControl.verify();
	}

	public void testNestedTransactionRollbackOnly() {
		MockControl pmfControl = EasyMock.controlFor(PersistenceManagerFactory.class);
		final PersistenceManagerFactory pmf = (PersistenceManagerFactory) pmfControl.getMock();
		MockControl pmControl = EasyMock.controlFor(PersistenceManager.class);
		PersistenceManager pm = (PersistenceManager) pmControl.getMock();
		final MockControl txControl = EasyMock.controlFor(Transaction.class);
		final Transaction tx = (Transaction) txControl.getMock();
		pmf.getPersistenceManager();
		pmfControl.setReturnValue(pm, 1);
		pm.currentTransaction();
		pmControl.setReturnValue(tx, 4);
		pm.close();
		pmControl.setReturnValue(null, 1);
		tx.isActive();
		txControl.setReturnValue(false, 1);
		tx.begin();
		txControl.setVoidCallable(1);
		pmfControl.activate();
		pmControl.activate();
		txControl.activate();

		PlatformTransactionManager tm = new JdoTransactionManager(pmf);
		final TransactionTemplate tt = new TransactionTemplate(tm);
		final List l = new ArrayList();
		l.add("test");

		try {
			tt.execute(new TransactionCallback() {
				public Object doInTransaction(TransactionStatus status) {
					txControl.reset();
					tx.isActive();
					txControl.setReturnValue(true, 1);
					tx.rollback();
					txControl.setVoidCallable(1);
					txControl.activate();

					return tt.execute(new TransactionCallback() {
						public Object doInTransaction(TransactionStatus status) {
							JdoTemplate jt = new JdoTemplate(pmf);
							jt.execute(new JdoCallback() {
								public Object doInJdo(PersistenceManager pm) {
									return l;
								}
							});
							status.setRollbackOnly();
							return null;
						}
					});
				}
			});
		}
		catch (RuntimeException ex) {
			fail("Should not have thrown RuntimeException");
		}

		pmfControl.verify();
		pmControl.verify();
		txControl.verify();
	}

	public void testInvalidIsolation() {
		MockControl pmfControl = EasyMock.controlFor(PersistenceManagerFactory.class);
		final PersistenceManagerFactory pmf = (PersistenceManagerFactory) pmfControl.getMock();
		MockControl pmControl = EasyMock.controlFor(PersistenceManager.class);
		PersistenceManager pm = (PersistenceManager) pmControl.getMock();
		MockControl txControl = EasyMock.controlFor(Transaction.class);
		Transaction tx = (Transaction) txControl.getMock();
		pmf.getPersistenceManager();
		pmfControl.setReturnValue(pm, 1);
		pm.currentTransaction();
		pmControl.setReturnValue(tx, 1);
		tx.isActive();
		txControl.setReturnValue(false, 1);
		pmfControl.activate();
		pmControl.activate();
		txControl.activate();

		PlatformTransactionManager tm = new JdoTransactionManager(pmf);
		TransactionTemplate tt = new TransactionTemplate(tm);
		tt.setIsolationLevel(TransactionDefinition.ISOLATION_SERIALIZABLE);
		try {
			tt.execute(new TransactionCallbackWithoutResult() {
				protected void doInTransactionWithoutResult(TransactionStatus status) {
				}
			});
			fail("Should have thrown InvalidIsolationException");
		}
		catch (InvalidIsolationException ex) {
			// expected
		}

		pmfControl.verify();
		pmControl.verify();
		txControl.verify();
	}

	public void testInvalidTimeout() {
		MockControl pmfControl = EasyMock.controlFor(PersistenceManagerFactory.class);
		final PersistenceManagerFactory pmf = (PersistenceManagerFactory) pmfControl.getMock();
		MockControl pmControl = EasyMock.controlFor(PersistenceManager.class);
		PersistenceManager pm = (PersistenceManager) pmControl.getMock();
		MockControl txControl = EasyMock.controlFor(Transaction.class);
		Transaction tx = (Transaction) txControl.getMock();
		pmf.getPersistenceManager();
		pmfControl.setReturnValue(pm, 1);
		pm.currentTransaction();
		pmControl.setReturnValue(tx, 1);
		tx.isActive();
		txControl.setReturnValue(false, 1);
		pmfControl.activate();
		pmControl.activate();
		txControl.activate();

		PlatformTransactionManager tm = new JdoTransactionManager(pmf);
		TransactionTemplate tt = new TransactionTemplate(tm);
		tt.setTimeout(10);
		try {
			tt.execute(new TransactionCallbackWithoutResult() {
				protected void doInTransactionWithoutResult(TransactionStatus status) {
				}
			});
			fail("Should have thrown InvalidTimeoutException");
		}
		catch (InvalidTimeoutException ex) {
			// expected
		}

		pmfControl.verify();
		pmControl.verify();
		txControl.verify();
	}

	public void testTransactionCommitWithPrebound() {
		MockControl pmfControl = EasyMock.controlFor(PersistenceManagerFactory.class);
		final PersistenceManagerFactory pmf = (PersistenceManagerFactory) pmfControl.getMock();
		MockControl pmControl = EasyMock.controlFor(PersistenceManager.class);
		PersistenceManager pm = (PersistenceManager) pmControl.getMock();
		MockControl txControl = EasyMock.controlFor(Transaction.class);
		Transaction tx = (Transaction) txControl.getMock();
		pm.currentTransaction();
		pmControl.setReturnValue(tx, 3);
		tx.isActive();
		txControl.setReturnValue(false, 1);
		tx.begin();
		txControl.setVoidCallable(1);
		tx.commit();
		txControl.setVoidCallable(1);
		pmfControl.activate();
		pmControl.activate();
		txControl.activate();

		PlatformTransactionManager tm = new JdoTransactionManager(pmf);
		TransactionTemplate tt = new TransactionTemplate(tm);
		final List l = new ArrayList();
		l.add("test");
		assertTrue("JTA synchronizations not active", !TransactionSynchronizationManager.isActive());
		PersistenceManagerFactoryUtils.getThreadObjectManager().bindThreadObject(pmf, new PersistenceManagerHolder(pm));
		assertTrue("Has thread pm", PersistenceManagerFactoryUtils.getThreadObjectManager().hasThreadObject(pmf));

		try {
			Object result = tt.execute(new TransactionCallback() {
				public Object doInTransaction(TransactionStatus status) {
					assertTrue("Has thread pm", PersistenceManagerFactoryUtils.getThreadObjectManager().hasThreadObject(pmf));
					JdoTemplate jt = new JdoTemplate(pmf);
					return jt.execute(new JdoCallback() {
						public Object doInJdo(PersistenceManager pm) {
							return l;
						}
					});
				}
			});
			assertTrue("Correct result list", result == l);
		}
		catch (RuntimeException ex) {
			fail("Should not have thrown RuntimeException");
		}

		assertTrue("Has thread pm", PersistenceManagerFactoryUtils.getThreadObjectManager().hasThreadObject(pmf));
		PersistenceManagerFactoryUtils.getThreadObjectManager().removeThreadObject(pmf);
		assertTrue("JTA synchronizations not active", !TransactionSynchronizationManager.isActive());
		pmfControl.verify();
		pmControl.verify();
		txControl.verify();
	}

}
