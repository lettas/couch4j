package com.coravy.couch4j.http;

import net.sf.json.JSONObject;

import com.coravy.couch4j.ServerResponse;

/**
 * 
 * 
 * @author Stefan Saasen (stefan@coravy.com)
 */
final class JsonServerResponse implements ServerResponse {

    static JsonServerResponse fromJson(JSONObject json) {
        return new JsonServerResponse(json);
    }

    JsonServerResponse(JSONObject json) {
        this.id = json.containsKey("id") ? json.getString("id") : null;
        this.rev = json.containsKey("rev") ? json.getString("rev") : null;
        this.ok = json.getBoolean("ok");
        this.json = json;
    }

    private final String id;
    private final String rev;
    private final boolean ok;
    private final JSONObject json;

    public String getId() {
        return id;
    }

    public String getRev() {
        return rev;
    }

    public boolean isOk() {
        return ok;
    }

    public String toJson() {
        return json.toString();
    }

}
