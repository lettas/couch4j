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

import java.io.IOException;

import net.sf.json.JSONObject;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.couch4j.api.Attachment;
import org.couch4j.api.Database;
import org.couch4j.api.Document;
import org.couch4j.api.Database.StreamContext;

/**
 * @author Stefan Saasen
 */
class AttachmentImpl implements Attachment {

    /*
     * From http://wiki.apache.org/couchdb/HTTP_Document_API:
     * When you update the document you must include the attachment stubs or 
     * CouchDB will delete the attachment.
     */
//    boolean isStub();
    
    private final boolean stub;
    private final String contentType;
    private final long length;

    private final String name;
    private final String docId;

    private Database database;

    AttachmentImpl(JSONObject json, String name, Document doc) {
        this.docId = doc.getId();
        this.name = name;
        stub = json.getBoolean("stub");
        contentType = json.getString("content_type");
        length = json.getLong("length");
        if (doc instanceof DatabaseAware) {
            database = ((DatabaseAware) doc).getDatabase();
        }
    }

    /**
     * @return the stub
     */
    public boolean isStub() {
        return stub;
    }

    /**
     * @return the contentType
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * @return the length
     */
    public long getLength() {
        return length;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }

    /**
     * @return the contentId
     */
    @Override
    public String getContentId() {
        return docId;
    }

    
    @Override
    public String getDocumentId() {
        return docId;
    }
    
    public void retrieve(StreamContext sc) throws IOException {
        // FIXME
        if(database instanceof DatabaseImpl) {
            ((DatabaseImpl)database).withAttachmentAsStream(this, sc);
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((contentType == null) ? 0 : contentType.hashCode());
        result = prime * result + ((docId == null) ? 0 : docId.hashCode());
        result = prime * result + (int) (length ^ (length >>> 32));
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + (stub ? 1231 : 1237);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof AttachmentImpl)) {
            return false;
        }
        AttachmentImpl other = (AttachmentImpl) obj;
        if (contentType == null) {
            if (other.contentType != null) {
                return false;
            }
        } else if (!contentType.equals(other.contentType)) {
            return false;
        }
        if (docId == null) {
            if (other.docId != null) {
                return false;
            }
        } else if (!docId.equals(other.docId)) {
            return false;
        }
        if (length != other.length) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (stub != other.stub) {
            return false;
        }
        return true;
    }

}
