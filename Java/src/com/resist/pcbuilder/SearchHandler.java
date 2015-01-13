package com.resist.pcbuilder;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.TermsFilterBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.resist.pcbuilder.pcparts.Case;
import com.resist.pcbuilder.pcparts.GraphicsCard;
import com.resist.pcbuilder.pcparts.HardDisk;
import com.resist.pcbuilder.pcparts.Memory;
import com.resist.pcbuilder.pcparts.PcPart;
import com.resist.pcbuilder.pcparts.Processor;

public class SearchHandler {
	public static final long DAY_IN_MS = 24 * 60 * 60 * 1000;

	private Client client;
	private PcBuilder pcbuilder;

	public SearchHandler(String address, int port, String clusterName, PcBuilder pcbuilder) {
		this.pcbuilder = pcbuilder;
		Settings settings = ImmutableSettings.settingsBuilder().put("cluster.name", clusterName).build();
		client = new TransportClient(settings);
		((TransportClient) client).addTransportAddress(new InetSocketTransportAddress(address, port));
	}

	public JSONArray handleSearch(JSONObject json) {
		Connection conn = pcbuilder.getDBConnection().getConnection();
		if (json.has("filters")) {
			return handleQuery(conn,json.getJSONArray("filters"));
		} else if (json.has("makechart")){
			return getPartsPriceForGraph(conn,json.getJSONArray("makechart"));
		}
		return null;
	}

	private JSONArray handleQuery(Connection conn,JSONArray json) {
		return getParts(conn,json,pcbuilder.getSettings().getInt("daysPartsRemainValid")*DAY_IN_MS,pcbuilder.getSettings().getInt("maxElasticResults"));
	}

	private JSONArray getPartsPriceForGraph(Connection conn,JSONArray json) {
		return getParts(conn,json,7*DAY_IN_MS,1);
	}

	private JSONArray getParts(Connection conn,JSONArray json,long timeago,int maxResults) {
		FilterBuilder filters = getElasticFilters(json);
		if (filters != null) {
			List<String> urls = new ArrayList<String>();
			JSONObject results = elasticSearchQuery(filters, maxResults, urls);
			if(!urls.isEmpty()) {
				Map<String,Integer> mysqlFilters = getMinMaxPrice(json);
				java.sql.Date sqlDate = getPastSQLDate(timeago);
				return combineMysqlResultsWithElasticsearch(results, PcPart.getPartsPrice(conn, urls, sqlDate, mysqlFilters.get("minPrice"), mysqlFilters.get("maxPrice")));
			}
		}
		return null;
	}

	private java.sql.Date getPastSQLDate(long ms) {
		java.util.Date utilDate = new java.util.Date();
		return new java.sql.Date(utilDate.getTime() - ms);
	}

	private JSONObject elasticSearchQuery(FilterBuilder filters, int numResults, List<String> urls) {
		JSONObject output = new JSONObject();
		List<PcPart> list = PcPart.getFilteredParts(client, filters, numResults);
		for(PcPart p : list) {
			@SuppressWarnings("unchecked")
			Map<String,Object> specs = (Map<String, Object>) p.getSpecs().get("specs");
			String url = (String) specs.get("url");
			urls.add(url);
			output.put(url,new JSONObject(specs));
		}
		return output;
	}

	private FilterBuilder getElasticFilters(JSONArray json) {
		FilterBuilder filters = null;
		for (int i = 0; i < json.length(); i++) {
			JSONObject filter = json.getJSONObject(i);
			if (validateTerm(filter)) {
				filters = appendElasticFilter(filter,filters);
			}
		}
		return filters;
	}

	private FilterBuilder appendElasticFilter(JSONObject filter, FilterBuilder filters) {
		String key = filter.getString("key");
		String value = filter.getString("value");
		if(filters == null) {
			return FilterBuilders.termsFilter(key,value);
		}
		TermsFilterBuilder part = FilterBuilders.termsFilter(key,value);
		return FilterBuilders.andFilter(filters, part);
	}

	private Map<String,Integer> getMinMaxPrice(JSONArray json) {
		Map<String,Integer> filters = new HashMap<String,Integer>();
		for(int i=0;i < json.length();i++) {
			JSONObject filter = json.getJSONObject(i);
			addMinMaxPrice(filter,filters);
		}
		return filters;
	}

