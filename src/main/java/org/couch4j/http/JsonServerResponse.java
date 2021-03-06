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

import org.couch4j.api.ServerResponse;

import net.sf.json.JSONObject;


/**
 * @author Stefan Saasen
 */
final class JsonServerResponse implements ServerResponse {

    static JsonServerResponse fromJson(JSONObject json) {
        return new JsonServerResponse(json);
    }

    JsonServerResponse(JSONObject json) {
        this.id = json.containsKey("id") ? json.getString("id") : null;
        this.rev = json.containsKey("rev") ? json.getString("rev") : null;
        this.ok = json.getBoolean("ok");
        this.json = json;
    }

    private final String id;
    private final String rev;
    private final boolean ok;
    private final JSONObject json;

    public String getId() {
        return id;
    }

    public String getRev() {
        return rev;
    }

    public boolean isOk() {
        return ok;
    }

    public String toJson() {
        return json.toString();
    }

    public JSONObject toJSONObject() {
        return json;
    }

}
