package com.resist.pcbuilder.pcparts;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;

import com.resist.pcbuilder.DBConnection;
import com.resist.pcbuilder.DatePrice;
import com.resist.pcbuilder.PcBuilder;
import com.resist.pcbuilder.filters.ElasticSearchFilter;
import com.resist.pcbuilder.filters.MySQLPriceFilter;
import com.resist.pcbuilder.filters.SearchFilter;

public abstract class PcPart {
	private String url;
	private String component;
	private String brand;
	private String name;
	private String ean;
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
			ean = (String) specs.get("ean");
			setSpec("url", url);
			setSpec("component", component);
			setSpec("brand", brand);
			setSpec("name", name);
			setSpec("ean", ean);
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

	public String getEan() {
		return ean;
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

	protected void setEun(String ean) {
		this.ean = ean;
		setSpec("ean", ean);
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

	public static boolean isValidMatchKey(String key) {
		return key.equals("component") ||
				key.equals("merk") ||
				key.equals("naam") ||
				key.equals("eun") ||
				Case.isValidMatchKey(key) ||
				GraphicsCard.isValidMatchKey(key) ||
				HardDisk.isValidMatchKey(key) ||
				Processor.isValidMatchKey(key);
	}

	public static boolean isValidRangeKey(String key) {
		return PowerSupplyUnit.isValidRangeKey(key);
	}

	public static boolean isPart(Map<String, Object> specs) {
		return false;
	}

	public static PcPart getInstance(int euro, int cent, Date crawlDate, Map<String, Object> specs) {
		final String[] basics = new String[] {"url","component","merk","naam","ean"};
		for(String field : basics) {
			if(!specs.containsKey(field) || !(specs.get(field) instanceof String)) {
				return null;
			}
		}
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
		} else if(PowerSupplyUnit.isPart(specs)) {
			return new PowerSupplyUnit(euro, cent, crawlDate, specs);
		} else if(Motherboard.isPart(specs)) {
			return new Motherboard(euro, cent, crawlDate, specs);
		} else if(ProcessorCooler.isPart(specs)) {
			return new ProcessorCooler(euro, cent, crawlDate, specs);
		}
		return null;
	}

	public static List<PcPart> getParts(Client client, Connection conn, List<SearchFilter> filterList, long timeAgo, int maxElasticResults, int maxSQLResults) {
		List<PcPart> out = new ArrayList<PcPart>();
		QueryBuilder query = ElasticSearchFilter.buildFilters(filterList);
		if(query != null) {
			Map<String, Map<String, Object>> elasticResults = getFilteredParts(client,query,maxElasticResults);
			Map<String,Integer> minMaxPrice = getMinMaxPrice(filterList);
			out = addPartPrices(conn,elasticResults,DBConnection.getPastSQLDate(timeAgo),minMaxPrice.get("minPrice"),minMaxPrice.get("maxPrice"),maxSQLResults);
		}
		return out;
	}

	private static Map<String, Map<String, Object>> getFilteredParts(Client client, QueryBuilder query, int numResults) {
		Map<String, Map<String, Object>> out = new HashMap<String, Map<String, Object>>();
		SearchResponse response = client.prepareSearch(PcBuilder.MONGO_SEARCH_INDEX)
				.setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
				.setSize(numResults).setQuery(query)
				.setExplain(false).execute().actionGet();
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
			if(specs != null && specs.containsKey("ean")) {
				out.put((String)specs.get("ean"), specs);
			}
		} catch(ClassCastException e) {
			PcBuilder.LOG.log(Level.WARNING,"Failed to get specs.",e);
		}
	}

	private static Map<String,Integer> getMinMaxPrice(List<SearchFilter> filters) {
		Map<String,Integer> out = new HashMap<String,Integer>();
		for(SearchFilter filter : filters) {
			if(filter instanceof MySQLPriceFilter) {
				return ((MySQLPriceFilter)filter).toMap();
			}
		}
		return out;
	}

