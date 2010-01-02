package com.coravy.couch4j.http;

import net.sf.json.JSONObject;

import com.coravy.couch4j.ServerResponse;

/**
 * 
 * 
 * @author Stefan Saasen (stefan@coravy.com)
 */
final class JsonServerResponse extends ServerResponse {

    static JsonServerResponse fromJson(JSONObject json) {
        return new JsonServerResponse(json.getString("id"), json.getString("rev"), json.getBoolean("ok"));
    }

    JsonServerResponse(String id, String rev, boolean ok) {
        this.id = id;
        this.rev = rev;
        this.ok = ok;
    }

    private final String id;
    private final String rev;
    private final boolean ok;

    public String getId() {
        return id;
    }

    public String getRev() {
        return rev;
    }

    public boolean isOk() {
        return ok;
    }

}
