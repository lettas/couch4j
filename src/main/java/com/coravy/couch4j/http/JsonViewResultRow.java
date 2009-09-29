package com.coravy.couch4j.http;

import java.util.Map;

import com.coravy.core.annotations.Immutable;
import com.coravy.couch4j.Document;
import com.coravy.couch4j.ViewResultRow;
import com.google.gson.Gson;

/**
 * @author Stefan Saasen <stefan@coravy.com>
 */
@Immutable
final class JsonViewResultRow implements ViewResultRow {
    private String id;
    private String key;
    private Map<String, Object> doc;

    // private final JSONObject json;

    /**
     * @param next
     */
    JsonViewResultRow() {
        // this.json = json;
        // this.key = json.getString("key");
        // this.id = json.getString("id");
    }

    public String getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public Document getDocument() {
        if (doc != null) {
            return new Document(doc);
        }
        return null; // TODO Fetch from database
    }

    @Override
    public String toString() {
        return new Gson().toJson(this); // TODO replace with json attribute
    }

}
