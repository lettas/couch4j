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

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CouchDBTest extends Couch4jBase {

    private Database test;

    public CouchDBTest(CouchDB server) {
        super(server);
    }

    @Before
    public void setUp() throws Exception {
        test = server.getDatabase(Couch4jTest.TEST_DATABASE_NAME);
        assertNotNull(test);
    }

    @After
    public void teardown() {
        server.disconnect();
    }

    @Test
    public void testGetDatabaseReturnsSameInstance() throws Exception {
        assertSame(test, server.getDatabase(Couch4jTest.TEST_DATABASE_NAME));
    }

    @Test
    public void testFactoryMethod() throws Exception {
        server = CouchDB.localServerInstance();
        assertEquals(5984, server.getPort());
        assertEquals("localhost", server.getHost());

        CouchDB server2 = new CouchDB();
        assertEquals(server, server2);
    }

    @Test
    public void testFactoryMethodHost() throws Exception {
        server = CouchDB.serverInstance("localhost");
        assertEquals(5984, server.getPort());
    }

    @Test
    public void testFactoryMethodsHostPort() throws Exception {
        server = CouchDB.serverInstance("localhost", 1234);
    }

}
