package com.coravy.couch4j;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

/**
 * @author Stefan Saasen (stefan@coravy.com)
 */
public class Document {

    private final Map attributes;

    public Document() {
        this.attributes = new HashMap<Object, Object>();
    }

    public Document(Map<? extends Object, ? extends Object> attributes) {
        if (null != attributes) {
            this.attributes = attributes;
        } else {
            this.attributes = new HashMap<Object, Object>();
        }
    }

    public String getId() {
        if (!attributes.containsKey("_id")) {
            return null;
        }
        return attributes.get("_id").toString();
    }

    public String getRev() {
        if (!attributes.containsKey("_rev")) {
            return null;
        }
        return attributes.get("_rev").toString();
    }

    public void put(Object key, Object value) {
        this.attributes.put(key, value);
    }

    public Object get(final String key) {
        return this.attributes.get(key);
    }

    public Map<? extends Object, ? extends Object> getAttributes() {
        return attributes;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this.attributes.toString());
    }

}
