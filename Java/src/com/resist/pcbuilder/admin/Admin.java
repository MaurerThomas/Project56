package com.resist.pcbuilder.admin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import com.resist.pcbuilder.DBConnection;
import com.resist.pcbuilder.PcBuilder;

public class Admin {
	private int aid;
	private String username;
	private String password;

	public Admin(int aid, String username) {
		this.aid = aid;
		this.username = username;
	}

	public int getAid() {
		return aid;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	/**
	 * Returns a list of admin usernames by id.
	 * 
	 * @param conn The connection to the database
	 * @return A list of admins
	 */
	public static List<Admin> getAdminList(Connection conn) {
		List<Admin> out = new ArrayList<Admin>();
		try {
			Statement s = conn.createStatement();
			ResultSet res = s.executeQuery("SELECT "+DBConnection.COLUMN_ADMINS_AID+", "+DBConnection.COLUMN_ADMINS_USERNAME+" FROM "+DBConnection.TABLE_ADMINS);
			while(res.next()) {
				out.add(new Admin(res.getInt(1),res.getString(2)));
			}
			res.close();
			s.close();
		} catch (SQLException e) {
			PcBuilder.LOG.log(Level.WARNING,"Failed to get admins.",e);
			return null;
		}
		return out;
	}

	/**
	 * Deletes an admin.
	 * 
	 * @param conn The connection to the database
	 * @param aid The id of the admin to delete
	 * @return True if the admin was deleted
	 */
	public static boolean deleteAdmin(Connection conn, int aid) {
		try {
			PreparedStatement s = conn.prepareStatement("DELETE FROM "+DBConnection.TABLE_ADMINS+" WHERE "+DBConnection.COLUMN_ADMINS_AID+" = ?");
			s.setInt(1,aid);
			s.executeUpdate();
			s.close();
			return true;
		} catch (SQLException e) {
			PcBuilder.LOG.log(Level.WARNING,"Failed to delete admin.",e);
			return false;
		}
	}

	/**
	 * Adds an admin.
	 * 
	 * @param conn The connection to the database
	 * @param username The username for the new admin
	 * @param password The password for the new admin
	 * @return The id of the new admin, -1 on failure
	 */
	public static int addAdmin(Connection conn, String username, String password) {
		try {
			int out = -1;
			PreparedStatement s = conn.prepareStatement("INSERT INTO "+DBConnection.TABLE_ADMINS+" ("+DBConnection.COLUMN_ADMINS_USERNAME+","+DBConnection.COLUMN_ADMINS_PASSWORD+") VALUES(?,?)",Statement.RETURN_GENERATED_KEYS);
			s.setString(1,username);
			s.setString(2,password);
			s.executeUpdate();
			ResultSet res = s.getGeneratedKeys();
			if(res.next()) {
				out = res.getInt(1);
			}
			res.close();
			s.close();
			return out;
		} catch (SQLException e) {
			PcBuilder.LOG.log(Level.WARNING,"Failed to add admin.",e);
			return -1;
		}
	}

	/**
	 * Modifies an admin account.
	 * 
	 * @param conn The connection to the database
	 * @param aid The id of the admin to modify
	 * @param username The username to assign to this admin
	 * @param password The password to assign to this admin
	 * @return True if the admin was modified
	 */
	public static boolean modifyAdmin(Connection conn, int aid, String username, String password) {
		try {
			PreparedStatement s;
			if(password == null) {
				s = conn.prepareStatement("UPDATE "+DBConnection.TABLE_ADMINS+" SET "+DBConnection.COLUMN_ADMINS_USERNAME+" = ? WHERE "+DBConnection.COLUMN_ADMINS_AID+" = ?");
				s.setString(1,username);
				s.setInt(2,aid);
			} else if(username == null) {
				s = conn.prepareStatement("UPDATE "+DBConnection.TABLE_ADMINS+" SET "+DBConnection.COLUMN_ADMINS_PASSWORD+" = ? WHERE "+DBConnection.COLUMN_ADMINS_AID+" = ?");
				s.setString(1,password);
				s.setInt(2,aid);
			} else {
				s = conn.prepareStatement("UPDATE "+DBConnection.TABLE_ADMINS+" SET "+DBConnection.COLUMN_ADMINS_USERNAME+" = ?, "+DBConnection.COLUMN_ADMINS_PASSWORD+" = ? WHERE "+DBConnection.COLUMN_ADMINS_AID+" = ?");
				s.setString(1,username);
				s.setString(2,password);
				s.setInt(3,aid);
			}
			s.executeUpdate();
			s.close();
			return true;
		} catch (SQLException e) {
			PcBuilder.LOG.log(Level.WARNING,"Failed to update admins.",e);
			return false;
		}
	}
}