	private void addMinMaxPrice(JSONObject filter, Map<String,Integer> filters) {
		String key = filter.getString("key");
		if(key.equals("minPrice") || key.equals("maxPrice")) {
			filters.put(key, filter.getInt("value"));

		}
	}

	private JSONArray combineMysqlResultsWithElasticsearch(JSONObject elasticsearch, List<PcPart> prices) {
		JSONArray out = new JSONArray();
		for(PcPart price : prices) {
			JSONObject part = new JSONObject().put("url", price.getUrl()).put("euro", price.getEuro()).put("cent", price.getCent());
			JSONObject elasticElement = elasticsearch.getJSONObject(price.getUrl());
			@SuppressWarnings("unchecked")
			Iterator<String> it = elasticElement.keys();
			while(it.hasNext()) {
				String key = it.next();
				part.put(key, elasticElement.get(key));
			}
			out.put(part);
		}
		return out;
	}

	private boolean validateTerm(JSONObject term) {
		try {
			String val = term.getString("key");
			return val.equals("component") || val.equals("merk");
		} catch (JSONException e) {
			return false;
		}
	}

	public void close() {
		client.close();
	}

	public JSONObject getInit() {
		JSONObject out = new JSONObject();
		Connection conn = pcbuilder.getDBConnection().getConnection();
		out.put("processors", initProcessors(conn));
        out.put("hardeschijven", initHarddisk(conn));
        out.put("grafischekaarten", initGpu(conn));
        out.put("geheugen", initRam(conn));
        out.put("behuizing", initFormfactor(conn));
		return out;
	}

	/**
	 * Returns a list of processor sockets by vendor.
     *
     * @param conn The connection to the database
     * @return A list of processor sockets by vendor
	 */
	private JSONObject initProcessors(Connection conn) {
		JSONObject out = new JSONObject();
		List<Processor> list = Processor.getProcessors(conn);
		for(Processor p : list) {
			if(!out.has(p.getBrand())) {
				out.put(p.getBrand(), new JSONArray());
			}
			out.getJSONArray(p.getBrand()).put(p.getSocket());
		}
		return out;
	}

    /**
     * Returns a list of harddisks by hard disk type.
     *
     * @param conn The connection to the database
     * @return A list of harddisks by hard disk type
     */
    private JSONObject initHarddisk(Connection conn) {
        JSONObject out = new JSONObject();
        List<HardDisk> list = HardDisk.getHardDisks(conn);
        for(HardDisk d : list) {
        	if(!out.has(d.getType())) {
        		out.put(d.getType(), new JSONArray());
        	}
        	out.getJSONArray(d.getType()).put(d.getSocket());
        }
        return out;
    }

    /**
     * Returns a list of graphic card brands and interfaces.
     *
     * @param conn The connection to the database
     * @return A list of graphic card brands and interfaces
     */
    private JSONObject initGpu(Connection conn) {
    	JSONObject out = new JSONObject();
    	List<GraphicsCard> list = GraphicsCard.getSockets(conn);
    	JSONArray aansluitingen = new JSONArray();
    	for(GraphicsCard g : list) {
    		aansluitingen.put(g.getSocket());
    	}
        out.put("aansluitingen",aansluitingen);
    	list = GraphicsCard.getBrands(conn);
    	JSONArray merken = new JSONArray();
    	for(GraphicsCard g : list) {
    		merken.put(g.getSocket());
    	}
        out.put("merken",merken);
        return out;
    }

    /**
     * Returns a list of RAM modules.
     *
     * @param conn The connection to the database
     * @return A list of RAM modules.
     */
    private JSONArray initRam(Connection conn) {
    	List<Memory> list = Memory.getSockets(conn);
    	JSONArray out = new JSONArray();
    	for(Memory m : list) {
    		out.put(m.getSocket());
    	}
        return out;
    }

    /**
     * Returns a list of case formfactors.
     *
     * @param conn The connection to the database
     * @return A list of case formfactors
     */
    private JSONArray initFormfactor(Connection conn) {
    	JSONArray out = new JSONArray();
    	List<Case> cases = Case.getFormFactors(conn);
    	for(Case c : cases) {
    		out.put(c.getFormFactor());
    	}
        return out;
    }
}
