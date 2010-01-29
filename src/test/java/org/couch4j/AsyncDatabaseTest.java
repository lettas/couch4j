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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import net.sf.json.JSONArray;

import org.couch4j.AsyncDatabase.AsyncToken;
import org.couch4j.AsyncDatabase.ResponseHandler;
import org.couch4j.exceptions.DocumentNotFoundException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class AsyncDatabaseTest extends Couch4jBase {

    private static final long TIMEOUT = 1000;

    private Database test;
    private Database testEmpty;

    private static class TestResultHandler implements ResponseHandler<ViewResult> {
        private ViewResult response;
        private Exception error;
        private AsyncToken token;

        @Override
        public void failed(Exception e) {
            this.error = e;
            synchronized (this) {
                notifyAll();
            }
        }

        @Override
        public void completed(ViewResult response, AsyncToken token) {
            this.response = response;
            this.token = token;
            synchronized (this) {
                notifyAll();
            }
        }

        public ViewResult getResponse() {
            return response;
        }

        @SuppressWarnings("unused")
        public Exception getError() {
            return error;
        }

        @SuppressWarnings("unused")
        public AsyncToken getToken() {
            return token;
        }
    }

    public AsyncDatabaseTest(CouchDbClient server) {
        super(server);
    }

    @Before
    public void setUp() throws Exception {
        test = server.getDatabase(TEST_DATABASE_NAME);
        assertNotNull(test);

        testEmpty = server.getDatabase(EMPTY_DATABASE_NAME);
        assertNotNull(testEmpty);
    }

    @After
    public void teardown() {
        try {
            Document d = test.fetchDocument(NEW_DOCUMENT_ID);
            test.deleteDocument(d);
        } catch (DocumentNotFoundException nfe) {
            // ignore
        }
        server.disconnect();
    }

    @Test
    public void testFetchAllDocuments() throws Exception {
        ViewResult rows = test.fetchAllDocuments();
        assertTrue(rows.getTotalRows() > 5);

        TestResultHandler testHandler = new TestResultHandler();
        test.fetchAllDocuments(testHandler);

        synchronized (testHandler) {
            testHandler.wait(TIMEOUT);
        }

        assertTrue(testHandler.getResponse().getTotalRows() > 5);
    }

    private void assertDocumentTest1(Document d) {
        assertEquals(VALID_DOC_ID, d.getId());

        JSONArray ary = (JSONArray) d.get("a");
        assertThat(1, is(ary.get(0)));
        assertThat(2, is(ary.get(1)));
        assertThat(3, is(ary.get(2)));
        assertEquals("test", d.get("b"));
    }

}
