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

	public AdminSession(PcBuilder pcbuilder) {
		this.pcbuilder = pcbuilder;
	}

	@Override
	public void handleMessage(Message message) {
		if(message.getType() == Connection.OPCODE_TEXT_FRAME) {
			JSONObject json = parseJSON(message.toString());
			if(json != null) {
				JSONObject out = handleJSON(json);
				if(out != null) {
					sendReturn(message,out.toString());
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

	private void sendReturn(Message conn, String message) {
		if(!conn.getConnection().isClosed()) {
			try {
				conn.getConnection().sendMessage(message);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
