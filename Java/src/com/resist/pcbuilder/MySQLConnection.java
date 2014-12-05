package com.resist.pcbuilder;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONObject;

public class MySQLConnection {
	private Connection conn;
	public MySQLConnection(String address, int port, String dbName, String username, String password) throws SQLException {
		conn = DriverManager.getConnection("jdbc:mysql://"+address+":"+port+"/"+dbName,username,password);
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
}