	private static String getPriceSQL(Set<String> urls, Integer minPrice, Integer maxPrice, Integer limit) {
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(DBConnection.COLUMN_EAN_EAN).append(",").append(DBConnection.COLUMN_PRICE_EURO).append(",")
				.append(DBConnection.COLUMN_PRICE_CENT).append(",").append(DBConnection.COLUMN_PRICE_DATE).append(",").append(DBConnection.COLUMN_EAN_URL)
				.append(" FROM ").append(DBConnection.TABLE_PRICE).append(" JOIN ").append(DBConnection.TABLE_EAN).append(" ON(")
				.append(DBConnection.COLUMN_EAN_URL).append(" = ").append(DBConnection.COLUMN_PRICE_URL).append(") WHERE ")
				.append(DBConnection.COLUMN_PRICE_DATE).append(" > ?");
		if (minPrice != null) {
			sql.append(" AND ").append(DBConnection.COLUMN_PRICE_EURO).append("*100+").append(DBConnection.COLUMN_PRICE_CENT).append(" >= ?");
		}
		if (maxPrice != null) {
			sql.append(" AND ").append(DBConnection.COLUMN_PRICE_EURO).append("*100+").append(DBConnection.COLUMN_PRICE_CENT).append(" <= ?");
		}
		sql.append(" AND ").append(DBConnection.COLUMN_EAN_EAN).append(DBConnection.getInQuery(urls.size()));
		sql.append(" ORDER BY ").append(DBConnection.COLUMN_EAN_EAN).append(",").append(DBConnection.COLUMN_PRICE_EURO).append(",").append(DBConnection.COLUMN_PRICE_CENT);
		if(limit != null) {
			sql.append(" LIMIT "+limit);
		}
		return sql.toString();
	}

	private static PreparedStatement getPriceStatement(Connection conn, Set<String> eans, Date date, Integer minPrice, Integer maxPrice, Integer limit) throws SQLException {
		PreparedStatement s = conn.prepareStatement(getPriceSQL(eans,minPrice,maxPrice,limit));
		s.setDate(1, date);
		int args = 1;
		if (minPrice != null) {
			args++;
			s.setInt(2, minPrice * 100);
		}
		if (maxPrice != null) {
			args++;
			s.setInt(args, maxPrice * 100);
		}
		int i = 1;
		for(String ean : eans) {
			s.setString(i + args, ean);
			i++;
		}
		return s;
	}

	private static List<PcPart> addPartPrices(Connection conn, Map<String, Map<String, Object>> parts, Date date, Integer minPrice, Integer maxPrice, Integer limit) {
		List<PcPart> out = new ArrayList<PcPart>();
		Set<String> eans = parts.keySet();
		if(eans.size() != 0) {
			try {
				PreparedStatement s = getPriceStatement(conn, eans, date, minPrice, maxPrice, limit);
				ResultSet res = s.executeQuery();
				while (res.next()) {
					Map<String,Object> specs = parts.get(res.getString(1));
					specs.put("url", res.getString(5));
					PcPart part = getInstance(res.getInt(2), res.getInt(3), res.getDate(4), specs);
					if(part != null) {
						out.add(part);
					}
				}
				res.close();
				s.close();
			} catch (SQLException e) {
				PcBuilder.LOG.log(Level.WARNING, "Failed to get prices.", e);
			}
		}
		return out;
	}

    public static List<DatePrice> getAvgPrice(Client client, Connection conn, String component) {
        List<DatePrice> out = new ArrayList<>();
        List<SearchFilter> filterList = new ArrayList<>();
        filterList.add(SearchFilter.getInstance("component", component));
        QueryBuilder query = ElasticSearchFilter.buildFilters(filterList);
        if(query != null) {
            Map<String, Map<String, Object>> elasticResults = getFilteredParts(client, query, 10);
            out = getAvgPrices(conn, elasticResults);
        }
        return out;
    }

    private static List<DatePrice> getAvgPrices(Connection conn, Map<String, Map<String, Object>> parts) {
        Set<String> eans = parts.keySet();
        System.out.println(eans);
        List<DatePrice> out = new ArrayList<>();
        //"SELECT AVG(euro*100+cent),datum FROM prijs_verloop JOIN url_ean ON(prijsverloop.url=url_ean.url) WHERE ean IN("+eans+")";
        try {
            PreparedStatement s = conn.prepareStatement("SELECT AVG " +"("+DBConnection.COLUMN_PRICE_EURO+")"+","+DBConnection.COLUMN_PRICE_DATE+" FROM "
                    +DBConnection.TABLE_PRICE+" JOIN "+DBConnection.TABLE_EAN+" ON " +"("+DBConnection.COLUMN_PRICE_URL+" = "+DBConnection.TABLE_EAN+""+"."+DBConnection.COLUMN_EAN_URL+") " +
                    "WHERE "+DBConnection.COLUMN_EAN_EAN+ "IN "+DBConnection.getInQuery(eans.size())+" GROUP BY "+DBConnection.COLUMN_PRICE_DATE);

            ResultSet resultSet = s.executeQuery();
            resultSet.close();
            s.close();
            while (resultSet.next()){
                DatePrice datePrice = new DatePrice(resultSet.getDate(2),resultSet.getInt(1));
                out.add(datePrice);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return out;
    }
}
