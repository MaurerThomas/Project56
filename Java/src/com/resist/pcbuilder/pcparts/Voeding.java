package com.resist.pcbuilder.pcparts;

import java.sql.Date;
import java.util.Map;

public class Voeding extends PcPart {
	private Voeding(int euro, int cent, Date crawlDate, Map<String, Object> specs) {
		super(euro, cent, crawlDate, specs);
	}

	public static boolean isValidRangeKey(String key) {
		return key.equals("wattage");
	}
}
