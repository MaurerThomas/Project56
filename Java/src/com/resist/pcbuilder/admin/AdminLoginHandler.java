package com.resist.pcbuilder.admin;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.json.JSONException;
import org.json.JSONObject;

import com.resist.pcbuilder.PcBuilder;
import com.resist.websocket.Connection;
import com.resist.websocket.Message;
import com.resist.websocket.MessageHandler;

/**
 * A MessageHandler that receives WebSocket messages from the admin panel.
 */
public class AdminLoginHandler implements MessageHandler {
	private PcBuilder pcbuilder;
	private Map<Connection, AdminSession> connections;
	private String salt = null;

	public AdminLoginHandler(PcBuilder pcbuilder) {
		this.pcbuilder = pcbuilder;
		connections = new HashMap<Connection, AdminSession>();
		if (pcbuilder.getSettings().has("adminPasswordSalt")) {
			salt = pcbuilder.getSettings().getString("adminPasswordSalt");
		}
	}

	/**
	 * Retrieves the PC Builder using this handler.
	 * 
	 * @return The PC Builder
	 */
	public PcBuilder getPcBuilder() {
		return pcbuilder;
	}

	/**
	 * Returns the connection to MySQL.
	 * 
	 * @return The database connection
	 */
	public java.sql.Connection getConnection() {
		return pcbuilder.getDBConnection().getConnection();
	}

	@Override
	public void handleMessage(Message message) {
		Connection conn = message.getConnection();
		if (!connections.containsKey(conn)) {
			if (isValidLogin(message)) {
				removeClosedSessions();
				connections.put(conn, new AdminSession(this, message));
			}
		} else {
			connections.get(conn).handleMessage(message);
		}
	}

	private void removeClosedSessions() {
		Iterator<Entry<Connection, AdminSession>> it = connections.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Connection, AdminSession> entry = it.next();
			if (entry.getKey().isClosed()) {
				it.remove();
			}
		}
	}

	private boolean isValidLogin(Message message) {
		if (message.getType() == Connection.OPCODE_TEXT_FRAME) {
			JSONObject json = parseJSON(message.toString());
			if (json != null) {
				return handleJSON(message, json);
			}
		}
		return false;
	}

	private JSONObject parseJSON(String message) {
		try {
			return new JSONObject(message);
		} catch (JSONException e) {
			return null;
		}
	}

	private boolean handleJSON(Message message, JSONObject input) {
		if (input.has("login")) {
			JSONObject login = input.getJSONObject("login");
			if (login.has("password") && login.has("username")) {
				JSONObject returnMessage = new JSONObject();
				boolean loggedIn = isValidLogin(pcbuilder.getDBConnection().getConnection(), login.getString("username"), login.getString("password"));
				returnMessage.put("login", loggedIn);
				if (!message.getConnection().isClosed()) {
					message.getConnection().sendMessage(returnMessage.toString());
					return loggedIn;
				}
			}
		}
		return false;
	}

	/**
	 * Calculates an admin password hash using the set salt.
	 * 
	 * @param password The password to hash
	 * @return A hexadecimal representation of the hashed password
	 */
	public String getPasswordHash(String password) {
		if (salt != null) {
			password = salt + password;
		}
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA1");
			byte[] hash = md.digest(password.getBytes());
			return toHexString(hash);
		} catch (NoSuchAlgorithmException e) {
			PcBuilder.LOG.log(Level.SEVERE, "SHA1 not found.");
			return null;
		}
	}

	/**
	 * Generates a hexadecimal string from a byte array.
	 * 
	 * @param hash The array to convert to hexadecimal
	 * @return The hexadecimal representation of the input
	 */
	private String toHexString(byte[] hash) {
		StringBuilder out = new StringBuilder();
		for (byte b : hash) {
			String h = Integer.toHexString(b & 0xFF);
			if (h.length() == 1) {
				out.append("0");
			}
			out.append(h);
		}
		return out.toString();
	}

	/**
	 * Checks if a username/password combination is a valid admin.
	 * 
	 * @param conn The connection to the database
	 * @param username The admin's username
	 * @param password The admin's password
	 * @return True if there is an admin with this username and password
	 */
	public boolean isValidLogin(java.sql.Connection conn, String username, String password) {
		password = getPasswordHash(password);
		if (password == null) {
			return false;
		}
		try {
			PreparedStatement s = conn.prepareStatement("SELECT 1 FROM admins WHERE username = ? AND password = ?");
			s.setString(1, username);
			s.setString(2, password);
			ResultSet res = s.executeQuery();
			boolean out = res.next();
			res.close();
			s.close();
			return out;
		} catch (SQLException e) {
			return false;
		}
	}
}
