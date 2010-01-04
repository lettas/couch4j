package com.coravy.couch4j.api;


/**
 * @author Stefan Saasen <stefan@coravy.com>
 */
public interface ViewResultRow<T> extends JsonExportable {

    String getId();

    String getKey();

    T getDocument();
}
