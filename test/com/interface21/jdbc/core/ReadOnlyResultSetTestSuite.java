package com.interface21.jdbc.core;

import java.io.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

import junit.framework.TestCase;

import com.mockobjects.sql.*;
import com.mockobjects.util.NotImplementedException;

/**
 * Tests that the authorised methods work as expected and unauthorised methods
 * throw an exception.
 * 
 * @author Yann Caroff
 */
public class ReadOnlyResultSetTestSuite extends TestCase {
  
  private class MyMockResultSetMetadata extends CommonMockResultSetMetaData {};
  private class MyMockStatement extends MockStatement {};
  
  private MockSingleRowResultSet rs;
  private MockSingleRowResultSet rs2;
  private ReadOnlyResultSet rors;
  private ReadOnlyResultSet rors2;
  private MyMockResultSetMetadata metadata = new MyMockResultSetMetadata();
  private MyMockStatement stmt = new MyMockStatement();
  
  private HashMap map = new HashMap();
  private Calendar cal = new GregorianCalendar();
  
  private static int i = 0;
  private static final int STRING = ++i;
  private static final String STRING_VALUE = "Spring";
  private static final int BOOLEAN = ++i;
  private static final Boolean BOOLEAN_VALUE = new Boolean(true);
  private static final int BYTE = ++i;
  private static final Byte BYTE_VALUE = new Byte("" + i);
  private static final int SHORT = ++i;
  private static final Short SHORT_VALUE = new Short("" + i);
  private static final int INT = ++i;
  private static final Integer INT_VALUE = new Integer("" + i);
  private static final int LONG = ++i;
  private static final Long LONG_VALUE = new Long("" + i);
  private static final int FLOAT = ++i;
  private static final Float FLOAT_VALUE = new Float("" + i);
  private static final int DOUBLE = ++i;
  private static final Double DOUBLE_VALUE = new Double("" + i);
  private static final int BIG_DECIMAL = ++i;
  private static final BigDecimal BIG_DECIMAL_VALUE = new BigDecimal("" + i);
  private static final int BYTES = ++i;
  private static final byte[] BYTES_VALUE = new String("" + i).getBytes();
  private static final int DATE = ++i;
  private static final java.sql.Date DATE_VALUE = new java.sql.Date(System.currentTimeMillis());
  private static final int TIME = ++i;
  private static final java.sql.Time TIME_VALUE = new java.sql.Time(System.currentTimeMillis());
  private static final int TIMESTAMP = ++i;
  private static final java.sql.Timestamp TIMESTAMP_VALUE = new java.sql.Timestamp(System.currentTimeMillis());
  private static final int INPUT_STREAM = ++i;
  private static final InputStream INPUT_STREAM_VALUE = new ByteArrayInputStream(new String("" + i).getBytes());
  private static final int OBJECT = ++i;
  private static final Object OBJECT_VALUE = new Object();
  private static final int READER = ++i;
  private static final Reader READER_VALUE = new StringReader("" + i);
  private static final int ARRAY = ++i;
  private static final MockArray ARRAY_VALUE = new MockArray();

  private static final String[] COLUMN_NAMES = new String[] {
    "" + STRING,
    "" + BOOLEAN,
    "" + BYTE,
    "" + SHORT,
    "" + INT,
    "" + LONG,
    "" + FLOAT,
    "" + DOUBLE,
    "" + BIG_DECIMAL,
    "" + BYTES,
    "" + DATE,
    "" + TIME,
    "" + TIMESTAMP,
    "" + INPUT_STREAM,
    "" + OBJECT,
    "" + READER,
    "" + ARRAY
  };
  
  private static final Object[] COLUMN_VALUES = new Object[] {
    STRING_VALUE,
    BOOLEAN_VALUE,
    BYTE_VALUE,
    SHORT_VALUE,
    INT_VALUE,
    LONG_VALUE,
    FLOAT_VALUE,
    DOUBLE_VALUE,
    BIG_DECIMAL_VALUE,
    BYTES_VALUE,
    DATE_VALUE,
    TIME_VALUE,
    TIMESTAMP_VALUE,
    INPUT_STREAM_VALUE,
    OBJECT_VALUE,
    READER_VALUE,
    ARRAY_VALUE
  };
  
