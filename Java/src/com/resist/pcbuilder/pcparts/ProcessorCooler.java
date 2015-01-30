package com.resist.pcbuilder.pcparts;

import com.resist.pcbuilder.DatePrice;

import org.elasticsearch.client.Client;

import java.sql.Connection;
import java.sql.Date;
import java.util.List;
import java.util.Map;

public class ProcessorCooler extends PcPart {
	public static final String COMPONENT = "Koeling";

	private String socket;
	private String rpm;
    private String coolerHeight;

	public ProcessorCooler(int euro, int cent, Date crawlDate, Map<String, Object> specs) {
		super(euro, cent, crawlDate, specs);
		socket = cleanSocketString((String) specs.get("Socket"));
		rpm = (String) specs.get("Toerental");
        coolerHeight = (String) specs.get("max, hoogte CPU koeler");
		setSpec("socket",socket);
		setSpec("toerental",rpm);
        setSpec("max. processorkoeler hoogte",coolerHeight);
	}

	private String cleanSocketString(String socket) {
		return socket.replaceAll("u'"," ").replace(')',' ').replace('(',' ').replace('\'',' ').replace(" voor , ","");
	}

	public static boolean isPart(Map<String, Object> specs) {
		return COMPONENT.equals(specs.get("component")) && specs.containsKey("Socket") && specs.containsKey("Toerental");
	}

	/**
	 * Retrieves the average price of this component over time.
	 * 
	 * @param client The client to find parts on
	 * @param conn The database connection to get prices from
	 * @return A list of prices and dates
	 */
    public static List<DatePrice> getAvgPrice(Client client, Connection conn) {
        return PcPart.getAvgPrice(client,conn,COMPONENT);
    }
}
