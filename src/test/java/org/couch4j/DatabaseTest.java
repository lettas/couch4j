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
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

import net.sf.json.JSONArray;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.couch4j.Database.StreamContext;
import org.couch4j.exceptions.DocumentNotFoundException;
import org.couch4j.exceptions.DocumentUpdateConflictException;
import org.couch4j.util.StreamUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.example.test.A;

@RunWith(Parameterized.class)
public class DatabaseTest extends Couch4jBase {



    private static final int NUM_ALL_DOCS = 5;

    private Database test;
    private Database testEmpty;

    public DatabaseTest(CouchDbClient server) {
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
        assertEquals(NUM_ALL_DOCS, rows.getTotalRows());
    }

    @Test
    public void testFetchAllDocumentsIterateViewResultRows() throws Exception {
        ViewResult rows = test.fetchAllDocuments();

        boolean iterate = false;
        for (ViewResultRow row : rows) {
            iterate = true;
            assertNotNull(row);
            assertNotNull(row.getId());
            // Document
            Document doc = row.getDocument();
            assertNotNull(doc.getId());
            assertNotNull(doc.toJson());
        }
        assertTrue("Should iterate over all rows in the ViewResult", iterate);
    }

    @Test
    public void testFetchDocumentById() throws Exception {
        Document d = test.fetchDocument(VALID_DOC_ID);
        assertDocumentTest1(d);
    }
    
    @Test(expected=DocumentNotFoundException.class)
    public void testFetchDocumentByInvalidId() throws Exception {
        Document d = test.fetchDocument("INVALID_DOC_ID");
        assertDocumentTest1(d);
    }
    
    @Test(expected=DocumentNotFoundException.class)
    public void testFetchDocumentByIdWithInvalidRevision() throws Exception {
        Document d = test.fetchDocument(VALID_DOC_ID, "2-1234");
        assertDocumentTest1(d);
    }
    
    @Test
    public void testFetchDocumentByIdWithRevision() throws Exception {
        Document d = test.fetchDocument(VALID_DOC_ID);
        assertDocumentTest1(d);
        
        Document d2 = test.fetchDocument(VALID_DOC_ID, d.getRev());
        assertEquals(d.getId(), d2.getId());
        assertDocumentTest1(d2);
    }

    @Test
    public void testWithAttachmentAsStream() throws Exception {
        final int CONTENT_LENGTH = 9276;

        final String docId = DOC_ID_WITH_ATTACHMENT;
        Document d = test.fetchDocument(docId);
        assertEquals(docId, d.getId());

        final String attachmentId = ATTACHMENT_NAME;
        Attachment a = d.getAttachment(attachmentId);
        assertNotNull(a);
        assertEquals("image/png", a.getContentType());
        assertEquals(CONTENT_LENGTH, a.getLength());
//        assertTrue(a.isStub());

        a.retrieve(new StreamContext() {
            public void withInputStream(InputStream is) throws IOException {
                assertNotNull(is);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                StreamUtils.copy(is, baos);
                byte[] b = baos.toByteArray();
                assertEquals(CONTENT_LENGTH, b.length);

                // is.close(); will be closed automatically
            }
        });
    }

    @Test
    public void testFetchView() throws Exception {
        ViewQuery v = ViewQuery.builder("test/t1");
        List<ViewResultRow> l = test.fetchView(v).getRows();
        assertNotNull(l);
        assertEquals(4, l.size());

        ViewResultRow row = l.get(0);
        assertEquals(VALID_DOC_ID, row.getId());

        Document d = row.getDocument();
        assertDocumentTest1(d);
    }

    @Test
    public void testFetchEmptyView() throws Exception {
        ViewQuery v = ViewQuery.builder("test/empty");
        List<ViewResultRow> l = test.fetchView(v).getRows();
        assertNotNull(l);
        assertEquals(0, l.size());
    }

    @Test
    public void testSaveExistingDocument() throws Exception {
        Document d = test.fetchDocument("test2");
        final String rand = UUID.randomUUID().toString();
        final String key = "rand_test_str";
        d.put(key, rand);

        // Save
        test.saveDocument(d);

        // Fetch again
        d = test.fetchDocument("test2");
        assertEquals(rand, d.get(key));
    }

    @Test
    // (expected=DocumentUpdateConflictException.class)
    public void testSaveExistingDocumentWithUpdateConflict() throws Exception {
        Document d1 = test.fetchDocument("test3");
        Document d = test.fetchDocument("test3");
        final String rand = UUID.randomUUID().toString();
        final String key = "rand_test_str";
        d.put(key, rand);

        // Save
        test.saveDocument(d);

        // Save the first document instance - should throw a conflict exception
        try {
            test.saveDocument(d1);
            fail("Expected DocumentUpdateConflictException");
        } catch (DocumentUpdateConflictException ce) {
        }
    }

    @Test
    public void testDeleteDocument() throws Exception {
        final String documentId = UUID.randomUUID().toString();
        Document d = new Document(documentId);
        test.saveDocument(d);

        d = test.fetchDocument(documentId);
        assertEquals(documentId, d.getId());

        test.deleteDocument(d);

        try {
            d = test.fetchDocument(documentId);
            // Should throw NotFoundException - but only here
            fail("NotFoundException expected");
        } catch (DocumentNotFoundException nfe) {

        }
    }

    @Test
    public void testSaveNewDocument() throws Exception {
        Document d = new Document(NEW_DOCUMENT_ID);
        final String rand = UUID.randomUUID().toString();
        final String key = "rand_test_str";
        d.put(key, rand);

        // Save
        test.saveDocument(d);

        // Fetch again
        d = test.fetchDocument(NEW_DOCUMENT_ID);
        assertEquals(rand, d.get(key));
    }
    
    @Test
    public void saveSerializable() throws Exception {
        
        A a = new A();
        
        ServerResponse r = testEmpty.saveDocument(a);
        assertNotNull(r.getId());
        
        Document doc = testEmpty.fetchDocument(r.getId());
        System.out.println(doc);
    }

    @Test
    public void testDelete() throws Exception {
        Document d = test.fetchDocument("test2");
        final String rand = UUID.randomUUID().toString();
        final String key = "rand_test_str";
        d.put(key, rand);

        // Save
        test.saveDocument(d);

        // Fetch again
        d = test.fetchDocument("test2");
        assertEquals(rand, d.get(key));
    }

    @Test
    public void testDeleteDatabase() throws Exception {
        testEmpty.delete();
        // Check if the database exists...
        HttpClient client = new DefaultHttpClient();
        // Check if the database exists
        HttpGet m = null;
        try {
            m = new HttpGet(server.toString() + "/" + testEmpty.getName());
            HttpResponse response = client.execute(m);
            assertThat(HttpStatus.SC_NOT_FOUND, is(response.getStatusLine().getStatusCode()));
        } catch (IOException e) {
            fail(e.getLocalizedMessage());
        }
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
