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

import static org.junit.Assert.assertTrue;

import java.util.LinkedHashMap;
import java.util.Map;

import org.couch4j.Couch4jBase;
import org.couch4j.CouchDbClient;
import org.junit.Before;
import org.junit.Test;

public class UrlBuilderTest extends Couch4jBase {

    private UrlBuilder resolver;

    public UrlBuilderTest(CouchDbClient server) {
        super(server);
    }

    @Before
    public void setUp() throws Exception {
        resolver = new UrlBuilder(server, Couch4jBase.TEST_DATABASE_NAME);
    }

    @Test
    public final void testBaseUrl() {
        assertTrue(resolver.baseUrl().endsWith(Couch4jBase.TEST_DATABASE_NAME));
    }

    @Test
    public final void testUrlForPathString() {
        assertTrue(resolver.urlForPath("test").endsWith(Couch4jBase.TEST_DATABASE_NAME + "/test"));
    }

    @Test
    public final void testUrlForPathStringMapOfStringString() {
        Map<String, String> params = new LinkedHashMap<String, String>();
        params.put("p1", "v1");
        params.put("p2", "v2");
        params.put("p3", "v3");
        String url = resolver.urlForPath("test", params);
        assertTrue(url.endsWith(Couch4jBase.TEST_DATABASE_NAME + "/test?p1=v1&p2=v2&p3=v3"));
    }

}
