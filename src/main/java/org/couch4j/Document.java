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
package org.couch4j;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

/*
 * TODO:
 * ''_rev_infos''
 *      Similar to _revisions, but more details about the history and the availability of ancient versions of the document
 * ''_conflicts''
 *      Information about conflicts
 * ''_deleted_conflicts''
 *  Information about conflicts
 */

/**
 * Represents a CouchDB document.
 * <p>
 * This class uses the {@link Object#equals(Object)} and
 * {@link Object#hashCode()} implementations! Calling methods that
 * <strong>return</strong> a Document will most likely return a
 * <strong>subtype</strong> of Document for which the equals/hashCode contract
 * would be violated.
 * 
 * @couchdbApi http://wiki.apache.org/couchdb/HTTP_Document_API
 * @author Stefan Saasen
 */
public class Document implements JsonExportable {

    private final Map<? super Object, ? super Object> attributes;

    public Document() {
        this.attributes = new HashMap<Object, Object>();
    }

    public Document(String id) {
        this();
        this.attributes.put("_id", id);
    }

    public Document(Map<? extends Object, ? extends Object> attributes) {
        this.attributes = new HashMap<Object, Object>();
        this.attributes.putAll(attributes);
    }

    public String getId() {
        if (!attributes.containsKey("_id")) {
            return null;
        }
        return attributes.get("_id").toString();
    }

    public String getRev() {
        return null; // This class always returns null
    }

    /**
     * Indicates that this document has been deleted and will be removed on next
     * compaction run.
     * 
     * @return true if this document will be deleted in next compaction run,
     *         false otherwise.
     */
    // public boolean isDeleted() {
    // throw new UnsupportedOperationException("Implement!");
    // }

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

    /**
     * If the document was requested with ?revs=true this field will hold a
     * simple list of the documents history
     * 
     * @return List of revisions or an empty list.
     */
    public List<String> getRevisions() {
        return Collections.emptyList(); // This class only returns an empty list
    }

    public Attachment getAttachment(final String name) {
        throw new UnsupportedOperationException("Implement!");
    }

    public List<Attachment> getAttachments() {
        throw new UnsupportedOperationException("Implement!");
    }

    public List<String> getAttachmentNames() {
        throw new UnsupportedOperationException("Implement!");
    }

    public String toJson() {
        return toJSONObject().toString();
    }

    public JSONObject toJSONObject() {
        return JSONObject.fromObject(this.attributes);
    }
}
