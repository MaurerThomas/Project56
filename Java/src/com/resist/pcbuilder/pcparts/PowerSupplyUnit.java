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

	public PowerSupplyUnit(int euro, int cent, Date crawlDate, Map<String, Object> specs) {
		super(euro, cent, crawlDate, specs);
		try {
			wattage = Integer.parseInt((String) specs.get("Vermogen"));
		} catch(NumberFormatException e) {
			PcBuilder.LOG.log(Level.WARNING,"Vermogen is geen int",e);
			wattage = 0;
		}
		setSpec("wattage", wattage);
	}

	public static boolean isPart(Map<String, Object> specs) {
		return COMPONENT.equals(specs.get("component")) && specs.containsKey("Vermogen");
	}

	public static boolean isValidRangeKey(String key) {
		return key.equals("Vermogen");
	}
    public static List<DatePrice> getAvgPrice(Client client, Connection conn) {
        return PcPart.getAvgPrice(client,conn,COMPONENT);
    }
}
