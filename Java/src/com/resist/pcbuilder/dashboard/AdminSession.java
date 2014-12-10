package com.resist.pcbuilder.dashboard;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import com.resist.pcbuilder.PcBuilder;
import com.resist.websocket.Connection;
import com.resist.websocket.Message;
import com.resist.websocket.MessageHandler;

public class AdminSession implements MessageHandler {
	private PcBuilder pcbuilder;
	private String username;

	public AdminSession(PcBuilder pcbuilder, Message message) {
		this.pcbuilder = pcbuilder;
		this.username = new JSONObject(message.toString()).getJSONObject("login").getString("username");
		initSession(message.getConnection());
	}

	private void initSession(Connection conn) {
		new OutputBuilder().html("#main","<div>"
				+ "<h1>Welkom, "+username+"!</h1>"
				+ "<h2>Kies een dashboard</h2>"
				+ "<div class=\"row\">"
					+ "<div class=\"col-md-6\">#1</div>"
					+ "<div class=\"col-md-6\">#2</div>"
				+ "</div>"
				+ "<div class=\"row\">"
					+ "<div class=\"col-md-6\">#3</div>"
					+ "<div class=\"col-md-6\">#4</div>"
				+ "</div>"
			+ "</div>").send(conn);
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
		return null;
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
