package com.resist.pcbuilder.admin.dashboards;


import com.resist.pcbuilder.admin.AdminSession;
import com.resist.pcbuilder.admin.Dashboard;
import com.resist.pcbuilder.admin.OutputBuilder;
import org.json.JSONObject;

/**
 * Created by Armindo on 9-1-2015.
 */
public class Filters implements Dashboard {
    public static final String IDENTIFIER = "filters";
    private AdminSession session;

    public Filters(AdminSession session) {this.session = session;}

    @Override
    public JSONObject handleJSON(JSONObject input) {
        if(input.has("switchDashboard") && input.getString("switchDashboard").equals(IDENTIFIER)) {
            return new OutputBuilder().htmlTemplate("#main","dashboard_filters").getOutput();
        }
        return null;
    }


}
