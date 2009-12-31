package com.coravy.couch4j;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

import net.sf.json.JSONArray;

import org.junit.Before;
import org.junit.Test;

import com.coravy.core.io.StreamUtils;
import com.coravy.couch4j.Database.StreamContext;

public class Couch4jTest {

    private Database test;

    @Before
    public void setUp() throws Exception {
        test = CouchDB.localServerInstance().getDatabase("couch4j");
        assertNotNull(test);
    }

    @Test
    public void testFetchDocumentById() throws Exception {
        Document d = test.fetchDocument("test1");
        assertDocumentTest1(d);
    }
    
    @Test
    public void testFetchDocumentWithAttachment() throws Exception {
        
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
        
        // Retrieve InputStream!
        
        a.withAttachmentAsStream(new StreamContext() {
            public void withResponseStream(InputStream is) throws IOException {
                assertNotNull(is);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                StreamUtils.copy(is, baos);
                byte[] b = baos.toByteArray();
                assertEquals(CONTENT_LENGTH, b.length);
            }
        });
    }

    @Test
    public void testFetchView() throws Exception {
        View v = View.builder("test/t1").build();
        List<ViewResultRow> l = test.fetchView(v).getRows();
        assertNotNull(l);
        assertEquals(1, l.size());

        ViewResultRow row = l.get(0);
        assertEquals("test1", row.getId());

        Document d = row.getDocument();
        assertDocumentTest1(d);
    }

    @Test
    public void testSaveDocument() throws Exception {
        Document d = test.fetchDocument("test2");
        final String rand = UUID.randomUUID().toString();
        final String key = "rand_test_str";
        d.put(key, rand);

        System.out.println(d.getClass());

        // Save
        test.saveDocument(d);

        // Fetch again
        d = test.fetchDocument("test2");
        assertEquals(rand, d.get(key));
    }

    private void assertDocumentTest1(Document d) {
        assertEquals("test1", d.getId());

        JSONArray ary = (JSONArray) d.get("a");
        assertEquals(1, ary.get(0));
        assertEquals(2, ary.get(1));
        assertEquals(3, ary.get(2));

        assertEquals("test", d.get("b"));
    }

}
