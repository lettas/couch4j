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
import org.couch4j.Attachment;
import org.couch4j.Database;
import org.couch4j.Document;
import org.couch4j.Database.StreamContext;


/**
 * @author Stefan Saasen
 */
class AttachmentImpl implements Attachment {

    private final boolean stub;
    private final String contentType;
    private final long length;

    private final String name;
    private final Document doc;

    private Database database;

    AttachmentImpl(JSONObject json, String name, Document doc) {
        this.doc = doc;
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
    public String getContentId() {
        return this.doc.getId();
    }

    public void retrieve(StreamContext sc) throws IOException {
        database.withAttachmentAsStream(this, sc);
    }
}
