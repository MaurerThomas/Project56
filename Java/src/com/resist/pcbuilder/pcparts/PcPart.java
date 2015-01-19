package com.resist.pcbuilder.pcparts;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;

import com.resist.pcbuilder.DBConnection;
import com.resist.pcbuilder.PcBuilder;
import com.resist.pcbuilder.SearchFilter;

public class PcPart {
	private String url;
	private String component;
	private String brand;
	private String name;
	private String eun;
	private int euro;
	private int cent;
	private Date crawlDate;
	private Map<String, Object> specs;

	protected PcPart(int euro, int cent, Date crawlDate, Map<String, Object> specs) {
		this.specs = new HashMap<String,Object>();
		setEuro(euro);
		setCent(cent);
		setCrawlDate(crawlDate);
		if(specs != null) {
			url = (String) specs.get("url");
			component = (String) specs.get("component");
			brand = (String) specs.get("merk");
			name = (String) specs.get("naam");
			eun = (String) specs.get("eun");
			setSpec("url", url);
			setSpec("component", component);
			setSpec("brand", brand);
			setSpec("name", name);
			setSpec("eun", eun);
		}
	}

	public String getUrl() {
		return url;
	}

	public String getComponent() {
		return component;
	}

	public String getBrand() {
		return brand;
	}

	public String getName() {
		return name;
	}

	public String getEun() {
		return eun;
	}

	public int getEuro() {
		return euro;
	}

	public int getCent() {
		return cent;
	}

	public Date getCrawlDate() {
		return crawlDate;
	}

	public Map<String, Object> getSpecs() {
		return specs;
	}

	protected void setUrl(String url) {
		this.url = url;
	}

	protected void setComponent(String component) {
		this.component = component;
		setSpec("component", component);
	}

	protected void setBrand(String brand) {
		this.brand = brand;
		setSpec("brand", brand);
	}

	protected void setName(String name) {
		this.name = name;
		setSpec("naam", name);
	}

	protected void setEun(String eun) {
		this.eun = eun;
		setSpec("eun", eun);
	}

	protected void setEuro(int euro) {
		this.euro = euro;
		setSpec("euro", euro);
	}

	protected void setCent(int cent) {
		this.cent = cent;
		setSpec("cent", cent);
	}

	protected void setCrawlDate(Date crawlDate) {
		this.crawlDate = crawlDate;
		setSpec("crawlDate", crawlDate);
	}

	protected void setSpec(String key, Object value) {
		specs.put(key, value);
	}

	public static boolean isPart(Map<String, Object> specs) {
		return false;
	}

	public static PcPart getInstance(int euro, int cent, Date crawlDate, Map<String, Object> specs) {
		if(Case.isPart(specs)) {
			return new Case(euro, cent, crawlDate, specs);
		} else if(GraphicsCard.isPart(specs)) {
			return new GraphicsCard(euro, cent, crawlDate, specs);
		} else if(HardDisk.isPart(specs)) {
			return new HardDisk(euro, cent, crawlDate, specs);
		} else if(Memory.isPart(specs)) {
			return new Memory(euro, cent, crawlDate, specs);
		} else if(Processor.isPart(specs)) {
			return new Processor(euro, cent, crawlDate, specs);
		}
		return new PcPart(euro, cent, crawlDate, specs);
	}

	public static List<PcPart> getParts(Client client, Connection conn, List<SearchFilter> filterList, long timeAgo, int maxResults) {
		List<PcPart> out = new ArrayList<PcPart>();
		QueryBuilder query = buildFilters(filterList);
		if(query != null) {
			Map<String, Map<String, Object>> elasticResults = getFilteredParts(client,query,maxResults);
			Map<String,Integer> minMaxPrice = getMinMaxPrice(filterList);
			out = addPartPrices(conn,elasticResults,DBConnection.getPastSQLDate(timeAgo),minMaxPrice.get("minPrice"),minMaxPrice.get("maxPrice"));
		}
		return out;
	}

	private static QueryBuilder buildFilters(List<SearchFilter> filters) {
		List<FilterBuilder> validFilters = new ArrayList<FilterBuilder>();
		QueryBuilder out = null;
		for(SearchFilter filter : filters) {
			if(isValidElasticFilter(filter)) {
				String key = filter.getKey(), value = filter.getValue();
				if(out == null) {
					out = QueryBuilders.termQuery(key,value);
				} else {
					validFilters.add(FilterBuilders.termFilter(key, value));
				}
			}
		}
		if(out != null && validFilters.size() > 0) {
			return QueryBuilders.filteredQuery(out, FilterBuilders.boolFilter().must(validFilters.toArray(new FilterBuilder[0])));
		}
		return out;
	}

