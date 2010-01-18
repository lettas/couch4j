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
import java.util.logging.Logger;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.params.HttpClientParams;

import com.coravy.core.annotations.ThreadSafe;
import com.coravy.couch4j.http.DatabaseImpl;

/**
 * Main entry point for clients...
 * <p>
 * 
 * <pre>
 * CouchDbClient couch = new CouchDbClient(&quot;http://abc.example.com&quot;);
 * Database db1 = couch.getDatabase(&quot;dbname&quot;);
 * </pre>
 * 
 * In Spring an approriate CouchDb implementation can be created like this:
 * 
 * <pre>
 * &lt;bean id="couchDb"
 *       class="com.coravy.couch4j.CouchDbClient" />
 * </pre>
 * 
 * Or
 * 
 * <pre>
 * &lt;bean id="couchDb"
 *       class="com.coravy.couch4j.CouchDbClient">
 *       &lt;constructor-arg value="http://abc.example.com"/>
 *       &lt;constructor-arg value="5985"/>
 * &lt;/bean>
 * </pre>
 * 
 * @author Stefan Saasen
 */
@ThreadSafe
public final class CouchDbClient {
    final static String DEFAULT_HOST = "localhost";
    final static int DEFAULT_PORT = 5984;

    private final static Logger logger = Logger.getLogger(CouchDbClient.class.getName());
    private static final int MAX_HOST_CONNECTIONS = 10;
    private HttpClient client;

    private final String host;
    private final int port;

    // public CouchDbClient(URL url) {
    // }

    public CouchDbClient(final String host) {
        this(host, DEFAULT_PORT);
    }

    public CouchDbClient() {
        this(DEFAULT_HOST, DEFAULT_PORT);
    }

    public CouchDbClient(final String host, final int port) {
        this.host = host;
        this.port = port;
        this.client = createHttpClient();
    }

    private HttpClient createHttpClient() {
        HttpClientParams params = new HttpClientParams();
        params.setConnectionManagerClass(org.apache.commons.httpclient.MultiThreadedHttpConnectionManager.class);
        params.setIntParameter("maxHostConnections", MAX_HOST_CONNECTIONS);

        logger.info("Creating new database instance. Please reuse this object for the same CouchDB database.");

        return new HttpClient(params);
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
                return instances.get(databaseName);
            }
        }

        Database d = new DatabaseImpl(this, client, databaseName);
        synchronized (instances) {
            instances.put(databaseName, d);
        }
        return d;
    }

    public void disconnect(Database d) {
        synchronized (instances) {
            if (instances.containsKey(d.getName())) {
                instances.remove(d.getName());
            }
            if (instances.size() < 1) {
                ((MultiThreadedHttpConnectionManager) client.getHttpConnectionManager()).shutdown();
                client = createHttpClient();
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
        // synchronized (instances) {
        // instances.clear();
        // }
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