  public ReadOnlyResultSetTestSuite(String name) {
    super(name);
  }
  
  public void setUp() {
      rs = new MockSingleRowResultSet();
      rs.setupMetaData(metadata);
      rs.setupStatement(stmt);
      rs.addExpectedIndexedValues(COLUMN_VALUES);
      rs2 = new MockSingleRowResultSet();
      rs2.addExpectedNamedValues(COLUMN_NAMES, COLUMN_VALUES);
      rors = new ReadOnlyResultSet(rs);
      rors2 = new ReadOnlyResultSet(rs2);
  }

  public void testNullResultSetProxy() {
    try {
      ReadOnlyResultSet rors = new ReadOnlyResultSet(null);
      fail("Constructor should have thrown an IllegalArgumentException");
    } catch (IllegalArgumentException ex) {
	    // expected
    } catch (Exception ex) {
      fail("Wrong exception type. Should have been IllegalArgumentException");
    }
  }
  
  public void testCorrectResultSetProxy() {
    try {
      ReadOnlyResultSet rors = new ReadOnlyResultSet(rs);
    } catch (IllegalArgumentException ex) {
      fail("Constructor should not have thrown an IllegalArgumentException");
    }
  }
  
  public void testNext() {
    try {
      rors.next();
      fail("Unauthorized method");
    } catch (InvalidResultSetMethodInvocationException ex) {
    } catch (NotImplementedException ex) {
    } catch (SQLException ex) {
      fail("SQLException");
    }
  }
  
  public void testClose() {
    try {
      rors.close();
      fail("Unauthorized method");
    } catch (InvalidResultSetMethodInvocationException ex) {
    } catch (NotImplementedException ex) {
    } catch (SQLException ex) {
      fail("SQLException");
    }
  }

  public void testWasNull() {
    try {
      rors.wasNull();
    } catch (InvalidResultSetMethodInvocationException ex) {
      fail("Authorized method");
    } catch (NotImplementedException ex) {
    } catch (SQLException ex) {
      fail("SQLException");
    }
  }

  public void testGetString() {
    try {
      assertTrue(rors.getString(STRING).equals(STRING_VALUE));
      assertTrue(rors2.getString("" + STRING).equals(STRING_VALUE));
    } catch (InvalidResultSetMethodInvocationException ex) {
      fail("Authorized method");
    } catch (NotImplementedException ex) {
    } catch (SQLException ex) {
      fail("SQLException");
    }
  }

  public void testGetBoolean() {
    try {
      assertTrue(rors.getBoolean(BOOLEAN) == BOOLEAN_VALUE.booleanValue());
      assertTrue(rors2.getBoolean("" + BOOLEAN) == BOOLEAN_VALUE.booleanValue());
    } catch (InvalidResultSetMethodInvocationException ex) {
      fail("Authorized method");
    } catch (NotImplementedException ex) {
    } catch (SQLException ex) {
      fail("SQLException");
    }
  }
  
  public void testGetByte() {
    try {
      assertTrue(rors.getByte(BYTE) == BYTE_VALUE.byteValue());
      assertTrue(rors2.getByte("" + BYTE) == BYTE_VALUE.byteValue());
    } catch (InvalidResultSetMethodInvocationException ex) {
      fail("Authorized method");
    } catch (NotImplementedException ex) {
    } catch (SQLException ex) {
      fail("SQLException");
    }
  }
  
  public void testGetShort() {
    try {
      assertTrue(rors.getShort(SHORT) == SHORT_VALUE.shortValue());
      assertTrue(rors2.getShort("" + SHORT) == SHORT_VALUE.shortValue());
    } catch (InvalidResultSetMethodInvocationException ex) {
      fail("Authorized method");
    } catch (NotImplementedException ex) {
    } catch (SQLException ex) {
      fail("SQLException");
    }
  }
  
  public void testGetInt() {
    try {
      assertTrue(rors.getInt(INT) == INT_VALUE.intValue());
      assertTrue(rors2.getInt("" + INT) == INT_VALUE.intValue());
    } catch (InvalidResultSetMethodInvocationException ex) {
      fail("Authorized method");
    } catch (NotImplementedException ex) {
    } catch (SQLException ex) {
      fail("SQLException");
    }
  }
  
