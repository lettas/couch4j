package com.coravy.couch4j;

import java.util.HashMap;
import java.util.Map;

import com.coravy.core.annotations.ThreadSafe;
import com.coravy.couch4j.http.DatabaseImpl;

/**
 * The CouchDB database server.
 * 
 * @author Stefan Saasen
 */
@ThreadSafe
public class CouchDB {

    private final Object lock = new Object();

    private Map<String, Database<Document>> instances = new HashMap<String, Database<Document>>();

    public Database<Document> getDatabase(final String databaseName) {
        synchronized (lock) {
            if (instances.containsKey(databaseName)) {
                return instances.get(databaseName);
            }
            Database<Document> d = new DatabaseImpl(this, databaseName);
            instances.put(databaseName, d);
            return d;
        }
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

    public void disconnect() {
        for (Map.Entry<String, Database<Document>> e : this.instances.entrySet()) {
            e.getValue().disconnect();
        }
        instances = new HashMap<String, Database<Document>>();
    }

}
