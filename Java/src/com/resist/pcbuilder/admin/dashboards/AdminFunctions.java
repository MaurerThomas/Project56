package com.resist.pcbuilder.admin.dashboards;

import org.json.JSONObject;

import com.resist.pcbuilder.PcBuilder;
import com.resist.pcbuilder.admin.Dashboard;

public class AdminFunctions implements Dashboard {
	public static final String IDENTIFIER = "adminfunctions";
	private PcBuilder pcbuilder;

	public AdminFunctions(PcBuilder pcbuilder) {
		this.pcbuilder = pcbuilder;
	}

	@Override
	public JSONObject handleJSON(JSONObject input) {
		if(input.has("switchDashboard") && input.getString("switchDashboard").equals(IDENTIFIER)) {
			return new JSONObject().put("admins",pcbuilder.getMysql().getAdminList());
		} else if(input.has("action")) {
			if(input.getString("action").equals("deleteAdmin") && input.has("aid")) {
				return new JSONObject().put("adminDeleted",pcbuilder.getMysql().deleteAdmin(input.getInt("aid")));
			}
		}
		return null;
	}
}