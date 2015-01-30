package com.resist.pcbuilder.pcparts;

import java.sql.Connection;
import java.sql.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import com.resist.pcbuilder.DatePrice;
import com.resist.pcbuilder.PcBuilder;
import org.elasticsearch.client.Client;

public class PowerSupplyUnit extends PcPart {
	public static final String COMPONENT = "Voedingen";
	private int wattage;
	private String formFactor;

	public PowerSupplyUnit(int euro, int cent, Date crawlDate, Map<String, Object> specs) {
		super(euro, cent, crawlDate, specs);
		try {
			wattage = Integer.parseInt(((String) specs.get("Vermogen")).replaceAll("[^0-9]", ""));
		} catch(NumberFormatException e) {
			PcBuilder.LOG.log(Level.WARNING,"Vermogen is geen int",e);
			wattage = 0;
		}
		setSpec("wattage", wattage);
		formFactor = (String) specs.get("Bouwvorm");
		setSpec("formFactor",formFactor);

	}

	public static boolean isPart(Map<String, Object> specs) {
		return COMPONENT.equals(specs.get("component")) && specs.containsKey("Vermogen");
	}

	public static boolean isValidRangeKey(String key) {
		return key.equals("Vermogen");
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
