package com.interface21.jdbc.datasource;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import junit.framework.TestCase;
import org.easymock.EasyMock;
import org.easymock.MockControl;

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

	public void testDataSourceTransactionManagerWithCommit() throws Exception {
		MockControl conControl = EasyMock.controlFor(Connection.class);
		Connection con = (Connection) conControl.getMock();
		con.setAutoCommit(false);
		conControl.setVoidCallable(1);
		con.commit();
		conControl.setVoidCallable(1);
		con.isReadOnly();
		conControl.setReturnValue(false, 1);
		con.setAutoCommit(true);
		conControl.setVoidCallable(1);
		con.close();
		conControl.setVoidCallable(1);

		MockControl dsControl = EasyMock.controlFor(DataSource.class);
		final DataSource ds = (DataSource) dsControl.getMock();
		ds.getConnection();
		dsControl.setReturnValue(con, 1);
		conControl.activate();
		dsControl.activate();

		PlatformTransactionManager tm = new DataSourceTransactionManager(ds);
		TransactionTemplate tt = new TransactionTemplate(tm);
		assertTrue("Hasn't thread connection", !DataSourceUtils.getThreadObjectManager().hasThreadObject(ds));

		tt.execute(new TransactionCallbackWithoutResult() {
			protected void doInTransactionWithoutResult(TransactionStatus status) throws RuntimeException {
				assertTrue("Has thread connection", DataSourceUtils.getThreadObjectManager().hasThreadObject(ds));
				assertTrue("Is new transaction", status.isNewTransaction());
			}
		});

		assertTrue("Hasn't thread connection", !DataSourceUtils.getThreadObjectManager().hasThreadObject(ds));
		conControl.verify();
		dsControl.verify();
	}

	public void testDataSourceTransactionManagerWithRollback() throws Exception {
		MockControl conControl = EasyMock.controlFor(Connection.class);
		Connection con = (Connection) conControl.getMock();
		con.setAutoCommit(false);
		conControl.setVoidCallable(1);
		con.rollback();
		conControl.setVoidCallable(1);
		con.isReadOnly();
		conControl.setReturnValue(false, 1);
		con.setAutoCommit(true);
		conControl.setVoidCallable(1);
		con.close();
		conControl.setVoidCallable(1);

		MockControl dsControl = EasyMock.controlFor(DataSource.class);
		final DataSource ds = (DataSource) dsControl.getMock();
		ds.getConnection();
		dsControl.setReturnValue(con, 1);
		conControl.activate();
		dsControl.activate();

		PlatformTransactionManager tm = new DataSourceTransactionManager(ds);
		TransactionTemplate tt = new TransactionTemplate(tm);
		assertTrue("Hasn't thread connection", !DataSourceUtils.getThreadObjectManager().hasThreadObject(ds));

		final RuntimeException ex = new RuntimeException("Application exception");
		try {
			tt.execute(new TransactionCallbackWithoutResult() {
				protected void doInTransactionWithoutResult(TransactionStatus status) throws RuntimeException {
					assertTrue("Has thread connection", DataSourceUtils.getThreadObjectManager().hasThreadObject(ds));
					assertTrue("Is new transaction", status.isNewTransaction());
					throw ex;
				}
			});
			fail("Should have thrown RuntimeException");
		}
		catch (RuntimeException ex2) {
			// expected
			assertTrue("Hasn't thread connection", !DataSourceUtils.getThreadObjectManager().hasThreadObject(ds));
			assertTrue("Correct exception thrown", ex2.equals(ex));
		}
		conControl.verify();
		dsControl.verify();
	}

	public void testDataSourceTransactionManagerWithRollbackOnly() throws Exception {
		MockControl conControl = EasyMock.controlFor(Connection.class);
		Connection con = (Connection) conControl.getMock();
		MockControl dsControl = EasyMock.controlFor(DataSource.class);
		final DataSource ds = (DataSource) dsControl.getMock();
		ds.getConnection();
		dsControl.setReturnValue(con, 1);
		conControl.activate();
		dsControl.activate();

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
					throw ex;
				}
			});
			fail("Should have thrown RuntimeException");
		}
		catch (RuntimeException ex2) {
			// expected
			assertTrue("Hasn't thread connection", !DataSourceUtils.getThreadObjectManager().hasThreadObject(ds));
			assertTrue("Correct exception thrown", ex2.equals(ex));
		}
		conControl.verify();
		dsControl.verify();
	}

	public void testDataSourceTransactionManagerWithExistingTransaction() throws Exception {
		MockControl conControl = EasyMock.controlFor(Connection.class);
		Connection con = (Connection) conControl.getMock();
		con.setAutoCommit(false);
		conControl.setVoidCallable(1);
		con.rollback();
		conControl.setVoidCallable(1);
		con.isReadOnly();
		conControl.setReturnValue(false, 1);
		con.setAutoCommit(true);
		conControl.setVoidCallable(1);
		con.close();
		conControl.setVoidCallable(1);

		MockControl dsControl = EasyMock.controlFor(DataSource.class);
		final DataSource ds = (DataSource) dsControl.getMock();
		ds.getConnection();
		dsControl.setReturnValue(con, 1);
		conControl.activate();
		dsControl.activate();

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
						status.setRollbackOnly();
					}
				});
				assertTrue("Is new transaction", status.isNewTransaction());
			}
		});
		conControl.verify();
		dsControl.verify();
	}

	public void testDataSourceTransactionManagerWithTimeout() throws Exception {
		MockControl conControl = EasyMock.controlFor(Connection.class);
		Connection con = (Connection) conControl.getMock();
		MockControl dsControl = EasyMock.controlFor(DataSource.class);
		DataSource ds = (DataSource) dsControl.getMock();
		ds.getConnection();
		dsControl.setReturnValue(con, 1);
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
		dsControl.setReturnValue(con, 1);
		con.getTransactionIsolation();
		conControl.setReturnValue(Connection.TRANSACTION_READ_COMMITTED, 1);
		con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
		conControl.setVoidCallable(1);
		con.setAutoCommit(false);
		conControl.setVoidCallable(1);
		con.commit();
		conControl.setVoidCallable(1);
		con.setAutoCommit(true);
		conControl.setVoidCallable(1);
		con.isReadOnly();
		conControl.setReturnValue(false, 1);
		con.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
		conControl.setVoidCallable(1);
		con.close();
		conControl.setVoidCallable(1);
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
		dsControl.setReturnValue(con, 1);
		con.setAutoCommit(false);
		conControl.setThrowable(new SQLException("Cannot begin"));
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
		dsControl.setReturnValue(con, 1);
		con.setAutoCommit(false);
		conControl.setVoidCallable(1);
		con.commit();
		conControl.setThrowable(new SQLException("Cannot commit"));
		con.setAutoCommit(true);
		conControl.setVoidCallable(1);
		con.isReadOnly();
		conControl.setReturnValue(false, 1);
		con.close();
		conControl.setVoidCallable(1);
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
		dsControl.setReturnValue(con, 1);
		con.setAutoCommit(false);
		conControl.setVoidCallable(1);
		con.rollback();
		conControl.setThrowable(new SQLException("Cannot rollback"));
		con.setAutoCommit(true);
		conControl.setVoidCallable(1);
		con.isReadOnly();
		conControl.setReturnValue(false, 1);
		con.close();
		conControl.setVoidCallable(1);
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
