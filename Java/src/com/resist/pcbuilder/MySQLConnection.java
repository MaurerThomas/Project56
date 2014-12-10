package com.resist.pcbuilder;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONObject;

public class MySQLConnection {
	private Connection conn;
	private String salt = null;

	public MySQLConnection(String address, int port, String dbName, String username, String password) throws SQLException {
		conn = DriverManager.getConnection("jdbc:mysql://"+address+":"+port+"/"+dbName,username,password);
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public void close() {
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public JSONObject getInit() {
		JSONObject out = new JSONObject();
		try {
			out.put("processors", initProcessors());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return out;
	}

	private JSONObject initProcessors() throws SQLException {
		JSONObject out = new JSONObject();
		ResultSet res = executeQuery("SELECT naam, socket FROM merk JOIN onderdeel_socket ON ( merk.mid = onderdeel_socket.mid )");
		while(res.next()) {
			String merk = res.getString(1);
			String socket = res.getString(2);
			if(!out.has(merk)) {
				out.put(merk, new JSONArray());
			}
			out.getJSONArray(merk).put(socket);
		}
		return out;
	}

	private ResultSet executeQuery(String sql) throws SQLException {
		PreparedStatement s = conn.prepareStatement(sql);
		return s.executeQuery();
	}

	private String getPasswordHash(String password) throws NoSuchAlgorithmException {
		if(salt != null) {
			password = salt+password;
		}
		MessageDigest md = MessageDigest.getInstance("SHA1");
		byte[] hash = md.digest(password.getBytes());
		return toHexString(hash);
	}

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

	public boolean isValidLogin(String username,String password) {
		try {
			password = getPasswordHash(password);
			PreparedStatement s = conn.prepareStatement("SELECT 1 FROM admins WHERE username = ? AND password = ?");
			s.setString(1,username);
			s.setString(2,password);
			ResultSet r = s.executeQuery();
			return r.next();
		} catch(SQLException | NoSuchAlgorithmException e) {
			return false;
		}
	}
}