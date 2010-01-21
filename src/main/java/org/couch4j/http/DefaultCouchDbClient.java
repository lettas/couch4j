/*
 * The MIT License
 *
 * Copyright (c) 2009, 2010 Stefan Saasen
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.couch4j.http;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.couch4j.CouchDbClient;
import org.couch4j.Database;
import org.couch4j.annotations.ThreadSafe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main entry point for clients...
 * 
 * @author Stefan Saasen
 */
@ThreadSafe
public final class DefaultCouchDbClient implements CouchDbClient {

    private final static Logger logger = LoggerFactory.getLogger(Database.class);

    final static String DEFAULT_HOST = "localhost";
    final static int DEFAULT_PORT = 5984;

    private HttpConnectionManager connections;

    private final String host;
    private final int port;

    // public CouchDbClient(URL url) {
    // }

    public DefaultCouchDbClient(final String host) {
        this(host, DEFAULT_PORT);
    }

    public DefaultCouchDbClient() {
        this(DEFAULT_HOST, DEFAULT_PORT);
    }

    public DefaultCouchDbClient(final String host, final int port) {
        this.host = host;
        this.port = port;
        connections = new HttpConnectionManager();
    }

    public String getRemoteHost() {
        return host;
    }

    public int getRemotePort() {
        return port;
    }

    private Map<String, Database> instances = new HashMap<String, Database>();

    public Database getDatabase(final String databaseName) {
        synchronized (instances) {
            if (instances.containsKey(databaseName)) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Reuse database instance for {}", databaseName);
                }
                return instances.get(databaseName);
            }
        }

        Database d = new DatabaseImpl(this, connections, databaseName);
        if (logger.isDebugEnabled()) {
            logger.debug("Create new database instance for {}", databaseName);
        }
        synchronized (instances) {
            instances.put(databaseName, d);
        }
        return d;
    }

    void disconnect(Database d) {
        synchronized (instances) {
            if (instances.containsKey(d.getName())) {
                instances.remove(d.getName());
            }
            if (instances.size() < 1) {
                connections.shutdown();
            }
        }
    }

    public List<String> databaseNames() {
        throw new UnsupportedOperationException(); // FIXME implement
    }

    public void disconnect() {
        // for (Map.Entry<String, Database> e : this.instances.entrySet()) {
        // e.getValue().disconnect();
        // }
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
        sb.append(getRemoteHost());
        sb.append(":");
        sb.append(getRemotePort());
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
        if (!(obj instanceof CouchDbClient))
            return false;
        CouchDbClient other = (CouchDbClient) obj;
        if (host == null) {
            if (other.getRemoteHost() != null)
                return false;
        } else if (!host.equals(other.getRemoteHost()))
            return false;
        if (port != other.getRemotePort())
            return false;
        return true;
    }
}
