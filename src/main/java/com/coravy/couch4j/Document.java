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
package com.coravy.couch4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

/**
 * @author Stefan Saasen
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((attributes == null) ? 0 : attributes.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof Document))
            return false;
        Document other = (Document) obj;
        if (attributes == null) {
            if (other.attributes != null)
                return false;
        } else if (!attributes.equals(other.attributes))
            return false;
        return true;
    }
}
