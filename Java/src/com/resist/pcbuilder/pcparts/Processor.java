package com.resist.pcbuilder.pcparts;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import com.resist.pcbuilder.PcBuilder;
import com.resist.pcbuilder.SearchFilter;

public class Processor extends PcPart {
	public static final String COMPONENT = "Processoren";
	private static final String brandTable = "merk";
	private static final String socketTable = "socket";
	private static final String brandNameColumn = "merk.naam";
	private static final String socketTypeColumn = "socket.type";
	private static final String brandMidColumn = "merk.mid";
	private static final String socketMidColumn = "socket.merkmid";

	private String socket;

	public Processor(int euro, int cent, Date crawlDate, Map<String,Object> specs) {
		super(euro,cent,crawlDate,specs);
		socket = (String) specs.get("Socket");
		this.specs.put("socket",socket);
	}

	private Processor(String brand, String socket) {
		super(0,0,null,null);
		this.brand = brand;
		this.socket = socket;
	}

	public String getSocket() {
		return socket;
	}

	public static boolean isPart(Map<String, Object> specs) {
		return COMPONENT.equals(specs.get("component")) && specs.containsKey("Socket");
	}

	public static boolean isValidElasticFilter(SearchFilter filter) {
		return filter.getKey().equals("Socket");
	}

	/**
	 * Gets a list of processors from the database.
     *
     * @param conn The connection to the database
     * @return A list of processors
	 */
	public static List<Processor> getProcessors(Connection conn) {
		List<Processor> out = new ArrayList<Processor>();
		try {
			PreparedStatement s = conn.prepareStatement("SELECT "+brandNameColumn+", "+socketTypeColumn+" FROM "+brandTable+", "+socketTable+" WHERE "+brandMidColumn+" = "+socketMidColumn);
			ResultSet res = s.executeQuery();
			while(res.next()) {
				String merk = res.getString(1);
				String socket = res.getString(2);
				out.add(new Processor(merk, socket));
			}
			res.close();
			s.close();
		} catch (SQLException e) {
			PcBuilder.LOG.log(Level.WARNING,"Failed to get processors.",e);
		}
		return out;
	}
}
