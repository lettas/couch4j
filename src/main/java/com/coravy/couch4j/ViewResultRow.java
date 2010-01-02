package com.coravy.couch4j;

/**
 * @author Stefan Saasen <stefan@coravy.com>
 */
public interface ViewResultRow<T> extends JsonExportable {

    String getId();

    String getKey();

    T getDocument();
}
