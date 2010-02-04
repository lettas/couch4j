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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.sf.json.JSONArray;

import org.couch4j.AsynchronousDatabase.AsyncToken;
import org.couch4j.AsynchronousDatabase.ResponseHandler;
import org.couch4j.exceptions.DocumentNotFoundException;
import org.couch4j.exceptions.DocumentUpdateConflictException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class AsyncDatabaseTest extends Couch4jBase {

    private static final long TIMEOUT = 1000;

    private AsynchronousDatabase test;
    private AsynchronousDatabase testEmpty;

    private static class TestResultHandler<T> implements ResponseHandler<T> {
        private T response;
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
        public void completed(T response, AsyncToken token) {
            this.response = response;
            this.token = token;
            synchronized (this) {
                notifyAll();
            }
        }

        public T getResponse() {
            return response;
        }

        public Exception getError() {
            return error;
        }

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
            Document d = ((Database) test).fetchDocument(NEW_DOCUMENT_ID);
            ((Database) test).deleteDocument(d);
        } catch (DocumentNotFoundException nfe) {
            // ignore
        }
        ((Database)testEmpty).delete();
        server.disconnect();
    }

    @Test
    public void fetchAllDocuments() throws Exception {
        TestResultHandler<ViewResult> testHandler = new TestResultHandler<ViewResult>();
        test.fetchAllDocuments(testHandler);

        synchronized (testHandler) {
            testHandler.wait(TIMEOUT);
        }

        assertTrue(testHandler.getResponse().getTotalRows() > 5);
    }

    @Test
    public void fetchAllDocumentsIncludeDocsTrue() throws Exception {
        TestResultHandler<ViewResult> testHandler = new TestResultHandler<ViewResult>();
        test.fetchAllDocuments(true, testHandler);

        synchronized (testHandler) {
            testHandler.wait(TIMEOUT);
        }

        assertTrue(testHandler.getResponse().getTotalRows() > 5);
    }

    @Test
    public void fetchAllDocumentsIncludeDocsFalse() throws Exception {
        TestResultHandler<ViewResult> testHandler = new TestResultHandler<ViewResult>();
        test.fetchAllDocuments(false, testHandler);

        synchronized (testHandler) {
            testHandler.wait(TIMEOUT);
        }

        assertTrue(testHandler.getResponse().getTotalRows() > 5);
    }

    @Test
    public void fetchView() throws Exception {
        TestResultHandler<ViewResult> testHandler = new TestResultHandler<ViewResult>();
        test.fetchView(ViewQuery.builder("test/t2"), testHandler);

        synchronized (testHandler) {
            testHandler.wait(TIMEOUT);
        }

        assertThat(testHandler.getResponse().getTotalRows(), is(4));
    }

    @Test
    public void bulkSave() throws Exception {

    }

//    @Test
//    public void storeAttachment() throws Exception {
//        TestResultHandler<ServerResponse> testHandler = new TestResultHandler<ServerResponse>();
//    }

    @Test
    public void saveDocument() throws Exception {
        TestResultHandler<ServerResponse> testHandler = new TestResultHandler<ServerResponse>();
        // void saveDocument(Object doc, ResponseHandler<ServerResponse>
        // response);
        
        Document doc = new Document("new_id");
        doc.put("key", UUID.randomUUID().toString());
        testEmpty.saveDocument(doc, testHandler);
        
        synchronized (testHandler) {
            testHandler.wait(TIMEOUT);
        }
        
        assertThat(testHandler.getResponse().getId(), is("new_id"));
        
        Document fromDb = ((Database)testEmpty).fetchDocument("new_id");
        
        assertThat(fromDb.get("key"), is(fromDb.get("key")));
    }

    @Test(expected=DocumentUpdateConflictException.class)
    public void saveDocumentTwice() throws Exception {
        TestResultHandler<ServerResponse> testHandler = new TestResultHandler<ServerResponse>();
        // void saveDocument(Object doc, ResponseHandler<ServerResponse>
        // response);
        
        Document doc = new Document("new_id");
        doc.put("key", UUID.randomUUID().toString());
        testEmpty.saveDocument(doc, testHandler);
        
        synchronized (testHandler) {
            testHandler.wait(TIMEOUT);
        }
        
        assertThat(testHandler.getResponse().getId(), is("new_id"));

        // Save again - should fail
        testEmpty.saveDocument(new Document("new_id"), testHandler);
        synchronized (testHandler) {
            testHandler.wait(TIMEOUT);
        }
        throw testHandler.getError();
    }
    
    @Test
    public void saveMapWithId() throws Exception {
        TestResultHandler<ServerResponse> testHandler = new TestResultHandler<ServerResponse>();
        // void saveDocument(Object doc, ResponseHandler<ServerResponse>
        // response);
        
        Map<String,String> doc = new HashMap<String, String>();
        doc.put("key", UUID.randomUUID().toString());
        testEmpty.saveDocument("new_id_2", doc, testHandler);
        synchronized (testHandler) {
            testHandler.wait(TIMEOUT);
        }
        
        assertThat(testHandler.getResponse().getId(), is("new_id_2"));
        
        Document fromDb = ((Database)testEmpty).fetchDocument("new_id_2");
        
        assertThat(fromDb.get("key"), is(fromDb.get("key")));;
    }
    
    @Test
    public void saveDocumentWithId() throws Exception {
        TestResultHandler<ServerResponse> testHandler = new TestResultHandler<ServerResponse>();
        // void saveDocument(Object doc, ResponseHandler<ServerResponse>
        // response);
        
        Document doc = new Document();
        doc.put("key", UUID.randomUUID().toString());
        testEmpty.saveDocument("new_id_2", doc, testHandler);
        synchronized (testHandler) {
            testHandler.wait(TIMEOUT);
        }
        
        
        assertThat(testHandler.getResponse().getId(), is("new_id_2"));
        
        Document fromDb = ((Database)testEmpty).fetchDocument("new_id_2");
        
        assertThat(fromDb.get("key"), is(fromDb.get("key")));;
    }

    @Test
    public void fetchDocument() throws Exception {
        TestResultHandler<Document> testHandler = new TestResultHandler<Document>();
        test.fetchDocument("test1", testHandler);
        synchronized (testHandler) {
            testHandler.wait(TIMEOUT);
        }
        assertDocumentTest1(testHandler.getResponse());
        assertNotNull(testHandler.getToken());
    }

    @Test
    public void fetchDocumentWithRev() throws Exception {

        Document d1 = ((Database) test).fetchDocument("test1");

        TestResultHandler<Document> testHandler = new TestResultHandler<Document>();
        test.fetchDocument("test1", d1.getRev(), testHandler);
        synchronized (testHandler) {
            testHandler.wait(TIMEOUT);
        }
        assertDocumentTest1(testHandler.getResponse());
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
