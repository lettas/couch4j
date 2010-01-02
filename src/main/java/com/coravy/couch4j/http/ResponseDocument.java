package com.coravy.couch4j.http;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import com.coravy.couch4j.Attachment;
import com.coravy.couch4j.Database;
import com.coravy.couch4j.Document;

/**
 * @author Stefan Saasen (stefan@coravy.com)
 */
public class ResponseDocument extends Document implements DatabaseAware<Document> {
    private String _id;
    private String _rev;

    private final JSONObject attachments;
    private final JSONObject jsonObject;

    private Database<Document> database;

    public ResponseDocument() {
        jsonObject = new JSONObject();
        this.attachments = new JSONObject();
    }

    public ResponseDocument(final String json) {
        this(JSONObject.fromObject(json));
    }

    public ResponseDocument(final JSONObject jsonObject) {
        this.jsonObject = jsonObject;
        this._id = jsonObject.getString("_id");
        this._rev = jsonObject.getString("_rev");
        if (jsonObject.has("_attachments")) {
            attachments = jsonObject.getJSONObject("_attachments");
        } else {
            attachments = null;
        }
    }

    @Override
    public String getId() {
        return _id;
    }

    @Override
    public String getRev() {
        return _rev;
    }

    @Override
    public String toString() {
        return toJson();
    }

    @Override
    public Object get(String key) {
        return jsonObject.get(key);
    }

    @Override
    public void put(Object key, Object value) {
        jsonObject.put(key.toString(), value);
    }

    @Override
    public void putAll(final Map<? extends Object, ? extends Object> attributes) {
        if(null == attributes) {
            return;
        }
        for(Map.Entry<? extends Object, ? extends Object> e : attributes.entrySet()) {
            jsonObject.put(e.getKey().toString(), e.getValue());
        }
    }

    public String toJson() {
        if (null != jsonObject) {
            return jsonObject.toString();
        }
        return "";
    }

    public Attachment getAttachment(final String name) {
        if (null != attachments && !attachments.isEmpty() && !attachments.isNullObject()) {
            try {
                return new AttachmentImpl(attachments.getJSONObject(name), name, this);
            } catch (JSONException jse) {
                return null; // ignore - just return null ??? WHY
            }
        }
        return null;
    }

    public List<Attachment> getAttachments() {
        return Collections.emptyList();
    }

    public List<String> getAttachmentNames() {
        return Collections.emptyList();
    }

    public Database<Document> getDatabase() {
        return database;
    }

    public void setDatabase(Database<Document> d) {
        this.database = d;
    }
}
