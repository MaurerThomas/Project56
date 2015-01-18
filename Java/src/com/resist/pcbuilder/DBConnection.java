package com.resist.pcbuilder;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;

public class DBConnection {
	private Connection conn;
	private String[] args;

	/**
	 * Creates a new database connections.
	 * 
	 * @param address The host to connect to
	 * @param port The port to connect on
	 * @param dbName The name of the database to connect to
	 * @param username The username of the user to connect with
	 * @param password The password of the user to connect with
	 * @throws SQLException
	 */
	public DBConnection(String address, int port, String dbName, String username, String password) throws SQLException {
		args = new String[] {"jdbc:mysql://"+address+":"+port+"/"+dbName,username,password};
		conn = DriverManager.getConnection(args[0],args[1],args[2]);
	}

	/**
	 * Returns the connection to the database.
	 * 
	 * @return The connection to the database
	 */
	public Connection getConnection() {
		restoreConnection();
		return conn;
	}

	/**
	 * Attempts to restore connection to the database.
	 */
	private void restoreConnection() {
		try {
			if(conn.isClosed() || !conn.isValid(3)) {
				PcBuilder.LOG.log(Level.INFO,"Trying to restore MySQL connection.");
				close();
				conn = DriverManager.getConnection(args[0],args[1],args[2]);
			}
		} catch (SQLException e) {
			PcBuilder.LOG.log(Level.SEVERE,"Failed to restore MySQL connection.",e);
		}
	}

	/**
	 * Closes the database connection.
	 */
	public void close() {
		try {
			conn.close();
		} catch (SQLException e) {
			PcBuilder.LOG.log(Level.WARNING, "Failed to close connection.", e);
		}
	}

	/**
	 * Creates the IN(?) part of a prepared statement.
	 * 
	 * @param size The number of variables in the IN
	 * @return IN(?, ?, ... ?)
	 */
    public static String getInQuery(int size) {
        StringBuilder sb = new StringBuilder();
        sb.append(" IN (");
        for(int i=0;i < size;i++) {
            if(i != 0) {
                sb.append(", ");
            }
            sb.append('?');
        }
        sb.append(") ");
        return sb.toString();
    }

    /**
     * Returns a past date.
     * 
     * @param ms The number of milliseconds to subtract from the current time
     * @return A past date
     */
	public static Date getPastSQLDate(long ms) {
		java.util.Date utilDate = new java.util.Date();
		return new Date(utilDate.getTime() - ms);
	}
}
