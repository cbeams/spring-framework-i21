
package com.interface21.jdbc.datasource;

import java.sql.Connection;

import javax.sql.DataSource;

/**
 * Interface to be implemented by classes that can provide a connection to
 * a relational database. Extends the javax.sql.DataSource interface
 * to allow classes using it to query whether or not the connection should
 * be closed after a given operation. This can sometimes be
 * useful for efficiency, if we know that we want to reuse
 * a connection.
 * @author  Rod Johnson
 * @version $Id$
 */
public interface SmartDataSource extends DataSource {
		
	/** 
	 * Should we close this connection, obtained from this factory?
	 * Code that uses connections from the factory should always
	 * use code like 
	 * <code>
	 * if (factory.shouldClose(conn)) 
	 * 	con.close()
	 * </code>
	 * in a finally block.
	 * However, the JdbcTemplate class in this package should
	 * take care of closing JDBC connections, freeing
	 * application code of this responsibility.
	 * @param conn connection, which should have been obtained
	 * from this data source, to check closure status of
	 */
	boolean shouldClose(Connection conn);

}