  public void testGetLong() {
    try {
      assertTrue(rors.getLong(LONG) == LONG_VALUE.longValue());
      assertTrue(rors2.getLong("" + LONG) == LONG_VALUE.longValue());
    } catch (InvalidResultSetMethodInvocationException ex) {
      fail("Authorized method");
    } catch (NotImplementedException ex) {
    } catch (SQLException ex) {
      fail("SQLException");
    }
  }
  
  public void testGetFloat() {
    try {
      assertTrue(rors.getFloat(FLOAT) == FLOAT_VALUE.floatValue());
      assertTrue(rors2.getFloat("" + FLOAT) == FLOAT_VALUE.floatValue());
    } catch (InvalidResultSetMethodInvocationException ex) {
      fail("Authorized method");
    } catch (NotImplementedException ex) {
    } catch (SQLException ex) {
      fail("SQLException");
    }
  }
  
  public void testGetDouble() {
    try {
      assertTrue(rors.getDouble(DOUBLE) == DOUBLE_VALUE.doubleValue());
      assertTrue(rors2.getDouble("" + DOUBLE) == DOUBLE_VALUE.doubleValue());
    } catch (InvalidResultSetMethodInvocationException ex) {
      fail("Authorized method");
    } catch (NotImplementedException ex) {
    } catch (SQLException ex) {
      fail("SQLException");
    }
  }
  
  public void testGetBigDecimal() {
    try {
      assertTrue(rors.getBigDecimal(BIG_DECIMAL).equals(BIG_DECIMAL_VALUE));
      assertTrue(rors.getBigDecimal(BIG_DECIMAL, 1).equals(BIG_DECIMAL_VALUE));
      assertTrue(rors2.getBigDecimal("" + BIG_DECIMAL).equals(BIG_DECIMAL_VALUE));
      assertTrue(rors2.getBigDecimal("" + BIG_DECIMAL, 1).equals(BIG_DECIMAL_VALUE));
    } catch (InvalidResultSetMethodInvocationException ex) {
      fail("Authorized method");
    } catch (NotImplementedException ex) {
    } catch (SQLException ex) {
      fail("SQLException");
    }
  }

  public void testGetBytes() {
    try {
      assertTrue(rors.getBytes(BYTES).equals(BYTES_VALUE));
      assertTrue(rors2.getBytes("" + BYTES).equals(BYTES_VALUE));
    } catch (InvalidResultSetMethodInvocationException ex) {
      fail("Authorized method");
    } catch (NotImplementedException ex) {
    } catch (SQLException ex) {
      fail("SQLException");
    }
  }

  public void testGetDate() {
    try {
      assertTrue(rors.getDate(DATE).equals(DATE_VALUE));
      assertTrue(rors2.getDate("" + DATE).equals(DATE_VALUE));
      assertTrue(rors.getDate(DATE, cal).equals(DATE_VALUE));
      assertTrue(rors2.getDate("" + DATE, cal).equals(DATE_VALUE));
    } catch (InvalidResultSetMethodInvocationException ex) {
      fail("Authorized method");
    } catch (NotImplementedException ex) {
    } catch (SQLException ex) {
      fail("SQLException");
    }
  }

  public void testGetTime() {
    try {
      assertTrue(rors.getTime(TIME).equals(TIME_VALUE));
      assertTrue(rors2.getTime("" + TIME).equals(TIME_VALUE));
      assertTrue(rors.getTime(TIME, cal).equals(TIME_VALUE));
      assertTrue(rors2.getTime("" + TIME, cal).equals(TIME_VALUE));
    } catch (InvalidResultSetMethodInvocationException ex) {
      fail("Authorized method");
    } catch (NotImplementedException ex) {
    } catch (SQLException ex) {
      fail("SQLException");
    }
  }

