package com.resist.pcbuilder.admin.dashboards;

import org.json.JSONObject;

import com.resist.pcbuilder.admin.AdminSession;
import com.resist.pcbuilder.admin.Dashboard;
import com.resist.pcbuilder.admin.OutputBuilder;

public class AdminFunctions implements Dashboard {
	public static final String IDENTIFIER = "adminfunctions";
	private AdminSession session;

	/**
	 * Creates a new Admin Functions dashboard.
	 * 
	 * @param session The admin session that spawned this dashboard
	 */
	public AdminFunctions(AdminSession session) {
		this.session = session;
	}

	@Override
	public JSONObject handleJSON(JSONObject input) {
		if(input.has("switchDashboard") && input.getString("switchDashboard").equals(IDENTIFIER)) {
			return new OutputBuilder().htmlTemplate("#main","dashboard_admin").getOutput();
		} else if(input.has("action")) {
			return handleActions(input);
		}
		return null;
	}

	private JSONObject handleActions(JSONObject input) {
		String action = input.getString("action");
		if(action.equals("deleteAdmin") && input.has("aid")) {
			return deleteAdmin(input.getInt("aid"));
		} else if(action.equals("addAdmin") && input.has("username") && input.has("password")) {
			return addAdmin(input.getString("username"),input.getString("password"));
		} else if(action.equals("getAdmins")) {
			return getAdmins();
		} else if(action.equals("modifyAdmin") && input.has("aid")) {
			return handleModifyAdmin(input);
		}
		return null;
	}

	private JSONObject getAdmins() {
		JSONObject admins = session.getPcBuilder().getMysql().getAdminList();
		return new JSONObject().put("admins",admins);
	}

	private JSONObject deleteAdmin(int aid) {
		boolean adminWasDeleted = session.getPcBuilder().getMysql().deleteAdmin(aid);
		return new JSONObject().put("adminDeleted",adminWasDeleted);
	}

	private JSONObject addAdmin(String username, String password) {
		if(username.isEmpty() || password.isEmpty()) {
			return null;
		}
		int aid = session.getPcBuilder().getMysql().addAdmin(username,password);
		return new JSONObject().put("adminAdded",aid);
	}

	private JSONObject handleModifyAdmin(JSONObject input) {
		int aid = input.getInt("aid");
		String username = getValueOrNull(input,"username");
		String password = getValueOrNull(input,"password");
		boolean adminWasModified = session.getPcBuilder().getMysql().modifyAdmin(aid,username,password);
		return new JSONObject().put("adminModified",adminWasModified);
	}

	private String getValueOrNull(JSONObject object, String key) {
		String value = null;
		if(object.has(key)) {
			value = object.getString(key);
			if(value.isEmpty()) {
				value = null;
			}
		}
		return key;
	}
}