package com.coravy.couch4j.http;

import net.sf.json.JSONObject;

import com.coravy.core.annotations.Immutable;
import com.coravy.couch4j.Document;
import com.coravy.couch4j.ViewResultRow;

/**
 * @author Stefan Saasen <stefan@coravy.com>
 */
@Immutable
final class JsonViewResultRow implements ViewResultRow {
    private String id;
    private String key;
    private JSONObject json;

    // private final JSONObject json;

    /**
     * @param next
     */
    JsonViewResultRow(JSONObject json) {
        this.json = json;
        this.key = json.getString("key");
        this.id = json.getString("id");
    }

    public String getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public Document getDocument() {
        if (json != null) {
            return new ResponseDocument(json.getJSONObject("value"));
        }
        return null; // TODO Fetch from database
    }

    public String toJson() {
        return json.toString();
    }

}
