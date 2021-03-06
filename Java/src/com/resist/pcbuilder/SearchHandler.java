package com.resist.pcbuilder;

import com.resist.pcbuilder.filters.SearchFilter;
import com.resist.pcbuilder.pcparts.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class SearchHandler {
    public static final long DAY_IN_MS = 24 * 60 * 60 * 1000;

    private PcBuilder pcbuilder;

    public SearchHandler(PcBuilder pcbuilder) {
        this.pcbuilder = pcbuilder;
    }

    /**
     * Handles a search request from the website.
     *
     * @param json The request sent by the website
     * @return A list of results to display
     */
    public JSONArray handleSearch(JSONObject json) {
        if (json.has("filters")) {
            return handleQuery(pcbuilder.getDBConnection().getConnection(), json.getJSONArray("filters"));
        }
        return null;
    }

    private JSONArray handleQuery(Connection conn, JSONArray json) {
        return getParts(conn, json, pcbuilder.getSettings().getInt("daysPartsRemainValid") * DAY_IN_MS, pcbuilder.getSettings().getInt("maxElasticResults"), pcbuilder.getSettings().getInt("maxMySQLResults"));
    }

    private JSONArray getParts(Connection conn, JSONArray json, long timeAgo, int maxElasticResults, int maxSQLResults) {
        List<SearchFilter> filters = parseJSONFilters(json);
        if (filters.size() != 0) {
            List<PcPart> results = PcPart.getParts(pcbuilder.getSearchClient(), conn, filters, timeAgo, maxElasticResults, maxSQLResults);
            return partsToJSON(results);
        }
        return null;
    }

    private List<SearchFilter> parseJSONFilters(JSONArray filters) {
        List<SearchFilter> out = new ArrayList<SearchFilter>(filters.length());
        for (int i = 0; i < filters.length(); i++) {
            SearchFilter filter = parseFilter(filters.getJSONObject(i));
            if (filter != null) {
                out.add(filter);
            }
        }
        return out;
    }

    private SearchFilter parseFilter(JSONObject filter) {
        if (filter != null && filter.has("key") && filter.has("value")) {
            Object value = filter.get("value");
            if (value instanceof JSONArray) {
                value = parseFilter((JSONArray) value);
            }
            return SearchFilter.getInstance(filter.getString("key"), value);
        }
        return null;
    }

    private List<Object> parseFilter(JSONArray filter) {
        List<Object> out = new ArrayList<Object>();
        for (int i = 0; i < filter.length(); i++) {
            out.add(filter.get(i));
        }
        return out;
    }

    private JSONArray partsToJSON(List<PcPart> parts) {
        JSONArray out = new JSONArray();
        for (PcPart part : parts) {
            out.put(new JSONObject(part.getSpecs()));
        }
        return out;
    }

    /**
     * Retrieves part filter options from the database.
     *
     * @return Filter options from the database
     */
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
        for (Processor p : list) {
            if (!out.has(p.getBrand())) {
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
        for (HardDisk d : list) {
            if (!out.has(d.getType())) {
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
        for (GraphicsCard g : list) {
            aansluitingen.put(g.getSocket());
        }
        out.put("aansluitingen", aansluitingen);
        list = GraphicsCard.getBrands(conn);
        JSONArray merken = new JSONArray();
        for (GraphicsCard g : list) {
            merken.put(g.getBrand());
        }
        out.put("merken", merken);
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
        for (Memory m : list) {
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
        for (Case c : cases) {
            out.put(c.getFormFactor());
        }
        return out;
    }
}
