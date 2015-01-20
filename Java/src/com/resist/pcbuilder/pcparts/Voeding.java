package com.resist.pcbuilder.pcparts;

import java.sql.Date;
import java.util.Map;
import java.util.logging.Level;

import com.resist.pcbuilder.PcBuilder;

public class Voeding extends PcPart {
	private int wattage;

	public Voeding(int euro, int cent, Date crawlDate, Map<String, Object> specs) {
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
		return specs.containsKey("Vermogen");
	}

	public static boolean isValidRangeKey(String key) {
		return key.equals("Vermogen");
	}
}
