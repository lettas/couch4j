package com.coravy.couch4j.http;

import net.sf.json.JSONObject;

import com.coravy.core.annotations.Immutable;
import com.coravy.couch4j.Database;
import com.coravy.couch4j.Document;
import com.coravy.couch4j.ViewResultRow;

/**
 * @author Stefan Saasen <stefan@coravy.com>
 */
@Immutable
final class JsonViewResultRow implements ViewResultRow {
    private final String id;
    private final String key;
    private final JSONObject json;
    private final Database database;

    // private final JSONObject json;

    /**
     * @param next
     */
    JsonViewResultRow(JSONObject json, Database database) {
        this.json = json;
        this.key = json.getString("key");
        this.id = json.getString("id");
        this.database = database;
    }

    public String getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public Document getDocument() {
        if (json != null) {
            ResponseDocument d;
            // If the value includes the document (include_docs = true)
            // we create a ResponseDocument instance
            if (json.containsKey("doc")) {
                d = new ResponseDocument(json.getJSONObject("doc"));
            } else {

                // Create a document stub that is able to lazily fetch the
                // document
                // content

                JSONObject value = json.getJSONObject("value");
                final String rev = value.has("_rev") ? value.getString("_rev") : (value.has("rev") ? value
                        .getString("rev") : null);
                d = new ResponseDocument(this.id, rev);
            }
            d.setDatabase(this.database);
            return d;
        }
        return null; // TODO Fetch from database
    }

    public String toJson() {
        return json.toString();
    }

    public JSONObject toJSONObject() {
        return json;
    }

}
