package com.resist.pcbuilder.dashboard;

import java.io.IOException;

import org.json.JSONObject;

import com.resist.websocket.Connection;

public class OutputBuilder {
	private JSONObject output;

	public OutputBuilder() {
		output = new JSONObject();
	}

	public OutputBuilder html(Object key,Object value) {
		return put("html",key.toString(),value.toString());
	}

	public OutputBuilder text(Object key,Object value) {
		return put("text",key.toString(),value.toString());
	}

	private OutputBuilder put(String object,String key,String value) {
		if(!output.has(object)) {
			output.put(object,new JSONObject());
		}
		output.getJSONObject(object).put(key,value);
		return this;
	}

	public boolean send(Connection conn) {
		if(!conn.isClosed()) {
			try {
				conn.sendMessage(output.toString());
				return true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
}
