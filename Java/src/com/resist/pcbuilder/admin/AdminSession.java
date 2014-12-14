package com.resist.pcbuilder.admin;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.resist.pcbuilder.PcBuilder;
import com.resist.pcbuilder.admin.dashboards.AdminFunctions;
import com.resist.websocket.Connection;
import com.resist.websocket.Message;
import com.resist.websocket.MessageHandler;

public class AdminSession implements MessageHandler {
	private PcBuilder pcbuilder;
	private String username;
	private Dashboard currentDashboard;
	private Map<String,Dashboard> dashboards;

	public AdminSession(PcBuilder pcbuilder, Message message) {
		this.pcbuilder = pcbuilder;
		this.username = new JSONObject(message.toString()).getJSONObject("login").getString("username");
		initDashboards();
		initSession(message.getConnection());
	}

	private void initDashboards() {
		dashboards = new HashMap<String,Dashboard>();
		dashboards.put("main",null);
		dashboards.put(AdminFunctions.IDENTIFIER,new AdminFunctions(pcbuilder));
	}

	private void initSession(Connection conn) {
		new OutputBuilder().replace("username",username).htmlTemplate("#main","index").send(conn);
	}

	@Override
	public void handleMessage(Message message) {
		if(message.getType() == Connection.OPCODE_TEXT_FRAME) {
			JSONObject json = parseJSON(message.toString());
			if(json != null) {
				JSONObject out = handleJSON(json);
				if(out != null) {
					sendReturn(message.getConnection(),out.toString());
				}
			}
		}
	}

	private JSONObject parseJSON(String message) {
		try {
			return new JSONObject(message);
		} catch(JSONException e) {
			return null;
		}
	}

	private JSONObject handleJSON(JSONObject input) {
		if(input.has("switchDashboard") && dashboards.containsKey(input.getString("switchDashboard"))) {
			currentDashboard = dashboards.get(input.getString("switchDashboard"));
		}
		if(currentDashboard == null) {
			return null;
		}
		return currentDashboard.handleJSON(input);
	}

	private void sendReturn(Connection conn, String message) {
		if(!conn.isClosed()) {
			try {
				conn.sendMessage(message);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
