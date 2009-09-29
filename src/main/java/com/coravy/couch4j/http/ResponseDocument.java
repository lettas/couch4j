package com.coravy.couch4j.http;

import java.util.Iterator;
import java.util.Map;

import net.sf.json.JSONObject;

import com.coravy.couch4j.Document;

/**
 * @author Stefan Saasen (stefan@coravy.com)
 */
public class ResponseDocument extends Document {
    private String _id;
    private String _rev;

    private final String json;
    private final JSONObject jsonObject;

    public ResponseDocument() {
        this.json = "";
        jsonObject = new JSONObject();
    }

    public ResponseDocument(final String json) {
        this.json = json;
        jsonObject = JSONObject.fromObject(json);
        this._id = jsonObject.getString("_id");
        this._rev = jsonObject.getString("_id");
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
        for (Iterator iterator = attributes.keySet().iterator(); iterator
                .hasNext();) {
            Object key = iterator.next();
            jsonObject.put(key.toString(), attributes.get(key));
        }
    }

}
