package com.coravy.couch4j.http;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

import com.coravy.couch4j.Attachment;
import com.coravy.couch4j.Database;
import com.coravy.couch4j.Document;
import com.coravy.couch4j.JsonExportable;

/**
 * @author Stefan Saasen (stefan@coravy.com)
 */
public class ResponseDocument extends Document implements JsonExportable {
    private String _id;
    private String _rev;

    private final JSONObject attachments;
    private String json;
    private final JSONObject jsonObject;

    private Database database;
    
    public ResponseDocument() {
        this.json = "";
        jsonObject = new JSONObject();
        this.attachments = new JSONObject();
    }

    public ResponseDocument(final String json) {
        this(JSONObject.fromObject(json));
        this.json = json;
    }

    public ResponseDocument(final JSONObject jsonObject) {
        this.json = "";
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
        return json;
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
        for (Iterator<? extends Object> iterator = attributes.keySet().iterator(); iterator.hasNext();) {
            Object key = iterator.next();
            jsonObject.put(key.toString(), attributes.get(key));
        }
    }

    public String toJson() {
        if (null != jsonObject) {
            return jsonObject.toString();
        }
        if (StringUtils.isNotBlank(json)) {
            return json;
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

    public Database getDatabase() {
        return database;
    }

    public void setDatabase(Database d) {
        this.database = d;
    }
}
