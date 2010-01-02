package com.coravy.couch4j;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import net.sf.json.JSONObject;

import org.junit.Before;
import org.junit.Test;

public class DocumentTest {

    private Database<Document> test;

    @Before
    public void setUp() throws Exception {
        test = CouchDB.localServerInstance().getDatabase("couch4j");
        assertNotNull(test);
    }

    @Test
    public void testDocumentToJson() throws Exception {
        Document d = test.fetchDocument(Couch4jTest.VALID_DOC_ID);
        JSONObject json = JSONObject.fromObject(d.toJson());
        assertEquals(d.toJson(), json.toString());
    }
    
}