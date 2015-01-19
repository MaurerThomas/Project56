package com.resist.pcbuilder;

import com.resist.pcbuilder.admin.Analytics;
import com.resist.websocket.Connection;
import com.resist.websocket.Message;
import com.resist.websocket.MessageHandler;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.logging.Level;

public class InputHandler implements MessageHandler {
	private PcBuilder pcbuilder;

	public InputHandler(PcBuilder pcbuilder) {
		this.pcbuilder = pcbuilder;
	}

	@Override
	public void handleMessage(Message message) {
		if (message.getType() == Connection.OPCODE_TEXT_FRAME) {
			JSONObject json = parseInput(message.toString());
			if (json != null) {
				getVisitor(message);
				JSONObject out = handleJSON(json);
				sendReturn(message, out.toString());
			}
		}
	}

	private void getVisitor(Message message) {

		Analytics.insertVisitors(message.getConnection().hashCode(), pcbuilder.getDBConnection());
	}

	private JSONObject parseInput(String message) {
		try {
			return new JSONObject(message);
		} catch (JSONException e) {
			PcBuilder.LOG.log(Level.INFO, "Invalid JSON string.", e);
			return null;
		}
	}

	private JSONObject handleJSON(JSONObject json) {
		JSONObject out = new JSONObject();
		if (json.has("action")) {
			String action = json.getString("action");
			if (action.equals("filter")) {
				out.put("resultaten", pcbuilder.getSearchHandler().handleSearch(json));
			} else if (action.equals("init")) {
				out.put("init", pcbuilder.getSearchHandler().getInit());
			}
		}
		return out;
	}

	private void sendReturn(Message conn, String message) {
		if (!conn.getConnection().isClosed()) {
			conn.getConnection().sendMessage(message);
		}
	}
}
