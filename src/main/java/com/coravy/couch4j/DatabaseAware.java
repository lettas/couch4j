package com.coravy.couch4j;

/**
 * 
 * 
 * @author Stefan Saasen
 */
public interface DatabaseAware {
    Database getDatabase();

    void setDatabase(Database d);
}
