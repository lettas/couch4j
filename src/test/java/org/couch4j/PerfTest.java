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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.couch4j.api.CouchDbClient;
import org.couch4j.api.Database;
import org.couch4j.api.Document;
import org.junit.Test;

/**
 * Run on the same machine
 * 
 * <p>
 * Check established connections:
 * 
 * <pre>
 * sudo netstat -tap tcp
 * </pre>
 */
public class PerfTest extends Couch4jBase {

    public PerfTest(CouchDbClient server) {
        super(server);
    }

    @Test(timeout = 10000) // It's quite slow on the CI machine :(
    // ~ 2700 ms on a 3.06 Core 2 Duo
    public void fetchMultipleDocuments() throws Exception {
        Database test = server.getDatabase("couch4j");
        final int UPPER = 1000;
        long start = System.currentTimeMillis();
        for (int i = 0; i <= UPPER; i++) {
            Document d = test.fetchDocument(Couch4jBase.VALID_DOC_ID);
            assertNotNull(d.getRev());
            assertEquals(Couch4jBase.VALID_DOC_ID, d.getId());
        }
        long duration = System.currentTimeMillis() - start;
        System.out.format("Fetching %d documents took %d ms", UPPER, duration);
    }
}
