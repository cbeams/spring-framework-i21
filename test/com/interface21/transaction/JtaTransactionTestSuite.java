package com.interface21.transaction;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import junit.framework.TestCase;
import org.easymock.EasyMock;
import org.easymock.MockControl;

import com.interface21.jndi.JndiTemplate;
import com.interface21.jndi.support.SimpleNamingContext;
import com.interface21.transaction.jta.JtaTransactionManager;
import com.interface21.transaction.support.TransactionSynchronizationManager;
import com.interface21.transaction.support.TransactionSynchronization;
import com.interface21.transaction.support.TransactionCallbackWithoutResult;
import com.interface21.transaction.support.TransactionTemplate;

/**
 * @author Juergen Hoeller
 * @since 12.05.2003
 */
public class JtaTransactionTestSuite extends TestCase {

	public JtaTransactionTestSuite(String msg) {
		super(msg);
	}

	public static TransactionTemplate getTransactionTemplateForJta(final String utName, final UserTransaction ut) {
		JndiTemplate jndiTemplate = new JndiTemplate() {
			protected Context createInitialContext() throws NamingException {
				Context mockContext = new SimpleNamingContext();
				mockContext.bind(utName, ut);
				return mockContext;
			}
		};
		JtaTransactionManager tm = new JtaTransactionManager();
		tm.setJndiTemplate(jndiTemplate);
		tm.setUserTransactionName(utName);
		return new TransactionTemplate(tm);
	}

	public void testJtaTransactionManagerWithCommit() throws Exception {
		MockControl utControl = EasyMock.controlFor(UserTransaction.class);
		UserTransaction ut = (UserTransaction) utControl.getMock();
		ut.getStatus();
		utControl.setReturnValue(Status.STATUS_NO_TRANSACTION, 1);
		ut.begin();
		utControl.setVoidCallable(1);
		ut.commit();
		utControl.setVoidCallable(1);
		utControl.activate();

		TransactionTemplate tt = getTransactionTemplateForJta(JtaTransactionManager.DEFAULT_USER_TRANSACTION_NAME, ut);
		tt.execute(new TransactionCallbackWithoutResult() {
			protected void doInTransactionWithoutResult(TransactionStatus status) {
				// something transactional
				TransactionSynchronizationManager.register(new TransactionSynchronization() {
					public void afterCompletion(int status) {
						assertTrue("Correct completion status", status == TransactionSynchronization.STATUS_COMMITTED);
					}
				});
			}
		});

		utControl.verify();
	}

	public void testJtaTransactionManagerWithRollback() throws Exception {
		MockControl utControl = EasyMock.controlFor(UserTransaction.class);
		UserTransaction ut = (UserTransaction) utControl.getMock();
		ut.getStatus();
		utControl.setReturnValue(Status.STATUS_NO_TRANSACTION, 1);
		ut.setTransactionTimeout(10);
		utControl.setVoidCallable(1);
		ut.begin();
		utControl.setVoidCallable(1);
		ut.rollback();
		utControl.setVoidCallable(1);
		utControl.activate();

		TransactionTemplate tt = getTransactionTemplateForJta("test", ut);
		tt.setTimeout(10);
		tt.execute(new TransactionCallbackWithoutResult() {
			protected void doInTransactionWithoutResult(TransactionStatus status) {
				TransactionSynchronizationManager.register(new TransactionSynchronization() {
					public void afterCompletion(int status) {
						assertTrue("Correct completion status", status == TransactionSynchronization.STATUS_ROLLED_BACK);
					}
				});
				status.setRollbackOnly();
			}
		});

		utControl.verify();
	}

	public void testJtaTransactionManagerWithExistingTransaction() throws Exception {
		MockControl utControl = EasyMock.controlFor(UserTransaction.class);
		UserTransaction ut = (UserTransaction) utControl.getMock();
		ut.getStatus();
		utControl.setReturnValue(Status.STATUS_ACTIVE, 1);
		ut.setRollbackOnly();
		utControl.setVoidCallable(1);
		utControl.activate();

		TransactionTemplate tt = getTransactionTemplateForJta(JtaTransactionManager.DEFAULT_USER_TRANSACTION_NAME, ut);
		tt.execute(new TransactionCallbackWithoutResult() {
			protected void doInTransactionWithoutResult(TransactionStatus status) {
				TransactionSynchronizationManager.register(new TransactionSynchronization() {
					public void afterCompletion(int status) {
						fail("Shouldn't have been triggered");
					}
				});
				status.setRollbackOnly();
			}
		});

		utControl.verify();
	}

