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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

import net.sf.json.JSONArray;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.coravy.core.io.StreamUtils;
import com.coravy.couch4j.Database.StreamContext;
import com.coravy.couch4j.exceptions.DocumentNotFoundException;
import com.coravy.couch4j.exceptions.DocumentUpdateConflictException;

@RunWith(Parameterized.class)
public class Couch4jTest extends Couch4jBase {

    static final String VALID_DOC_ID = "test1";
    static final String NEW_DOCUMENT_ID = "new_document";

    static final String EMPTY_DATABASE_NAME = "couch4j-empty";
    static final String TEST_DATABASE_NAME = "couch4j";

    private static final int NUM_ALL_DOCS = 5;

    private Database test;
    private Database testEmpty;

    public Couch4jTest(Server server) {
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

    @Test
    public void testWithAttachmentAsStream() throws Exception {
        final int CONTENT_LENGTH = 9276;

        final String docId = "test3_with_attachment";
        Document d = test.fetchDocument(docId);
        assertEquals(docId, d.getId());

        final String attachmentId = "Icon-128x128.png";
        Attachment a = d.getAttachment(attachmentId);
        assertNotNull(a);
        assertEquals("image/png", a.getContentType());
        assertEquals(CONTENT_LENGTH, a.getLength());
        assertTrue(a.isStub());

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
        View v = View.builder("test/t1");
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
        View v = View.builder("test/empty");
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
        HttpClient client = new HttpClient();
        // Check if the database exists
        HttpMethod m = null;
        try {
            m = new GetMethod(server.toString() + "/" + testEmpty.getName());
            int statusCode = client.executeMethod(m);
            assertEquals(HttpStatus.SC_NOT_FOUND, statusCode);
        } catch (IOException e) {
            fail(e.getLocalizedMessage());
        } finally {
            if (null != m) {
                m.releaseConnection();
            }
        }
    }

    private void assertDocumentTest1(Document d) {
        assertEquals(VALID_DOC_ID, d.getId());

        JSONArray ary = (JSONArray) d.get("a");
        assertEquals(1, ary.get(0));
        assertEquals(2, ary.get(1));
        assertEquals(3, ary.get(2));

        assertEquals("test", d.get("b"));
    }

}
