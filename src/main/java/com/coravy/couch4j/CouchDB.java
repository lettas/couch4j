package com.coravy.couch4j;

import com.coravy.couch4j.http.DatabaseImpl;

/**
 * The CouchDB database server.
 *  
 * @author Stefan Saasen
 */
public class CouchDB {

    public Database getDatabase(final String databaseName) {
        return new DatabaseImpl(this, databaseName);
    }

    private final static String DEFAULT_HOST = "localhost";
    private final static int DEFAULT_PORT = 5984;

    public static CouchDB localServerInstance() {
        return serverInstance(DEFAULT_HOST, DEFAULT_PORT);
    }

    public static CouchDB serverInstance(final String host, int port) {
        return new CouchDB(host, port);
    }

    public static CouchDB serverInstance(final String host) {
        return serverInstance(host, DEFAULT_PORT);
    }

    private final String host;
    private final int port;

    public CouchDB() {
        this(DEFAULT_HOST, DEFAULT_PORT);
    }
    
    public CouchDB(final String host, final int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

}