	public void testJtaTransactionManagerWithIsolationLevel() throws Exception {
		MockControl utControl = EasyMock.controlFor(UserTransaction.class);
		UserTransaction ut = (UserTransaction) utControl.getMock();
		ut.getStatus();
		utControl.setReturnValue(Status.STATUS_NO_TRANSACTION, 1);
		utControl.activate();

		try {
			TransactionTemplate tt = getTransactionTemplateForJta(JtaTransactionManager.DEFAULT_USER_TRANSACTION_NAME, ut);
			tt.setIsolationLevel(TransactionDefinition.ISOLATION_SERIALIZABLE);
			tt.execute(new TransactionCallbackWithoutResult() {
				protected void doInTransactionWithoutResult(TransactionStatus status) {
					// something transactional
				}
			});
			fail("Should have thrown InvalidIsolationException");
		}
		catch (InvalidIsolationException ex) {
			// expected
		}

		utControl.verify();
	}

	public void testJtaTransactionManagerWithoutJtaSupport() throws Exception {
		JtaTransactionManager tm = new JtaTransactionManager();
		try {
			tm.getTransaction(null);
			fail("Should have thrown CannotCreateTransactionException");
		}
		catch (CannotCreateTransactionException ex) {
			// expected
		}
	}

	public void testJtaTransactionManagerWithSystemExceptionOnIsExisting() throws Exception {
		MockControl utControl = EasyMock.controlFor(UserTransaction.class);
		UserTransaction ut = (UserTransaction) utControl.getMock();
		ut.getStatus();
		utControl.setThrowable(new SystemException("system exception"));
		utControl.activate();

		try {
			TransactionTemplate tt = getTransactionTemplateForJta(JtaTransactionManager.DEFAULT_USER_TRANSACTION_NAME, ut);
			tt.execute(new TransactionCallbackWithoutResult() {
				protected void doInTransactionWithoutResult(TransactionStatus status) {
					// something transactional
				}
			});
			fail("Should have thrown TransactionSystemException");
		}
		catch (TransactionSystemException ex) {
			// expected
		}

		utControl.verify();
	}

	public void testJtaTransactionManagerWithNotSupportedExceptionOnBegin() throws Exception {
		MockControl utControl = EasyMock.controlFor(UserTransaction.class);
		UserTransaction ut = (UserTransaction) utControl.getMock();
		ut.getStatus();
		utControl.setReturnValue(Status.STATUS_NO_TRANSACTION, 1);
		ut.begin();
		utControl.setThrowable(new NotSupportedException("not supported"));
		utControl.activate();

		try {
			TransactionTemplate tt = getTransactionTemplateForJta(JtaTransactionManager.DEFAULT_USER_TRANSACTION_NAME, ut);
			tt.execute(new TransactionCallbackWithoutResult() {
				protected void doInTransactionWithoutResult(TransactionStatus status) {
					// something transactional
				}
			});
			fail("Should have thrown NestedTransactionNotPermittedException");
		}
		catch (NestedTransactionNotPermittedException ex) {
			// expected
		}

		utControl.verify();
	}

	public void testJtaTransactionManagerWithUnsupportedOperationExceptionOnBegin() throws Exception {
		MockControl utControl = EasyMock.controlFor(UserTransaction.class);
		UserTransaction ut = (UserTransaction) utControl.getMock();
		ut.getStatus();
		utControl.setReturnValue(Status.STATUS_NO_TRANSACTION, 1);
		ut.begin();
		utControl.setThrowable(new UnsupportedOperationException("not supported"));
		utControl.activate();

		try {
			TransactionTemplate tt = getTransactionTemplateForJta(JtaTransactionManager.DEFAULT_USER_TRANSACTION_NAME, ut);
			tt.execute(new TransactionCallbackWithoutResult() {
				protected void doInTransactionWithoutResult(TransactionStatus status) {
					// something transactional
				}
			});
			fail("Should have thrown NestedTransactionNotPermittedException");
		}
		catch (NestedTransactionNotPermittedException ex) {
			// expected
		}

		utControl.verify();
	}

