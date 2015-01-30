package com.resist.pcbuilder.admin;

import org.json.JSONObject;

public interface Dashboard {
    /**
     * Implement this method to create a dashboard.
     *
     * @param input The input received from the website
     * @return The output to send to the website
     */
    public JSONObject handleJSON(JSONObject input);
}
