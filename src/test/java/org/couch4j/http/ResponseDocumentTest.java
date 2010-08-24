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
package org.couch4j.http;

import static org.couch4j.util.CollectionUtils.map;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import net.sf.json.JSONObject;
import net.sf.json.util.JSONUtils;

import org.couch4j.api.Attachment;
import org.couch4j.Couch4jBase;
import org.couch4j.api.CouchDbClient;
import org.couch4j.api.Database;
import org.couch4j.api.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ResponseDocumentTest extends Couch4jBase {

    private static final String TEST_ID = "test-000";
    
    private Database test;
    private Database empty;

    public ResponseDocumentTest(CouchDbClient server) {
        super(server);
    }

    @Before
    public void setUp() throws Exception {
        test = server.getDatabase("couch4j");
        assertNotNull(test);
        empty = server.getDatabase("couch4j-" + UUID.randomUUID().toString().substring(0, 6));
        assertNotNull(empty);
        
        Document d1 = new Document(TEST_ID);
        d1.put("k1", "v1");
        empty.saveDocument(d1);
        empty.saveDocument(new Document("test-001"));
        empty.saveDocument(new Document("test-002"));
    }

    @After
    public void teardown() {
        empty.delete();
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testInstantion() {
        new ResponseDocument(null, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testInstantion2() {
        new ResponseDocument(null, "");
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testInstantion3() {
        new ResponseDocument("", null);
    }
    
    @Test
    public void documentToJson() throws Exception {
        Document d = test.fetchDocument(Couch4jBase.VALID_DOC_ID);
        JSONObject json = JSONObject.fromObject(d.toJson());
        assertThat(d.toJson(), is(json.toString()));
    }

    @Test
    public void documentToJSONObject() throws Exception {
        Document d = test.fetchDocument(Couch4jBase.VALID_DOC_ID);
        JSONObject json = d.toJSONObject();
        assertNotNull(json);
    }

    @Test
    public final void testGetId() {
        Document d = test.fetchDocument(Couch4jBase.VALID_DOC_ID);
        JSONObject json = d.toJSONObject();
        assertEquals(d.getId(), json.getString("_id"));
    }

    @Test
    public final void testGetRev() {
        Document d = test.fetchDocument(Couch4jBase.VALID_DOC_ID);
        JSONObject json = d.toJSONObject();
        assertEquals(d.getRev(), json.getString("_rev"));
    }

    @Test
    public final void testPut() {
        Document d = empty.fetchDocument(TEST_ID);
        assertNotNull(d);
        
        assertThat((String)d.get("k1"), is("v1"));
        
        d.put("k2", "v2");
        
        empty.saveDocument(d);

        Document d2 = empty.fetchDocument(TEST_ID);
        assertTrue(!d.equals(d2));
        assertNotNull(d);
        
        assertThat((String)d2.get("k2"), is("v2"));
    }

    @Test
    public final void testPutAll() {
        Document d = empty.fetchDocument(TEST_ID);
        assertNotNull(d);
        
        assertThat((String)d.get("k1"), is("v1"));
        
        Map<String,String> map = map("k3", "v3", "k4", "v4");
        d.putAll(map);
        empty.saveDocument(d);

        Document d2 = empty.fetchDocument(TEST_ID);
        assertTrue(!d.equals(d2));
        assertNotNull(d);
        
        assertThat((String)d2.get("k3"), is("v3"));
        assertThat((String)d2.get("k4"), is("v4"));
    }

    @Test
    public final void testPutAllNull() {
        Document d = empty.fetchDocument(TEST_ID);
        assertNotNull(d);
        d.putAll(null);
        empty.saveDocument(d);
    }
    
    @SuppressWarnings("deprecation")
    @Test
    public final void testGetAttachment() {
        Document d = test.fetchDocument(Couch4jBase.DOC_ID_WITH_ATTACHMENT);
        
        Attachment a = d.getAttachment(Couch4jBase.ATTACHMENT_NAME);
        assertNotNull(a);
        assertThat(a.getName(), is(Couch4jBase.ATTACHMENT_NAME));
        assertThat(a.getContentId(), is(d.getId()));
        assertThat(a.getDocumentId(), is(d.getId()));
    }

    @Test
    public final void testGetAttachmentEmpty() {
        Document d = test.fetchDocument(Couch4jBase.DOC_ID_WITHOUT_ATTACHMENT);
        
        Attachment a = d.getAttachment(Couch4jBase.ATTACHMENT_NAME);
        assertNull(a);
    }
    
    @Test
    public final void testGetAttachments() {
        Document d = test.fetchDocument(Couch4jBase.DOC_ID_WITH_ATTACHMENT);
        
        Attachment a = d.getAttachment(Couch4jBase.ATTACHMENT_NAME);
        Collection<Attachment> attachments = d.getAttachments();
        assertThat(attachments.size(), is(1));
        assertThat(attachments.iterator().next(), is(a));
    }
    
    @Test
    public final void testGetAttachmentsEmpty() {
        Document d = test.fetchDocument(Couch4jBase.DOC_ID_WITHOUT_ATTACHMENT);
        
        Collection<Attachment> attachments = d.getAttachments();
        assertThat(attachments.size(), is(0));
    }

    @Test
    public final void testGetAttachmentNames() {
        Document d = test.fetchDocument(Couch4jBase.DOC_ID_WITH_ATTACHMENT);
        
        Collection<String> names = d.getAttachmentNames();
        assertNotNull(names);
        assertThat(names.size(), is(1));
        assertThat(names.iterator().next(), is(Couch4jBase.ATTACHMENT_NAME));
    }
    
    @Test
    public final void testGetAttachmentNamesEmpty() {
        Document d = test.fetchDocument(Couch4jBase.DOC_ID_WITHOUT_ATTACHMENT);
        
        Collection<String> names = d.getAttachmentNames();
        assertNotNull(names);
        assertThat(names.size(), is(0));
    }

    @Test
    public final void testToJson() {
        Document d = test.fetchDocument(Couch4jBase.DOC_ID_WITH_ATTACHMENT);
        assertNotNull(d.toJson());
        assertTrue(JSONUtils.mayBeJSON(d.toJson()));
    }
    
    @Test
    public final void testToJSONObject() {
        Document d = test.fetchDocument(Couch4jBase.DOC_ID_WITH_ATTACHMENT);
        assertNotNull(d.toJSONObject());
        assertTrue(!d.toJSONObject().isNullObject());
    }

    @Test
    public final void testToString() {
        Document d = test.fetchDocument(Couch4jBase.DOC_ID_WITH_ATTACHMENT);
        assertThat(d.toString(), is(d.toJson()));
    }

    @Test
    public final void testGetDatabase() {
        ResponseDocument d = (ResponseDocument)test.fetchDocument(Couch4jBase.DOC_ID_WITH_ATTACHMENT);
        assertThat(d.getDatabase(), is(test));
    }
    
    @Test(expected=IllegalStateException.class)
    public final void testLazilyFetchWithoutDatabaseFails() {
        ResponseDocument d = new ResponseDocument("id", "rev");
        assertNotNull(d.toJson());
    }
}