  public void testGetTimestamp() {
    try {
      assertTrue(rors.getTimestamp(TIMESTAMP).equals(TIMESTAMP_VALUE));
      assertTrue(rors2.getTimestamp("" + TIMESTAMP).equals(TIMESTAMP_VALUE));
      assertTrue(rors.getTimestamp(TIMESTAMP, cal).equals(TIMESTAMP_VALUE));
      assertTrue(rors2.getTimestamp("" + TIMESTAMP, cal).equals(TIMESTAMP_VALUE));
    } catch (InvalidResultSetMethodInvocationException ex) {
      fail("Authorized method");
    } catch (NotImplementedException ex) {
    } catch (SQLException ex) {
      fail("SQLException");
    }
  }

  public void testGetAsciiStream() {
    try {
      assertTrue(rors.getBinaryStream(INPUT_STREAM).equals(INPUT_STREAM_VALUE));
      assertTrue(rors2.getBinaryStream("" + INPUT_STREAM).equals(INPUT_STREAM_VALUE));
    } catch (InvalidResultSetMethodInvocationException ex) {
      fail("Authorized method");
    } catch (NotImplementedException ex) {
    } catch (SQLException ex) {
      fail("SQLException");
    }
  }
  
  public void testGetWarnings() {
    try {
      rors.getWarnings();
      fail("Unauthorized method");
    } catch (InvalidResultSetMethodInvocationException ex) {
    } catch (NotImplementedException ex) {
    } catch (SQLException ex) {
      fail("SQLException");
    }
  }
  
  public void testClearWarnings() {
    try {
      rors.clearWarnings();
      fail("Unauthorized method");
    } catch (InvalidResultSetMethodInvocationException ex) {
    } catch (NotImplementedException ex) {
    } catch (SQLException ex) {
      fail("SQLException");
    }
  }
  
  public void testGetCursorName() {
    try {
      rors.getCursorName();
      fail("Unauthorized method");
    } catch (InvalidResultSetMethodInvocationException ex) {
    } catch (NotImplementedException ex) {
    } catch (SQLException ex) {
      fail("SQLException");
    }
  }

  public void testGetMetaData() {
    try {
      assertTrue(rors.getMetaData().equals(metadata));
    } catch (InvalidResultSetMethodInvocationException ex) {
      fail("Authorized method");
    } catch (NotImplementedException ex) {
    } catch (SQLException ex) {
      fail("SQLException");
    }
  }

  public void testGetObject() {
    try {
      assertTrue(rors.getObject(OBJECT).equals(OBJECT_VALUE));
      assertTrue(rors2.getObject("" + OBJECT).equals(OBJECT_VALUE));
      assertTrue(rors.getObject(OBJECT, map).equals(OBJECT_VALUE));
      assertTrue(rors2.getObject("" + OBJECT, map).equals(OBJECT_VALUE));
    } catch (InvalidResultSetMethodInvocationException ex) {
      fail("Authorized method");
    } catch (NotImplementedException ex) {
    } catch (SQLException ex) {
      fail("SQLException");
    }
  }

  public void testFindColumn() {
    try {
      assertTrue(rors.findColumn("" + OBJECT) == OBJECT);
    } catch (InvalidResultSetMethodInvocationException ex) {
      fail("Authorized method");
    } catch (NotImplementedException ex) {
    } catch (SQLException ex) {
      fail("SQLException");
    }
  }

  public void testGetCharacterStream() {
    try {
      assertTrue(rors.getCharacterStream(READER).equals(READER_VALUE));
      assertTrue(rors2.getCharacterStream("" + READER).equals(READER_VALUE));
    } catch (InvalidResultSetMethodInvocationException ex) {
      fail("Authorized method");
    } catch (NotImplementedException ex) {
    } catch (SQLException ex) {
      fail("SQLException");
    }
  }

  public void testCursorPositions() {
    try {
      rors.isBeforeFirst();
    } catch (InvalidResultSetMethodInvocationException ex) {
      fail("Authorized method");
    } catch (NotImplementedException ex) {
    } catch (SQLException ex) {
      fail("SQLException");
    }

    try {
      rors.isAfterLast();
    } catch (InvalidResultSetMethodInvocationException ex) {
      fail("Authorized method");
    } catch (NotImplementedException ex) {
    } catch (SQLException ex) {
      fail("SQLException");
    }

    try {
      rors.isFirst();
    } catch (InvalidResultSetMethodInvocationException ex) {
      fail("Authorized method");
    } catch (NotImplementedException ex) {
    } catch (SQLException ex) {
      fail("SQLException");
    }

    try {
      rors.isLast();
    } catch (InvalidResultSetMethodInvocationException ex) {
      fail("Authorized method");
    } catch (NotImplementedException ex) {
    } catch (SQLException ex) {
      fail("SQLException");
    }
  }
  
