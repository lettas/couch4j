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

import static org.couch4j.util.CollectionUtils.map;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.codec.binary.Base64;
import org.couch4j.exceptions.Couch4JException;
import org.couch4j.util.StreamUtils;

import eu.medsea.mimeutil.MimeUtil2;

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
 * {@link Object#hashCode()} implementations!
 * <p>
 * Calling methods that <strong>return</strong> a Document will most likely
 * return a <strong>subtype</strong> of Document for which the equals/hashCode
 * contract would be violated.
 * 
 * @couchdbApi http://wiki.apache.org/couchdb/HTTP_Document_API
 * @author Stefan Saasen
 */
// TODO cleanup this mess ;-)
public class Document implements JsonExportable {

    // TODO Move to database or CouchDbClient...
    private final static ThreadLocal<MimeUtil2> MIME_UTIL = new ThreadLocal<MimeUtil2>() {
        @Override
        protected MimeUtil2 initialValue() {
            MimeUtil2 mimeUtil = new MimeUtil2();
            mimeUtil.registerMimeDetector("eu.medsea.mimeutil.detector.MagicMimeMimeDetector");
            return mimeUtil;
        }
    };

    private final Map<String, Map<String, String>> attachments = new HashMap<String, Map<String, String>>();
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

    public Collection<Attachment> getAttachments() {
        throw new UnsupportedOperationException("Implement!");
    }

    public Collection<String> getAttachmentNames() {
        return Collections.unmodifiableCollection(this.attachments.keySet());
    }

    public String toJson() {
        return toJSONObject().toString();
    }

    public JSONObject toJSONObject() {
        if (attachments.size() > 0) {
            attributes.put("_attachments", this.attachments);
        }
        return JSONObject.fromObject(this.attributes);
    }

    /**
     * Add an attachment to the document using the name and the given
     * {@link InputStream}.
     * <p>
     * This method should only be used for "small-ish" attachments.
     * <p>
     * For larger attachments use the
     * {@link Database#saveAttachment(Document, String, InputStream)}.
     * 
     * @param name
     *            The name for the attachment.
     * @param content
     *            InputStream
     */
    public void addAttachment(String name, InputStream content) {
        try {
            byte[] binaryData = StreamUtils.toByteArray(content);
            attachments.put(name, map("content_type", MimeUtil2.getMostSpecificMimeType(
                    MIME_UTIL.get().getMimeTypes(binaryData)).toString(), "data", new String(Base64
                    .encodeBase64(binaryData))));
        } catch (IOException e) {
            throw new Couch4JException(e);
        }
    }
}
