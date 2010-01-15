package com.coravy.couch4j;

/**
 * @author Stefan Saasen <stefan@coravy.com>
 */
public interface ViewResultRow extends JsonExportable {

    String getId();

    String getKey();

    Document getDocument();
}
