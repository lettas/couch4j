package com.coravy.couch4j;

import java.util.HashMap;
import java.util.List;
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

    private Map<String, Database> instances = new HashMap<String, Database>();

    public Database getDatabase(final String databaseName) {
        synchronized (instances) {
            if (instances.containsKey(databaseName)) {
                return instances.get(databaseName);
            }
            Database d = new DatabaseImpl(this, databaseName);
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

    public List<String> databaseNames() {
        throw new UnsupportedOperationException(); // FIXME implement
    }

    public void disconnect() {
        for (Map.Entry<String, Database> e : this.instances.entrySet()) {
            e.getValue().disconnect();
        }
        synchronized (instances) {
            instances.clear();
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("http");
        /*
         * if (useSsl) { sb.append("s"); }
         */
        sb.append("://");
        sb.append(getHost());
        sb.append(":");
        sb.append(getPort());
        return sb.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((host == null) ? 0 : host.hashCode());
        result = prime * result + port;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof CouchDB))
            return false;
        CouchDB other = (CouchDB) obj;
        if (host == null) {
            if (other.host != null)
                return false;
        } else if (!host.equals(other.host))
            return false;
        if (port != other.port)
            return false;
        return true;
    }

}
