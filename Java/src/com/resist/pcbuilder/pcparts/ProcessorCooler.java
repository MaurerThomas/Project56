package com.resist.pcbuilder.pcparts;

import java.sql.Date;
import java.util.Map;

public class ProcessorCooler extends PcPart {
	public static final String COMPONENT = "Koeling";

	public ProcessorCooler(int euro, int cent, Date crawlDate, Map<String, Object> specs) {
		super(euro, cent, crawlDate, specs);
	}

	public static boolean isPart(Map<String, Object> specs) {
		return COMPONENT.equals(specs.get("component"));
	}
}
