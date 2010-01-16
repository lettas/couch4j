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
public class Server {

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

    private final static String DEFAULT_HOST = "localhost";
    private final static int DEFAULT_PORT = 5984;

    public static Server localServerInstance() {
        return serverInstance(DEFAULT_HOST, DEFAULT_PORT);
    }

    public static Server serverInstance(final String host, int port) {
        return new Server(host, port);
    }

    public static Server serverInstance(final String host) {
        return serverInstance(host, DEFAULT_PORT);
    }

    private final String host;
    private final int port;

    public Server() {
        this(DEFAULT_HOST, DEFAULT_PORT);
    }

    public Server(final String host, final int port) {
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
        if (!(obj instanceof Server))
            return false;
        Server other = (Server) obj;
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