  public void testMoveCursor() {
    try {
      rors.beforeFirst();
      fail("Unauthorized method");
    } catch (InvalidResultSetMethodInvocationException ex) {
    } catch (NotImplementedException ex) {
    } catch (SQLException ex) {
      fail("SQLException");
    }

    try {
      rors.afterLast();
      fail("Unauthorized method");
    } catch (InvalidResultSetMethodInvocationException ex) {
    } catch (NotImplementedException ex) {
    } catch (SQLException ex) {
      fail("SQLException");
    }

    try {
      rors.first();
      fail("Unauthorized method");
    } catch (InvalidResultSetMethodInvocationException ex) {
    } catch (NotImplementedException ex) {
    } catch (SQLException ex) {
      fail("SQLException");
    }

    try {
      rors.last();
      fail("Unauthorized method");
    } catch (InvalidResultSetMethodInvocationException ex) {
    } catch (NotImplementedException ex) {
    } catch (SQLException ex) {
      fail("SQLException");
    }

    try {
      rors.absolute(1);
      fail("Unauthorized method");
    } catch (InvalidResultSetMethodInvocationException ex) {
    } catch (NotImplementedException ex) {
    } catch (SQLException ex) {
      fail("SQLException");
    }

    try {
      rors.relative(1);
      fail("Unauthorized method");
    } catch (InvalidResultSetMethodInvocationException ex) {
    } catch (NotImplementedException ex) {
    } catch (SQLException ex) {
      fail("SQLException");
    }

    try {
      rors.previous();
      fail("Unauthorized method");
    } catch (InvalidResultSetMethodInvocationException ex) {
    } catch (NotImplementedException ex) {
    } catch (SQLException ex) {
      fail("SQLException");
    }
  }

  public void testGetRow() {
    try {
      assertTrue(rors.getRow() == 0);
    } catch (InvalidResultSetMethodInvocationException ex) {
      fail("Authorized method");
    } catch (NotImplementedException ex) {
    } catch (SQLException ex) {
      fail("SQLException");
    }
  }
  
  public void testSetters() {
    try {
      rors.setFetchDirection(1);
      fail("Unauthorized method");
    } catch (InvalidResultSetMethodInvocationException ex) {
    } catch (NotImplementedException ex) {
    } catch (SQLException ex) {
      fail("SQLException");
    }

    try {
      rors.setFetchSize(1);
      fail("Unauthorized method");
    } catch (InvalidResultSetMethodInvocationException ex) {
    } catch (NotImplementedException ex) {
    } catch (SQLException ex) {
      fail("SQLException");
    }
  }

  public void testGetters() {
    try {
      rors.getFetchDirection();
    } catch (InvalidResultSetMethodInvocationException ex) {
      fail("Authorized method");
    } catch (NotImplementedException ex) {
    } catch (SQLException ex) {
      fail("SQLException");
    }

    try {
      rors.getFetchSize();
    } catch (InvalidResultSetMethodInvocationException ex) {
      fail("Authorized method");
    } catch (NotImplementedException ex) {
    } catch (SQLException ex) {
      fail("SQLException");
    }

    try {
      rors.getType();
    } catch (InvalidResultSetMethodInvocationException ex) {
      fail("Authorized method");
    } catch (NotImplementedException ex) {
    } catch (SQLException ex) {
      fail("SQLException");
    }

    try {
      rors.getConcurrency();
    } catch (InvalidResultSetMethodInvocationException ex) {
      fail("Authorized method");
    } catch (NotImplementedException ex) {
    } catch (SQLException ex) {
      fail("SQLException");
    }

    try {
      rors.rowUpdated();
    } catch (InvalidResultSetMethodInvocationException ex) {
      fail("Authorized method");
    } catch (NotImplementedException ex) {
    } catch (SQLException ex) {
      fail("SQLException");
    }

    try {
      rors.rowInserted();
    } catch (InvalidResultSetMethodInvocationException ex) {
      fail("Authorized method");
    } catch (NotImplementedException ex) {
    } catch (SQLException ex) {
      fail("SQLException");
    }

    try {
      rors.rowDeleted();
    } catch (InvalidResultSetMethodInvocationException ex) {
      fail("Authorized method");
    } catch (NotImplementedException ex) {
    } catch (SQLException ex) {
      fail("SQLException");
    }
  }
  
