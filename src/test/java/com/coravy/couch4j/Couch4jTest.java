package com.coravy.couch4j;

import static org.junit.Assert.*;

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

import com.coravy.core.io.StreamUtils;
import com.coravy.couch4j.api.Attachment;
import com.coravy.couch4j.api.Database;
import com.coravy.couch4j.api.ViewResult;
import com.coravy.couch4j.api.ViewResultRow;
import com.coravy.couch4j.api.Database.StreamContext;

public class Couch4jTest {

    static final String VALID_DOC_ID = "test1";
    static final String NEW_DOCUMENT_ID = "new_document";

    static final String EMPTY_DATABASE_NAME = "couch4j-empty";
    static final String TEST_DATABASE_NAME = "couch4j";

    private static final int NUM_ALL_DOCS = 5;
    
    private CouchDB server;
    private Database<Document> test;
    private Database<Document> testEmpty;
    
    

    static CouchDB testDbInstance() {
        return CouchDB.localServerInstance(); // CouchDB 0.9.0
        //return new CouchDB("localhost", 59810);
    }
    
    @Before
    public void setUp() throws Exception {

        server = testDbInstance();

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
        ViewResult<Document> rows = test.fetchAllDocuments();
        assertEquals(NUM_ALL_DOCS, rows.getTotalRows());
    }
    
    @Test
    public void testFetchAllDocumentsIterateViewResultRows() throws Exception {
        ViewResult<Document> rows = test.fetchAllDocuments();
        
        boolean iterate = false;
        for (ViewResultRow<Document> row : rows) {
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
        View v = View.builder("test/t1").build();
        List<ViewResultRow<Document>> l = test.fetchView(v).getRows();
        assertNotNull(l);
        assertEquals(4, l.size());

        ViewResultRow<Document> row = l.get(0);
        assertEquals(VALID_DOC_ID, row.getId());

        Document d = row.getDocument();
        assertDocumentTest1(d);
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
