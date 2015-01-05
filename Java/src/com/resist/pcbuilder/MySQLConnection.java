package com.resist.pcbuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.List;
import java.util.logging.Level;

public class MySQLConnection {
	private Connection conn;
	private String salt = null;
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
	public MySQLConnection(String address, int port, String dbName, String username, String password) throws SQLException {
		args = new String[] {"jdbc:mysql://"+address+":"+port+"/"+dbName,username,password};
		conn = DriverManager.getConnection(args[0],args[1],args[2]);
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
			PcBuilder.LOG.log(Level.WARNING, "Failed to close connection.", e);
		}
	}

	/**
	 * Returns the data the PcBuilder needs on start-up.
	 * 
	 * @return The initial data for the PcBuilder
	 */
	public JSONObject getInit() {
		restoreConnection();
		JSONObject out = new JSONObject();
		try {
			out.put("processors", initProcessors());
		} catch (SQLException e) {
			PcBuilder.LOG.log(Level.WARNING, "Failed to get init.", e);
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
		PreparedStatement s = conn.prepareStatement("SELECT merk.naam, socket.type FROM merk, socket WHERE merk.mid = socket.merkmid");
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
		restoreConnection();
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
		restoreConnection();
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
		restoreConnection();
		try {
			PreparedStatement s = conn.prepareStatement("DELETE FROM admins WHERE aid = ?");
			s.setInt(1,aid);
			s.executeUpdate();
			s.close();
			return true;
		} catch (SQLException e) {
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
		restoreConnection();
		int out = -1;
		try {
			password = getPasswordHash(password);
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
		} catch (SQLException | NoSuchAlgorithmException e) {
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
		restoreConnection();
		PreparedStatement s;
		try {
			if(password == null) {
				s = conn.prepareStatement("UPDATE admins SET username = ? WHERE aid = ?");
				s.setString(1,username);
				s.setInt(2,aid);
			} else if(username == null) {
				password = getPasswordHash(password);
				s = conn.prepareStatement("UPDATE admins SET password = ? WHERE aid = ?");
				s.setString(1,password);
				s.setInt(2,aid);
			} else {
				password = getPasswordHash(password);
				s = conn.prepareStatement("UPDATE admins SET username = ?, password = ? WHERE aid = ?");
				s.setString(1,username);
				s.setString(2,password);
				s.setInt(3,aid);
			}
			s.executeUpdate();
			s.close();
			return true;
		} catch (SQLException | NoSuchAlgorithmException e) {
		}
		return false;
	}

    private String getInQuery(int size) {
        StringBuilder sb = new StringBuilder();
        sb.append("IN (");
        for(int i=0;i < size;i++) {
            if(i != 0) {
                sb.append(", ");
            }
            sb.append('?');
        }
        sb.append(")");
        return sb.toString();
    }

    public JSONArray getPartsPrice(List<String> urls, Date date, Integer minPrice, Integer maxPrice) {
    	restoreConnection();
        JSONArray out = new JSONArray();
        try {
            String sql = "SELECT url,euro,cent FROM prijs_verloop WHERE datum > ?";
            int args = 1;
            if(minPrice != null) {
                sql += " AND euro*100+cent > ?";
                args++;
            }
            if(maxPrice != null) {
                sql += " AND euro*100+cent < ?";
                args++;
            }
            PreparedStatement s = conn.prepareStatement(sql+" AND url "+getInQuery(urls.size()));
            s.setDate(1, date);
            if(minPrice != null) {
                s.setInt(2,minPrice);
            }
            if(maxPrice != null) {
                s.setInt(args,maxPrice);
            }
            for(int i=0;i < urls.size();i++) {
                s.setString(i+1+args,urls.get(i));
            }

            ResultSet res = s.executeQuery();
            while(res.next()) {
                out.put(new JSONObject().put("url",res.getString(1))
                        .put("euro",res.getInt(2))
                        .put("cent",res.getInt(3)));
            }
            res.close();
            s.close();
        } catch (SQLException e) {
        }
        return out;
    }
}