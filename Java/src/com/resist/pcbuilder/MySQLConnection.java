package com.resist.pcbuilder;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.JSONArray;
import org.json.JSONObject;

public class MySQLConnection {
	private Connection conn;
	private String salt = null;

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
	public MySQLConnection(String address, int port, String dbName, String username, String password) throws SQLException {
		conn = DriverManager.getConnection("jdbc:mysql://"+address+":"+port+"/"+dbName,username,password);
	}

	/**
	 * Sets the salt to use for admin password hashing.
	 * 
	 * @param salt The salt to use
	 */
	public void setSalt(String salt) {
		this.salt = salt;
	}

	/**
	 * Closes the database connection.
	 */
	public void close() {
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns the data the PcBuilder needs on start-up.
	 * 
	 * @return The initial data for the PcBuilder
	 */
	public JSONObject getInit() {
		JSONObject out = new JSONObject();
		try {
			out.put("processors", initProcessors());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return out;
	}

	/**
	 * Gets a list of processor sockets by vendor from the database.
	 *  
	 * @return A list of processor sockets by vendor
	 * @throws SQLException
	 */
	private JSONObject initProcessors() throws SQLException {
		JSONObject out = new JSONObject();
		PreparedStatement s = conn.prepareStatement("SELECT naam, socket FROM merk JOIN onderdeel_socket ON ( merk.mid = onderdeel_socket.mid )"); 
		ResultSet res = s.executeQuery();
		while(res.next()) {
			String merk = res.getString(1);
			String socket = res.getString(2);
			if(!out.has(merk)) {
				out.put(merk, new JSONArray());
			}
			out.getJSONArray(merk).put(socket);
		}
		res.close();
		s.close();
		return out;
	}

	/**
	 * Calculates an admin password hash using the set salt.
	 * 
	 * @param password The password to hash
	 * @return A hexadecimal representation of the hashed password
	 * @throws NoSuchAlgorithmException
	 */
	private String getPasswordHash(String password) throws NoSuchAlgorithmException {
		if(salt != null) {
			password = salt+password;
		}
		MessageDigest md = MessageDigest.getInstance("SHA1");
		byte[] hash = md.digest(password.getBytes());
		return toHexString(hash);
	}

	/**
	 * Generates a hexadecimal string from a byte array.
	 * 
	 * @param hash The array to convert to hexadecimal
	 * @return The hexadecimal representation of the input
	 */
	private String toHexString(byte[] hash) {
		StringBuilder out = new StringBuilder();
		for(byte b : hash) {
			String h = Integer.toHexString(b & 0xFF);
			if(h.length() == 1) {
				out.append("0");
			}
			out.append(h);
		}
		return out.toString();
	}

	/**
	 * Checks if a username/password combination is a valid admin.
	 * 
	 * @param username The admin's username
	 * @param password The admin's password
	 * @return True if there is an admin with this username and password
	 */
	public boolean isValidLogin(String username,String password) {
		try {
			password = getPasswordHash(password);
			PreparedStatement s = conn.prepareStatement("SELECT 1 FROM admins WHERE username = ? AND password = ?");
			s.setString(1,username);
			s.setString(2,password);
			ResultSet res = s.executeQuery();
			boolean out = res.next();
			res.close();
			s.close();
			return out;
		} catch (SQLException | NoSuchAlgorithmException e) {
			return false;
		}
	}

	/**
	 * Returns a list of admin usernames by id.
	 * 
	 * @return A list of admins
	 */
	public JSONObject getAdminList() {
		JSONObject out = new JSONObject();
		try {
			PreparedStatement s = conn.prepareStatement("SELECT aid, username FROM admins");
			ResultSet res = s.executeQuery();
			while(res.next()) {
				out.put(res.getString(1),res.getString(2));
			}
			res.close();
			s.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return out;
	}

	/**
	 * Deletes an admin.
	 * 
	 * @param aid The id of the admin to delete
	 * @return True if the admin was deleted
	 */
	public boolean deleteAdmin(int aid) {
		try {
			PreparedStatement s = conn.prepareStatement("DELETE FROM admins WHERE aid = ?");
			s.setInt(1,aid);
			s.executeUpdate();
			s.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Adds an admin.
	 * 
	 * @param username The username for the new admin
	 * @param password The password for the new admin
	 * @return The id of the new admin, -1 on failure
	 */
	public int addAdmin(String username, String password) {
		int out = -1;
		try {
			PreparedStatement s = conn.prepareStatement("INSERT INTO admins (username,password) VALUES(?,?)",Statement.RETURN_GENERATED_KEYS);
			s.setString(1,username);
			s.setString(2,password);
			s.executeUpdate();
			ResultSet res = s.getGeneratedKeys();
			if(res.next()) {
				out = res.getInt(1);
			}
			res.close();
			s.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return out;
	}

	/**
	 * Modifies an admin account.
	 * 
	 * @param aid The id of the admin to modify
	 * @param username The username to assign to this admin
	 * @param password The password to assign to this admin
	 * @return True if the admin was modified
	 */
	public boolean modifyAdmin(int aid, String username, String password) {
		PreparedStatement s;
		try {
			if(password == null) {
				s = conn.prepareStatement("UPDATE admins SET username = ? WHERE aid = ?");
				s.setString(1,username);
				s.setInt(2,aid);
			} else if(username == null) {
				s = conn.prepareStatement("UPDATE admins SET password = ? WHERE aid = ?");
				s.setString(1,password);
				s.setInt(2,aid);
			} else {
				s = conn.prepareStatement("UPDATE admins SET username = ?, password = ? WHERE aid = ?");
				s.setString(1,username);
				s.setString(2,password);
				s.setInt(3,aid);
			}
			s.executeUpdate();
			s.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
}