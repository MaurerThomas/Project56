package com.resist.pcbuilder.dashboard;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONException;
import org.json.JSONObject;

import com.resist.pcbuilder.PcBuilder;
import com.resist.websocket.Connection;
import com.resist.websocket.Message;
import com.resist.websocket.MessageHandler;

public class AdminLoginHandler implements MessageHandler {
	private PcBuilder pcbuilder;
	private Map<Connection,AdminSession> connections;

	public AdminLoginHandler(PcBuilder pcbuilder) {
		this.pcbuilder = pcbuilder;
		connections = new HashMap<Connection,AdminSession>();
	}

	@Override
	public void handleMessage(Message message) {
		Connection conn = message.getConnection();
		if(!connections.containsKey(conn)) {
			if(isValidLogin(message)) {
				removeClosedSessions();
				connections.put(conn,new AdminSession(pcbuilder,message));
			}
		} else {
			connections.get(conn).handleMessage(message);
		}
	}

	private void removeClosedSessions() {
		Iterator<Entry<Connection, AdminSession>> it = connections.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry<Connection,AdminSession> entry = it.next();
			if(entry.getKey().isClosed()) {
				it.remove();
			}
		}
	}

	private boolean isValidLogin(Message message) {
		if(message.getType() == Connection.OPCODE_TEXT_FRAME) {
			JSONObject json = parseJSON(message.toString());
			if(json != null) {
				return handleJSON(message,json);
			}
		}
		return false;
	}

	private JSONObject parseJSON(String message) {
		try {
			return new JSONObject(message);
		} catch(JSONException e) {
			return null;
		}
	}

	private boolean handleJSON(Message message,JSONObject input) {
		if(input.has("login")) {
			JSONObject login = input.getJSONObject("login");
			if(login.has("password") && login.has("username")) {
				JSONObject returnMessage = new JSONObject();
				boolean loggedIn = pcbuilder.getMysql().isValidLogin(login.getString("username"),login.getString("password"));
				returnMessage.put("login",loggedIn);
				if(!message.getConnection().isClosed()) {
					try {
						message.getConnection().sendMessage(returnMessage.toString());
						return loggedIn;
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return false;
	}
}