	public static boolean isValidElasticFilter(SearchFilter filter) {
		String key = filter.getKey();
		return key.equals("component") ||
				key.equals("merk") ||
				key.equals("naam") ||
				key.equals("eun") ||
				Case.isValidElasticFilter(filter) ||
				GraphicsCard.isValidElasticFilter(filter) ||
				HardDisk.isValidElasticFilter(filter) ||
				Processor.isValidElasticFilter(filter);
	}

	private static Map<String, Map<String, Object>> getFilteredParts(Client client, QueryBuilder query, int numResults) {
		Map<String, Map<String, Object>> out = new HashMap<String, Map<String, Object>>();
		SearchRequestBuilder q = client.prepareSearch(PcBuilder.MONGO_SEARCH_INDEX)
				.setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
				.setSize(numResults).setQuery(query).setExplain(false);
		SearchResponse response = q.execute().actionGet();
		SearchHit[] results = response.getHits().getHits();
		for (SearchHit hit : results) {
			addSpecs(out,hit);
		}
		return out;
	}

	private static void addSpecs(Map<String, Map<String, Object>> out, SearchHit hit) {
		try {
			@SuppressWarnings("unchecked")
			Map<String, Object> specs = (Map<String, Object>) hit.getSource().get("specs");
			if(specs != null && specs.containsKey("url")) {
				out.put((String)specs.get("url"), specs);
			}
		} catch(ClassCastException e) {
			PcBuilder.LOG.log(Level.WARNING,"Failed to get specs.",e);
		}
	}

	private static Map<String,Integer> getMinMaxPrice(List<SearchFilter> filters) {
		Map<String,Integer> out = new HashMap<String,Integer>();
		int found = 0;
		for(SearchFilter filter : filters) {
			String key = filter.getKey();
			if(key.equals("minPrice") || key.equals("maxPrice")) {
				found++;
				try {
					out.put(key, Integer.parseInt(filter.getValue()));
				} catch(NumberFormatException e) {
					PcBuilder.LOG.log(Level.WARNING, key+" not an int.", e);
				}
			}
			if(found == 2) {
				break;
			}
		}
		return out;
	}

	private static List<PcPart> addPartPrices(Connection conn, Map<String, Map<String, Object>> parts, Date date, Integer minPrice, Integer maxPrice) {
		List<PcPart> out = new ArrayList<PcPart>();
		Set<String> urls = parts.keySet();
		if(urls.size() != 0) {
			try {
				StringBuilder sql = new StringBuilder("SELECT DISTINCT ");
				sql.append(DBConnection.COLUMN_PRICE_URL).append(",").append(DBConnection.COLUMN_PRICE_EURO).append(",")
						.append(DBConnection.COLUMN_PRICE_CENT).append(",").append(DBConnection.COLUMN_PRICE_DATE)
						.append(" FROM ").append(DBConnection.TABLE_PRICE).append(" WHERE ")
						.append(DBConnection.COLUMN_PRICE_DATE).append(" > ?");
				int args = 1;
				if (minPrice != null) {
					sql.append(" AND ").append(DBConnection.COLUMN_PRICE_EURO).append("*100+").append(DBConnection.COLUMN_PRICE_CENT).append(" >= ?");
					args++;
				}
				if (maxPrice != null) {
					sql.append(" AND ").append(DBConnection.COLUMN_PRICE_EURO).append("*100+").append(DBConnection.COLUMN_PRICE_CENT).append(" <= ?");
					args++;
				}
				sql.append(" AND ").append(DBConnection.COLUMN_PRICE_URL).append(DBConnection.getInQuery(urls.size()));
				PreparedStatement s = conn.prepareStatement(sql.toString());
				s.setDate(1, date);
				if (minPrice != null) {
					s.setInt(2, minPrice * 100);
				}
				if (maxPrice != null) {
					s.setInt(args, maxPrice * 100);
				}
				int i = 0;
				for(String url : urls) {
					s.setString(i + 1 + args, url);
					i++;
				}
				ResultSet res = s.executeQuery();
				while (res.next()) {
					String url = res.getString(1);
					out.add(getInstance(res.getInt(2), res.getInt(3), res.getDate(4), parts.get(url)));
				}
				res.close();
				s.close();
			} catch (SQLException e) {
				PcBuilder.LOG.log(Level.WARNING, "Failed to get prices.", e);
			}
		}
		return out;
	}
}
