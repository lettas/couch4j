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

import com.coravy.couch4j.http.CouchDbImpl;

/**
 * Main entry point for clients...
 * <p>
 * 
 * <pre>
 * CouchDb couch = CouchDbClient.newInstance(&quot;http://abc.example.com&quot;);
 * Database db1 = couch.getDatabase(&quot;dbname&quot;);
 * </pre>
 * 
 * In Spring an approriate CouchDb implementation can be created like this:
 * 
 * <pre>
 * &lt;bean id="couchDb"
 *       class="com.coravy.couch4j.CouchDbClient"
 *       factory-method="newLocalInstance" />
 * </pre>
 * 
 * Or
 * 
 * <pre>
 * &lt;bean id="couchDb"
 *       class="com.coravy.couch4j.CouchDbClient"
 *       factory-method="newInstance">
 *       &lt;constructor-arg value="http://abc.example.com"/>
 *       &lt;constructor-arg value="5985"/>
 * &lt;/bean>
 * </pre>
 * 
 * @author Stefan Saasen
 */
public class CouchDbClient {
    final static String DEFAULT_HOST = "localhost";
    final static int DEFAULT_PORT = 5984;

    public static CouchDb newLocalInstance() {
        return newInstance(DEFAULT_HOST, DEFAULT_PORT);
    }

    public static CouchDb newInstance(final String host) {
        return newInstance(host, DEFAULT_PORT);
    }

    public static CouchDb newInstance(final String host, int port) {
        return new CouchDbImpl(host, port);
    }
}