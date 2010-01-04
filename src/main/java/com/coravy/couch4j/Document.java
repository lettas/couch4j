package com.coravy.couch4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import com.coravy.couch4j.api.Attachment;
import com.coravy.couch4j.api.JsonExportable;

/**
 * @author Stefan Saasen (stefan@coravy.com)
 */
public class Document implements JsonExportable {

    private final Map attributes;

    public Document() {
        this.attributes = new HashMap<Object, Object>();
    }

    public Document(String id) {
        this();
        this.attributes.put("_id", id);
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

    public void putAll(Map<? extends Object, ? extends Object> attributes) {
        this.attributes.putAll(attributes);
    }

    public Object get(String key) {
        return this.attributes.get(key);
    }

    public Map<? extends Object, ? extends Object> getAttributes() {
        return attributes;
    }

    public Attachment getAttachment(final String name) {
        throw new UnsupportedOperationException("");
    }

    public List<Attachment> getAttachments() {
        throw new UnsupportedOperationException("");
    }

    public List<String> getAttachmentNames() {
        throw new UnsupportedOperationException("");
    }

    public String toJson() {
        return toJSONObject().toString();
    }

    public JSONObject toJSONObject() {
        return JSONObject.fromObject(this.attributes);
    }
}
