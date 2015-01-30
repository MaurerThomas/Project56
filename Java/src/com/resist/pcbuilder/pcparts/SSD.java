package com.resist.pcbuilder.pcparts;

import java.sql.Date;
import java.util.Map;

public class SSD extends HardDisk {
	public static final String COMPONENT = "SSD's";

	public SSD(int euro, int cent, Date crawlDate, Map<String, Object> specs) {
		super(euro, cent, crawlDate, specs);
	}

	public static boolean isPart(Map<String, Object> specs) {
		return COMPONENT.equals(specs.get("component")) && specs.containsKey("Interface");
	}
}