  public void testUpdateNull() {
    try {
      rors.updateNull(STRING);
      rors2.updateNull("" + STRING);
      fail("Unauthorized method");
    } catch (InvalidResultSetMethodInvocationException ex) {
    } catch (NotImplementedException ex) {
    } catch (SQLException ex) {
      fail("SQLException");
    }
  }
  
  public void testUpdateBoolean() {
    try {
      rors.updateBoolean(BOOLEAN, BOOLEAN_VALUE.booleanValue());
      rors2.updateBoolean("" + BOOLEAN, BOOLEAN_VALUE.booleanValue());
      fail("Unauthorized method");
    } catch (InvalidResultSetMethodInvocationException ex) {
    } catch (NotImplementedException ex) {
    } catch (SQLException ex) {
      fail("SQLException");
    }
  }
  
  public void testUpdateByte() {
    try {
      rors.updateByte(BYTE, BYTE_VALUE.byteValue());
      rors2.updateByte("" + BYTE, BYTE_VALUE.byteValue());
      fail("Unauthorized method");
    } catch (InvalidResultSetMethodInvocationException ex) {
    } catch (NotImplementedException ex) {
    } catch (SQLException ex) {
      fail("SQLException");
    }
  }
  
  public void testUpdateShort() {
    try {
      rors.updateShort(SHORT, SHORT_VALUE.shortValue());
      rors2.updateShort("" + SHORT, SHORT_VALUE.shortValue());
      fail("Unauthorized method");
    } catch (InvalidResultSetMethodInvocationException ex) {
    } catch (NotImplementedException ex) {
    } catch (SQLException ex) {
      fail("SQLException");
    }
  }
  
  public void testUpdateInt() {
    try {
      rors.updateInt(INT, INT_VALUE.intValue());
      rors2.updateInt("" + INT, INT_VALUE.intValue());
      fail("Unauthorized method");
    } catch (InvalidResultSetMethodInvocationException ex) {
    } catch (NotImplementedException ex) {
    } catch (SQLException ex) {
      fail("SQLException");
    }
  }
  
  public void testUpdateLong() {
    try {
      rors.updateLong(LONG, LONG_VALUE.longValue());
      rors2.updateLong("" + LONG, LONG_VALUE.longValue());
      fail("Unauthorized method");
    } catch (InvalidResultSetMethodInvocationException ex) {
    } catch (NotImplementedException ex) {
    } catch (SQLException ex) {
      fail("SQLException");
    }
  }
  
  public void testUpdateFloat() {
    try {
      rors.updateFloat(FLOAT, FLOAT_VALUE.floatValue());
      rors2.updateFloat("" + FLOAT, FLOAT_VALUE.floatValue());
      fail("Unauthorized method");
    } catch (InvalidResultSetMethodInvocationException ex) {
    } catch (NotImplementedException ex) {
    } catch (SQLException ex) {
      fail("SQLException");
    }
  }
  
  public void testUpdateDouble() {
    try {
      rors.updateDouble(DOUBLE, DOUBLE_VALUE.doubleValue());
      rors2.updateDouble("" + DOUBLE, DOUBLE_VALUE.doubleValue());
      fail("Unauthorized method");
    } catch (InvalidResultSetMethodInvocationException ex) {
    } catch (NotImplementedException ex) {
    } catch (SQLException ex) {
      fail("SQLException");
    }
  }
  
  public void testUpdateBigDecimal() {
    try {
      rors.updateBigDecimal(BIG_DECIMAL, BIG_DECIMAL_VALUE);
      rors2.updateBigDecimal("" + BIG_DECIMAL, BIG_DECIMAL_VALUE);
      fail("Unauthorized method");
    } catch (InvalidResultSetMethodInvocationException ex) {
    } catch (NotImplementedException ex) {
    } catch (SQLException ex) {
      fail("SQLException");
    }
  }
  
