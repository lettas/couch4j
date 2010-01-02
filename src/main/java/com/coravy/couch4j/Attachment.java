package com.coravy.couch4j;

import java.io.IOException;

import com.coravy.couch4j.Database.StreamContext;

/**
 * @author Stefan Saasen
 */
public interface Attachment {
    boolean isStub();

    String getContentType();

    long getLength();

    String getName();

    String getContentId();

    void retrieve(StreamContext sc) throws IOException;
}
