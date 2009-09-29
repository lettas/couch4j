package com.coravy.couch4j;

/**
 * @author Stefan Saasen <stefan@coravy.com>
 */
public interface ViewResultRow {

    String getId();

    String getKey();

    Document getDocument();
}