  public void testUpdateString() {
    try {
      rors.updateString(STRING, STRING_VALUE);
      rors2.updateString("" + STRING, STRING_VALUE);
      fail("Unauthorized method");
    } catch (InvalidResultSetMethodInvocationException ex) {
    } catch (NotImplementedException ex) {
    } catch (SQLException ex) {
      fail("SQLException");
    }
  }
  
  public void testUpdateBytes() {
    try {
      rors.updateBytes(BYTES, BYTES_VALUE);
      rors2.updateBytes("" + BYTES, BYTES_VALUE);
      fail("Unauthorized method");
    } catch (InvalidResultSetMethodInvocationException ex) {
    } catch (NotImplementedException ex) {
    } catch (SQLException ex) {
      fail("SQLException");
    }
  }
  
  public void testUpdateDate() {
    try {
      rors.updateDate(DATE, DATE_VALUE);
      rors2.updateDate("" + DATE, DATE_VALUE);
      fail("Unauthorized method");
    } catch (InvalidResultSetMethodInvocationException ex) {
    } catch (NotImplementedException ex) {
    } catch (SQLException ex) {
      fail("SQLException");
    }
  }
  
  public void testUpdateTime() {
    try {
      rors.updateTime(TIME, TIME_VALUE);
      rors2.updateTime("" + TIME, TIME_VALUE);
      fail("Unauthorized method");
    } catch (InvalidResultSetMethodInvocationException ex) {
    } catch (NotImplementedException ex) {
    } catch (SQLException ex) {
      fail("SQLException");
    }
  }
  
  public void testUpdateTimestamp() {
    try {
      rors.updateTimestamp(TIMESTAMP, TIMESTAMP_VALUE);
      rors2.updateTimestamp("" + TIMESTAMP, TIMESTAMP_VALUE);
      fail("Unauthorized method");
    } catch (InvalidResultSetMethodInvocationException ex) {
    } catch (NotImplementedException ex) {
    } catch (SQLException ex) {
      fail("SQLException");
    }
  }
  
  public void testUpdateAsciiStream() {
    try {
      rors.updateAsciiStream(INPUT_STREAM, INPUT_STREAM_VALUE, 1);
      rors2.updateAsciiStream("" + INPUT_STREAM, INPUT_STREAM_VALUE, 1);
      fail("Unauthorized method");
    } catch (InvalidResultSetMethodInvocationException ex) {
    } catch (NotImplementedException ex) {
    } catch (SQLException ex) {
      fail("SQLException");
    }
  }
  
  public void testUpdateBinaryStream() {
    try {
      rors.updateBinaryStream(INPUT_STREAM, INPUT_STREAM_VALUE, 1);
      rors2.updateBinaryStream("" + INPUT_STREAM, INPUT_STREAM_VALUE, 1);
      fail("Unauthorized method");
    } catch (InvalidResultSetMethodInvocationException ex) {
    } catch (NotImplementedException ex) {
    } catch (SQLException ex) {
      fail("SQLException");
    }
  }
  
  public void testUpdateCharacterStream() {
    try {
      rors.updateCharacterStream(READER, READER_VALUE, 1);
      rors2.updateCharacterStream("" + READER, READER_VALUE, 1);
      fail("Unauthorized method");
    } catch (InvalidResultSetMethodInvocationException ex) {
    } catch (NotImplementedException ex) {
    } catch (SQLException ex) {
      fail("SQLException");
    }
  }
  
  public void testUpdateObject() {
    try {
      rors.updateObject(OBJECT, OBJECT_VALUE);
      rors.updateObject(OBJECT, OBJECT_VALUE, 1);
      rors2.updateObject("" + OBJECT, OBJECT_VALUE);
      rors2.updateObject("" + OBJECT, OBJECT_VALUE, 1);
      fail("Unauthorized method");
    } catch (InvalidResultSetMethodInvocationException ex) {
    } catch (NotImplementedException ex) {
    } catch (SQLException ex) {
      fail("SQLException");
    }
  }

