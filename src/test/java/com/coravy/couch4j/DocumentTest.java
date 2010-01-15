package com.coravy.couch4j;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import net.sf.json.JSONObject;

import org.junit.Before;
import org.junit.Test;

public class DocumentTest extends Couch4jBase {

    private Database test;

    public DocumentTest(CouchDB server) {
        super(server);
    }

    @Before
    public void setUp() throws Exception {
        test = server.getDatabase("couch4j");
        assertNotNull(test);
    }

    @Test
    public void testDocumentToJson() throws Exception {
        Document d = test.fetchDocument(Couch4jTest.VALID_DOC_ID);
        JSONObject json = JSONObject.fromObject(d.toJson());
        assertEquals(d.toJson(), json.toString());
    }

    @Test
    public void testDocumentToJSONObject() throws Exception {
        Document d = test.fetchDocument(Couch4jTest.VALID_DOC_ID);
        JSONObject json = d.toJSONObject();
        assertEquals(d.getId(), json.getString("_id"));
    }
}