	public void testJtaTransactionManagerWithSystemExceptionOnBegin() throws Exception {
		MockControl utControl = EasyMock.controlFor(UserTransaction.class);
		UserTransaction ut = (UserTransaction) utControl.getMock();
		ut.getStatus();
		utControl.setReturnValue(Status.STATUS_NO_TRANSACTION, 1);
		ut.begin();
		utControl.setThrowable(new SystemException("system exception"));
		utControl.activate();

		try {
			TransactionTemplate tt = getTransactionTemplateForJta(JtaTransactionManager.DEFAULT_USER_TRANSACTION_NAME, ut);
			tt.execute(new TransactionCallbackWithoutResult() {
				protected void doInTransactionWithoutResult(TransactionStatus status) {
					// something transactional
				}
			});
			fail("Should have thrown TransactionSystemException");
		}
		catch (TransactionSystemException ex) {
			// expected
		}

		utControl.verify();
	}

	public void testJtaTransactionManagerWithRollbackExceptionOnCommit() throws Exception {
		MockControl utControl = EasyMock.controlFor(UserTransaction.class);
		UserTransaction ut = (UserTransaction) utControl.getMock();
		ut.getStatus();
		utControl.setReturnValue(Status.STATUS_NO_TRANSACTION, 1);
		ut.begin();
		utControl.setVoidCallable(1);
		ut.commit();
		utControl.setThrowable(new RollbackException("unexpected rollback"));
		utControl.activate();

		try {
			TransactionTemplate tt = getTransactionTemplateForJta(JtaTransactionManager.DEFAULT_USER_TRANSACTION_NAME, ut);
			tt.execute(new TransactionCallbackWithoutResult() {
				protected void doInTransactionWithoutResult(TransactionStatus status) {
					// something transactional
					TransactionSynchronizationManager.register(new TransactionSynchronization() {
						public void afterCompletion(int status) {
							assertTrue("Correct completion status", status == TransactionSynchronization.STATUS_ROLLED_BACK);
						}
					});
				}
			});
			fail("Should have thrown UnexpectedRollbackException");
		}
		catch (UnexpectedRollbackException ex) {
			// expected
		}

		utControl.verify();
	}

	public void testJtaTransactionManagerWithHeuristicMixedExceptionOnCommit() throws Exception {
		MockControl utControl = EasyMock.controlFor(UserTransaction.class);
		UserTransaction ut = (UserTransaction) utControl.getMock();
		ut.getStatus();
		utControl.setReturnValue(Status.STATUS_NO_TRANSACTION, 1);
		ut.begin();
		utControl.setVoidCallable(1);
		ut.commit();
		utControl.setThrowable(new HeuristicMixedException("heuristic exception"));
		utControl.activate();

		try {
			TransactionTemplate tt = getTransactionTemplateForJta(JtaTransactionManager.DEFAULT_USER_TRANSACTION_NAME, ut);
			tt.execute(new TransactionCallbackWithoutResult() {
				protected void doInTransactionWithoutResult(TransactionStatus status) {
					// something transactional
					TransactionSynchronizationManager.register(new TransactionSynchronization() {
						public void afterCompletion(int status) {
							assertTrue("Correct completion status", status == TransactionSynchronization.STATUS_UNKNOWN);
						}
					});
				}
			});
			fail("Should have thrown HeuristicCompletionException");
		}
		catch (HeuristicCompletionException ex) {
			// expected
			assertTrue(ex.getOutcomeState() == HeuristicCompletionException.STATE_MIXED);
		}

		utControl.verify();
	}

	public void testJtaTransactionManagerWithHeuristicRollbackExceptionOnCommit() throws Exception {
		MockControl utControl = EasyMock.controlFor(UserTransaction.class);
		UserTransaction ut = (UserTransaction) utControl.getMock();
		ut.getStatus();
		utControl.setReturnValue(Status.STATUS_NO_TRANSACTION, 1);
		ut.begin();
		utControl.setVoidCallable(1);
		ut.commit();
		utControl.setThrowable(new HeuristicRollbackException("heuristic exception"));
		utControl.activate();

		try {
			TransactionTemplate tt = getTransactionTemplateForJta(JtaTransactionManager.DEFAULT_USER_TRANSACTION_NAME, ut);
			tt.execute(new TransactionCallbackWithoutResult() {
				protected void doInTransactionWithoutResult(TransactionStatus status) {
					// something transactional
					TransactionSynchronizationManager.register(new TransactionSynchronization() {
						public void afterCompletion(int status) {
							assertTrue("Correct completion status", status == TransactionSynchronization.STATUS_UNKNOWN);
						}
					});
				}
			});
			fail("Should have thrown HeuristicCompletionException");
		}
		catch (HeuristicCompletionException ex) {
			// expected
			assertTrue(ex.getOutcomeState() == HeuristicCompletionException.STATE_ROLLED_BACK);
		}

		utControl.verify();
	}

