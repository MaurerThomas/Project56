package com.resist.pcbuilder;

import java.util.List;
import java.util.logging.Level;

import org.json.JSONException;
import org.json.JSONObject;

import com.resist.pcbuilder.admin.Analytics;
import com.resist.pcbuilder.pcparts.PcPart;
import com.resist.websocket.Connection;
import com.resist.websocket.Message;
import com.resist.websocket.MessageHandler;

/**
 * A MessageHandler for incoming WebSocket messages from the website.
 */
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
				getVisitor(message,json);
				JSONObject out = handleJSON(json);
				sendReturn(message, out.toString());
			}
		}
	}

	private void getVisitor(Message message, JSONObject json) {
		if(json.has("action") && json.get("action").equals("init")) {
			Analytics.insertVisitors(message.getConnection().hashCode(), pcbuilder.getDBConnection());
		}
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
			} else if(action.equals("getPricesForComp") && json.has("ean")) {
				out.put("pricesForComp", getPricesForComponent(json.getString("ean"))).put("pricesForEAN", json.getString("ean"));
			}
		}
		return out;
	}

	private JSONObject getPricesForComponent(String ean) {
		JSONObject out = new JSONObject();
		List<DatePrice> prices = PcPart.getAvgPriceForComponent(pcbuilder.getDBConnection().getConnection(), ean);
		for(DatePrice dp : prices) {
			out.put(dp.getDate().toString(),dp.getPrice());
		}
		return out;
	}

	private void sendReturn(Message conn, String message) {
		if (!conn.getConnection().isClosed()) {
			conn.getConnection().sendMessage(message);
		}
	}
}
