package com.coravy.couch4j;

/**
 * @author Stefan Saasen <stefan@coravy.com>
 */
public interface Attachment {
    boolean isStub();

    String getContentType();

    long getLength();

    String getName();

    String getContentId();
}
