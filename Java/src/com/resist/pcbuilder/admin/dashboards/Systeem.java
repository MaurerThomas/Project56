package com.resist.pcbuilder.admin.dashboards;

import com.resist.pcbuilder.admin.AdminSession;
import com.resist.pcbuilder.admin.Dashboard;
import com.resist.pcbuilder.admin.OutputBuilder;
import org.json.JSONObject;

/**
 * Created by Wouter
 */
public class Systeem implements Dashboard {

    public static final String IDENTIFIER = "systeem";
    private AdminSession session;

    public Systeem(AdminSession session){this.session = session;}

    @Override
    public JSONObject handleJSON(JSONObject input) {
        if(input.has("switchDashboard") && input.getString("switchDashboard").equals(IDENTIFIER)) {
            return new OutputBuilder().htmlTemplate("#main","dashboard_systeem").getOutput();
        }

        return null;
    }

}
