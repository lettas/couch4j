package com.coravy.couch4j.http;

import java.util.Map;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import com.coravy.couch4j.Document;
import com.coravy.lib.core.annotations.Immutable;

/**
 * @author Stefan Saasen <stefan@coravy.com>
 */
@Immutable
final class JsonViewResultRow implements com.coravy.couch4j.ViewResultRow {
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
        return ReflectionToStringBuilder.toString(this);
    }

}