	public void testJtaTransactionManagerWithSystemExceptionOnCommit() throws Exception {
		MockControl utControl = EasyMock.controlFor(UserTransaction.class);
		UserTransaction ut = (UserTransaction) utControl.getMock();
		ut.getStatus();
		utControl.setReturnValue(Status.STATUS_NO_TRANSACTION, 1);
		ut.begin();
		utControl.setVoidCallable(1);
		ut.commit();
		utControl.setThrowable(new SystemException("system exception"));
		utControl.activate();

		try {
			TransactionTemplate tt = getTransactionTemplateForJta(JtaTransactionManager.DEFAULT_USER_TRANSACTION_NAME, ut);
			tt.execute(new TransactionCallbackWithoutResult() {
				protected void doInTransactionWithoutResult(TransactionStatus status) {
					// something transactional
					TransactionSynchronizationManager.register(new TransactionSynchronization() {
						public void afterCompletion(int status) {
							assertTrue("Correct completion status", status == TransactionSynchronization.STATUS_UNKNOWN);
						}
					});
				}
			});
			fail("Should have thrown TransactionSystemException");
		}
		catch (TransactionSystemException ex) {
			// expected
		}

		utControl.verify();
	}

	public void testJtaTransactionManagerWithSystemExceptionOnRollback() throws Exception {
		MockControl utControl = EasyMock.controlFor(UserTransaction.class);
		UserTransaction ut = (UserTransaction) utControl.getMock();
		ut.getStatus();
		utControl.setReturnValue(Status.STATUS_NO_TRANSACTION, 1);
		ut.begin();
		utControl.setVoidCallable(1);
		ut.rollback();
		utControl.setThrowable(new SystemException("system exception"));
		utControl.activate();

		try {
			TransactionTemplate tt = getTransactionTemplateForJta(JtaTransactionManager.DEFAULT_USER_TRANSACTION_NAME, ut);
			tt.execute(new TransactionCallbackWithoutResult() {
				protected void doInTransactionWithoutResult(TransactionStatus status) {
					TransactionSynchronizationManager.register(new TransactionSynchronization() {
						public void afterCompletion(int status) {
							assertTrue("Correct completion status", status == TransactionSynchronization.STATUS_UNKNOWN);
						}
					});
					status.setRollbackOnly();
				}
			});
			fail("Should have thrown TransactionSystemException");
		}
		catch (TransactionSystemException ex) {
			// expected
		}

		utControl.verify();
	}

	public void testJtaTransactionManagerWithIllegalStateExceptionOnRollbackOnly() throws Exception {
		MockControl utControl = EasyMock.controlFor(UserTransaction.class);
		UserTransaction ut = (UserTransaction) utControl.getMock();
		ut.getStatus();
		utControl.setReturnValue(Status.STATUS_ACTIVE, 1);
		ut.setRollbackOnly();
		utControl.setThrowable(new IllegalStateException("no existing transaction"));
		utControl.activate();

		try {
			TransactionTemplate tt = getTransactionTemplateForJta(JtaTransactionManager.DEFAULT_USER_TRANSACTION_NAME, ut);
			tt.execute(new TransactionCallbackWithoutResult() {
				protected void doInTransactionWithoutResult(TransactionStatus status) {
					status.setRollbackOnly();
				}
			});
			fail("Should have thrown NoTransactionException");
		}
		catch (NoTransactionException ex) {
			// expected
		}

		utControl.verify();
	}

	public void testJtaTransactionManagerWithSystemExceptionOnRollbackOnly() throws Exception {
		MockControl utControl = EasyMock.controlFor(UserTransaction.class);
		UserTransaction ut = (UserTransaction) utControl.getMock();
		ut.getStatus();
		utControl.setReturnValue(Status.STATUS_ACTIVE, 1);
		ut.setRollbackOnly();
		utControl.setThrowable(new SystemException("system exception"));
		utControl.activate();

		try {
			TransactionTemplate tt = getTransactionTemplateForJta(JtaTransactionManager.DEFAULT_USER_TRANSACTION_NAME, ut);
			tt.execute(new TransactionCallbackWithoutResult() {
				protected void doInTransactionWithoutResult(TransactionStatus status) {
					status.setRollbackOnly();
					TransactionSynchronizationManager.register(new TransactionSynchronization() {
						public void afterCompletion(int status) {
							fail("Should not have been triggered");
						}
					});
				}
			});
			fail("Should have thrown TransactionSystemException");
		}
		catch (TransactionSystemException ex) {
			// expected
		}

		utControl.verify();
	}

}
