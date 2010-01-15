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
package com.coravy.couch4j.http;

import net.sf.json.JSONObject;

import com.coravy.core.annotations.Immutable;
import com.coravy.couch4j.Database;
import com.coravy.couch4j.Document;
import com.coravy.couch4j.ViewResultRow;

/**
 * @author Stefan Saasen
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
