package com.coravy.couch4j.http;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.coravy.core.annotations.ThreadSafe;
import com.coravy.couch4j.CouchDb;
import com.coravy.couch4j.Database;

/**
 * 
 * @author Stefan Saasen
 */
@ThreadSafe
public class CouchDbImpl implements CouchDb {

    private final String host;
    private final int port;

    public CouchDbImpl(final String host, final int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    private Map<String, Database> instances = new HashMap<String, Database>();

    public Database getDatabase(final String databaseName) {
        synchronized (instances) {
            if (instances.containsKey(databaseName)) {
                return instances.get(databaseName);
            }
        }

        Database d = new DatabaseImpl(this, databaseName);
        synchronized (instances) {
            instances.put(databaseName, d);
        }
        return d;
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
        if (!(obj instanceof CouchDb))
            return false;
        CouchDb other = (CouchDb) obj;
        if (host == null) {
            if (other.getHost() != null)
                return false;
        } else if (!host.equals(other.getHost()))
            return false;
        if (port != other.getPort())
            return false;
        return true;
    }

}
