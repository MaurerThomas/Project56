package com.resist.pcbuilder.admin.dashboards;

import org.json.JSONObject;

import com.resist.pcbuilder.PcBuilder;
import com.resist.pcbuilder.admin.Dashboard;

public class AdminFunctions implements Dashboard {
	public static final String IDENTIFIER = "adminfunctions";
	private PcBuilder pcbuilder;

	/**
	 * Creates a new Admin Functions dashboard.
	 * 
	 * @param pcbuilder The PcBuilder this dashboard belongs to
	 */
	public AdminFunctions(PcBuilder pcbuilder) {
		this.pcbuilder = pcbuilder;
	}

	@Override
	public JSONObject handleJSON(JSONObject input) {
		if(input.has("switchDashboard") && input.getString("switchDashboard").equals(IDENTIFIER)) {
			return getAdmins();
		} else if(input.has("action")) {
			return handleActions(input);
		}
		return null;
	}

	private JSONObject handleActions(JSONObject input) {
		if(input.getString("action").equals("deleteAdmin") && input.has("aid")) {
			return deleteAdmin(input.getInt("aid"));
		} else if(input.getString("action").equals("addAdmin") && input.has("username") && input.has("password")) {
			return addAdmin(input.getString("username"),input.getString("password"));
		} else if(input.getString("action").equals("modifyAdmin") && input.has("aid")) {
			if(input.has("username") && input.has("password")) {
				return modifyAdmin(input.getInt("aid"),input.getString("username"),input.getString("password"));
			} else if(input.has("username")) {
				return modifyAdmin(input.getInt("aid"),input.getString("username"),null);
			} else if(input.has("password")) {
				return modifyAdmin(input.getInt("aid"),null,input.getString("password"));
			}
		}
		return null;
	}

	private JSONObject getAdmins() {
		return new JSONObject().put("admins",pcbuilder.getMysql().getAdminList());
	}

	private JSONObject deleteAdmin(int aid) {
		return new JSONObject().put("adminDeleted",pcbuilder.getMysql().deleteAdmin(aid));
	}

	private JSONObject addAdmin(String username, String password) {
		return new JSONObject().put("adminAdded",pcbuilder.getMysql().addAdmin(username,password));
	}

	private JSONObject modifyAdmin(int aid, String username, String password) {
		return new JSONObject().put("adminModified",pcbuilder.getMysql().modifyAdmin(aid,username,password));
	}
}