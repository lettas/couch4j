package com.coravy.couch4j.http;

import java.io.IOException;

import net.sf.json.JSONObject;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import com.coravy.couch4j.Attachment;
import com.coravy.couch4j.Database;
import com.coravy.couch4j.DatabaseAware;
import com.coravy.couch4j.Document;
import com.coravy.couch4j.Database.StreamContext;

/**
 * @author Stefan Saasen <stefan@coravy.com>
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
        if(doc instanceof DatabaseAware) {
            database = ((DatabaseAware)doc).getDatabase();
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

    public void withAttachmentAsStream(StreamContext sc) throws IOException {
        database.withAttachmentAsStream(this, sc);
    }
}
