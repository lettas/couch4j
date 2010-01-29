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
package org.couch4j;

import java.util.List;

/**
 * Main entry point for clients...
 * <p>
 * 
 * <pre>
 * CouchDbClient couch = new DefaultCouchDbClient(&quot;http://abc.example.com&quot;);
 * Database db1 = couch.getDatabase(&quot;dbname&quot;);
 * </pre>
 * 
 * In Spring an approriate CouchDb implementation can be created like this:
 * 
 * <pre>
 * &lt;bean id="couchDb"
 *       class="org.couch4j.http.DefaultCouchDbClient" />
 * </pre>
 * 
 * Or
 * 
 * <pre>
 * &lt;bean id="couchDb"
 *       class="org.couch4j.http.DefaultCouchDbClient">
 *       &lt;constructor-arg value="http://abc.example.com"/>
 *       &lt;constructor-arg value="5985"/>
 * &lt;/bean>
 * </pre>
 * 
 * @author Stefan Saasen
 */
public interface CouchDbClient {

    /**
     * @return The hostname of the CouchDB server this client is connected to.
     */
    String getRemoteHost();

    /**
     * @return The port of the CouchDB server this client is connected to.
     */
    int getRemotePort();

    /**
     * Returns a {@link Database} instance for the CouchDB database with the
     * name {@code databaseName}.
     * 
     * @param databaseName
     *            The name of the CouchDB database
     * @return Shared {@link Database} instance.
     */
    Database getDatabase(final String databaseName);

    /**
     * @return A List<String> of database names that are available on the
     *         CouchDB server.
     */
    List<String> databaseNames();

    /**
     * Disconnect all {@link Database} instances. Another call to
     * {@link CouchDbClient#getDatabase(String)} will reconnect or initialize
     * the connection pool.
     */
    void disconnect();
}
