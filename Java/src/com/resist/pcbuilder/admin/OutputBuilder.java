package com.resist.pcbuilder.admin;

import com.resist.websocket.Connection;
import org.json.JSONObject;

public class OutputBuilder {
    private JSONObject output;

    public OutputBuilder() {
        output = new JSONObject();
    }

    /**
     * Creates a request to change the html of an element.
     *
     * @param key   The CSS selector of the element to change
     * @param value The html to change to
     * @return The OutputBuilder, for chaining
     */
    public OutputBuilder html(Object key, Object value) {
        return put("html", key.toString(), value.toString());
    }

    /**
     * Loads a template file and puts it in a given element.
     *
     * @param key      The CSS selector of the element to change
     * @param template The name of the template to load
     * @return The OutputBuilder, for chaining
     */
    public OutputBuilder htmlTemplate(Object key, String template) {
        JSONObject value = new JSONObject().put("template", template);
        return html(key, value);
    }

    /**
     * Creates a request to change the text of an element.
     *
     * @param key   The CSS selector of the element to change
     * @param value The text to change to
     * @return The OutputBuilder, for chaining
     */
    public OutputBuilder text(Object key, Object value) {
        return put("text", key.toString(), value.toString());
    }

    /**
     * Replaces the value of an element with the given replace key.
     *
     * @param key   The name of the element to replace
     * @param value The value to change to
     * @return The OutputBuilder, for chaining
     */
    public OutputBuilder replace(Object key, Object value) {
        return put("replace", key.toString(), value.toString());
    }

    private OutputBuilder put(String object, String key, String value) {
        if (!output.has(object)) {
            output.put(object, new JSONObject());
        }
        output.getJSONObject(object).put(key, value);
        return this;
    }

    /**
     * Provides the JSON built by this builder.
     *
     * @return The JSON built by this builder
     */
    public JSONObject getOutput() {
        return output;
    }

    /**
     * Sends this OutputBuilder to a user.
     *
     * @param conn The connection to send the builder over
     * @return True if the message was sent
     */
    public boolean send(Connection conn) {
        return !conn.isClosed() && conn.sendMessage(output.toString());
    }
}
