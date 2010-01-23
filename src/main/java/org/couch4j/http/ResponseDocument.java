/*
 * The MIT License
 *
 * Copyright (c) 2009, 2010 Stefan Saasen
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.couch4j.http;

import static org.couch4j.util.CollectionUtils.map;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.apache.commons.codec.binary.Base64;
import org.couch4j.Attachment;
import org.couch4j.Database;
import org.couch4j.Document;
import org.couch4j.exceptions.Couch4JException;
import org.couch4j.util.StreamUtils;

import eu.medsea.mimeutil.MimeUtil2;


/**
 * @author Stefan Saasen
 */
class ResponseDocument extends Document implements DatabaseAware {
    private final static ThreadLocal<MimeUtil2> MIME_UTIL = new ThreadLocal<MimeUtil2>() {
        @Override
        protected MimeUtil2 initialValue() {
            MimeUtil2 mimeUtil = new MimeUtil2();
            mimeUtil.registerMimeDetector("eu.medsea.mimeutil.detector.MagicMimeMimeDetector");
            return mimeUtil;
        }
    };
    
    private final String _id;
    private final String _rev;
    private boolean isAvailable;

    private JSONObject attachments;
    private JSONObject jsonObject;

    private Database database;

    public ResponseDocument(final String id) {
        if (null == id) {
            throw new IllegalArgumentException("'id' and 'rev' are required to create a ResponseDocument instance.");
        }
        this._id = id;
        this._rev = null;
    }
    
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
    
    
    
    // TODO cache the attachment instances...

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

    @SuppressWarnings("unchecked")
    public Collection<Attachment> getAttachments() {
        fetchDocument();
        if (null == attachments || attachments.isEmpty()) {
            return Collections.emptyList();
        }
        ArrayList<Attachment> _attachments = new ArrayList<Attachment>(this.attachments.size());
        for (Iterator iterator = this.attachments.keys(); iterator.hasNext();) {
            String name = (String) iterator.next();
            _attachments.add(new AttachmentImpl(this.attachments.getJSONObject(name), name, this));
        }
        return _attachments;
    }
    
    @Override
    public void addAttachment(String name, InputStream content) {
        if(null != attachments) {
            try {
                byte[] binaryData = StreamUtils.toByteArray(content);
                attachments.put(name, JSONObject.fromObject(map("content_type", MimeUtil2.getMostSpecificMimeType(
                        MIME_UTIL.get().getMimeTypes(binaryData)).toString(), "data", new String(Base64
                        .encodeBase64(binaryData)))));
            } catch (IOException e) {
                throw new Couch4JException(e);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public Collection<String> getAttachmentNames() {
        fetchDocument();
        if (null == attachments || attachments.isEmpty()) {
            return Collections.emptyList();
        }
        ArrayList<String> _attachments = new ArrayList<String>(this.attachments.size());
        for (Iterator iterator = this.attachments.keys(); iterator.hasNext();) {
            String name = (String) iterator.next();
            _attachments.add(name);
        }
        return _attachments;
    }

    public Database getDatabase() {
        return database;
    }

    public void setDatabase(Database d) {
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
