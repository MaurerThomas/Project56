package com.resist.pcbuilder.pcparts;

import com.resist.pcbuilder.DatePrice;
import org.elasticsearch.client.Client;

import java.sql.Connection;
import java.sql.Date;
import java.util.List;
import java.util.Map;

public class Motherboard extends PcPart {
	public static final String COMPONENT = "Moederborden";

	public Motherboard(int euro, int cent, Date crawlDate, Map<String, Object> specs) {
		super(euro, cent, crawlDate, specs);
	}

	public static boolean isPart(Map<String, Object> specs) {
		return COMPONENT.equals(specs.get("component"));
	}
    public static List<DatePrice> getAvgPrice(Client client, Connection conn) {
        return PcPart.getAvgPrice(client,conn,COMPONENT);
    }
}