  public void testRowModifiers() {
    try {
      rors.insertRow();
      fail("Unauthorised method");
    } catch (InvalidResultSetMethodInvocationException ex) {
    } catch (NotImplementedException ex) {
    } catch (SQLException ex) {
      fail("SQLException");
    }

    try {
      rors.updateRow();
      fail("Unauthorised method");
    } catch (InvalidResultSetMethodInvocationException ex) {
    } catch (NotImplementedException ex) {
    } catch (SQLException ex) {
      fail("SQLException");
    }

    try {
      rors.deleteRow();
      fail("Unauthorised method");
    } catch (InvalidResultSetMethodInvocationException ex) {
    } catch (NotImplementedException ex) {
    } catch (SQLException ex) {
      fail("SQLException");
    }

    try {
      rors.refreshRow();
      fail("Unauthorised method");
    } catch (InvalidResultSetMethodInvocationException ex) {
    } catch (NotImplementedException ex) {
    } catch (SQLException ex) {
      fail("SQLException");
    }

    try {
      rors.cancelRowUpdates();
      fail("Unauthorised method");
    } catch (InvalidResultSetMethodInvocationException ex) {
    } catch (NotImplementedException ex) {
    } catch (SQLException ex) {
      fail("SQLException");
    }

    try {
      rors.moveToInsertRow();
      fail("Unauthorised method");
    } catch (InvalidResultSetMethodInvocationException ex) {
    } catch (NotImplementedException ex) {
    } catch (SQLException ex) {
      fail("SQLException");
    }

    try {
      rors.moveToCurrentRow();
      fail("Unauthorised method");
    } catch (InvalidResultSetMethodInvocationException ex) {
    } catch (NotImplementedException ex) {
    } catch (SQLException ex) {
      fail("SQLException");
    }
  }

  public void testGetStatement() {
    try {
      assertTrue(rors.getStatement().equals(stmt));
    } catch (InvalidResultSetMethodInvocationException ex) {
      fail("Authorized method");
    } catch (NotImplementedException ex) {
    } catch (SQLException ex) {
      fail("SQLException");
    }
  }

  public void testGetRef() {
    try {
      rors.getRef(1);
    } catch (InvalidResultSetMethodInvocationException ex) {
      fail("Authorized method");
    } catch (NotImplementedException ex) {
    } catch (SQLException ex) {
      fail("SQLException");
    } catch (ClassCastException ex) {
    }

    try {
      rors2.getRef("1");
    } catch (InvalidResultSetMethodInvocationException ex) {
      fail("Authorized method");
    } catch (NotImplementedException ex) {
    } catch (SQLException ex) {
      fail("SQLException");
    } catch (ClassCastException ex) {
    }
  }

  public void testGetBlob() {
    try {
      rors.getBlob(1);
    } catch (InvalidResultSetMethodInvocationException ex) {
      fail("Authorized method");
    } catch (NotImplementedException ex) {
    } catch (SQLException ex) {
      fail("SQLException");
    } catch (ClassCastException ex) {
    }

    try {
      rors2.getBlob("1");
    } catch (InvalidResultSetMethodInvocationException ex) {
      fail("Authorized method");
    } catch (NotImplementedException ex) {
    } catch (SQLException ex) {
      fail("SQLException");
    } catch (ClassCastException ex) {
    }
  }

  public void testGetClob() {
    try {
      rors.getClob(1);
    } catch (InvalidResultSetMethodInvocationException ex) {
      fail("Authorized method");
    } catch (NotImplementedException ex) {
    } catch (SQLException ex) {
      fail("SQLException");
    } catch (ClassCastException ex) {
    }

    try {
      rors2.getClob("1");
    } catch (InvalidResultSetMethodInvocationException ex) {
      fail("Authorized method");
    } catch (NotImplementedException ex) {
    } catch (SQLException ex) {
      fail("SQLException");
    } catch (ClassCastException ex) {
    }
  }
  
  public void testGetArray() {
    try {
      assertTrue(rors.getArray(ARRAY).equals(ARRAY_VALUE));
      assertTrue(rors2.getArray("" + ARRAY).equals(ARRAY_VALUE));
    } catch (InvalidResultSetMethodInvocationException ex) {
      fail("Authorized method");
    } catch (NotImplementedException ex) {
    } catch (SQLException ex) {
      fail("SQLException");
    }
  }
}