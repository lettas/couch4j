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
    private final String _id;
    private final String _rev;
    private boolean isAvailable;

    private JSONObject attachments;
    private JSONObject jsonObject;

    private Database<Document> database;

    public ResponseDocument(final String id, final String rev) {
        if (null == id || null == rev) {
            throw new IllegalArgumentException("'id' and 'rev' are required to create a ResponseDocument instance.");
        }
        this._id = id;
        this._rev = rev;
    }

    public ResponseDocument(final JSONObject jsonObject) {
        this(jsonObject.getString("_id"), jsonObject.getString("_rev"));
        load(jsonObject);
    }

    private void load(final JSONObject jsonObject) {
        this.jsonObject = jsonObject;
        if (jsonObject.has("_attachments")) {
            attachments = jsonObject.getJSONObject("_attachments");
        } else {
            attachments = null;
        }
        isAvailable = true;
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
        fetchDocument();
        return toJson();
    }

    @Override
    public Object get(String key) {
        fetchDocument();
        return jsonObject.get(key);
    }

    @Override
    public void put(Object key, Object value) {
        fetchDocument();
        jsonObject.put(key.toString(), value);
    }

    @Override
    public void putAll(final Map<? extends Object, ? extends Object> attributes) {
        fetchDocument();
        if (null == attributes) {
            return;
        }
        for (Map.Entry<? extends Object, ? extends Object> e : attributes.entrySet()) {
            jsonObject.put(e.getKey().toString(), e.getValue());
        }
    }

    public String toJson() {
        fetchDocument();
        if (null != jsonObject) {
            return jsonObject.toString();
        }
        return "";
    }

    public Attachment getAttachment(final String name) {
        fetchDocument();
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
        fetchDocument();
        return Collections.emptyList();
    }

    public List<String> getAttachmentNames() {
        fetchDocument();
        return Collections.emptyList();
    }

    public Database<Document> getDatabase() {
        return database;
    }

    public void setDatabase(Database<Document> d) {
        this.database = d;
    }

    private void fetchDocument() {
        if (isAvailable) {
            return;
        }
        if (null == this.database) {
            throw new IllegalStateException(
                    "Database instance is not available. Unable to lazily load the response document.");
        }

        Document d = database.fetchDocument(this._id);
        this.jsonObject = d.toJSONObject();
        this.isAvailable = true;
    }

    @Override
    public JSONObject toJSONObject() {
        fetchDocument();
        return jsonObject;
    }
    
    
}
